package org.abondar.experimental.todolist.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.abondar.experimental.todolist.datamodel.Item;
import org.abondar.experimental.todolist.datamodel.TodoList;
import org.abondar.experimental.todolist.datamodel.User;
import org.abondar.experimental.todolist.mappers.DatabaseMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;

public class RestServiceImpl implements RestService {
    private static final Logger logger = LoggerFactory.getLogger(RestServiceImpl.class);
    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private DatabaseMapper dbMapper;

    @GET
    @Path("/echo")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response get() throws IOException {
        String resp = mapper.writeValueAsString("Server is up");
        return Response.ok(resp).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/log_in")
    @Override
    public Response logIn(User user) {
        logger.info(user.toString());
        Long userId = dbMapper.insertOrUpdateUser(user);
        return Response.ok(userId).build();
    }

    @POST
    @Path("/list")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response createOrEditList(TodoList list) {
        Long listId=dbMapper.insertOrUpdateList(list);
        return Response.ok(listId).build();
    }

    @GET
    @Path("/list_by_user_id")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response getListsByUser(@QueryParam("user_id") Long userId) {
        List<TodoList> todos = dbMapper.findListsByUserId(userId);
        return Response.ok(todos).build();
    }

    @POST
    @Path("/item")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response createOrEditItem(Item item) {
        Long itemId = dbMapper.insertOrUpdateItem(item);
        return Response.ok(itemId).build();
    }

    @GET
    @Path("/items_for_list")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response getItemsForList(@QueryParam("list_id")Long listId) {
        List<Item> itemsForList = dbMapper.findItemsForList(listId);
        return Response.ok(itemsForList).build();
    }


    @GET
    @Path("/delete_item")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response deleteItem(@QueryParam("item_id")Long id) {
        dbMapper.deleteItemById(id);
        return Response.ok().build();
    }

    @GET
    @Path("/clear_list")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response clearList(@QueryParam("list_id") Long listId) {
        dbMapper.deleteItemsForList(listId);
        return Response.ok().build();
    }

    @GET
    @Path("/delete_list")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response deleteList(@QueryParam("list_id") Long id) {
        dbMapper.deleteListById(id);
        return null;
    }


}
