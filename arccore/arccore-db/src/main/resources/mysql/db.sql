
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS users;

CREATE TABLE IF NOT EXISTS users (
    `id`             INTEGER      NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `login`          VARCHAR(32)  NOT NULL,
    `hashed_pass`    VARCHAR(128) NOT NULL,
    `salt`           VARCHAR(16)  NOT NULL,
    -- Treat emails as case-insensitive.
    `email`          VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
    `first_name`     VARCHAR(50)  NOT NULL,
    `last_name`      VARCHAR(50)  NOT NULL,
    `active`         BOOLEAN      NOT NULL DEFAULT TRUE,

    CONSTRAINT unique_user_login UNIQUE (`login`),
    CONSTRAINT unique_user_email UNIQUE (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS roles (
    `name`           VARCHAR(30)  NOT NULL,
    `user_id`        INTEGER      NOT NULL,
    
    CONSTRAINT unique_role UNIQUE (`name`, `user_id`),

    CONSTRAINT fk_roles_user_id FOREIGN KEY (`user_id`)
        REFERENCES users(`id`) ON DELETE CASCADE,

    INDEX idx_roles_name USING HASH (`name`),
    INDEX idx_roles_user_id USING HASH (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

