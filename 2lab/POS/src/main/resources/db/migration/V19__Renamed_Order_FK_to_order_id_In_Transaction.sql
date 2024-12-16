ALTER TABLE transaction
    DROP CONSTRAINT fk_transaction_on_order;

ALTER TABLE transaction
    ADD order_id UUID;

ALTER TABLE transaction
    ALTER COLUMN order_id SET NOT NULL;

ALTER TABLE transaction
    ADD CONSTRAINT FK_TRANSACTION_ON_ORDER FOREIGN KEY (order_id) REFERENCES "order" (id);

ALTER TABLE transaction
    DROP COLUMN "order";

ALTER TABLE transaction
    DROP COLUMN amount;

ALTER TABLE transaction
    ADD amount DECIMAL NOT NULL;