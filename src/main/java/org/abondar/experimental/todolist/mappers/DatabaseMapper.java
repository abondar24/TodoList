package org.abondar.experimental.todolist.mappers;

import org.abondar.experimental.todolist.datamodel.Item;
import org.abondar.experimental.todolist.datamodel.TodoList;
import org.abondar.experimental.todolist.datamodel.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DatabaseMapper {
    void insertOrUpdateUser(@Param("user") User user);
    void insertOrUpdateList(@Param("todoList") TodoList todoList);
    void insertOrUpdateItem(@Param("item") Item item);
    User findUserById(@Param("id") Long  id);
    User findUserByName(@Param("username") String name);
    TodoList findListById(@Param("id") Long  id);
    Item findItemById(@Param("id") Long  id);
    List<Item> findItemsForList(@Param("listId") Long listId);
    List<TodoList> findListsByUsername(@Param("username") String username);
    void deleteAllUsers();
    void deleteAllLists();
    void deleteAllItems();
    void deleteListById(@Param("id")Long id);
    void deleteItemById(@Param("id")Long id);

}
