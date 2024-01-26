-- Write your Task 1 answers in this file
drop schema if exists bedandbreakfast;

create database bedandbreakfast;

use bedandbreakfast;

create table users(
    email varchar(128),
    name varchar(128),

    primary key (email)
);

create table bookings(
    booking_id char(8),
    listing_id varchar(20),
    duration int,
    email varchar(128),

    primary key (booking_id),
    constraint foreign_id foreign key(email) references users(email)
);

create table reviews(
    id int auto_increment,
    date timestamp,
    listing_id varchar(20),
    reviewer_name varchar(64),
    comments text,

    primary key (id)
);

USE bedandbreakfast;

INSERT INTO users(email, name)
	VALUES("fred@gmail.com", "Fred Flintstone"), 
        ("barney@gmail.com", "Barney Rubble"), 
        ("fry@planetexpress.com", "Philip J Fry"), 
        ("hlmer@gmail.com", "Homer Simpson");