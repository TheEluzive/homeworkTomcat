CREATE TABLE users
(
    id       BIGSERIAL PRIMARY KEY,
    username TEXT        NOT NULL UNIQUE,
    password TEXT        NOT NULL,
    active   BOOLEAN     NOT NULL DEFAULT TRUE,
    created  timestamptz NOT NULL DEFAULT current_timestamp
);

CREATE TABLE roles
(
    id bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    role text,
    CONSTRAINT id PRIMARY KEY (id)
);

CREATE TABLE user_roles
(
    "user" bigint,
    role bigint,
    CONSTRAINT roles_fkey FOREIGN KEY (role)
        REFERENCES roles (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT users_fkey FOREIGN KEY ("user")
        REFERENCES users (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

CREATE TABLE tokens
(
    token    TEXT PRIMARY KEY,
    "userId" BIGINT      NOT NULL REFERENCES users,
    created  timestamptz NOT NULL DEFAULT current_timestamp
);
CREATE TABLE registration_attempts
(
    id       BIGSERIAL PRIMARY KEY,
    username TEXT        NOT NULL UNIQUE,
    created  timestamptz NOT NULL DEFAULT current_timestamp
);
CREATE TABLE login_attempts
(
    id       BIGSERIAL PRIMARY KEY,
    username TEXT        NOT NULL UNIQUE,
    created  timestamptz NOT NULL DEFAULT current_timestamp
);
CREATE TABLE cards
(
    id        BIGSERIAL PRIMARY KEY,
    "ownerId" BIGINT  NOT NULL REFERENCES users,
    number    TEXT    NOT NULL,
    balance   BIGINT  NOT NULL DEFAULT 0,
    active    BOOLEAN NOT NULL DEFAULT TRUE
);
