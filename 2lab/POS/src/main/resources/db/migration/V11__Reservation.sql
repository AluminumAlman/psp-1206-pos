CREATE TABLE reservation
(
    id           UUID                        NOT NULL,
    first_name   VARCHAR(100)                NOT NULL,
    last_name    VARCHAR(100)                NOT NULL,
    phone        VARCHAR(20),
    appointed_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    service_id   UUID                        NOT NULL,
    employee_id  UUID                        NOT NULL,
    created_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at   TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_reservation PRIMARY KEY (id)
);

ALTER TABLE reservation
    ADD CONSTRAINT FK_RESERVATION_ON_EMPLOYEE FOREIGN KEY (employee_id) REFERENCES "user" (id);

ALTER TABLE reservation
    ADD CONSTRAINT FK_RESERVATION_ON_SERVICE FOREIGN KEY (service_id) REFERENCES service (id);