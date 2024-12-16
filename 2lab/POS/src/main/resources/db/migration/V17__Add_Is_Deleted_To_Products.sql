ALTER TABLE product
    ADD is_deleted BOOLEAN;

ALTER TABLE product
    ALTER COLUMN is_deleted SET NOT NULL;

ALTER TABLE product_variation
    ADD is_deleted BOOLEAN;

ALTER TABLE product_variation
    ALTER COLUMN is_deleted SET NOT NULL;