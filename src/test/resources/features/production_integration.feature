# language: pt

Funcionalidade: Processamento de pedidos - Integração de produção

  Esquema do Cenario: Processamento de pedido com sucesso
    Dado que um evento válido é enviado para a fila <queue_name> com o status <status> e o paymentStatus <payment_status>
    Quando o evento é recebido pelo serviço de produção
    Entao o pedido deve ser processado com o status <expected_order_status>
    E o evento deve ser publicado na fila de callback <callback_queue> com o status <callback_status>
    Exemplos:
      | queue_name      | status  | payment_status | expected_order_status | callback_queue           | callback_status |
      | production_queue | SUCCESS | PENDING        | RECEIVED              | production_callback_queue | SUCCESS         |
      | production_queue | SUCCESS | PROCESSED      | PREPARING             | production_callback_queue | SUCCESS         |

  Esquema do Cenario: Processamento de pedido com falha e rollback
    Dado que um evento válido é enviado para a fila <queue_name> com o status <status> e o paymentStatus <payment_status>
    Quando o evento é recebido pelo serviço de produção
    Entao o pedido deve ser cancelado com o status <expected_order_status>
    E o evento deve ser publicado na fila de callback <callback_queue> com o status <callback_status>
    Exemplos:
      | queue_name      | status           | payment_status | expected_order_status | callback_queue           | callback_status |
      | production_queue | FAIL             | PENDING        | CANCELLED             | production_callback_queue | FAIL            |
      | production_queue | ROLLBACK_PENDING | PROCESSED      | CANCELLED             | production_callback_queue | FAIL            |

  Esquema do Cenario: Processamento de pedido inexistente no rollback
    Dado que um evento válido é enviado para a fila <queue_name> com o status <status> e o paymentStatus <payment_status>
    E o pedido não existe no repositório
    Quando o evento é recebido pelo serviço de produção
    Entao o evento deve ser publicado na fila de callback <callback_queue> com o status <callback_status>
    Exemplos:
      | queue_name      | status           | payment_status | callback_queue           | callback_status |
      | production_queue | FAIL             | PENDING        | production_callback_queue | FAIL            |
      | production_queue | ROLLBACK_PENDING | PROCESSED      | production_callback_queue | FAIL            |
