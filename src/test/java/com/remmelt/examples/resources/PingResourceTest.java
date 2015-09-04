package com.remmelt.examples.resources;

import com.google.common.io.Resources;
import com.remmelt.examples.OAuth2Application;
import com.remmelt.examples.Oauth2Configuration;
import io.dropwizard.testing.junit.DropwizardAppRule;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.util.HashMap;
import java.util.Optional;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static org.fest.assertions.Assertions.assertThat;

@Slf4j
public class PingResourceTest {
	@ClassRule
	public static final DropwizardAppRule<Oauth2Configuration> RULE = new DropwizardAppRule<>(OAuth2Application.class, resourceFilePath("config.yml"));

	public static final String SECRET_STRING = "secret_string";
	private String uri;

	@Before
	public void setUri() {
		uri = String.format("http://localhost:%d", RULE.getLocalPort());
	}

	@Test
	public void testGetPingUnauthorised() {
		Response response = get("/ping", Optional.empty());
		assertThat(response.getStatus()).isEqualTo(HTTP_UNAUTHORIZED);
	}

	@Test
	public void testGetPingOldDateUnauthorised() {
		final OAuth2Resource oAuth2Resource = new OAuth2Resource(SECRET_STRING.getBytes());
		Response response = get("/ping", Optional.of(oAuth2Resource.signedToken(Optional.of(new DateTime().plusWeeks(1)))));
		System.out.println(response.readEntity(String.class));
		assertThat(response.getStatus()).isEqualTo(HTTP_UNAUTHORIZED);
	}

	@Test
	public void testGetPingAuthorised() {
		String accessToken = authenticate("username", "password", 1, Optional.empty());
		Response response = get("/ping", Optional.of(accessToken));
		assertThat(response.getStatus()).isEqualTo(HTTP_OK);
	}

	@Test
	public void testGetTokenWrongPasswordBadRequest() {
		String accessToken = authenticate("username", "rabbits", 1, Optional.of(HTTP_BAD_REQUEST));
		assertThat(accessToken).isNullOrEmpty();
	}

	protected Response get(final String endPoint, final Optional<String> accessToken) {
		Client client = ClientBuilder.newClient();

		WebTarget webTarget = client.target(uri).path(endPoint);
		Invocation.Builder request = webTarget.request();

		if (accessToken.isPresent()) {
			request.header("Authorization", String.format("Bearer %s", accessToken.get()));
		}

		return request.get();
	}

	protected String authenticate(final String username, final String password, final int clientId, final Optional<Integer> expectedStatusCode) {
		Form form = new Form();
		form.param("grant_type", "password");
		form.param("username", username);
		form.param("password", password);
		form.param("client_id", Integer.toString(clientId));
		Entity<Form> entity = Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE);

		Response response = ClientBuilder.newClient()
				.target(uri)
				.path("/token")
				.request(MediaType.APPLICATION_JSON_TYPE)
				.post(entity);

		assertThat(response.getStatus()).isEqualTo(expectedStatusCode.orElse(HTTP_OK));

		return (String) response.readEntity(HashMap.class).get("token");
	}

	protected static String resourceFilePath(final String resourceClassPathLocation) {
		try {
			return new File(Resources.getResource(resourceClassPathLocation).toURI()).getAbsolutePath();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
