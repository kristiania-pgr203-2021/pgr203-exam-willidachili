create table QUESTIONS
(
    question_id serial primary key,
    title varchar(100) not null,
    text varchar(200) not null
);