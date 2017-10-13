package org.abondar.experimental.todolist.test;


import org.abondar.experimental.todolist.app.Application;
import org.abondar.experimental.todolist.datamodel.Item;
import org.abondar.experimental.todolist.datamodel.TodoList;
import org.abondar.experimental.todolist.datamodel.User;
import org.abondar.experimental.todolist.mapper.DatabaseMapper;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
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

        User foundUser = mapper.findUserById(user.getId());
        assertEquals(foundUser.getUsername(), user.getUsername());
        assertEquals(foundUser.getId(), foundUser.getId());

    }

    @Test
    public void testFindUserByName() {
        logger.info("Find user by name Test");
        mapper.deleteAllItems();
        mapper.deleteAllLists();
        mapper.deleteAllUsers();


        User user = new User("alex", "alex1");
        mapper.insertOrUpdateUser(user);

        User foundUser = mapper.findUserByName(user.getUsername());
        assertEquals(foundUser.getUsername(), user.getUsername());

    }

    @Test
    public void testUpdateUser() {
        logger.info("Update User Test");
        mapper.deleteAllItems();
        mapper.deleteAllLists();
        mapper.deleteAllUsers();

        User user = new User("alex", "alex1");
        mapper.insertOrUpdateUser(user);

        Long userId = user.getId();
        user.setPassword("alex21");
        mapper.insertOrUpdateUser(user);

        assertEquals(userId, user.getId());

    }

    @Test
    public void testDeleteUserById() {
        logger.info("Delete User Test");
        mapper.deleteAllItems();
        mapper.deleteAllLists();
        mapper.deleteAllUsers();
        User user = new User("alex", "alex1");
        mapper.insertOrUpdateUser(user);

        mapper.deleteUserById(user.getId());

        user = mapper.findUserById(user.getId());
        assertEquals(null, user);

    }


    @Test
    public void testInsertList() {
        logger.info("Insert list Test");
        mapper.deleteAllItems();
        mapper.deleteAllLists();
        mapper.deleteAllUsers();

        User user = new User("alex", "alex1");
        mapper.insertOrUpdateUser(user);

        TodoList list = new TodoList("Salo", user.getId());
        mapper.insertOrUpdateList(list);

        List<TodoList> foundLists = mapper.findListsByUserId(user.getId());
        TodoList foundById = mapper.findListById(list.getId());
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

        TodoList list = new TodoList("Salo", user.getId());
        mapper.insertOrUpdateList(list);

        Long listId = list.getId();
        list.setName("asasa");
        mapper.insertOrUpdateList(list);


        assertEquals(listId, list.getId());


    }


    @Test
    public void testDeleteListById() {
        logger.info("Delete list Test");
        mapper.deleteAllItems();
        mapper.deleteAllLists();
        mapper.deleteAllUsers();

        User user = new User("alex", "alex1");

        mapper.insertOrUpdateUser(user);

        TodoList list = new TodoList("Salo", user.getId());

        mapper.insertOrUpdateList(list);

        mapper.deleteListById(list.getId());

        List<TodoList> foundLists = mapper.findListsByUserId(user.getId());

        assertEquals(0, foundLists.size());

    }


    @Test
    public void testDeleteListsForUser() {
        logger.info("Delete list Test");
        mapper.deleteAllItems();
        mapper.deleteAllLists();
        mapper.deleteAllUsers();

        User user = new User("alex", "alex1");

        mapper.insertOrUpdateUser(user);

        TodoList list = new TodoList("Salo", user.getId());
        mapper.insertOrUpdateList(list);
        TodoList list1 = new TodoList("Salo1", user.getId());
        mapper.insertOrUpdateList(list1);

        mapper.deleteListsForUser(user.getId());

        List<TodoList> foundLists = mapper.findListsByUserId(user.getId());

        assertEquals(0, foundLists.size());

    }


    @Test
    public void testInsertItem() {
        logger.info("Insert item test");
        mapper.deleteAllItems();
        mapper.deleteAllLists();
        mapper.deleteAllUsers();

        User user = new User("alex", "alex1");

        mapper.insertOrUpdateUser(user);
        TodoList list = new TodoList("Salo", user.getId());

        mapper.insertOrUpdateList(list);

        Item item = new Item("eat", false, list.getId());
        mapper.insertOrUpdateItem(item);

        List<Item> foundItems = mapper.findItemsForList(list.getId());

        Item foundById = mapper.findItemById(item.getId());

        assertEquals(1, foundItems.size());
        assertEquals(item.getName(), foundItems.get(0).getName());
        assertEquals(foundById.getId(), foundById.getId());


    }


    @Test
    public void testFindItemsForLists() {
        logger.info("Insert item test");
        mapper.deleteAllItems();
        mapper.deleteAllLists();
        mapper.deleteAllUsers();

        User user = new User("alex", "alex1");
        mapper.insertOrUpdateUser(user);

        TodoList list = new TodoList("Salo", user.getId());
        mapper.insertOrUpdateList(list);

        Item item = new Item("eat", false, list.getId());
        mapper.insertOrUpdateItem(item);


        TodoList list1 = new TodoList("Salo1", user.getId());
        mapper.insertOrUpdateList(list1);
        Item item1 = new Item("eat1", false, list1.getId());
        mapper.insertOrUpdateItem(item1);


        List<Item> itemsByLists = mapper.findItemsForLists(Arrays.asList(list.getId(), list1.getId()));

        assertEquals(2, itemsByLists.size());
        assertEquals(item.getName(), itemsByLists.get(0).getName());
        assertEquals(item1.getName(), itemsByLists.get(1).getName());


    }


    @Test
    public void testUpdateItem() {
        logger.info("Update item test");
        mapper.deleteAllItems();
        mapper.deleteAllLists();
        mapper.deleteAllUsers();

        User user = new User("alex", "alex1");
        mapper.insertOrUpdateUser(user);

        TodoList list = new TodoList("Salo", user.getId());
        mapper.insertOrUpdateList(list);

        Item item = new Item("eat", false, list.getId());
        mapper.insertOrUpdateItem(item);

        Long id = item.getId();
        item.setDone(true);
        mapper.insertOrUpdateItem(item);

        assertEquals(id, item.getId());

    }


    @Test
    public void testDelteItemById() {
        logger.info("Delete item by id test");
        mapper.deleteAllItems();
        mapper.deleteAllLists();
        mapper.deleteAllUsers();

        User user = new User("alex", "alex1");

        mapper.insertOrUpdateUser(user);
        TodoList list = new TodoList("Salo", user.getId());

        mapper.insertOrUpdateList(list);

        Item item = new Item("eat", false, list.getId());
        mapper.insertOrUpdateItem(item);

        mapper.deleteItemById(item.getId());

        List<Item> foundItems = mapper.findItemsForList(list.getId());

        assertEquals(0, foundItems.size());

    }


    @Test
    public void testDelteItemForList() {
        logger.info("Delete item by id test");
        mapper.deleteAllItems();
        mapper.deleteAllLists();
        mapper.deleteAllUsers();

        User user = new User("alex", "alex1");

        mapper.insertOrUpdateUser(user);

        TodoList list = new TodoList("Salo", user.getId());

        mapper.insertOrUpdateList(list);

        Item item = new Item("eat", false, list.getId());
        mapper.insertOrUpdateItem(item);

        mapper.deleteItemsForList(list.getId());

        List<Item> foundItems = mapper.findItemsForList(list.getId());

        assertEquals(0, foundItems.size());

    }

    @Test
    public void testDelteItemsForLists() {
        logger.info("Delete items for lists test");
        mapper.deleteAllItems();
        mapper.deleteAllLists();
        mapper.deleteAllUsers();

        List<Long> listIds = new ArrayList<>();
        User user = new User("alex", "alex1");
        mapper.insertOrUpdateUser(user);

        TodoList list1 = new TodoList("Salo", user.getId());
        mapper.insertOrUpdateList(list1);

        listIds.add(list1.getId());
        Item item1 = new Item("eat", false, list1.getId());
        mapper.insertOrUpdateItem(item1);


        TodoList list2 = new TodoList("Salo1", user.getId());
        mapper.insertOrUpdateList(list2);

        listIds.add(list2.getId());
        Item item2 = new Item("eat1", false, list2.getId());
        mapper.insertOrUpdateItem(item2);

        mapper.deleteItemsForLists(listIds);

        List<Item> foundItems = mapper.findItemsForList(list1.getId());
        assertEquals(0, foundItems.size());

        foundItems = mapper.findItemsForList(list2.getId());
        assertEquals(0, foundItems.size());

    }


    @After
    public void cleanDatabase() {
        mapper.deleteAllItems();
        mapper.deleteAllLists();
        mapper.deleteAllUsers();
    }
}
