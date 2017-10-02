package org.abondar.experimental.todolist.test;


import org.abondar.experimental.todolist.app.Application;
import org.abondar.experimental.todolist.datamodel.User;
import org.abondar.experimental.todolist.mapper.DatabaseMapper;
import org.abondar.experimental.todolist.service.RestService;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSBindingFactory;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.transport.local.LocalConduit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class ApiTest {

    static Logger logger = LoggerFactory.getLogger(ApiTest.class);
    private Server server;

    @Autowired
    private RestService restService;

    @Autowired
    private DatabaseMapper mapper;


    private String endpoint = "local://todo_list_test";
    @Before
    public void beforeMethod() {
        JAXRSServerFactoryBean factory = new JAXRSServerFactoryBean();
        factory.setBindingId(JAXRSBindingFactory.JAXRS_BINDING_ID);
        factory.setAddress(endpoint);
        factory.setServiceBean(restService);
        server = factory.create();
        server.start();
    }

    @Test
    public void testEcho(){
        WebClient client = WebClient.create(endpoint);
        WebClient.getConfig(client).getRequestContext().put(LocalConduit.DIRECT_DISPATCH, Boolean.TRUE);

        client.path("/echo");
        Response response = client.get();
        assertEquals(200, response.getStatus());
    }


    @Test
    public void testCreateUser(){

        mapper.deleteAllItems();
        mapper.deleteAllLists();
        mapper.deleteAllUsers();
        WebClient client = WebClient.create(endpoint);
        WebClient.getConfig(client).getRequestContext().put(LocalConduit.DIRECT_DISPATCH, Boolean.TRUE);

        String username = "alex";
        String password = "salo";
        Form form = new Form();
        form.param("username",username);
        form.param("password",password);

        client.path("/createUser");
        client.form(form);

        User user = (User) client.get().getEntity();
        assertEquals(username, user.getUsername());
    }

    @After
    public void afterMethod() {
        server.stop();
        server.destroy();
    }
}
