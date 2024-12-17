ALTER TABLE order_charge
    ADD order_id UUID;

ALTER TABLE order_charge
    ALTER COLUMN order_id SET NOT NULL;

ALTER TABLE orders_order_charges
    ADD CONSTRAINT uc_orders_order_charges_order_charge UNIQUE (order_charge_id);

ALTER TABLE order_charge
    ADD CONSTRAINT FK_ORDER_CHARGE_ON_ORDER FOREIGN KEY (order_id) REFERENCES "order" (id);