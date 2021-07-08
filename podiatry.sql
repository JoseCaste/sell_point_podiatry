create database podiatry;
use podiatry;

create table user (id mediumint auto_increment, name text, lastname text, user_name varchar(10), password text, primary key(id));
insert into user (name, lastname, user_name,password) value ('Jose', 'Castellanos','jose',md5('12345'));
select * from user;

create table purchase(id_purchase mediumint auto_increment, fk_iduser mediumint, date_ datetime, primary key(id_purchase),foreign key(fk_iduser)references user(id));

create table contains_(fk_idpurchase mediumint,fk_idproduct mediumint, foreign key(fk_idpurchase)references purchase(id_purchase),
foreign key(fk_idproduct)references product(id_product));

create table product (id_product mediumint auto_increment,name text,total int, price decimal,img text, primary key(id_product));
select * from product;
delete from product;