insert into nodes (id, path) values ('1', '1');
insert into nodes (id, path) values ('2', '1.2');
insert into nodes (id, path) values ('3', '1.3');
insert into nodes (id, path) values ('4', '1.3.4');
insert into nodes (id, path) values ('5', '1.3.5');
insert into nodes (id, path) values ('6', '1.3.6');
insert into nodes (id, path) values ('7', '1.2.7');


-- Test data representing the following structure:
--
--                   1
--                /     \
--               2       3
--             /       / | \
--            7       4  5  6
