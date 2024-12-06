ALTER TABLE inventory_log
    ADD "order" UUID;

ALTER TABLE inventory_log
    ADD CONSTRAINT uc_inventorylog_order UNIQUE ("order");

ALTER TABLE inventory_log
    ADD CONSTRAINT FK_INVENTORYLOG_ON_ORDER FOREIGN KEY ("order") REFERENCES "order" (id);