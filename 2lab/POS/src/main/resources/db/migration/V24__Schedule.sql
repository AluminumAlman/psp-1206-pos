CREATE TABLE schedule
(
    id          UUID                        NOT NULL,
    user_id     UUID                        NOT NULL,
    merchant_id UUID                        NOT NULL,
    day_of_week VARCHAR(255)                NOT NULL,
    start_time  time WITHOUT TIME ZONE      NOT NULL,
    end_time    time WITHOUT TIME ZONE      NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_schedule PRIMARY KEY (id)
);

ALTER TABLE schedule
    ADD CONSTRAINT FK_SCHEDULE_ON_MERCHANT FOREIGN KEY (merchant_id) REFERENCES merchant (id);

ALTER TABLE schedule
    ADD CONSTRAINT FK_SCHEDULE_ON_USER FOREIGN KEY (user_id) REFERENCES "user" (id);