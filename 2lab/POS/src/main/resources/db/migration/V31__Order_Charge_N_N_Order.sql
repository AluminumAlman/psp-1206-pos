CREATE TABLE order_charges_orders
(
    order_charge_id UUID NOT NULL,
    order_id        UUID NOT NULL
);

ALTER TABLE order_charges_orders
    ADD CONSTRAINT fk_ordchaord_on_order FOREIGN KEY (order_id) REFERENCES "order" (id);

ALTER TABLE order_charges_orders
    ADD CONSTRAINT fk_ordchaord_on_order_charge FOREIGN KEY (order_charge_id) REFERENCES order_charge (id);

ALTER TABLE order_charge
    ADD merchant_id UUID;

ALTER TABLE order_charge
    ADD CONSTRAINT FK_ORDER_CHARGE_ON_MERCHANT FOREIGN KEY (merchant_id) REFERENCES merchant (id);

ALTER TABLE order_charge
    ALTER COLUMN merchant_id SET NOT NULL;

ALTER TABLE order_charge
DROP
CONSTRAINT fk_order_charge_on_order;

ALTER TABLE order_charge
DROP
COLUMN order_id;