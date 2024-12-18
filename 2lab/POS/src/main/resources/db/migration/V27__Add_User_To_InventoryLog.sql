ALTER TABLE inventory_log
    ADD "user" UUID;

ALTER TABLE inventory_log
    ALTER COLUMN "user" SET NOT NULL;

ALTER TABLE inventory_log
    ADD CONSTRAINT FK_INVENTORY_LOG_ON_USER FOREIGN KEY ("user") REFERENCES "user" (id);