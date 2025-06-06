CREATE TABLE IF NOT EXISTS user_actions(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    event_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    mark DECIMAL NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    UNIQUE (user_id, event_id)
);

CREATE TABLE IF NOT EXISTS events_similarity(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    event_a BIGINT NOT NULL,
    event_b BIGINT NOT NULL,
    score DECIMAL NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    UNIQUE (event_a, event_b)
);