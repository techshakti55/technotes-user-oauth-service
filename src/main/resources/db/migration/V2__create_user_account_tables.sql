CREATE TABLE user_accounts (
                               id UUID PRIMARY KEY,
                               email VARCHAR(255) NOT NULL UNIQUE,
                               display_name VARCHAR(100) NOT NULL,
                               password_hash VARCHAR(255) NOT NULL,
                               status VARCHAR(20) NOT NULL,
                               created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_account_roles (
                                    user_id UUID NOT NULL,
                                    role VARCHAR(50) NOT NULL,

                                    CONSTRAINT pk_user_account_roles
                                        PRIMARY KEY (user_id, role),

                                    CONSTRAINT fk_user_account_roles_user
                                        FOREIGN KEY (user_id)
                                            REFERENCES user_accounts(id)
                                            ON DELETE CASCADE
);

CREATE INDEX idx_user_accounts_email
    ON user_accounts(email);
