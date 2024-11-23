CREATE TABLE service
(
    id          UUID         NOT NULL,
    name        VARCHAR(255) NOT NULL,
    price       INTEGER      NOT NULL,
    duration    BIGINT       NOT NULL,
    merchant_id UUID         NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_service PRIMARY KEY (id)
);

CREATE TABLE services_users
(
    service_id UUID NOT NULL,
    user_id    UUID NOT NULL
);

ALTER TABLE service
    ADD CONSTRAINT FK_SERVICE_ON_MERCHANT FOREIGN KEY (merchant_id) REFERENCES merchant (id);

ALTER TABLE services_users
    ADD CONSTRAINT fk_seruse_on_service FOREIGN KEY (service_id) REFERENCES service (id);

ALTER TABLE services_users
    ADD CONSTRAINT fk_seruse_on_user FOREIGN KEY (user_id) REFERENCES "user" (id);