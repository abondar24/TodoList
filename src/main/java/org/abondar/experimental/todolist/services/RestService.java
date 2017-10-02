package org.abondar.experimental.todolist.services;


import org.abondar.experimental.todolist.datamodel.Item;
import org.abondar.experimental.todolist.datamodel.TodoList;
import org.abondar.experimental.todolist.datamodel.User;

import javax.ws.rs.core.Response;
import java.io.IOException;

public interface RestService {

    public Response get();

    public Response createUser(String login, String password);

    public Response createOrEditList(TodoList list);

    public Response getListsByUser(Long userId);

    public Response createOrEditItem(Item item);

    public Response getItemsForList(Long listId);

    public Response deleteUser(Long id);

    public Response deleteItem(Long id);

    public Response clearList(Long listId);

    public Response deleteList(Long id);

}
