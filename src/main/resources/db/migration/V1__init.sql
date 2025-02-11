create table if not exists users (
    id bigint primary key generated always as identity,
    first_name varchar(45) not null,
    last_name varchar(45) not null,
    username varchar(100) not null unique,
    password varchar(60) not null,
    is_active boolean not null
);

create table if not exists trainees (
    user_id bigint primary key references users(id) on delete cascade,
    date_of_birth date constraint past_date_of_birth check ( date_of_birth <= now() ),
    address varchar(255)
);

create table if not exists training_types (
    id bigint primary key generated always as identity,
    name varchar(100) not null unique
);

create table if not exists trainers (
    user_id bigint primary key references users(id) on delete cascade,
    specialization bigint not null references training_types(id)
);

create table if not exists trainings (
    id bigint primary key generated always as identity,
    trainee_id bigint not null references trainees(user_id) on delete cascade,
    trainer_id bigint not null references trainers(user_id) on delete cascade,
    name varchar(100) not null,
    type_id bigint not null references training_types(id),
    date date not null,
    duration int2 not null constraint positive_duration check (duration > 0)
);