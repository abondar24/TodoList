package org.abondar.experimental.todolist.service;

import org.abondar.experimental.todolist.datamodel.User;
import org.abondar.experimental.todolist.service.exception.InvalidPasswordException;

import java.util.Date;
import java.util.List;

public interface AuthService {

    String renewToken(String tokenStr) throws Exception;

    public String getSubject(String token);

    public String createToken(String login, String issuer, List<String> roles);

    public boolean validateUser(Long userId,String password) throws InvalidPasswordException;

    public String authorizeUser(User user) throws InvalidPasswordException;


    public void logRecord(Date logDate, String action, String subject, String record);

}
