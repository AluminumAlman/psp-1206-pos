CREATE TABLE "order"
(
    id         UUID     NOT NULL,
    status     SMALLINT NOT NULL,
    merchant   UUID     NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_order PRIMARY KEY (id)
);

ALTER TABLE "order"
    ADD CONSTRAINT FK_ORDER_ON_MERCHANT FOREIGN KEY (merchant) REFERENCES merchant (id);

CREATE TABLE order_item
(
    id                UUID    NOT NULL,
    "order"           UUID    NOT NULL,
    quantity          INTEGER NOT NULL,
    product           UUID,
    product_variation UUID,
    service           UUID,
    created_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at        TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_orderitem PRIMARY KEY (id)
);

ALTER TABLE order_item
    ADD CONSTRAINT FK_ORDERITEM_ON_ORDER FOREIGN KEY ("order") REFERENCES "order" (id);

ALTER TABLE order_item
    ADD CONSTRAINT FK_ORDERITEM_ON_PRODUCT FOREIGN KEY (product) REFERENCES product (id);

ALTER TABLE order_item
    ADD CONSTRAINT FK_ORDERITEM_ON_PRODUCT_VARIATION FOREIGN KEY (product_variation) REFERENCES product_variation (id);

ALTER TABLE order_item
    ADD CONSTRAINT FK_ORDERITEM_ON_SERVICE FOREIGN KEY (service) REFERENCES service (id);