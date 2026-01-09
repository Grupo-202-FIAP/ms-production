# MS Production - Microserviço de Produção de Pedidos

Microserviço responsável pelo gerenciamento do ciclo de vida de produção de pedidos em uma arquitetura de microsserviços. Este serviço processa eventos de pedidos recebidos via SQS, gerencia o status de produção e publica callbacks para outros serviços.

## 📋 Índice

- [Visão Geral](#visão-geral)
- [Tecnologias](#tecnologias)
- [Arquitetura](#arquitetura)
- [Pré-requisitos](#pré-requisitos)
- [Configuração e Instalação](#configuração-e-instalação)
- [Executando a Aplicação](#executando-a-aplicação)
- [API Endpoints](#api-endpoints)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Status dos Pedidos](#status-dos-pedidos)
- [Integração com SQS](#integração-com-sqs)
- [Deploy](#deploy)
- [Testes](#testes)
- [Observabilidade](#observabilidade)

## 🎯 Visão Geral

O MS Production é parte de uma arquitetura de microsserviços que implementa o padrão Saga para orquestração de transações distribuídas. Este serviço é responsável por:

- Receber pedidos via filas SQS
- Gerenciar o ciclo de vida de produção dos pedidos
- Atualizar status de pedidos (RECEIVED, PREPARING, READY, COMPLETED, CANCELLED)
- Publicar eventos de callback para outros serviços
- Implementar rollback em caso de falhas

## 🛠 Tecnologias

- **Java 17** - Linguagem de programação
- **Spring Boot 3.2.6** - Framework principal
- **Spring Cloud AWS 3.0.4** - Integração com AWS SQS
- **PostgreSQL 17** - Banco de dados relacional
- **Flyway** - Migração de banco de dados
- **MapStruct** - Mapeamento de objetos
- **Lombok** - Redução de boilerplate
- **SpringDoc OpenAPI** - Documentação da API
- **Docker** - Containerização
- **Kubernetes** - Orquestração de containers
- **Terraform** - Infraestrutura como código
- **Datadog** - Observabilidade e monitoramento

## 🏗 Arquitetura

O projeto segue os princípios da **Arquitetura Hexagonal (Ports and Adapters)**, separando a lógica de negócio da infraestrutura:

```
src/main/java/com/nextimefood/msproduction/
├── domain/              # Camada de domínio (regras de negócio)
│   ├── entity/         # Entidades de domínio
│   ├── enums/          # Enumeradores
│   └── order/          # Exceções de domínio
├── application/         # Camada de aplicação (casos de uso)
│   ├── config/         # Configurações
│   ├── gateways/       # Portas (interfaces)
│   ├── mapper/         # Mapeadores
│   └── usecases/       # Casos de uso
└── infrastructure/      # Camada de infraestrutura (adaptadores)
    ├── adapters/       # Adaptadores
    ├── controller/     # Controllers REST
    ├── messaging/      # Mensageria (SQS)
    └── persistence/    # Repositórios e entidades JPA
```

### Fluxo de Processamento

1. **Recebimento de Eventos**: O serviço consome mensagens da fila SQS `production-queue`
2. **Processamento**: Baseado no status do evento (SUCCESS, FAIL, ROLLBACK_PENDING), o serviço:
   - Recebe pedidos com status PENDING
   - Inicia produção para pedidos com status PROCESSED
   - Executa rollback quando necessário
3. **Callback**: Após processar, publica eventos na fila `production-callback-queue`

## 📦 Pré-requisitos

- Java 17 ou superior
- Maven 3.6+
- Docker e Docker Compose (para ambiente local)
- AWS CLI (para testes locais com LocalStack)
- PostgreSQL 17 (ou usar Docker)

## ⚙️ Configuração e Instalação

### 1. Clone o repositório

```bash
git clone <repository-url>
cd ms-production
```

### 2. Configuração de Ambiente

O projeto utiliza variáveis de ambiente para configuração. As principais são:

| Variável | Descrição | Valor Padrão |
|----------|-----------|--------------|
| `SPRING_PROFILES_ACTIVE` | Perfil ativo do Spring | `local` |
| `POSTGRES_DB_URL` | URL do banco de dados | `jdbc:postgresql://localhost:5432/production_db` |
| `POSTGRES_DB_USERNAME` | Usuário do banco | `postgres` |
| `POSTGRES_DB_PASSWORD` | Senha do banco | `production_password` |
| `AWS_REGION` | Região AWS | `us-east-1` |
| `AWS_ACCESS_KEY_ID` | Chave de acesso AWS | - |
| `AWS_SECRET_ACCESS_KEY` | Chave secreta AWS | - |
| `SQS_PRODUCTION_QUEUE` | Nome da fila de produção | `production-queue` |
| `SQS_PRODUCTION_CALLBACK_QUEUE` | Nome da fila de callback | `production-callback-queue` |

## 🚀 Executando a Aplicação

### Opção 1: Docker Compose (Recomendado para desenvolvimento)

1. Inicie os serviços:

```bash
docker-compose up -d
```

Isso irá iniciar:
- **production-api**: Aplicação Spring Boot na porta 8090
- **postgres**: Banco de dados PostgreSQL na porta 5432
- **localstack**: Emulador AWS para SQS na porta 4566

2. Verifique se os serviços estão rodando:

```bash
docker-compose ps
```

3. Acesse a documentação da API:

```
http://localhost:8090/swagger-ui.html
```

### Opção 2: Execução Local

1. Inicie o PostgreSQL e LocalStack:

```bash
docker-compose up -d postgres localstack
```

2. Aguarde alguns segundos para o LocalStack inicializar e criar as filas SQS

3. Execute a aplicação:

```bash
./mvnw spring-boot:run
```

ou

```bash
mvn spring-boot:run
```

A aplicação estará disponível em `http://localhost:8090`

### Opção 3: Build e Execução com Docker

1. Construa a imagem:

```bash
docker build -t ms-production:latest .
```

2. Execute o container:

```bash
docker run -p 8090:8090 \
  -e SPRING_PROFILES_ACTIVE=local \
  -e POSTGRES_DB_URL=jdbc:postgresql://host.docker.internal:5432/production_db \
  -e AWS_REGION=us-east-1 \
  -e AWS_ACCESS_KEY_ID=test \
  -e AWS_SECRET_ACCESS_KEY=test \
  ms-production:latest
```

## 📡 API Endpoints

### Listar Pedidos

```http
GET /order
```

**Parâmetros de Query:**
- `page` (opcional): Número da página (padrão: 0)
- `size` (opcional): Tamanho da página (padrão: 10)
- `sort` (opcional): Campo para ordenação (padrão: createdAt,DESC)

**Resposta:**
```json
{
  "content": [
    {
      "id": "uuid",
      "transactionId": "uuid",
      "identifier": "ORD-123",
      "totalPrice": 99.99,
      "totalItems": 3,
      "customerId": "uuid",
      "status": "PREPARING",
      "paymentStatus": "PROCESSED",
      "items": [...],
      "createdAt": "2024-01-01T10:00:00",
      "updatedAt": "2024-01-01T10:05:00"
    }
  ],
  "totalElements": 100,
  "totalPages": 10,
  "size": 10,
  "number": 0
}
```

### Marcar Pedido como Pronto

```http
PUT /order/{orderId}/ready
```

**Resposta:**
```json
{
  "id": "uuid",
  "status": "READY",
  ...
}
```

### Marcar Pedido como Completo

```http
PUT /order/{orderId}/complete
```

**Resposta:**
```json
{
  "id": "uuid",
  "status": "COMPLETED",
  ...
}
```

## 📁 Estrutura do Projeto

```
ms-production/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/nextimefood/msproduction/
│   │   │       ├── application/        # Casos de uso e lógica de aplicação
│   │   │       ├── domain/             # Entidades e regras de negócio
│   │   │       ├── infrastructure/    # Adaptadores (REST, SQS, JPA)
│   │   │       └── utils/              # Utilitários
│   │   └── resources/
│   │       ├── application.yaml        # Configuração padrão
│   │       └── application-local.yaml  # Configuração local
│   └── test/                           # Testes unitários e de integração
├── infra/
│   ├── k8s/                            # Manifests Kubernetes
│   └── terraform/                      # Código Terraform
├── local/                              # Scripts para ambiente local
├── docker-compose.yml                  # Orquestração local
├── Dockerfile                          # Imagem Docker
└── pom.xml                             # Dependências Maven
```

## 🔄 Status dos Pedidos

O serviço gerencia os seguintes status de pedidos:

| Status | Descrição |
|--------|-----------|
| `RECEIVED` | Pedido recebido e aguardando processamento |
| `PREPARING` | Pedido em produção/preparação |
| `READY` | Pedido pronto para entrega |
| `COMPLETED` | Pedido finalizado |
| `CANCELLED` | Pedido cancelado |

### Fluxo de Status

```
RECEIVED → PREPARING → READY → COMPLETED
    ↓
CANCELLED (em caso de rollback ou cancelamento)
```

## 🔌 Integração com SQS

### Filas Utilizadas

1. **production-queue**: Fila de entrada para receber eventos de pedidos
2. **production-callback-queue**: Fila de saída para publicar callbacks

### Formato de Eventos

Os eventos seguem o seguinte formato:

```json
{
  "transactionId": "uuid",
  "orderId": "uuid",
  "status": "SUCCESS|FAIL|ROLLBACK_PENDING",
  "source": "PAYMENT|PRODUCTION|...",
  "payload": {
    "id": "uuid",
    "transactionId": "uuid",
    "identifier": "ORD-123",
    "totalPrice": 99.99,
    "status": "RECEIVED",
    "paymentStatus": "PENDING|PROCESSED",
    "items": [...]
  },
  "message": "Descrição do evento",
  "createdAt": "2024-01-01T10:00:00"
}
```

### Testando com LocalStack

Scripts auxiliares estão disponíveis em `local/` para publicar eventos de teste:

```bash
# Publicar pedido pendente
./local/publish-order-pending.sh

# Publicar pedido processado
./local/publish-order-processed.sh

# Publicar falha
./local/publish-order-fail.sh

# Publicar rollback
./local/publish-order-rollback.sh
```

## 🚢 Deploy

### Kubernetes

O projeto inclui manifests Kubernetes em `infra/k8s/`:

- `deployment.yaml`: Deployment da aplicação
- `service.yaml`: Service para expor a aplicação
- `hpa.yaml`: Horizontal Pod Autoscaler
- `db/`: Configurações do PostgreSQL
- `externalsecret.yaml`: Gerenciamento de secrets via External Secrets Operator

### Terraform

A infraestrutura AWS é gerenciada via Terraform em `infra/terraform/`:

- ECR para imagens Docker
- IAM roles e policies
- IRSA (IAM Roles for Service Accounts)

### CI/CD

O projeto utiliza GitHub Actions para CI/CD (ver `.github/workflows/`).

## 🧪 Testes

Execute os testes com:

```bash
./mvnw test
```

ou

```bash
mvn test
```

## 📊 Observabilidade

### Datadog

A aplicação está configurada para integração com Datadog através do Java Agent, que é automaticamente incluído no Dockerfile.

### Logs

Os logs são estruturados em formato JSON usando Logstash Logback Encoder, facilitando a análise e processamento por ferramentas de observabilidade.

### Health Checks

A aplicação expõe endpoints de health check do Spring Boot Actuator (quando configurado).

## 📝 Licença

Este projeto é proprietário da NextimeFood.

## 👥 Contribuindo

Para contribuir com o projeto, siga o fluxo de trabalho padrão:

1. Crie uma branch a partir de `main`
2. Faça suas alterações
3. Execute os testes
4. Crie um Pull Request

## 📞 Suporte

Para questões ou suporte, entre em contato com a equipe de desenvolvimento.

