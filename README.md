# Microsserviço de Produção

Microsserviço responsável pelo fluxo de produção ("cozinha"): acompanhar fila de pedidos, etapas de preparo e atualização de status.

## Arquitetura

Este projeto segue os princípios da **Clean Architecture**, organizando o código em camadas bem definidas:

```
src/main/java/com/nextime/msproduction/
├── domain/                    # Camada de Domínio
│   ├── enums/                 # Enumeradores do domínio
│   ├── repository/            # Interfaces de repositório (portas)
│   ├── ProductionOrder.java   # Entidade de ordem de produção
│   ├── ProductionStep.java    # Entidade de etapa de produção
│   └── KitchenQueue.java      # Entidade de fila da cozinha
├── application/               # Camada de Aplicação
│   ├── dto/                   # DTOs de aplicação
│   ├── mapper/                # Mappers de aplicação
│   └── usecase/               # Casos de uso
│       ├── interfaces/        # Interfaces dos casos de uso
│       └── impl/              # Implementações dos casos de uso
├── infrastructure/            # Camada de Infraestrutura
│   ├── adapter/               # Adaptadores (implementações de repositórios)
│   ├── persistence/           # Persistência
│   │   ├── entity/            # Entidades JPA (PostgreSQL)
│   │   ├── mongodb/           # Documentos MongoDB
│   │   ├── mapper/            # Mappers de persistência
│   │   └── repository/        # Repositórios JPA e MongoDB
├── api/                       # Camada de API
│   ├── controller/            # Controllers REST
│   └── dto/                   # DTOs de request/response da API
└── config/                    # Configurações
```

## Tecnologias

- **Java 17**
- **Spring Boot 3.5.7**
- **PostgreSQL** (banco SQL)
- **MongoDB** (banco NoSQL)
- **Spring Data JPA**
- **Spring Data MongoDB**
- **Flyway** (migrações de banco)
- **Spring Actuator** (healthcheck e métricas)
- **Swagger/OpenAPI** (documentação da API)

## Endpoints REST

### GET /production/queue
Consulta a fila de produção (dados do MongoDB).

**Resposta:**
```json
[
  {
    "id": "string",
    "productionOrderId": "uuid",
    "orderIdentifier": "string",
    "status": "PENDING|IN_PREPARATION|READY|COMPLETED|CANCELLED",
    "priority": 1,
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T00:00:00",
    "items": [
      {
        "productName": "string",
        "quantity": 1,
        "status": "PENDING|IN_PREPARATION|READY|COMPLETED|CANCELLED"
      }
    ]
  }
]
```

### PATCH /production/step/{id}/status
Atualiza o status de uma etapa de produção.

**Request:**
```json
{
  "status": "IN_PREPARATION",
  "notes": "Iniciando preparo"
}
```

**Resposta:**
```json
{
  "id": "uuid",
  "productionOrderId": "uuid",
  "productName": "string",
  "quantity": 1,
  "status": "IN_PREPARATION",
  "notes": "Iniciando preparo",
  "startedAt": "2024-01-01T00:00:00",
  "completedAt": null,
  "priority": 1
}
```

### POST /production/{id}/finish
Finaliza uma ordem de produção.

**Resposta:**
```json
{
  "id": "uuid",
  "orderIdentifier": "string",
  "originalOrderId": "uuid",
  "totalPrice": 100.00,
  "status": "COMPLETED",
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00",
  "steps": [...]
}
```

## Configuração

### Variáveis de Ambiente

```bash
# PostgreSQL
DATABASE_URL=jdbc:postgresql://localhost:5432/ms_production
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=postgres

# MongoDB
MONGODB_URI=mongodb://localhost:27017/ms_production
MONGODB_DATABASE=ms_production

# Server
SERVER_PORT=8081
```

### Banco de Dados

O projeto utiliza dois bancos de dados:

1. **PostgreSQL**: Armazena `ProductionOrder` e `ProductionStep`
2. **MongoDB**: Armazena `KitchenQueue` (fila de produção)

As migrações do PostgreSQL são gerenciadas pelo Flyway e estão em `src/main/resources/db/migration/`.

## Estrutura de Dados

### ProductionOrder (PostgreSQL)
- Ordem de produção principal
- Relacionada com múltiplos `ProductionStep`

### ProductionStep (PostgreSQL)
- Etapas individuais de produção
- Cada etapa representa um produto/item do pedido

### KitchenQueue (MongoDB)
- Fila de produção para visualização rápida
- Otimizada para consultas frequentes
- Contém informações agregadas da ordem e seus itens

## Status de Produção

- `PENDING`: Aguardando início
- `IN_PREPARATION`: Em preparação
- `READY`: Pronto
- `COMPLETED`: Finalizado
- `CANCELLED`: Cancelado

## Healthcheck e Monitoramento

- **Health**: `http://localhost:8081/actuator/health`
- **Metrics**: `http://localhost:8081/actuator/prometheus`
- **Swagger UI**: `http://localhost:8081/swagger-ui.html`

## Próximos Passos

- [ ] Implementar comunicação assíncrona (eventos)
- [ ] Adicionar testes unitários e de integração
- [ ] Implementar lógica completa de negócio
- [ ] Adicionar validações de negócio
- [ ] Implementar tratamento de erros customizado
- [ ] Adicionar logging estruturado


