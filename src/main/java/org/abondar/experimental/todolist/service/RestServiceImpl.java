package org.abondar.experimental.todolist.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;
import org.abondar.experimental.todolist.datamodel.Item;
import org.abondar.experimental.todolist.datamodel.TodoList;
import org.abondar.experimental.todolist.datamodel.User;
import org.abondar.experimental.todolist.mapper.DatabaseMapper;

import static org.abondar.experimental.todolist.security.PasswordUtil.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Date;
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

    private AuthService authService;

    public RestServiceImpl(AuthService authService) {
        this.authService = authService;
    }

    @Autowired
    private DatabaseMapper dbMapper;

    private ObjectMapper objectMapper = new ObjectMapper();

    @GET
    @Path("/echo")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    @ApiOperation(
            tags = {"TodoAPI"},
            value = "Check service status",
            notes = "Returns if service is up")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Server is up")})
    @Override
    public Response echo() {
        return Response.ok().build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/create_user")
    @PermitAll
    @ApiOperation(
            tags = {"TodoAPI"},
            value = "Create user",
            notes = "Creates a new user",
            consumes = "application/x-www-urlformencoded",
            produces = "application/json")
    @ApiResponses(value = {@ApiResponse(code = 202, message = "User id"),
            @ApiResponse(code = 302, message = "Username exists")})
    @Override
    public Response createUser(@ApiParam(value = "Username", required = true)
                               @FormParam("username") String username,
                               @ApiParam(value = "Password", required = true)
                               @FormParam("password") String password) throws CannotPerformOperationException,IOException {
        User user = dbMapper.findUserByName(username);
        if (user != null) {
            logger.info("User already exists");
            return Response.status(Response.Status.FOUND).build();
        }

        String pwdHash = createHash(password);
        user = new User(username, pwdHash);
        dbMapper.insertOrUpdateUser(user);
        logger.info("User created: " + user.toString());

        NewCookie authCookie = new NewCookie(new Cookie("X-JWT-AUTH",
                authService.createToken(username,"borscht",null),"/",null),
                "JWT token", 6000,new Date((new Date()).getTime() + 60000), false,false);

        return Response.status(Response.Status.ACCEPTED).cookie(authCookie)
                .entity(objectMapper.writeValueAsString(user.getId())).build();

    }


    @GET
    @Path("/find_user")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            tags = {"TodoAPI"},
            value = "Find user",
            notes = "Finds a user by username",
            produces = "application/json")
    @Override
    public Response findUser(@ApiParam(value = "username", required = true) @QueryParam("username") String username) throws IOException {
        User user = dbMapper.findUserByName(username);
        if (user == null) {
            logger.info("User not found");
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        logger.info("User found");
        return Response.ok(user.getId()).build();
    }


    @POST
    @Path("/login_user")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    @ApiOperation(
            tags = {"TodoAPI"},
            value = "Log in a user",
            consumes = "application/x-www-urlformencoded",
            produces = "application/json")
    @ApiResponses(value = {@ApiResponse(code = 202, message = "User id"),
            @ApiResponse(code = 401, message = "Wrong credentials")})
    @Override
    public Response loginUser(
            @ApiParam(value = "username", required = true)
            @FormParam("username") String username,
            @ApiParam(value = "password", required = true)
            @FormParam("password") String password) throws InvalidHashException, CannotPerformOperationException, IOException {
        User user = dbMapper.findUserByName(username);
        if (user == null) {
            logger.info("User not found");
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        logger.info("User found: " + user.toString());
        if (!verifyPassword(password, user.getPassword())) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        NewCookie authCookie = new NewCookie(new Cookie("X-JWT-AUTH",
                authService.authorizeUser(user,password),"/",null),
                "JWT token", 6000,new Date((new Date()).getTime() + 60000), false,false);

        logger.info("User has logged in");
        return Response.status(Response.Status.ACCEPTED).cookie(authCookie)
                .entity(objectMapper.writeValueAsString(user.getId())).build();
    }


    @GET
    @Path("/logout_user")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            tags = {"TodoAPI"},
            value = "Log out a user",
            produces = "application/json")
    @ApiResponses(value = {@ApiResponse(code = 202, message = "User id"),
            @ApiResponse(code = 404, message = "User not found")})
    @Override
    public Response logoutUser(
            @ApiParam(value = "user_id", required = true)
            @QueryParam("user_id") Long userId) throws IOException {
        User user = dbMapper.findUserById(userId);
        if (user == null) {
            logger.info("User not found");
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        logger.info("User found: " + user.toString());

        NewCookie authCookie = new NewCookie(new Cookie("X-JWT-AUTH", "","/",""),
                "JWT token", 6000, false);
        logger.info("User has logged out");
        return Response.ok().cookie(authCookie).build();
    }

    @POST
    @Path("/create_update_list")
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
    public Response createOrEditList(@ApiParam(value = "List data", required = true) TodoList list) throws IOException {
        dbMapper.insertOrUpdateList(list);
        logger.info("list added: " + list.toString());
        return Response.ok(objectMapper.writeValueAsString(list.getId())).build();
    }

    @GET
    @Path("/get_lists_by_user_id")
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
        if (user == null) {
            logger.info("User not found");
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        logger.info("User found: "+user.toString());
        List<TodoList> todos = dbMapper.findListsByUserId(userId);
        logger.info("List added: "+todos.toString());
        return Response.ok(todos).build();
    }

    @POST
    @Path("/create_update_item")
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
    public Response createOrEditItem(@ApiParam(value = "Item data", required = true) Item item) throws IOException {

        dbMapper.insertOrUpdateItem(item);
        logger.info("Item added: "+item.toString());
        return  Response.ok(objectMapper.writeValueAsString(item.getId())).build();
    }

    @GET
    @Path("/get_items_for_list")
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
        if (list == null) {
            logger.info("List not found");
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        logger.info("List found: "+list.toString());

        List<Item> itemsForList = dbMapper.findItemsForList(listId);
        logger.info("Items: "+itemsForList.toString());
        return Response.ok(itemsForList).build();


    }

    @POST
    @Path("/get_items_for_lists")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(
            tags = {"TodoAPI"},
            value = "Find all items for lists",
            produces = "application/json",
            response = Item.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Lists of items")
    })
    @Override
    public Response getItemsForLists(List<Long> listIds) {

        List<Item> itemsForLists = dbMapper.findItemsForLists(listIds);
        logger.info("Items: "+itemsForLists.toString());
        return Response.ok(itemsForLists).build();


    }

    @GET
    @Path("/delete_user")
    @ApiOperation(
            tags = {"TodoAPI"},
            value = "Delete selected user")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "User deleted")})
    @Override
    public Response deleteUser(@ApiParam(value = "User ID", required = true)
                               @QueryParam("user_id") Long id) {

        dbMapper.findListsByUserId(id).forEach(l-> dbMapper.deleteItemsForList(l.getId()));
        dbMapper.deleteListsForUser(id);
        dbMapper.deleteUserById(id);
        logger.info("user deleted");
        return Response.ok().build();
    }

    @GET
    @Path("/delete_item")
    @ApiOperation(
            tags = {"TodoAPI"},
            value = "Delete selected item")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Item deleted")})
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
    @ApiResponses(value = {@ApiResponse(code = 200, message = "List cleared")})
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
    @ApiResponses(value = {@ApiResponse(code = 200, message = "List deleted")})
    @Override
    public Response deleteList(@ApiParam(value = "List ID", required = true)
                               @QueryParam("list_id") Long id) {
        dbMapper.deleteItemsForList(id);
        dbMapper.deleteListById(id);
        logger.info("list deleted");
        return Response.ok().build();
    }


    @GET
    @Path("/delete_lists_for_user")
    @ApiOperation(
            tags = {"TodoAPI"},
            value = "Delete lists for user")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "List deleted")})
    @Override
    public Response deleteListsForUser(@ApiParam(value = "User ID", required = true)
                               @QueryParam("user_id") Long id) {
        dbMapper.deleteListsForUser(id);
        logger.info("lists deleted");
        return Response.ok().build();
    }

}
