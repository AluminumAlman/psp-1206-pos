ALTER TABLE product_variation
    DROP COLUMN price;

ALTER TABLE product_variation
    ADD price DECIMAL NOT NULL;