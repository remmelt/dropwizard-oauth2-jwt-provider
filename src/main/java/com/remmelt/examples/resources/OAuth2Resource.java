package com.remmelt.examples.resources;

import com.github.toastshaman.dropwizard.auth.jwt.hmac.HmacSHA512Signer;
import com.github.toastshaman.dropwizard.auth.jwt.model.JsonWebToken;
import com.github.toastshaman.dropwizard.auth.jwt.model.JsonWebTokenClaim;
import com.github.toastshaman.dropwizard.auth.jwt.model.JsonWebTokenHeader;
import org.joda.time.DateTime;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.Map;

@Path("/oauth2/token")
@Produces(MediaType.APPLICATION_JSON)
public class OAuth2Resource {

	private final byte[] tokenSecret;

	public OAuth2Resource(byte[] tokenSecret) {
		this.tokenSecret = tokenSecret;
	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Map<String, String> postForToken(
			@FormParam("grant_type") String grantType,
			@FormParam("username") String username,
			@FormParam("password") String password,
			@FormParam("client_id") String clientId
	) {
		final HmacSHA512Signer signer = new HmacSHA512Signer(tokenSecret);
		final JsonWebToken token = JsonWebToken.builder()
				.header(JsonWebTokenHeader.HS512())
				.claim(JsonWebTokenClaim.builder()
						.param("principal", "1") // put a real userId here
						.iat(new DateTime())
						.build())
				.build();
		final String signedToken = signer.sign(token);
		return Collections.singletonMap("token", signedToken);
	}

}
