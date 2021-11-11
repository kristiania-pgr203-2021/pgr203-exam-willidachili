create table ANSWERS
(
    response_id serial primary key,
    option_id int not null,
    constraint fk_options foreign key (option_id) references options(option_id)
);