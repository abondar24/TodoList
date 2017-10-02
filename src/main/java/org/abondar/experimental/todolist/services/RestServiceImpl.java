package org.abondar.experimental.todolist.services;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;
import org.abondar.experimental.todolist.datamodel.Item;
import org.abondar.experimental.todolist.datamodel.TodoList;
import org.abondar.experimental.todolist.datamodel.User;
import org.abondar.experimental.todolist.mappers.DatabaseMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;

@Api(value = "/", tags = "TodoAPI", description = "API to add and retrieve data from database")
@Path("/")
@SwaggerDefinition(
        info = @Info(
                description = "Another TodoList application with Spring Boot and Swagger",
                version = "V1.0",
                title = "TodoList Application",
                contact = @Contact(
                        name = "Alex Bondar",
                        email = "desertalex@icloud.com",
                        url = "https://github.com/abondar24"

                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "http://www.apache.org/licenses/LICENSE-2.0"
                )
        ),
        consumes = {"application/json"},
        produces = {"application/json"},
        schemes = {SwaggerDefinition.Scheme.HTTP, SwaggerDefinition.Scheme.HTTPS},
        tags = {
                @Tag(name = "TodoAPI", description = "Todo API methods")
        }
)
public class RestServiceImpl implements RestService {
    private final Logger logger = LoggerFactory.getLogger(RestServiceImpl.class);

    @Autowired
    private DatabaseMapper dbMapper;

    private ObjectMapper objectMapper = new ObjectMapper();

    @GET
    @Path("/echo")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            tags = {"TodoAPI"},
            value = "Check service status",
            notes = "Returns if service is up")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Server is up")})
    @Override
    public Response get() {
        return Response.ok().build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/log_in")
    @ApiOperation(
            tags = {"TodoAPI"},
            value = "User log in",
            notes = "Creates a new user or logs in an exising one",
            consumes = "application/x-www-urlformEncoded",
            produces = "application/json")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "User id"),
                          @ApiResponse(code = 302, message = "Username exists")})
    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true)
    @Override
    public Response createUser(@ApiParam(value = "Username", required = true)
                               @FormParam("username") String username,
                               @ApiParam(value = "Password", required = true)
                               @FormParam("password") String password) {
        User user = dbMapper.findUserByName(username);
        if (user != null) {
            return Response.status(302).build();
        } else {
            user = new User(username, password);
            dbMapper.insertOrUpdateUser(user);
            logger.info("user created: " + user.toString());
            return Response.ok(user.getId()).build();
        }
    }


    @POST
    @Path("/list")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            tags = {"TodoAPI"},
            value = "Create or edit a list",
            notes = "Creates a new list or edits an existing one",
            consumes = "application/json",
            produces = "application/json")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "List id")})
    @Override
    public Response createOrEditList(@ApiParam(value = "List data", required = true) TodoList list) {
        dbMapper.insertOrUpdateList(list);
        logger.info("list added: " + list.toString());
        return Response.ok(list.getId()).build();
    }

    @GET
    @Path("/list_by_user_id")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            tags = {"TodoAPI"},
            value = "Find all lists by userId",
            produces = "application/json",
            response = TodoList.class,
            responseContainer = "List")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "List of todo lists"),
            @ApiResponse(code = 404, message = "User not found")
    })
    @Override
    public Response getListsByUser(@ApiParam(value = "User ID", required = true)
                                   @QueryParam("user_id") Long userId) {
        User user = dbMapper.findUserById(userId);

        if (user != null) {
            List<TodoList> todos = dbMapper.findListsByUserId(userId);
            logger.info(todos.toString());
            return Response.ok(todos).build();
        } else {
            logger.info("User not found");
            return Response.status(404).build();
        }
    }

    @POST
    @Path("/item")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            tags = {"TodoAPI"},
            value = "Create or edit an item in list",
            notes = "Creates a new item or edits an existing one",
            consumes = "application/json",
            produces = "application/json")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Item id")})
    @Override
    public Response createOrEditItem(@ApiParam(value = "Item data", required = true) Item item) {
        dbMapper.insertOrUpdateItem(item);
        logger.info(item.toString());
        return Response.ok(item.getId()).build();
    }

    @GET
    @Path("/items_for_list")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            tags = {"TodoAPI"},
            value = "Find all items for list",
            produces = "application/json",
            response = Item.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "List of items"),
            @ApiResponse(code = 404, message = "TodoList not found")
    })
    @Override
    public Response getItemsForList(@ApiParam(value = "List ID", required = true)
                                    @QueryParam("list_id") Long listId) {
        TodoList list = dbMapper.findListById(listId);
        if (list != null) {
            List<Item> itemsForList = dbMapper.findItemsForList(listId);
            logger.info(itemsForList.toString());
            return Response.ok(itemsForList).build();
        } else {
            logger.info("List not found");
            return Response.status(404).build();
        }

    }

    @GET
    @Path("/delete_user")
    @ApiOperation(
            tags = {"TodoAPI"},
            value = "Delete selected user")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "")})
    @Override
    public Response deleteUser(@ApiParam(value = "User ID", required = true)
                               @QueryParam("user_id") Long id) {
        dbMapper.deleteUserById(id);
        logger.info("item deleted");
        return Response.ok().build();
    }

    @GET
    @Path("/delete_item")
    @ApiOperation(
            tags = {"TodoAPI"},
            value = "Delete selected item")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "")})
    @Override
    public Response deleteItem(@ApiParam(value = "Item ID", required = true)
                               @QueryParam("item_id") Long id) {
        dbMapper.deleteItemById(id);
        logger.info("item deleted");
        return Response.ok().build();
    }

    @GET
    @Path("/clear_list")
    @ApiOperation(
            tags = {"TodoAPI"},
            value = "Clear selected list")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "")})
    @Override
    public Response clearList(@ApiParam(value = "List ID", required = true)
                              @QueryParam("list_id") Long listId) {
        dbMapper.deleteItemsForList(listId);
        logger.info("list cleared");
        return Response.ok().build();
    }

    @GET
    @Path("/delete_list")
    @ApiOperation(
            tags = {"TodoAPI"},
            value = "Delete selected list")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "")})
    @Override
    public Response deleteList(@ApiParam(value = "List ID", required = true)
                               @QueryParam("list_id") Long id) {
        dbMapper.deleteListById(id);
        logger.info("list deleted");
        return Response.ok().build();
    }


}
