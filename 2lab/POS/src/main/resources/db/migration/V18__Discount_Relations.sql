ALTER TABLE discount
    ADD is_active BOOLEAN;

CREATE TABLE discounts_services
(
    discount_id UUID NOT NULL,
    service_id  UUID NOT NULL
);

ALTER TABLE discounts_services
    ADD CONSTRAINT fk_disser_on_discount FOREIGN KEY (discount_id) REFERENCES discount (id);

ALTER TABLE discounts_services
    ADD CONSTRAINT fk_disser_on_service FOREIGN KEY (service_id) REFERENCES service (id);

CREATE TABLE discounts_product_categories
(
    discount_id         UUID NOT NULL,
    product_category_id UUID NOT NULL
);

ALTER TABLE discounts_product_categories
    ADD CONSTRAINT fk_disprocat_on_discount FOREIGN KEY (discount_id) REFERENCES discount (id);

ALTER TABLE discounts_product_categories
    ADD CONSTRAINT fk_disprocat_on_product_category FOREIGN KEY (product_category_id) REFERENCES product_category (id);

CREATE TABLE discounts_products
(
    discount_id UUID NOT NULL,
    product_id  UUID NOT NULL
);

ALTER TABLE discounts_products
    ADD CONSTRAINT fk_dispro_on_discount FOREIGN KEY (discount_id) REFERENCES discount (id);

ALTER TABLE discounts_products
    ADD CONSTRAINT fk_dispro_on_product FOREIGN KEY (product_id) REFERENCES product (id);

CREATE TABLE discounts_product_variations
(
    discount_id          UUID NOT NULL,
    product_variation_id UUID NOT NULL
);

ALTER TABLE discounts_product_variations
    ADD CONSTRAINT fk_disprovar_on_discount FOREIGN KEY (discount_id) REFERENCES discount (id);

ALTER TABLE discounts_product_variations
    ADD CONSTRAINT fk_disprovar_on_product_variation FOREIGN KEY (product_variation_id) REFERENCES product_variation (id);

CREATE TABLE orders_discounts
(
    discount_id UUID NOT NULL,
    order_id    UUID NOT NULL
);

ALTER TABLE orders_discounts
    ADD CONSTRAINT fk_orddis_on_order FOREIGN KEY (order_id) REFERENCES "order" (id);

ALTER TABLE orders_discounts
    ADD CONSTRAINT fk_orddis_on_discount FOREIGN KEY (discount_id) REFERENCES discount (id);

CREATE TABLE order_items_discounts
(
    discount_id   UUID NOT NULL,
    order_item_id UUID NOT NULL
);

ALTER TABLE order_items_discounts
    ADD CONSTRAINT fk_orditedis_on_order_item FOREIGN KEY (order_item_id) REFERENCES order_item (id);

ALTER TABLE order_items_discounts
    ADD CONSTRAINT fk_orditedis_on_discount FOREIGN KEY (discount_id) REFERENCES discount (id);