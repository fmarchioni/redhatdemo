package com.sample;

import java.io.InputStream;
import java.nio.charset.Charset;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.camel.Body;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;

@Path("/start")
public class Resource {

    @Inject
    ProducerTemplate producerTemplate;

    @GET
    public Response startCamel() {

        producerTemplate.requestBody("direct:start", null, String.class);
        return Response.ok("Camel Route started").build();
    }

}
