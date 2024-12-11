ALTER TABLE service
    DROP COLUMN price;

ALTER TABLE service
    ADD price DECIMAL(19, 2) NOT NULL;