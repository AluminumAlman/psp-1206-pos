ALTER TABLE "order"
    DROP CONSTRAINT fk_order_on_merchant;

ALTER TABLE order_item
    DROP CONSTRAINT fk_orderitem_on_order;

ALTER TABLE order_item
    DROP CONSTRAINT fk_orderitem_on_product;

ALTER TABLE order_item
    DROP CONSTRAINT fk_orderitem_on_product_variation;

ALTER TABLE order_item
    DROP CONSTRAINT fk_orderitem_on_service;

ALTER TABLE "order"
    ADD merchant_id UUID;

ALTER TABLE "order"
    ALTER COLUMN merchant_id SET NOT NULL;

ALTER TABLE order_item
    ADD order_id UUID;

ALTER TABLE order_item
    ADD product_id UUID;

ALTER TABLE order_item
    ADD product_variation_id UUID;

ALTER TABLE order_item
    ADD service_id UUID;

ALTER TABLE order_item
    ALTER COLUMN order_id SET NOT NULL;

ALTER TABLE order_item
    ADD CONSTRAINT FK_ORDER_ITEM_ON_ORDER FOREIGN KEY (order_id) REFERENCES "order" (id);

ALTER TABLE order_item
    ADD CONSTRAINT FK_ORDER_ITEM_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES product (id);

ALTER TABLE order_item
    ADD CONSTRAINT FK_ORDER_ITEM_ON_PRODUCT_VARIATION FOREIGN KEY (product_variation_id) REFERENCES product_variation (id);

ALTER TABLE order_item
    ADD CONSTRAINT FK_ORDER_ITEM_ON_SERVICE FOREIGN KEY (service_id) REFERENCES service (id);

ALTER TABLE "order"
    ADD CONSTRAINT FK_ORDER_ON_MERCHANT FOREIGN KEY (merchant_id) REFERENCES merchant (id);

ALTER TABLE "order"
    DROP COLUMN merchant;

ALTER TABLE order_item
    DROP COLUMN "order";

ALTER TABLE order_item
    DROP COLUMN product;

ALTER TABLE order_item
    DROP COLUMN product_variation;

ALTER TABLE order_item
    DROP COLUMN service;

ALTER TABLE order_charge
    DROP COLUMN amount;

ALTER TABLE order_charge
    ADD amount DECIMAL(19, 2);