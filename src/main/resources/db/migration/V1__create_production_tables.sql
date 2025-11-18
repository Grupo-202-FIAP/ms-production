-- Tabela de ordens de produção
CREATE TABLE IF NOT EXISTS production_orders (
    id UUID PRIMARY KEY,
    order_identifier VARCHAR(255) NOT NULL UNIQUE,
    original_order_id UUID NOT NULL,
    total_price DECIMAL(19, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    CONSTRAINT fk_production_order_status CHECK (status IN ('PENDING', 'IN_PREPARATION', 'READY', 'COMPLETED', 'CANCELLED'))
);

-- Tabela de etapas de produção
CREATE TABLE IF NOT EXISTS production_steps (
    id UUID PRIMARY KEY,
    production_order_id UUID NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL,
    status VARCHAR(50) NOT NULL,
    notes TEXT,
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    priority INTEGER,
    CONSTRAINT fk_production_step_order FOREIGN KEY (production_order_id) REFERENCES production_orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_production_step_status CHECK (status IN ('PENDING', 'IN_PREPARATION', 'READY', 'COMPLETED', 'CANCELLED'))
);

-- Índices para melhor performance
CREATE INDEX IF NOT EXISTS idx_production_orders_status ON production_orders(status);
CREATE INDEX IF NOT EXISTS idx_production_orders_order_identifier ON production_orders(order_identifier);
CREATE INDEX IF NOT EXISTS idx_production_steps_order_id ON production_steps(production_order_id);
CREATE INDEX IF NOT EXISTS idx_production_steps_status ON production_steps(status);


