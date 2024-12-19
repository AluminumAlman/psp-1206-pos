ALTER TABLE order_item
    DROP CONSTRAINT fk_order_item_on_service;

ALTER TABLE orders_order_charges
    DROP CONSTRAINT fk_ordordcha_on_order;

ALTER TABLE orders_order_charges
    DROP CONSTRAINT fk_ordordcha_on_order_charge;

ALTER TABLE order_item
    ADD reservation_id UUID;

ALTER TABLE order_item
    ADD CONSTRAINT uc_order_item_reservation UNIQUE (reservation_id);

ALTER TABLE order_item
    ADD CONSTRAINT FK_ORDER_ITEM_ON_RESERVATION FOREIGN KEY (reservation_id) REFERENCES reservation (id);

DROP TABLE orders_order_charges CASCADE;

ALTER TABLE order_item
    DROP COLUMN service_id;