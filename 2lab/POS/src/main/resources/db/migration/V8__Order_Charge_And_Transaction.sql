CREATE TABLE order_charge
(
    id         UUID         NOT NULL,
    type       SMALLINT     NOT NULL,
    name       VARCHAR(100) NOT NULL,
    percent    INTEGER,
    amount     INTEGER,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_ordercharge PRIMARY KEY (id)
);

CREATE TABLE orders_order_charges
(
    order_charge_id UUID NOT NULL,
    order_id        UUID NOT NULL
);

ALTER TABLE orders_order_charges
    ADD CONSTRAINT fk_ordordcha_on_order FOREIGN KEY (order_id) REFERENCES "order" (id);

ALTER TABLE orders_order_charges
    ADD CONSTRAINT fk_ordordcha_on_order_charge FOREIGN KEY (order_charge_id) REFERENCES order_charge (id);

CREATE TABLE transaction
(
    id             UUID     NOT NULL,
    status         SMALLINT NOT NULL,
    payment_method SMALLINT NOT NULL,
    amount         INTEGER  NOT NULL,
    "order"        UUID     NOT NULL,
    created_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at     TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_transaction PRIMARY KEY (id)
);

ALTER TABLE transaction
    ADD CONSTRAINT FK_TRANSACTION_ON_ORDER FOREIGN KEY ("order") REFERENCES "order" (id);

CREATE TABLE order_items_order_charges
(
    order_charge_id UUID NOT NULL,
    order_item_id   UUID NOT NULL
);

ALTER TABLE order_items_order_charges
    ADD CONSTRAINT fk_orditeordcha_on_order_charge FOREIGN KEY (order_charge_id) REFERENCES order_charge (id);

ALTER TABLE order_items_order_charges
    ADD CONSTRAINT fk_orditeordcha_on_order_item FOREIGN KEY (order_item_id) REFERENCES order_item (id);