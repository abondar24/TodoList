package org.abondar.experimental.todolist.services;


import io.swagger.annotations.*;
import org.abondar.experimental.todolist.datamodel.Item;
import org.abondar.experimental.todolist.datamodel.TodoList;
import org.abondar.experimental.todolist.datamodel.User;
import org.abondar.experimental.todolist.mappers.DatabaseMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;

@Service
@Api(value = "/", tags = {"Todo List API"}, description = "API to add and retrieve data from database")
@Path("/")
public class RestServiceImpl implements RestService {
    private final Logger logger = LoggerFactory.getLogger(RestServiceImpl.class);

    @Autowired
    private DatabaseMapper dbMapper;

    @GET
    @Path("/echo")
    @ApiOperation(value = "Check service status",
            notes = "Returns if service is up",
            httpMethod = "GET")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Server is up")})
    @Override
    public Response get() throws IOException {
        return Response.ok("Server is up").build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/log_in")
    @ApiOperation(
            value = "User log in",
            notes = "Creates a new user or logs in an exising one",
            httpMethod = "POST",
            consumes = "application/json",
            produces = "application/json")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "User id")})
    @Override
    public Response logIn(@ApiParam("User data") User user) {
        dbMapper.insertOrUpdateUser(user);
        logger.info("logged in: " + user.toString());
        return Response.ok(user.getId()).build();
    }

    @POST
    @Path("/list")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Create or edit a list",
            notes = "Creates a new list or edits an existing one",
            httpMethod = "POST",
            consumes = "application/json",
            produces = "application/json")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "List id")})
    @Override
    public Response createOrEditList(@ApiParam("List data") TodoList list) {
        dbMapper.insertOrUpdateList(list);
        logger.info("list added: " + list.toString());
        return Response.ok(list.getId()).build();
    }

    @GET
    @Path("/list_by_user_id")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Find all lists by userId",
            httpMethod = "GET",
            produces = "application/json",
            response = TodoList.class)
    @Override
    public Response getListsByUser(@ApiParam("User ID")
                                   @QueryParam("user_id") Long userId) {
        List<TodoList> todos = dbMapper.findListsByUserId(userId);
        logger.info(todos.toString());
        return Response.ok(todos).build();
    }

    @POST
    @Path("/item")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Create or edit an item in list",
            notes = "Creates a new item or edits an existing one",
            httpMethod = "POST",
            consumes = "application/json",
            produces = "application/json")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Item id")})
    @Override
    public Response createOrEditItem(@ApiParam("Item data") Item item) {
        dbMapper.insertOrUpdateItem(item);
        logger.info(item.toString());
        return Response.ok(item.getId()).build();
    }

    @GET
    @Path("/items_for_list")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Find all items for list",
            httpMethod = "GET",
            produces = "application/json",
            response = Item.class)
    @Override
    public Response getItemsForList(@ApiParam("List ID")
                                    @QueryParam("list_id") Long listId) {
        List<Item> itemsForList = dbMapper.findItemsForList(listId);
        logger.info(itemsForList.toString());
        return Response.ok(itemsForList).build();
    }


    @GET
    @Path("/delete_item")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Delete selected tem",
            httpMethod = "GET",
            produces = "application/json")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Item deleted")})
    @Override
    public Response deleteItem(@ApiParam("Item ID")
                               @QueryParam("item_id") Long id) {
        dbMapper.deleteItemById(id);
        logger.info("item deleted");
        return Response.ok("Item deleted").build();
    }

    @GET
    @Path("/clear_list")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Clear selected list",
            httpMethod = "GET",
            produces = "application/json")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "List cleared")})
    @Override
    public Response clearList(@ApiParam("List ID")
                              @QueryParam("list_id") Long listId) {
        dbMapper.deleteItemsForList(listId);
        logger.info("list cleared");
        return Response.ok("List cleared").build();
    }

    @GET
    @Path("/delete_list")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Delete selected list",
            httpMethod = "GET",
            produces = "application/json")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "List deleted")})
    @Override
    public Response deleteList(@ApiParam("List ID")
                               @QueryParam("list_id") Long id) {
        dbMapper.deleteListById(id);
        logger.info("list deleted");
        return Response.ok("List deleted").build();
    }


}
