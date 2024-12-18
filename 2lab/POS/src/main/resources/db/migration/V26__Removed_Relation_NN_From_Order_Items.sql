ALTER TABLE order_items_order_charges
    DROP CONSTRAINT fk_orditeordcha_on_order_charge;

ALTER TABLE order_items_order_charges
    DROP CONSTRAINT fk_orditeordcha_on_order_item;

DROP TABLE order_items_order_charges CASCADE;