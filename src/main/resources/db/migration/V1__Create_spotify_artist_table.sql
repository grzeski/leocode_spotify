CREATE TABLE spotify_artist(
    local_id SERIAL,
    id varchar(100) NOT NULL,
    name TEXT NOT NULL,
    uri TEXT,
    href TEXT,
    PRIMARY KEY (id)
)
