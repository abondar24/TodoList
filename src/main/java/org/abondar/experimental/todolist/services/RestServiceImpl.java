package org.abondar.experimental.todolist.services;


import org.abondar.experimental.todolist.datamodel.Item;
import org.abondar.experimental.todolist.datamodel.TodoList;
import org.abondar.experimental.todolist.datamodel.User;
import org.abondar.experimental.todolist.mappers.DatabaseMapper;
import org.apache.cxf.jaxrs.model.wadl.Description;
import org.apache.cxf.jaxrs.model.wadl.Descriptions;
import org.apache.cxf.jaxrs.model.wadl.DocTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;

public class RestServiceImpl implements RestService {
    private final Logger logger = LoggerFactory.getLogger(RestServiceImpl.class);

    @Autowired
    private DatabaseMapper dbMapper;

    @GET
    @Path("/echo")
    @Override
    public Response get() throws IOException {
        //String resp = mapper.writeValueAsString("Server is up");
        return Response.ok("Server is up").build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/log_in")
    @Override
    public Response logIn(User user) {
        dbMapper.insertOrUpdateUser(user);
        logger.info("logged in: "+user.toString());
        return Response.ok(user.getId()).build();
    }

    @POST
    @Path("/list")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response createOrEditList(TodoList list) {
        dbMapper.insertOrUpdateList(list);
        logger.info("list added: "+list.toString());
        return Response.ok(list.getId()).build();
    }

    @GET
    @Path("/list_by_user_id")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response getListsByUser(@QueryParam("user_id") Long userId) {
        List<TodoList> todos = dbMapper.findListsByUserId(userId);
        logger.info(todos.toString());
        return Response.ok(todos).build();
    }

    @POST
    @Path("/item")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response createOrEditItem(Item item) {
        dbMapper.insertOrUpdateItem(item);
        logger.info(item.toString());
        return Response.ok(item.getId()).build();
    }

    @GET
    @Path("/items_for_list")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response getItemsForList(@QueryParam("list_id") Long listId) {
        List<Item> itemsForList = dbMapper.findItemsForList(listId);
        logger.info(itemsForList.toString());
        return Response.ok(itemsForList).build();
    }


    @GET
    @Path("/delete_item")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response deleteItem(@QueryParam("item_id") Long id) {
        dbMapper.deleteItemById(id);
        logger.info("item deleted");
        return Response.ok().build();
    }

    @GET
    @Path("/clear_list")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response clearList(@QueryParam("list_id") Long listId) {
        dbMapper.deleteItemsForList(listId);
        logger.info("list cleared");
        return Response.ok().build();
    }

    @GET
    @Path("/delete_list")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response deleteList(@QueryParam("list_id") Long id) {
        dbMapper.deleteListById(id);
        logger.info("list deleted");
        return null;
    }


}
