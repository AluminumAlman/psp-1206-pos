ALTER TABLE product_variation
    ALTER COLUMN price TYPE DECIMAL(19, 2) USING (price::DECIMAL(19, 2));