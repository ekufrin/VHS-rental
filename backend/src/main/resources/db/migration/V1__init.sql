CREATE TABLE IF NOT EXISTS genres
(
    id
    UUID
    PRIMARY
    KEY,
    name
    VARCHAR
(
    255
) NOT NULL UNIQUE
    );

CREATE TABLE IF NOT EXISTS users
(
    id
    UUID
    PRIMARY
    KEY,
    name
    VARCHAR
(
    255
) NOT NULL,
    email VARCHAR
(
    255
) NOT NULL UNIQUE,
    password VARCHAR
(
    255
) NOT NULL
    );

CREATE TABLE IF NOT EXISTS users_favorite_genres
(
    user_id
    UUID
    NOT
    NULL,
    favorite_genres_id
    UUID
    NOT
    NULL,
    FOREIGN
    KEY
(
    user_id
) REFERENCES users
(
    id
),
    FOREIGN KEY
(
    favorite_genres_id
) REFERENCES genres
(
    id
) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS vhs
(
    id
    UUID
    PRIMARY
    KEY,
    title
    VARCHAR
(
    255
) NOT NULL,
    release_date TIMESTAMP NOT NULL,
    genre_id UUID NOT NULL,
    rental_price DECIMAL
(
    10,
    2
) NOT NULL,
    stock_level INTEGER NOT NULL,
    image_id UUID,
    image_extension VARCHAR
(
    10
),
    status VARCHAR
(
    50
) NOT NULL,
    CONSTRAINT fk_genre FOREIGN KEY
(
    genre_id
) REFERENCES genres
(
    id
)
    );

CREATE TABLE IF NOT EXISTS rentals
(
    id
    UUID
    PRIMARY
    KEY,
    vhs_id
    UUID
    NOT
    NULL,
    user_id
    UUID
    NOT
    NULL,
    rental_date
    TIMESTAMP
    NOT
    NULL,
    due_date
    TIMESTAMP
    NOT
    NULL,
    return_date
    TIMESTAMP,
    price
    DOUBLE
    PRECISION,
    version
    BIGINT
    NOT
    NULL
    DEFAULT
    0,
    FOREIGN
    KEY
(
    vhs_id
) REFERENCES vhs
(
    id
),
    FOREIGN KEY
(
    user_id
) REFERENCES users
(
    id
)
    );

CREATE TABLE IF NOT EXISTS reviews
(
    id
    UUID
    PRIMARY
    KEY,
    rental_id
    UUID
    NOT
    NULL
    UNIQUE,
    rating
    DOUBLE
    PRECISION
    NOT
    NULL,
    comment
    VARCHAR
(
    255
),
    FOREIGN KEY
(
    rental_id
) REFERENCES rentals
(
    id
)
    );

CREATE TABLE IF NOT EXISTS refresh_token
(
    id
    UUID
    PRIMARY
    KEY,
    jti
    VARCHAR
(
    255
) NOT NULL UNIQUE,
    user_id UUID NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT false,
    CONSTRAINT fk_refresh_token_user FOREIGN KEY
(
    user_id
) REFERENCES users
(
    id
) ON DELETE CASCADE
    );