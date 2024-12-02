CREATE TABLE product_category
(
    id          UUID         NOT NULL,
    name        VARCHAR(100) NOT NULL,
    merchant_id UUID         NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_productcategory PRIMARY KEY (id)
);

ALTER TABLE product_category
    ADD CONSTRAINT FK_PRODUCTCATEGORY_ON_MERCHANT FOREIGN KEY (merchant_id) REFERENCES merchant (id);

CREATE TABLE product
(
    id         UUID         NOT NULL,
    name       VARCHAR(100) NOT NULL,
    price      INTEGER      NOT NULL,
    category   UUID         NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_product PRIMARY KEY (id)
);

ALTER TABLE product
    ADD CONSTRAINT FK_PRODUCT_ON_CATEGORY FOREIGN KEY (category) REFERENCES product_category (id);

CREATE TABLE product_variation
(
    id         UUID         NOT NULL,
    name       VARCHAR(100) NOT NULL,
    price      INTEGER      NOT NULL,
    product    UUID         NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_productvariation PRIMARY KEY (id)
);

ALTER TABLE product_variation
    ADD CONSTRAINT FK_PRODUCTVARIATION_ON_PRODUCT FOREIGN KEY (product) REFERENCES product (id);

CREATE TABLE charge
(
    id         UUID         NOT NULL,
    type       SMALLINT     NOT NULL,
    scope      SMALLINT     NOT NULL,
    name       VARCHAR(100) NOT NULL,
    percent    INTEGER,
    amount     INTEGER,
    merchant   UUID         NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_charge PRIMARY KEY (id)
);

ALTER TABLE charge
    ADD CONSTRAINT FK_CHARGE_ON_MERCHANT FOREIGN KEY (merchant) REFERENCES merchant (id);

CREATE TABLE charges_products
(
    charge_id  UUID NOT NULL,
    product_id UUID NOT NULL
);

ALTER TABLE charges_products
    ADD CONSTRAINT fk_chapro_on_charge FOREIGN KEY (charge_id) REFERENCES charge (id);

ALTER TABLE charges_products
    ADD CONSTRAINT fk_chapro_on_product FOREIGN KEY (product_id) REFERENCES product (id);

CREATE TABLE charges_services
(
    charge_id  UUID NOT NULL,
    service_id UUID NOT NULL
);

ALTER TABLE charges_services
    ADD CONSTRAINT fk_chaser_on_charge FOREIGN KEY (charge_id) REFERENCES charge (id);

ALTER TABLE charges_services
    ADD CONSTRAINT fk_chaser_on_service FOREIGN KEY (service_id) REFERENCES service (id);