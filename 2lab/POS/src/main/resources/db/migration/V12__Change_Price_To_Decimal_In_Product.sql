ALTER TABLE product
    DROP COLUMN price;

ALTER TABLE product
    ADD price DECIMAL(19, 2) NOT NULL;