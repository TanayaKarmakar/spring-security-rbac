create database user_db_rbac;

create user rbac_user;

grant all privileges on user_db_rbac.* to rbac_user;