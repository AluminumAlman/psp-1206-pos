CREATE TABLE discount
(
    id          UUID                        NOT NULL,
    name        VARCHAR(255)                NOT NULL,
    percent     INTEGER,
    amount      INTEGER,
    valid_from  TIMESTAMP WITHOUT TIME ZONE,
    valid_until TIMESTAMP WITHOUT TIME ZONE,
    merchant_id UUID                        NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_discount PRIMARY KEY (id)
);

ALTER TABLE merchant
    ADD updated_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE discount
    ADD CONSTRAINT FK_DISCOUNT_ON_MERCHANT FOREIGN KEY (merchant_id) REFERENCES merchant (id);