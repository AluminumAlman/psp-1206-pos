ALTER TABLE charge
DROP
COLUMN scope;

ALTER TABLE discount
DROP
COLUMN amount;

ALTER TABLE discount
    ADD amount DECIMAL(19, 2);

ALTER TABLE discount
    ADD scope SMALLINT;

ALTER TABLE discount
    ALTER COLUMN scope SET NOT NULL;