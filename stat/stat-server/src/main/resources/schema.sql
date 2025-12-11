DROP TABLE IF EXISTS endpoint_hit CASCADE;

CREATE TABLE IF NOT EXISTS endpoint_hit (
              id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
              app VARCHAR,
              uri VARCHAR,
              ip VARCHAR,
              time_request TIMESTAMP WITHOUT TIME ZONE
);