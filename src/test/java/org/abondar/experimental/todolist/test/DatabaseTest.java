package org.abondar.experimental.todolist.test;


import org.abondar.experimental.todolist.app.Application;
import org.abondar.experimental.todolist.configuration.DatabaseConfiguration;
import org.abondar.experimental.todolist.datamodel.Item;
import org.abondar.experimental.todolist.datamodel.TodoList;
import org.abondar.experimental.todolist.datamodel.User;
import org.abondar.experimental.todolist.mappers.DatabaseMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@Import(DatabaseConfiguration.class)
@SpringBootApplication(scanBasePackageClasses = Application.class)
public class DatabaseTest {
    static Logger logger = LoggerFactory.getLogger(DatabaseTest.class);

    @Autowired
    private DatabaseMapper mapper;

    @Test
    public void testInsertUser() {
        logger.info("Insert User Test");
        mapper.deleteAllItems();
        mapper.deleteAllLists();
        mapper.deleteAllUsers();


        User user = new User("alex", "alex1");

        mapper.insertOrUpdateUser(user);

        User foundUser = mapper.findUserByName("alex");
        assertEquals(foundUser.getUsername(), user.getUsername());

        User foundUserById = mapper.findUserById(foundUser.getId());
        assertEquals(foundUser.getId(), foundUserById.getId());

    }

    @Test
    public void testUpdateUser() {
        logger.info("Update User Test");
        mapper.deleteAllItems();
        mapper.deleteAllLists();
        mapper.deleteAllUsers();

        User user = new User("alex", "alex1");
        mapper.insertOrUpdateUser(user);

        User upd = mapper.findUserByName("alex");

        upd.setPassword("alex21");
        mapper.insertOrUpdateUser(user);

        User foundUser = mapper.findUserById(upd.getId());

        assertEquals(foundUser.getPassword(), user.getPassword());

    }


    @Test
    public void testInsertList() {
        logger.info("Insert list Test");
        mapper.deleteAllItems();
        mapper.deleteAllLists();
        mapper.deleteAllUsers();

        User user = new User("alex", "alex1");

        mapper.insertOrUpdateUser(user);

        user = mapper.findUserByName("alex");

        TodoList list = new TodoList("Salo", user.getId());

        mapper.insertOrUpdateList(list);
        List<TodoList> foundLists = mapper.findListsByUsername(user.getUsername());

        TodoList foundById = mapper.findListById(foundLists.get(0).getId());
        assertEquals(1, foundLists.size());
        assertEquals(list.getName(), foundLists.get(0).getName());
        assertEquals(foundLists.get(0).getId(), foundById.getId());


    }

    @Test
    public void testUpdateList() {
        logger.info("Update list Test");
        mapper.deleteAllItems();
        mapper.deleteAllLists();
        mapper.deleteAllUsers();

        User user = new User("alex", "alex1");

        mapper.insertOrUpdateUser(user);

        user = mapper.findUserByName("alex");

        TodoList list = new TodoList("Salo", user.getId());

        mapper.insertOrUpdateList(list);
        List<TodoList> foundLists = mapper.findListsByUsername(user.getUsername());


        TodoList upd = foundLists.get(0);
        upd.setName("asasa");
        mapper.insertOrUpdateList(upd);

        TodoList actualUpd = mapper.findListById(upd.getId());

       assertEquals(upd.getId(),actualUpd.getId());


    }


    @Test
    public void testDeleteListById() {
        logger.info("Delete list Test");
        mapper.deleteAllItems();
        mapper.deleteAllLists();
        mapper.deleteAllUsers();

        User user = new User("alex", "alex1");

        mapper.insertOrUpdateUser(user);

        user = mapper.findUserByName("alex");

        TodoList list = new TodoList("Salo", user.getId());

        mapper.insertOrUpdateList(list);
        List<TodoList> foundLists = mapper.findListsByUsername(user.getUsername());


        mapper.deleteListById(foundLists.get(0).getId());

        foundLists = mapper.findListsByUsername(user.getUsername());

        assertEquals(0,foundLists.size());

    }


    @Test
    public void testInsertItem() {
        logger.info("Insert item test");
        mapper.deleteAllItems();
        mapper.deleteAllLists();
        mapper.deleteAllUsers();

        User user = new User("alex", "alex1");

        mapper.insertOrUpdateUser(user);

        user = mapper.findUserByName("alex");

        TodoList list = new TodoList("Salo", user.getId());

        mapper.insertOrUpdateList(list);
        List<TodoList> foundLists = mapper.findListsByUsername(user.getUsername());

        Item item = new Item("eat",false,foundLists.get(0).getId());
        mapper.insertOrUpdateItem(item);

        List<Item> foundItems = mapper.findItemsForList(foundLists.get(0).getId());

        Item foundById = mapper.findItemById(foundItems.get(0).getId());

        assertEquals(1, foundItems.size());
        assertEquals(item.getName(), foundItems.get(0).getName());
        assertEquals(foundById.getId(), foundById.getId());


    }


    @Test
    public void testUpdateItem() {
        logger.info("Update item test");
        mapper.deleteAllItems();
        mapper.deleteAllLists();
        mapper.deleteAllUsers();

        User user = new User("alex", "alex1");

        mapper.insertOrUpdateUser(user);

        user = mapper.findUserByName("alex");

        TodoList list = new TodoList("Salo", user.getId());

        mapper.insertOrUpdateList(list);
        List<TodoList> foundLists = mapper.findListsByUsername(user.getUsername());

        Item item = new Item("eat",false,foundLists.get(0).getId());
        mapper.insertOrUpdateItem(item);

        List<Item> foundItems = mapper.findItemsForList(foundLists.get(0).getId());

        Item upd = foundItems.get(0);

        upd.setDone(true);
        mapper.insertOrUpdateItem(upd);

        upd = mapper.findItemById(upd.getId());
        assertEquals(true, upd.getDone());
    }


    @Test
    public void testDelteItemById() {
        logger.info("Delete item by id test");
        mapper.deleteAllItems();
        mapper.deleteAllLists();
        mapper.deleteAllUsers();

        User user = new User("alex", "alex1");

        mapper.insertOrUpdateUser(user);

        user = mapper.findUserByName("alex");

        TodoList list = new TodoList("Salo", user.getId());

        mapper.insertOrUpdateList(list);
        List<TodoList> foundLists = mapper.findListsByUsername(user.getUsername());

        Item item = new Item("eat",false,foundLists.get(0).getId());
        mapper.insertOrUpdateItem(item);

        List<Item> foundItems = mapper.findItemsForList(foundLists.get(0).getId());

        mapper.deleteItemById(foundItems.get(0).getId());

        foundItems = mapper.findItemsForList(foundLists.get(0).getId());

        assertEquals(0,foundItems.size());
    }
}
