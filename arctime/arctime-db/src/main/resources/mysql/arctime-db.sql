
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS holidays;
DROP TABLE IF EXISTS audit_logs;
DROP TABLE IF EXISTS timesheets;
DROP TABLE IF EXISTS bills;
DROP TABLE IF EXISTS pay_periods;
DROP TABLE IF EXISTS assignments;
DROP TABLE IF EXISTS tasks;
DROP TABLE IF EXISTS supervisors;
DROP TABLE IF EXISTS active_user_counts;
DROP TABLE IF EXISTS active_company_counts;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS companies;

CREATE TABLE IF NOT EXISTS companies (
    `id`             INTEGER      NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `name`           VARCHAR(250) NOT NULL,
    `active`         BOOLEAN      NOT NULL DEFAULT TRUE,

    CONSTRAINT unique_company_name UNIQUE (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS users (
    `id`             INTEGER      NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `company_id`     INTEGER      NOT NULL,
    `login`          VARCHAR(32)  NOT NULL,
    `hashed_pass`    VARCHAR(128) NOT NULL,
    `salt`           VARCHAR(16)  NOT NULL,
    -- Treat emails as case-insensitive.
    `email`          VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
    `first_name`     VARCHAR(50)  NOT NULL,
    `last_name`      VARCHAR(50)  NOT NULL,
    `active`         BOOLEAN      NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_users_company_id FOREIGN KEY (`company_id`)
        REFERENCES companies(`id`) ON DELETE CASCADE,

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

CREATE TABLE IF NOT EXISTS active_company_counts (
    `day`            DATE         NOT NULL,
    `active`         INTEGER      NOT NULL,

    INDEX idx_active_company_counts_day USING HASH (`day`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS active_user_counts (
    `day`            DATE         NOT NULL,
    `company_id`     INTEGER      NOT NULL,
    `active`         INTEGER      NOT NULL,

    CONSTRAINT fk_active_user_counts_company_id FOREIGN KEY (`company_id`)
        REFERENCES companies(`id`) ON DELETE CASCADE,

    INDEX idx_active_company_counts_day USING HASH (`day`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS supervisors (
    `company_id`     INTEGER      NOT NULL,
    `user_id`        INTEGER      NOT NULL,
    `supervisor_id`  INTEGER      NOT NULL,
    `is_primary`     BOOLEAN      NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_supervisors_company_id FOREIGN KEY (company_id)
        REFERENCES companies(id) ON DELETE CASCADE,
    CONSTRAINT fk_supervisors_user_id FOREIGN KEY (`user_id`)
        REFERENCES users(`id`) ON DELETE CASCADE,
    CONSTRAINT fk_supervisors_supervisor_id FOREIGN KEY (`supervisor_id`)
        REFERENCES users(`id`) ON DELETE CASCADE,

    INDEX idx_supervisors_company_id USING HASH (`company_id`),
    INDEX idx_supervisors_user_id USING HASH (`user_id`),
    INDEX idx_supervisors_supervisor_id USING HASH (`supervisor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS tasks (
    `id`             INTEGER      NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `company_id`     INTEGER      NOT NULL,
    `description`    VARCHAR(255) NOT NULL,
    `job_code`       VARCHAR(255) NOT NULL,
    `admin`          BOOLEAN      NOT NULL DEFAULT FALSE,
    `active`         BOOLEAN      NOT NULL DEFAULT TRUE,

    CONSTRAINT unique_task UNIQUE (`job_code`),

    CONSTRAINT fk_tasks_company_id FOREIGN KEY (`company_id`)
        REFERENCES companies(`id`) ON DELETE CASCADE,

    INDEX idx_tasks_job_code USING HASH (`job_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS assignments (
    `id`             INTEGER      NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `company_id`     INTEGER      NOT NULL,
    `task_id`        INTEGER      NOT NULL,
    `user_id`        INTEGER      NOT NULL,
    `labor_cat`      VARCHAR(120) NOT NULL,
    `item_name`      VARCHAR(255) NOT NULL,
    `begin`          DATE,
    `end`            DATE,

    CONSTRAINT fk_assignments_company_id FOREIGN KEY (`company_id`)
        REFERENCES companies(`id`) ON DELETE CASCADE,
    CONSTRAINT fk_assignments_task_id FOREIGN KEY (`task_id`)
        REFERENCES tasks(`id`) ON DELETE CASCADE,
    CONSTRAINT fk_assignments_user_id FOREIGN KEY (`user_id`)
        REFERENCES users(`id`) ON DELETE CASCADE,

    INDEX idx_assignments_task_id USING HASH (`task_id`),
    INDEX idx_assignments_user_id USING HASH (`user_id`),
    INDEX idx_assignments_begin USING HASH (`begin`),
    INDEX idx_assignments_end USING HASH (`end`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS pay_periods (
    `company_id`     INTEGER      NOT NULL,
    `begin`          DATE         NOT NULL PRIMARY KEY,
    `end`            DATE         NOT NULL,
    `type`           VARCHAR(20)  NOT NULL,

    CONSTRAINT fk_pay_periods_company_id FOREIGN KEY (`company_id`)
        REFERENCES companies(`id`) ON DELETE CASCADE,

    INDEX idx_pay_periods_begin USING HASH (`begin`),
    INDEX idx_pay_periods_end USING HASH (`end`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS bills (
    `id`             INTEGER      NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `assignment_id`  INTEGER,
    `task_id`        INTEGER      NOT NULL,
    `user_id`        INTEGER      NOT NULL,
    `day`            DATE         NOT NULL,
    `hours`          VARCHAR(6)   NOT NULL,
    `timestamp`      TIMESTAMP    NOT NULL DEFAULT NOW(),

    CONSTRAINT unique_bill UNIQUE (`assignment_id`, `task_id`, `user_id`, `day`),

    CONSTRAINT fk_bills_assignment_id FOREIGN KEY (`assignment_id`)
        REFERENCES assignments(`id`) ON DELETE CASCADE,
    CONSTRAINT fk_bills_task_id FOREIGN KEY (`task_id`)
        REFERENCES tasks(`id`) ON DELETE CASCADE,
    CONSTRAINT fk_bills_user_id FOREIGN KEY (`user_id`)
        REFERENCES users(`id`) ON DELETE CASCADE,

    INDEX idx_bills_assignment_id USING HASH (`assignment_id`),
    INDEX idx_bills_task_id USING HASH (`task_id`),
    INDEX idx_bills_user_id USING HASH (`user_id`),
    INDEX idx_bills_day USING HASH (`day`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS timesheets (
    `id`             INTEGER      NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `company_id`     INTEGER      NOT NULL,
    `user_id`        INTEGER      NOT NULL,
    `pp_begin`       DATE         NOT NULL,
    `completed`      BOOLEAN      NOT NULL DEFAULT FALSE,
    `approved`       BOOLEAN      NOT NULL DEFAULT FALSE,
    `verified`       BOOLEAN      NOT NULL DEFAULT FALSE,
    `exported`       BOOLEAN      NOT NULL DEFAULT FALSE,
    `approver_id`    INTEGER,
    `verifier_id`    INTEGER,
    `exporter_id`    INTEGER,

    CONSTRAINT unique_timesheet UNIQUE (`user_id`, `pp_begin`),

    CONSTRAINT fk_timesheets_company_id FOREIGN KEY (`company_id`)
        REFERENCES companies(`id`) ON DELETE CASCADE,
    CONSTRAINT fk_timesheets_user_id FOREIGN KEY (`user_id`)
        REFERENCES users(`id`) ON DELETE CASCADE,
    CONSTRAINT fk_timesheets_pp_begin FOREIGN KEY (`pp_begin`)
        REFERENCES pay_periods(`begin`) ON DELETE CASCADE,
    CONSTRAINT fk_timesheets_approver_id FOREIGN KEY (`approver_id`)
        REFERENCES users(`id`) ON DELETE CASCADE,
    CONSTRAINT fk_timesheets_verifier_id FOREIGN KEY (`verifier_id`)
        REFERENCES users(`id`) ON DELETE CASCADE,
    CONSTRAINT fk_timesheets_exporter_id FOREIGN KEY (`exporter_id`)
        REFERENCES users(`id`) ON DELETE CASCADE,

    INDEX idx_timesheets_company_id USING HASH (`company_id`),
    INDEX idx_timesheets_user_id USING HASH (`user_id`),
    INDEX idx_timesheets_approver_id USING HASH (`approver_id`),
    INDEX idx_timesheets_verifier_id USING HASH (`verifier_id`),
    INDEX idx_timesheets_exporter_id USING HASH (`exporter_id`),
    INDEX idx_timesheets_pp_begin USING HASH (`pp_begin`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS audit_logs (
    `company_id`     INTEGER      NOT NULL,
    `timesheet_id`   INTEGER      NOT NULL,
    `log`            LONGTEXT     NOT NULL,
    `timestamp`      TIMESTAMP    NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_audit_logs_company_id FOREIGN KEY (`company_id`)
        REFERENCES companies(`id`) ON DELETE CASCADE,
    CONSTRAINT fk_audit_logs_timesheet_id FOREIGN KEY (`timesheet_id`)
        REFERENCES timesheets(`id`) ON DELETE CASCADE,

    INDEX idx_audit_logs_timesheet_id USING HASH (`timesheet_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS holidays (
    `id`             INTEGER      NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `company_id`     INTEGER      NOT NULL,
    `description`    VARCHAR(100) NOT NULL,
    `config`         VARCHAR(100) NOT NULL,

    CONSTRAINT unique_holiday_desc UNIQUE (`description`),
    CONSTRAINT unique_holiday_config UNIQUE (`config`),

    CONSTRAINT fk_holidays_company_id FOREIGN KEY (`company_id`)
        REFERENCES companies(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS transactions (
    `id`             INTEGER      NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `company_id`     INTEGER      NOT NULL,
    `user_id`        INTEGER      NOT NULL,
    `timestamp`      TIMESTAMP    NOT NULL DEFAULT NOW(),
    `type`           VARCHAR(80)  NOT NULL,
    `description`    VARCHAR(200) NOT NULL,
    `amount`         FLOAT        NOT NULL,
    `notes`          LONGTEXT,

    CONSTRAINT fk_transactions_company_id FOREIGN KEY (`company_id`)
        REFERENCES companies(`id`) ON DELETE CASCADE,
    CONSTRAINT fk_transactions_user_id FOREIGN KEY (`user_id`)
        REFERENCES users(`id`) ON DELETE CASCADE,

    INDEX idx_transactions_type USING HASH (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

