    docker-compose up
    plsql --host localhost --user postgres --db postgres
    create table operations(date timestamp, amount money);
    insert into operations(date, amount) values ('2019-04-26 00:05:00Europe/Paris', -22.50);
