create database podiatry;
use podiatry;

create user 'spring'@'localhost' identified by '12345';
GRANT ALL PRIVILEGES ON * . * TO 'spring'@'localhost';

create table user (id mediumint auto_increment, name text, lastname text, user_name varchar(10), password text, primary key(id));
insert into user (name, lastname, user_name,password) value ('Jose', 'Castellanos','jose',md5('12345'));
select * from user;

create table purchase(id_purchase mediumint auto_increment, fk_iduser mediumint, date_ datetime, primary key(id_purchase),foreign key(fk_iduser)references user(id));

create table product (id_product mediumint auto_increment,name text,total int, price decimal,img text, primary key(id_product));


create table contains_(fk_idpurchase mediumint,fk_idproduct mediumint, foreign key(fk_idpurchase)references purchase(id_purchase),
foreign key(fk_idproduct)references product(id_product));

INSERT INTO role (id_role,name) VALUES (1,'ROLE_USER');
INSERT INTO role (id_role,name) VALUES (2,'ROLE_ADMIN');
insert into podiatry.user_roles(user_id,role_id) values (2,2);

insert into user (id,name, lastname, user_name,password) value (2,'Juan', 'Castellanos','juan','$2a$10$QOMPqHGrGarDAAnFfNPJLenTfaYtiNNRH8izae7oAnBVA6Bp3zKeW');
insert into podiatry.user_roles(user_id,role_id) values (1,1);
#insert into user_role
ALTER TABLE podiatry.product modify img mediumblob;
DESCRIBE podiatry.product;