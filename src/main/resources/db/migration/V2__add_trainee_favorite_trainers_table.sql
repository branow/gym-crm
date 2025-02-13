create table if not exists trainee_favorite_trainers (
    trainee_id bigint not null references trainees(user_id) on delete cascade,
    trainer_id bigint not null references trainers(user_id) on delete cascade,
    primary key (trainee_id, trainer_id)
);