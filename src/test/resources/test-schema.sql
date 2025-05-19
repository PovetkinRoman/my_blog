-- Таблица с пользователями
DROP TABLE IF EXISTS Post;
CREATE TABLE Post
(
    id          BIGINT PRIMARY KEY,
    title       VARCHAR(255),
    text        TEXT,
    image_path  VARCHAR(255),
    likes_count INT
);

-- insert into Post(id, title, text, image_path, likes_count)
-- values (1, 'title1', 'text1', '1746438946080.jpg', 30);
-- insert into Post(title, text, image_path, likes_count)
-- values (2, 'title2', 'text2', '1746438946080.jpg', 2);
-- insert into Post(title, text, image_path, likes_count)
-- values ('title3', 'text3', '1746438946080.jpg', 15);

DROP TABLE IF EXISTS Comment;
CREATE TABLE Comment
(

    id      BIGINT PRIMARY KEY,
    text    TEXT,
    post_id BIGINT,
    FOREIGN KEY (post_id) REFERENCES Post (id) -- ON DELETE CASCADE

);
-- insert into Comment(post_id, text)
-- values (1, 'comments1Forpost_id1');
-- insert into Comment(post_id, text)
-- values (1, 'comments2Forpost_id1');
-- insert into Comment(post_id, text)
-- values (2, 'comments1Forpost_id2');
-- insert into Comment(post_id, text)
-- values (3, 'comments1Forpost_id3');
-- insert into Comment(post_id, text)
-- values (3, 'comments2Forpost_id3');
-- insert into Comment(post_id, text)
-- values (3, 'comments3Forpost_id3');
DROP TABLE IF EXISTS Tag;
CREATE TABLE Tag
(
    id      BIGINT PRIMARY KEY,
    name    VARCHAR(255),
    post_id BIGINT,
    FOREIGN KEY (post_id) REFERENCES Post (id) ON DELETE CASCADE
);

-- insert into Tag(post_id, name)
-- values (1, 'tags1Forpost_id1');
-- insert into Tag(post_id, name)
-- values (1, 'tags2Forpost_id1');
-- insert into Tag(post_id, name)
-- values (2, 'tags1Forpost_id2');
-- insert into Tag(post_id, name)
-- values (3, 'tags1Forpost_id3');
-- insert into Tag(post_id, name)
-- values (3, 'tags2Forpost_id3');
-- insert into Tag(post_id, name)
-- values (3, 'tags3Forpost_id3');