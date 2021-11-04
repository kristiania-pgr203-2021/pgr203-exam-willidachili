create table OPTIONS
(
    option_id serial primary key,
    label varchar(100) not null,
    question_id int not null,
    constraint fk_questions foreign key (question_id) references questions(question_id)
);