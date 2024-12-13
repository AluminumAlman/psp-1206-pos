ALTER TABLE charge
    DROP CONSTRAINT fk_charge_on_merchant;

ALTER TABLE charge
    ADD is_active BOOLEAN;

ALTER TABLE charge
    ADD merchant_id UUID;

ALTER TABLE charge
    ALTER COLUMN merchant_id SET NOT NULL;

ALTER TABLE charge
    ADD CONSTRAINT FK_CHARGE_ON_MERCHANT FOREIGN KEY (merchant_id) REFERENCES merchant (id);

ALTER TABLE charge
    DROP COLUMN merchant;

ALTER TABLE charge
    DROP COLUMN amount;

ALTER TABLE charge
    ADD amount DECIMAL(19, 2);