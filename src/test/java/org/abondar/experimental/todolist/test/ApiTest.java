package org.abondar.experimental.todolist.test;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.abondar.experimental.todolist.app.Application;
import org.abondar.experimental.todolist.datamodel.Item;
import org.abondar.experimental.todolist.datamodel.TodoList;
import org.abondar.experimental.todolist.datamodel.User;
import org.abondar.experimental.todolist.mapper.DatabaseMapper;
import org.abondar.experimental.todolist.service.RestService;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSBindingFactory;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.client.WebClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.ws.rs.core.*;
import java.io.IOException;
import java.util.*;

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

    private ObjectMapper objectMapper = new ObjectMapper();

    private String endpoint = "local://todo_list_test";

    @Before
    public void beforeMethod() {
        JAXRSServerFactoryBean factory = new JAXRSServerFactoryBean();
        factory.setBindingId(JAXRSBindingFactory.JAXRS_BINDING_ID);
        factory.setProvider(new JacksonJsonProvider());
        factory.setAddress(endpoint);
        factory.setServiceBean(restService);
        server = factory.create();
        server.start();
    }

    @Test
    public void testEcho() {
        WebClient client = WebClient.create(endpoint);

        client.path("/echo");
        Response response = client.get();
        assertEquals(200, response.getStatus());
    }


    @Test
    public void testCreateUser() {

        mapper.deleteAllItems();
        mapper.deleteAllLists();
        mapper.deleteAllUsers();
        WebClient client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        String username = "alex";
        String password = "salo";

        client.path("/create_user").accept(MediaType.APPLICATION_JSON);

        Long userId = client.post(new Form().param("username", username)
                .param("password", password)).readEntity(Long.class);
        User user = mapper.findUserById(userId);
        assertEquals(username, user.getUsername());
    }


    @Test
    public void testCreateUserExists() {

        mapper.deleteAllItems();
        mapper.deleteAllLists();
        mapper.deleteAllUsers();
        WebClient client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        String username = "alex";
        String password = "salo";
        Form form = new Form()
                .param("username", username)
                .param("password", password);

        client.path("/create_user").accept(MediaType.APPLICATION_JSON);
        client.post(form);

        Response response = client.post(form);
        assertEquals(302, response.getStatus());
    }

    @Test
    public void testLogInUser() {

        mapper.deleteAllItems();
        mapper.deleteAllLists();
        mapper.deleteAllUsers();
        WebClient client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        String username = "alex";
        String password = "salo";
        Form form = new Form()
                .param("username", username)
                .param("password", password);

        client.path("/create_user").accept(MediaType.APPLICATION_JSON);
        Response response = client.post(form);
        NewCookie cookie = response.getCookies().get("X-JWT-AUTH");
        String token = cookie.getValue();
        Long userId = response.readEntity(Long.class);


        client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/login_user").accept(MediaType.APPLICATION_JSON).header("Authorization", "JWT " + token);

        response = client.post(form);
        assertEquals(202, response.getStatus());
        Long id = response.readEntity(Long.class);

        assertEquals(userId, id);


    }


    @Test
    public void testLogInUserNotFound() {

        mapper.deleteAllItems();
        mapper.deleteAllLists();
        mapper.deleteAllUsers();
        WebClient client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        String username = "alex";
        String password = "salo";
        Form form = new Form();
        form.param("username", username);
        form.param("password", password);

        client.path("/login_user").accept(MediaType.APPLICATION_JSON);
        Response response = client.post(form);

        assertEquals(404, response.getStatus());

    }


    @Test
    public void testLogInUserIncorrectPassword() {

        mapper.deleteAllItems();
        mapper.deleteAllLists();
        mapper.deleteAllUsers();
        WebClient client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        String username = "alex";
        String password = "salo";
        Form form = new Form()
                .param("username", username)
                .param("password", password);

        client.path("/create_user").accept(MediaType.APPLICATION_JSON);
        Response response = client.post(form);
        NewCookie cookie = response.getCookies().get("X-JWT-AUTH");
        String token = cookie.getValue();


        client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/login_user").accept(MediaType.APPLICATION_JSON).header("Authorization", "JWT " + token);

        form = new Form()
                .param("username", username)
                .param("password", "aaaaaa");
        response = client.post(form);
        assertEquals(401, response.getStatus());


    }


    @Test
    public void testLogOutUser() {

        mapper.deleteAllItems();
        mapper.deleteAllLists();
        mapper.deleteAllUsers();
        WebClient client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        String username = "alex";
        String password = "salo";
        Form form = new Form()
                .param("username", username)
                .param("password", password);

        client.path("/create_user").accept(MediaType.APPLICATION_JSON);
        Response response = client.post(form);
        NewCookie cookie = response.getCookies().get("X-JWT-AUTH");
        String token = cookie.getValue();
        Long userId = response.readEntity(Long.class);


        client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/login_user").accept(MediaType.APPLICATION_JSON).header("Authorization", "JWT " + token);
        client.post(form);

        client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/logout_user")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "JWT " + token);
        client.query("user_id",userId.toString());
        response=client.get();
        cookie = response.getCookies().get("X-JWT-AUTH");

        assertEquals(200, response.getStatus());
        assertEquals("", cookie.getValue());


    }


    @Test
    public void testCreateList() throws JsonProcessingException {

        mapper.deleteAllItems();
        mapper.deleteAllLists();
        mapper.deleteAllUsers();
        WebClient client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        client.path("/create_user").accept(MediaType.APPLICATION_JSON);
        Response response = client.post(new Form()
                .param("username", "alex")
                .param("password", "salo"));
        NewCookie cookie = response.getCookies().get("X-JWT-AUTH");
        String token = cookie.getValue();
        Long userId = response.readEntity(Long.class);


        client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/create_list")
                .type(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "JWT " + token);

        TodoList list = new TodoList("list1", userId);
        response = client.post(list);
        assertEquals(200, response.getStatus());

        Long listId = response.readEntity(Long.class);
        TodoList addedList = mapper.findListById(listId);
        assertEquals(list.getName(), addedList.getName());
    }


    @Test
    public void testGetListByUserId() throws IOException {

        mapper.deleteAllItems();
        mapper.deleteAllLists();
        mapper.deleteAllUsers();
        WebClient client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        client.path("/create_user").accept(MediaType.APPLICATION_JSON);
        Response response = client.post(new Form()
                .param("username", "alex")
                .param("password", "salo"));
        NewCookie cookie = response.getCookies().get("X-JWT-AUTH");
        String token = cookie.getValue();
        Long userId = response.readEntity(Long.class);


        client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/create_list")
                .type(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "JWT " + token);
        TodoList list = new TodoList("list1", userId);
        client.post(list);


        client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/get_lists_by_user_id")
                .type(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "JWT " + token);

        client.query("user_id", userId.toString());

        response = client.get();
        assertEquals(200, response.getStatus());

        List<TodoList> todoLists = objectMapper.readValue(response.readEntity(String.class),
                TypeFactory.defaultInstance().constructCollectionType(List.class, TodoList.class));
        assertEquals(1, todoLists.size());
        assertEquals(list.getName(), todoLists.get(0).getName());
    }


    @Test
    public void testCreateItem() throws JsonProcessingException {

        mapper.deleteAllItems();
        mapper.deleteAllLists();
        mapper.deleteAllUsers();
        WebClient client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        client.path("/create_user").accept(MediaType.APPLICATION_JSON);
        Response response = client.post(new Form()
                .param("username", "alex")
                .param("password", "salo"));
        NewCookie cookie = response.getCookies().get("X-JWT-AUTH");
        String token = cookie.getValue();
        Long userId = response.readEntity(Long.class);


        client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/create_list")
                .type(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "JWT " + token);

        TodoList list = new TodoList("list1", userId);
        client.post(list);

        client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/create_item")
                .type(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "JWT " + token);

        Item item = new Item("item1", false, list.getId());

        response = client.post(item);
        assertEquals(200, response.getStatus());

        Long itemId = response.readEntity(Long.class);
        Item addedItem = mapper.findItemById(itemId);
        assertEquals(item.getName(), addedItem.getName());

    }


    @Test
    public void testGetItemsForList() throws IOException {

        mapper.deleteAllItems();
        mapper.deleteAllLists();
        mapper.deleteAllUsers();
        WebClient client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        client.path("/create_user").accept(MediaType.APPLICATION_JSON);
        Response response = client.post(new Form()
                .param("username", "alex")
                .param("password", "salo"));
        NewCookie cookie = response.getCookies().get("X-JWT-AUTH");
        String token = cookie.getValue();
        Long userId = response.readEntity(Long.class);


        client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/create_list")
                .type(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "JWT " + token);

        TodoList list = new TodoList("list1", userId);
        response = client.post(list);
        Long listId = response.readEntity(Long.class);

        client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/create_item")
                .type(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "JWT " + token);

        Item item = new Item("item1", false, listId);
        client.post(item);


        client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/get_items_for_list")
                .type(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "JWT " + token);

        client.query("list_id", listId.toString());
        response = client.get();
        assertEquals(200, response.getStatus());

        List<Item> itemsForList = objectMapper.readValue(response.readEntity(String.class),
                TypeFactory.defaultInstance().constructCollectionType(List.class, Item.class));
        assertEquals(1, itemsForList.size());
        assertEquals(item.getName(), itemsForList.get(0).getName());
    }


    @Test
    public void testGetItemsForLists() throws IOException {

        mapper.deleteAllItems();
        mapper.deleteAllLists();
        mapper.deleteAllUsers();
        WebClient client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        client.path("/create_user").accept(MediaType.APPLICATION_JSON);
        Response response = client.post(new Form()
                .param("username", "alex")
                .param("password", "salo"));
        NewCookie cookie = response.getCookies().get("X-JWT-AUTH");
        String token = cookie.getValue();
        Long userId = response.readEntity(Long.class);


        client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/create_list")
                .type(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "JWT " + token);

        TodoList list = new TodoList("list1", userId);
        response = client.post(list);
        Long listId = response.readEntity(Long.class);

        client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/create_item")
                .type(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "JWT " + token);

        Item item = new Item("item1", false, listId);
        client.post(item);

        client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/create_list")
                .type(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "JWT " + token);

        TodoList list1 = new TodoList("list1", userId);
        response = client.post(list1);
        Long list1Id = response.readEntity(Long.class);

        client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/create_item")
                .type(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "JWT " + token);

        Item item1 = new Item("item2", false, list1Id);
        client.post(item1);


        client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/get_items_for_lists")
                .type(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "JWT " + token);
        response = client.post(Arrays.asList(listId,list1Id));
        assertEquals(200, response.getStatus());

        List<Item> itemsForList = objectMapper.readValue(response.readEntity(String.class),
                TypeFactory.defaultInstance().constructCollectionType(List.class, Item.class));
        assertEquals(2, itemsForList.size());
        assertEquals(item.getName(), itemsForList.get(0).getName());
        assertEquals(item1.getName(), itemsForList.get(1).getName());
    }



    @Test
    public void testDeleteUser() {
        mapper.deleteAllItems();
        mapper.deleteAllLists();
        mapper.deleteAllUsers();
        WebClient client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        client.path("/create_user").accept(MediaType.APPLICATION_JSON);
        Response response = client.post(new Form()
                .param("username", "alex")
                .param("password", "salo"));
        NewCookie cookie = response.getCookies().get("X-JWT-AUTH");
        String token = cookie.getValue();
        Long userId = response.readEntity(Long.class);

        client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/delete_user")
                .type(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "JWT " + token);
        client.query("user_id",userId.toString());

        response = client.get();
        assertEquals(200, response.getStatus());

    }


    @Test
    public void testDeleteItem() {
        mapper.deleteAllItems();
        mapper.deleteAllLists();
        mapper.deleteAllUsers();
        WebClient client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        client.path("/create_user").accept(MediaType.APPLICATION_JSON);
        Response response = client.post(new Form()
                .param("username", "alex")
                .param("password", "salo"));
        NewCookie cookie = response.getCookies().get("X-JWT-AUTH");
        String token = cookie.getValue();
        Long userId = response.readEntity(Long.class);

        client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/create_list")
                .type(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "JWT " + token);

        TodoList list = new TodoList("list1", userId);
        response = client.post(list);
        Long listId = response.readEntity(Long.class);

        client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/create_item")
                .type(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "JWT " + token);

        Item item = new Item("item1", false, listId);
        response = client.post(item);
        Long item1Id = response.readEntity(Long.class);


        client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/create_item")
                .type(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "JWT " + token);

        item = new Item("item2", false, listId);
        client.post(item);

        client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/delete_item")
                .type(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "JWT " + token);
        client.query("item_id",item1Id.toString());

        response = client.get();
        assertEquals(200, response.getStatus());

        List<Item> itemsForList = mapper.findItemsForList(listId);
        assertEquals(1,itemsForList.size());

    }



    @Test
    public void testClearList() {
        mapper.deleteAllItems();
        mapper.deleteAllLists();
        mapper.deleteAllUsers();
        WebClient client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        client.path("/create_user").accept(MediaType.APPLICATION_JSON);
        Response response = client.post(new Form()
                .param("username", "alex")
                .param("password", "salo"));
        NewCookie cookie = response.getCookies().get("X-JWT-AUTH");
        String token = cookie.getValue();
        Long userId = response.readEntity(Long.class);

        client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/create_list")
                .type(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "JWT " + token);

        TodoList list = new TodoList("list1", userId);
        response = client.post(list);
        Long listId = response.readEntity(Long.class);

        client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/create_item")
                .type(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "JWT " + token);

        Item item = new Item("item1", false, listId);
        client.post(item);

        client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/create_item")
                .type(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "JWT " + token);

        item = new Item("item2", false, listId);
        client.post(item);

        client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/clear_list")
                .type(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "JWT " + token);
        client.query("list_id",listId.toString());

        response = client.get();
        assertEquals(200, response.getStatus());

        List<Item> itemsForList = mapper.findItemsForList(listId);
        assertEquals(0,itemsForList.size());

    }


    @Test
    public void testDeleteList() {
        mapper.deleteAllItems();
        mapper.deleteAllLists();
        mapper.deleteAllUsers();
        WebClient client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        client.path("/create_user").accept(MediaType.APPLICATION_JSON);
        Response response = client.post(new Form()
                .param("username", "alex")
                .param("password", "salo"));
        NewCookie cookie = response.getCookies().get("X-JWT-AUTH");
        String token = cookie.getValue();
        Long userId = response.readEntity(Long.class);

        client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/create_list")
                .type(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "JWT " + token);

        TodoList list = new TodoList("list1", userId);
        response = client.post(list);
        Long listId = response.readEntity(Long.class);


        client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/delete_list")
                .type(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "JWT " + token);
        client.query("list_id",listId.toString());

        response = client.get();
        assertEquals(200, response.getStatus());

    }


    @After
    public void afterMethod() {
        mapper.deleteAllItems();
        mapper.deleteAllLists();
        mapper.deleteAllUsers();
        server.stop();
        server.destroy();
    }
}
