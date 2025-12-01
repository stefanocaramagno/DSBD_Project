-- Tabella degli aeroporti monitorabili
CREATE TABLE IF NOT EXISTS airports (
    id          BIGSERIAL PRIMARY KEY,
    code        VARCHAR(10) NOT NULL UNIQUE,
    name        VARCHAR(255),
    city        VARCHAR(255),
    country     VARCHAR(255),
    timezone    VARCHAR(64)
);

-- Tabella degli interessi utente sugli aeroporti
CREATE TABLE IF NOT EXISTS user_airport_interest (
    id          BIGSERIAL PRIMARY KEY,
    user_email  VARCHAR(255) NOT NULL,
    airport_id  BIGINT NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_user_airport_interest_airport
        FOREIGN KEY (airport_id) REFERENCES airports(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_user_airport_interest UNIQUE (user_email, airport_id)
);

-- Tabella dei voli raccolti dall'API esterna
CREATE TABLE IF NOT EXISTS flight_records (
    id                  BIGSERIAL PRIMARY KEY,
    airport_id          BIGINT NOT NULL,
    external_flight_id  VARCHAR(64),
    flight_number       VARCHAR(32),
    direction           VARCHAR(16),    -- es: 'DEPARTURE' o 'ARRIVAL'
    scheduled_time      TIMESTAMP,
    actual_time         TIMESTAMP,
    status              VARCHAR(32),    -- es: 'ON_TIME', 'DELAYED', 'CANCELLED'
    delay_minutes       INTEGER,
    collected_at        TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_flight_records_airport
        FOREIGN KEY (airport_id) REFERENCES airports(id)
        ON DELETE CASCADE
);
