package org.abondar.experimental.todolist.test;

import org.abondar.experimental.todolist.app.Application;
import org.abondar.experimental.todolist.datamodel.User;
import org.abondar.experimental.todolist.mapper.DatabaseMapper;
import org.abondar.experimental.todolist.service.AuthService;
import org.abondar.experimental.todolist.service.AuthServiceImpl;
import org.apache.cxf.rt.security.crypto.CryptoUtils;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.abondar.experimental.todolist.security.PasswordUtil.*;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class SecurityTest {

    @Autowired
    private DatabaseMapper mapper;

    @Autowired
    private  AuthService authService;

    @Test
    public void genShmacSigTest() {
        String sig = CryptoUtils.encodeBytes("borscht".getBytes());
        System.out.println(sig);
    }

    @Test
    public void createHashTest() throws CannotPerformOperationException, InvalidHashException {
        String pwd = "salo";
        String pwdHash = createHash(pwd);

        assertTrue(verifyPassword(pwd, pwdHash));

    }

    @Test
    public void verifyUser() throws CannotPerformOperationException {


        mapper.deleteAllUsers();
        String pwd = "salo";
        String pwdHash = createHash(pwd);

        User user = new User("alex", pwdHash);
        mapper.insertOrUpdateUser(user);

        user =mapper.findUserById(user.getId());

        assertEquals(pwdHash,user.getPassword());
        assertTrue(authService.validateUser(user.getId(),pwd));

    }


    @After
    public void afterMethod() {
        mapper.deleteAllItems();
        mapper.deleteAllLists();
        mapper.deleteAllUsers();

    }
}
