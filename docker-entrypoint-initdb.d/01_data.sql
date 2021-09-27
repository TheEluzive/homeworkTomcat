INSERT INTO users(id, username, password)
VALUES (1, 'admin', '$argon2id$v=19$m=4096,t=3,p=1$CjM/3pdG9sYDzxGFDbesMA$HA1Fm8y23U9QucCFmq1hynPzyCsMfSNKpEqyZVif/og');

INSERT INTO users(id, username, password)
VALUES (2, 'anonymous', '$argon2id$v=19$m=4096,t=3,p=1$gCAC+zI1HRM5IRKJc8HhGw$RadiUVwSqt6m7icr6+Cp67Jh8Sc+YRs+zJ9xw2nYh6M99999');

ALTER SEQUENCE users_id_seq RESTART WITH 4;

INSERT INTO tokens(token, "userId")
VALUES ('6NSb+2kcdKF44ut4iBu+dm6YLu6pakWapvxHtxqaPgMr5iRhox/HlhBerAZMILPjwnRtXms+zDfVTLCsao9nuw==', 1);

INSERT INTO tokens(token, "userId")
VALUES ('GQsxIs7qizYfEXgZvru0VbPQZZjpDWfYit3R1zxr5zZfBO0ZP+S4Nd28i/XWzTS1N6sNyodDzz2ia75jhRYUXQ==99999', 2);




INSERT INTO base64data(base64) VALUES ('Basic $argon2id$v=19$m=4096,t=3,p=1$bZmJV1G8SmPQrqTj/dYTsA$EVA8+h0BSpriIlEIl4DYqfbgyRX5mULE3adj77FMzDI');

INSERT INTO cards(id, "ownerId", number, balance)
VALUES (1, 1, '**** *888', 50000),
       (2, 2, '**** *999', 90000);

ALTER SEQUENCE cards_id_seq RESTART WITH 3;


INSERT INTO roles (role) VALUES ('ADMIN');
INSERT INTO roles (role) VALUES ('USER');
INSERT INTO user_roles ("user", role) VALUES (1, 1);
