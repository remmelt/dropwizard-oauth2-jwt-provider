package com.remmelt.examples.resources;

import com.codahale.metrics.annotation.Timed;
import com.remmelt.examples.auth.User;
import io.dropwizard.auth.Auth;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/ping")
@Produces(MediaType.APPLICATION_JSON)
public class PingResource {
	@GET
	@Timed
	public String pong(@Auth User user) {
		return String.format("{\"answer\": \"pong for user %s\"}", user.getName());
	}
}
