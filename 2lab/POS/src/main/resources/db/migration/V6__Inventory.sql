CREATE TABLE inventory
(
    id                UUID                        NOT NULL,
    product           UUID,
    product_variation UUID,
    quantity          INTEGER                     NOT NULL,
    created_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at        TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_inventory PRIMARY KEY (id)
);

ALTER TABLE inventory
    ADD CONSTRAINT FK_INVENTORY_ON_PRODUCT FOREIGN KEY (product) REFERENCES product (id);

ALTER TABLE inventory
    ADD CONSTRAINT uc_inventory_product UNIQUE (product);

ALTER TABLE inventory
    ADD CONSTRAINT FK_INVENTORY_ON_PRODUCTVARIATION FOREIGN KEY (product_variation) REFERENCES product_variation (id);

ALTER TABLE inventory
    ADD CONSTRAINT uc_inventory_productvariation UNIQUE (product_variation);

CREATE TABLE inventory_log
(
    id         UUID                        NOT NULL,
    inventory  UUID                        NOT NULL,
    adjustment INTEGER                     NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_inventorylog PRIMARY KEY (id)
);

ALTER TABLE inventory_log
    ADD CONSTRAINT FK_INVENTORYLOG_ON_INVENTORY FOREIGN KEY (inventory) REFERENCES inventory (id);