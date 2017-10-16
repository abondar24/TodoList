# TodoList
Small TodoList app

The idea of the app: to build another tiny todo list application. It's simple: you can authorize there, and create,update 
or delete todolists.

# What was used to write it

- Java 9
- Mybatis
- Spring Boot
- Apache CXF
- JWT
- AngularJs
- Mysql
- Docker

# Build and run

- You need to have a mysql server running with db created from db.sql file
- To run on your machine just
```
mvn clean install
java -jar target/TodoList-1.0.jar
```
- To build docker image and run
```
mvn clean deploy
docker run -d --name <name> -p <host_post>:8024 abondar/todolist
```

- Open your browser and enter https://<hostname>:8024

# API reference
if you open https://<hostname>:8024/cxf you will see the link for swagger ui and xml description of api 

# Disclaimer
The app uses https and jks generated by me. It's a silly idea to store certificates and credentials on github, 
It's been done only for demo purpose. In production never do like I did here
