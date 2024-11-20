CREATE TABLE merchant
(
    id         UUID                        NOT NULL,
    name       VARCHAR(100)                NOT NULL,
    phone      VARCHAR(20),
    email      VARCHAR(255)                NOT NULL,
    currency   VARCHAR(10)                 NOT NULL,
    address    VARCHAR(255),
    city       VARCHAR(50),
    country    VARCHAR(50)                 NOT NULL,
    postcode   VARCHAR(20),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_merchant PRIMARY KEY (id)
);

ALTER TABLE merchant
    ADD CONSTRAINT uc_merchant_email UNIQUE (email);