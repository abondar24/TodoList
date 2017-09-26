package org.abondar.experimental.todolist.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

public class RestServiceImpl implements RestService{
    private static final Logger logger = LoggerFactory.getLogger(RestServiceImpl.class);
    private  ObjectMapper mapper = new ObjectMapper();

    @GET
    @Path("/echo")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get() {
        String resp ="";
        try {
            resp = mapper.writeValueAsString("Server is up");
        } catch (IOException ex){
            logger.error(ex.getMessage());
        }


        return Response.ok(resp).build();
    }
}
