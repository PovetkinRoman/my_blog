-- Таблица с пользователями
create table if not exists posts(
                                    id bigserial primary key,
                                    title varchar(256) not null,
                                    text varchar(256) not null,
                                    imagePath varchar(256) not null,
                                    likesCount integer not null);

insert into posts(title, text, imagePath, likesCount) values ('title1', 'text1', 'imagePath1', 30);
insert into posts(title, text, imagePath, likesCount) values ('title2', 'text2', 'imagePath2', 2);
insert into posts(title, text, imagePath, likesCount) values ('title3', 'text3', 'imagePath3', 15);

-- Таблица с пользователями
create table if not exists comments(
                                    id bigserial primary key,
                                    postId bigserial,
                                    text varchar(256) not null);
insert into comments(postId, text) values (1, 'comment1ForPostId1');
insert into comments(postId, text) values (1, 'comment2ForPostId1');
insert into comments(postId, text) values (2, 'comment1ForPostId2');
insert into comments(postId, text) values (3, 'comment1ForPostId3');
insert into comments(postId, text) values (3, 'comment2ForPostId3');
insert into comments(postId, text) values (3, 'comment3ForPostId3');
