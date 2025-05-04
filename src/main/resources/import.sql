insert into posts(id, title, text, imagePath, likesCount) values (1, 'title1', 'text1', 'imagePath1', 30);
insert into posts(id, title, text, imagePath, likesCount) values (2, 'title2', 'text2', 'imagePath2', 2);
insert into posts(id, title, text, imagePath, likesCount) values (3, 'title3', 'text3', 'imagePath3', 15);

insert into comments(post_id, text) values (1, 'comments1Forpost_id1');
insert into comments(post_id, text) values (1, 'comments2Forpost_id1');
insert into comments(post_id, text) values (2, 'comments1Forpost_id2');
insert into comments(post_id, text) values (3, 'comments1Forpost_id3');
insert into comments(post_id, text) values (3, 'comments2Forpost_id3');
insert into comments(post_id, text) values (3, 'comments3Forpost_id3');

insert into tags(post_id, name) values (1, 'tags1Forpost_id1');
insert into tags(post_id, name) values (1, 'tags2Forpost_id1');
insert into tags(post_id, name) values (2, 'tags1Forpost_id2');
insert into tags(post_id, name) values (3, 'tags1Forpost_id3');
insert into tags(post_id, name) values (3, 'tags2Forpost_id3');
insert into tags(post_id, name) values (3, 'tags3Forpost_id3');
