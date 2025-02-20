CREATE DATABASE IF NOT EXISTS `efuture`;

USE `efuture`;

create table if not exists product
(
    price       decimal(38, 2) not null,
    id          bigint auto_increment
        primary key,
    category    varchar(255)   null,
    description varchar(255)   null,
    name        varchar(255)   not null,
    status      varchar(255)   null
);

