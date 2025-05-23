CREATE TABLE IF NOT EXISTS Post (
                                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                    title VARCHAR(255) NOT NULL,
                                    text TEXT NOT NULL,
                                    image_path VARCHAR(255),
                                    likes_count INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS Comment (
                                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                       post_id BIGINT NOT NULL,
                                       text TEXT NOT NULL,
                                       FOREIGN KEY (post_id) REFERENCES Post(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Tag (
                                   id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                   post_id BIGINT NOT NULL,
                                   name VARCHAR(255) NOT NULL,
                                   FOREIGN KEY (post_id) REFERENCES Post(id) ON DELETE CASCADE
);