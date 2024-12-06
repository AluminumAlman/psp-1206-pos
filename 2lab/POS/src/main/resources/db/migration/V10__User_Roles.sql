ALTER TABLE user_roles
    DROP CONSTRAINT fk_user_roles_on_user;

ALTER TABLE "user"
    ADD role VARCHAR(40);

ALTER TABLE "user"
    ALTER COLUMN role SET NOT NULL;

DROP TABLE user_roles CASCADE;