
DROP TABLE active_user_counts IF EXISTS;
DROP TABLE active_company_counts IF EXISTS;
DROP TABLE transactions IF EXISTS;
DROP TABLE roles IF EXISTS;
DROP TABLE users IF EXISTS;
DROP TABLE companies IF EXISTS;

CREATE TABLE companies (
    id             INTEGER      GENERATED BY DEFAULT AS IDENTITY
                                (START WITH 1, INCREMENT BY 1) NOT NULL PRIMARY KEY,
    name           VARCHAR(250) NOT NULL,
    active         BOOLEAN      DEFAULT TRUE NOT NULL,

    CONSTRAINT unique_company_name UNIQUE (name)
);

CREATE TABLE users (
    id             INTEGER      GENERATED BY DEFAULT AS IDENTITY
                                (START WITH 1, INCREMENT BY 1) NOT NULL PRIMARY KEY,
    company_id     INTEGER      NOT NULL,
    login          VARCHAR(32)  NOT NULL,
    hashed_pass    VARCHAR(128) NOT NULL,
    salt           VARCHAR(16)  NOT NULL,
    email          VARCHAR_IGNORECASE(255) NOT NULL,
    first_name     VARCHAR(50)  NOT NULL,
    last_name      VARCHAR(50)  NOT NULL,
    active         BOOLEAN      DEFAULT TRUE NOT NULL,

    CONSTRAINT fk_users_company_id FOREIGN KEY (company_id)
        REFERENCES companies(id) ON DELETE CASCADE,

    CONSTRAINT unique_user_login UNIQUE (login),
    CONSTRAINT unique_user_email UNIQUE (email)
);

CREATE TABLE roles (
    name           VARCHAR(30)  NOT NULL,
    user_id        INTEGER      NOT NULL,
    
    CONSTRAINT unique_role UNIQUE (name, user_id),

    CONSTRAINT fk_roles_user_id FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE transactions (
    id             INTEGER      GENERATED BY DEFAULT AS IDENTITY
                                (START WITH 1, INCREMENT BY 1) NOT NULL PRIMARY KEY,
    company_id     INTEGER      NOT NULL,
    user_id        INTEGER      NOT NULL,
    timestamp      TIMESTAMP    DEFAULT NOW() NOT NULL,
    type           VARCHAR(80)  NOT NULL,
    description    VARCHAR(200) NOT NULL,
    amount         FLOAT        NOT NULL,
    notes          LONGVARCHAR,

    CONSTRAINT fk_transactions_company_id FOREIGN KEY (company_id)
        REFERENCES companies(id) ON DELETE CASCADE,
    CONSTRAINT fk_transactions_user_id FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE active_company_counts (
    day            DATE         NOT NULL,
    active         INTEGER      NOT NULL
);

CREATE TABLE active_user_counts (
    day            DATE         NOT NULL,
    company_id     INTEGER      NOT NULL,
    active         INTEGER      NOT NULL,

    CONSTRAINT fk_active_user_counts_company_id FOREIGN KEY (company_id)
        REFERENCES companies(id) ON DELETE CASCADE
);

