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
import org.drools.compiler.kie.builder.impl.KieServicesImpl;
import org.drools.core.event.DefaultAgendaEventListener;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

@Path("/start")
public class Resource {

    @Inject
    ProducerTemplate producerTemplate;

   

    /* Start Drool Rule through a Camel Route */

    @GET
    public Response startCamel() {

        producerTemplate.requestBody("direct:start", null, String.class);
        return Response.ok("Camel Route started").build();
    }


     /* You can also test the Drools Rule from here:
     @GET
     @Path("/direct")
     public Response startRouteDirectly() {
 
         callDrools();
         return Response.ok("Rule started").build();
     }
   
    public void callDrools() {

        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId("com.sample", "basic-kjar", "1.0.0");
        KieContainer kContainer = ks.newKieContainer(releaseId);
        KieSession kSession = kContainer.newKieSession();

        try {
            Person john = new Person("john", 25);
            kSession.insert(john);
            kSession.fireAllRules();
        } finally {
            kSession.dispose();
        }
    }
     */
}
