
package com.sample;

import javax.inject.Singleton;
import org.apache.camel.builder.RouteBuilder;

@Singleton
public class Routes extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        from("direct:start").setBody().simple("Hello World Camel fired at:").bean(new MyBean(), "callDrools()");

    }
}
