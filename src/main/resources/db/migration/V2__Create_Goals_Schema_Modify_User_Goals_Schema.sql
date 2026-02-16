CREATE TABLE goals (
    id VARCHAR(50) PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    description TEXT
);

DROP TABLE IF EXISTS user_goals;

CREATE TABLE user_goals (
    user_id BIGINT NOT NULL,
    goal_id VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, goal_id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_goal FOREIGN KEY (goal_id) REFERENCES goals(id) ON DELETE CASCADE
);