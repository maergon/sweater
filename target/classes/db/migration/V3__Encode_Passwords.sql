-- автоматически шифруем все пароли в БД введенные ранее
create extension if not exists pgcrypto;

update usr set password = crypt(password, gen_salt('bf', 8));