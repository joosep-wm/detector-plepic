CREATE TABLE person
(
    id BIGSERIAL PRIMARY KEY,
    person_code TEXT NOT NULL UNIQUE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now()
);

CREATE TABLE account
(
    id BIGSERIAL PRIMARY KEY,
    number TEXT NOT NULL UNIQUE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now()
);

CREATE TABLE device
(
    id BIGSERIAL PRIMARY KEY,
    mac TEXT NOT NULL UNIQUE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now()
);

CREATE TABLE transaction
(
    id BIGSERIAL PRIMARY KEY,
    legitimate BOOLEAN NOT NULL,
    amount decimal(10, 2) NOT NULL,
    sender_id BIGINT NOT NULL REFERENCES person (id),
    sender_account_id BIGINT NOT NULL REFERENCES account (id),
    recipient_id BIGINT NOT NULL REFERENCES person (id),
    recipient_account_id BIGINT NOT NULL REFERENCES account (id),
    device_id BIGINT NOT NULL REFERENCES device (id),
    timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    deadline TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now()
);
