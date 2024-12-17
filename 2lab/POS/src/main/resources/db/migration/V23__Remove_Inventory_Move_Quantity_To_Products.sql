ALTER TABLE inventory
    DROP CONSTRAINT fk_inventory_on_product;

ALTER TABLE inventory
    DROP CONSTRAINT fk_inventory_on_productvariation;

ALTER TABLE inventory_log
    DROP CONSTRAINT fk_inventorylog_on_inventory;

ALTER TABLE inventory_log
    ADD product_id UUID;

ALTER TABLE inventory_log
    ADD product_variation_id UUID;

ALTER TABLE inventory_log
    ADD type VARCHAR(255);

ALTER TABLE product
    ADD quantity INTEGER;

ALTER TABLE product
    ADD updated_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE product
    ALTER COLUMN quantity SET NOT NULL;

ALTER TABLE product_variation
    ADD quantity INTEGER;

ALTER TABLE product_variation
    ADD updated_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE product_variation
    ALTER COLUMN quantity SET NOT NULL;

ALTER TABLE inventory_log
    ALTER COLUMN type SET NOT NULL;

ALTER TABLE inventory_log
    ADD CONSTRAINT FK_INVENTORY_LOG_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES product (id);

ALTER TABLE inventory_log
    ADD CONSTRAINT FK_INVENTORY_LOG_ON_PRODUCT_VARIATION FOREIGN KEY (product_variation_id) REFERENCES product_variation (id);

DROP TABLE inventory CASCADE;

ALTER TABLE inventory_log
    DROP COLUMN inventory;