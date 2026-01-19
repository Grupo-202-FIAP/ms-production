# Testes de Integração - MS Production

Este diretório contém os testes de integração do microsserviço de produção utilizando Cucumber/BDD.

## Estrutura

```
integration/
├── config/
│   ├── CucumberSpringConfig.java    # Configuração Spring para Cucumber
│   └── SqsTestConfig.java           # Configuração do SQS para testes
├── consumer/
│   └── ConsumeMessage.java          # Utilitário para consumir mensagens SQS
├── hooks/
│   └── TestHooks.java               # Hooks para limpar estado entre testes
├── steps/
│   └── ProductionIntegrationSteps.java  # Step definitions do Cucumber
├── utils/
│   └── SqsTestSupport.java          # Utilitários de suporte para SQS
└── ProductionApplicationIntegrationTests.java  # Runner principal dos testes
```

## Pré-requisitos

1. **LocalStack** rodando na porta 4566
2. **Java 17+**
3. **Maven**

## Configuração do Ambiente

### 1. Iniciar o LocalStack

```bash
# Usando docker-compose (na raiz do projeto)
docker-compose up -d localstack
```

### 2. Criar as filas SQS

```bash
# Execute o script de inicialização
cd local
./init-aws.sh
```

O script deve criar as seguintes filas:
- `production-queue`
- `production-callback-queue`

## Executando os Testes

### Executar todos os testes de integração

```bash
mvn test -Dtest=ProductionApplicationIntegrationTests
```

### Executar com profile de teste específico

```bash
mvn test -Dtest=ProductionApplicationIntegrationTests -Dspring.profiles.active=test
```

### Executar com logs detalhados

```bash
mvn test -Dtest=ProductionApplicationIntegrationTests -X
```

## Cenários de Teste

Os testes cobrem os seguintes cenários definidos em `production_integration.feature`:

### 1. Processamento de pedido com sucesso
- **Cenário 1**: Pedido recebido com pagamento pendente (PENDING → RECEIVED)
- **Cenário 2**: Pedido iniciando produção com pagamento processado (PROCESSED → PREPARING)

### 2. Processamento de pedido com falha e rollback
- **Cenário 1**: Pedido com falha (FAIL → CANCELLED)
- **Cenário 2**: Pedido com rollback pendente (ROLLBACK_PENDING → CANCELLED)

### 3. Processamento de pedido inexistente no rollback
- **Cenário 1**: Rollback de pedido que não existe com status FAIL
- **Cenário 2**: Rollback de pedido que não existe com status ROLLBACK_PENDING

## Verificações dos Testes

Cada teste verifica:

1. **Processamento do evento**: O evento é consumido corretamente da fila
2. **Atualização do pedido**: O status do pedido é atualizado no banco de dados
3. **Publicação do callback**: Um evento de callback é publicado na fila de resposta
4. **Conteúdo do callback**: O evento de callback contém o status e source corretos
5. **Histórico do evento**: O histórico é atualizado com informações do PRODUCTION

## Estrutura dos Steps

### Given (Dado)
- `que um evento válido é enviado para a fila {queue_name} com o status {status} e o paymentStatus {payment_status}`
- `o pedido não existe no repositório`

### When (Quando)
- `o evento é recebido pelo serviço de produção`

### Then (Então)
- `o pedido deve ser processado com o status {expected_order_status}`
- `o pedido deve ser cancelado com o status {expected_order_status}`

### And (E)
- `o evento deve ser publicado na fila de callback {callback_queue} com o status {callback_status}`

## Troubleshooting

### Problema: Testes falham com erro de conexão

**Solução**: Verifique se o LocalStack está rodando:
```bash
docker ps | grep localstack
```

### Problema: Filas não encontradas

**Solução**: Execute o script de inicialização das filas:
```bash
cd local && ./init-aws.sh
```

### Problema: Mensagens não são consumidas

**Solução**: Aumente o tempo de espera em `ProductionIntegrationSteps.java`:
```java
Thread.sleep(2000); // Aumentar para 3000 ou 5000 se necessário
```

### Problema: Conflitos entre testes

**Solução**: Os hooks em `TestHooks.java` devem limpar as filas automaticamente. 
Verifique se o hook está sendo executado corretamente.

## Dependências

As dependências necessárias já estão configuradas no `pom.xml`:

- `cucumber-java` (7.15.0)
- `cucumber-junit-platform-engine` (7.15.0)
- `cucumber-spring` (7.15.0)
- `spring-cloud-aws-starter-sqs`
- `junit-platform-suite`

## Notas Importantes

1. Os testes usam **H2 in-memory database** configurado em `application.yaml` de teste
2. O **LocalStack** é usado para simular o SQS da AWS
3. Cada cenário é **isolado** - o estado é limpo entre execuções
4. Os testes são **não destrutivos** - não afetam dados reais

## Contribuindo

Ao adicionar novos cenários:

1. Adicione o cenário no arquivo `.feature`
2. Implemente os steps necessários em `ProductionIntegrationSteps.java`
3. Adicione verificações adequadas (assertions)
4. Documente o cenário neste README
5. Execute os testes para garantir que passam

## Referências

- [Cucumber Documentation](https://cucumber.io/docs/cucumber/)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [LocalStack Documentation](https://docs.localstack.cloud/)
- [AWS SQS Documentation](https://docs.aws.amazon.com/sqs/)
