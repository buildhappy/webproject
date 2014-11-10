insert into spitter (id,username, password, fullname, email, update_by_email) values (1,'habuma', 'password', 'Craig Walls', 'craig@habuma.com', false);
insert into spitter (id,username, password, fullname, email, update_by_email) values (2,'artnames', 'password', 'Art Names', 'artnames@habuma.com', false);

insert into spittle (id,spitter_id, spittleText, postedTime) values (1,1, 'Have you read Spring in Action 3? I hear it is awesome!', '2010-06-09');
insert into spittle (id,spitter_id, spittleText, postedTime) values (2,2, 'Trying out Spring''s new expression language.', '2010-06-11');
insert into spittle (id,spitter_id, spittleText, postedTime) values (3,1, 'Who''s going to SpringOne/2GX this year?', '2010-06-19');