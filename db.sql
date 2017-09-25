CREATE DATABASE todo_list;

USE todo_list;

GRANT ALL ON todo_list.* to 'root'@'%' identified by 'alex21';


CREATE TABLE todo_list(
  id BIGINT NOT NULL PRIMARY KEY  AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  user_id BIGINT
);

CREATE TABLE item(
  id BIGINT NOT NULL PRIMARY KEY  AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  done BOOL,
  list_id BIGINT
);


CREATE TABLE user(
  id BIGINT NOT NULL PRIMARY KEY  AUTO_INCREMENT,
  username VARCHAR(255) NOT NULL,
  password VARCHAR(512) NOT NULL
);


ALTER TABLE todo_list ADD CONSTRAINT fk_user_list
FOREIGN KEY(user_id) REFERENCES  user(id);

ALTER TABLE item ADD CONSTRAINT fk_list_item
FOREIGN KEY (list_id) REFERENCES todoLIst (id);
