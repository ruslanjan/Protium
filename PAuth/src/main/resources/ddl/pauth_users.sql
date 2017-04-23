CREATE TABLE table_name
(
  user_id     SERIAL       NOT NULL
    CONSTRAINT table_name_pkey
    PRIMARY KEY,
  login       VARCHAR(64)  NOT NULL,
  email       VARCHAR(128) NOT NULL,
  password    VARCHAR(64)  NOT NULL,
  first_name  VARCHAR(64),
  middle_name VARCHAR(64),
  last_name   VARCHAR(64),
  perm_group  VARCHAR(20)
);

CREATE UNIQUE INDEX table_name_user_id_uindex
  ON pauth_users (user_id);

CREATE UNIQUE INDEX table_name_login_uindex
  ON pauth_users (login);

CREATE UNIQUE INDEX table_name_email_uindex
  ON pauth_users (email);


