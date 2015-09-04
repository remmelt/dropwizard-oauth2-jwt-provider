package com.remmelt.examples.resources;

import com.github.toastshaman.dropwizard.auth.jwt.hmac.HmacSHA512Signer;
import com.github.toastshaman.dropwizard.auth.jwt.model.JsonWebToken;
import com.github.toastshaman.dropwizard.auth.jwt.model.JsonWebTokenClaim;
import com.github.toastshaman.dropwizard.auth.jwt.model.JsonWebTokenHeader;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

import static java.util.Collections.singletonMap;

@Slf4j
@Path("/token")
@Produces(MediaType.APPLICATION_JSON)
public class OAuth2Resource {

	private final byte[] tokenSecret;

	public OAuth2Resource(byte[] tokenSecret) {
		this.tokenSecret = tokenSecret;
	}

	protected String signedToken(final Optional<DateTime> issuedAt) {
		final JsonWebToken token = JsonWebToken.builder()
				.header(JsonWebTokenHeader.HS512())
				.claim(JsonWebTokenClaim.builder()
						.subject("1") // put a real userId here
						.issuedAt(issuedAt.orElse(new DateTime()))
						.expiration(new DateTime().plusDays(1))
						.build())
				.build();
		return new HmacSHA512Signer(tokenSecret).sign(token);

	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response postForToken(
			@FormParam("grant_type") String grantType,
			@FormParam("username") String username,
			@FormParam("password") String password,
			@FormParam("client_id") String clientId
	) {
		log.info("Token request; grant_type: {} client_id: {} username: {}", grantType, clientId, username);

		// check username and password are valid, find the corresponding userId
		if (!(clientId.equals("1") && username.equals("username") && password.equals("password"))) {
			return Response.status(Response.Status.BAD_REQUEST).entity(singletonMap("error", "invalid_grant")).build();
		}

		return Response.ok(singletonMap("token", signedToken(Optional.empty()))).build();
	}

}
