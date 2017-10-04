package org.abondar.experimental.todolist.service;


import org.abondar.experimental.todolist.datamodel.Item;
import org.abondar.experimental.todolist.datamodel.TodoList;
import static org.abondar.experimental.todolist.security.PasswordUtil.*;

import javax.ws.rs.core.Response;
import java.io.IOException;

public interface RestService {

    public Response echo();

    public Response createUser(String username, String password) throws CannotPerformOperationException,IOException;

    public Response loginUser(String username, String password) throws InvalidHashException,CannotPerformOperationException,IOException;

    public Response logoutUser(Long userId) throws IOException;


    public Response createOrEditList(TodoList list)throws IOException;

    public Response getListsByUser(Long userId);

    public Response createOrEditItem(Item item)throws IOException;

    public Response getItemsForList(Long listId);

    public Response deleteUser(Long id);

    public Response deleteItem(Long id);

    public Response clearList(Long listId);

    public Response deleteList(Long id);

}
