import com.google.common.io.Resources;
import com.remmelt.examples.OAuth2Application;
import com.remmelt.examples.Oauth2Configuration;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import io.dropwizard.testing.junit.DropwizardAppRule;
import lombok.extern.slf4j.Slf4j;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.io.File;
import java.util.HashMap;

import static org.fest.assertions.Assertions.assertThat;

@Slf4j
public class PingResourceTest {
	@ClassRule
	public static final DropwizardAppRule<Oauth2Configuration> RULE =
			new DropwizardAppRule<>(OAuth2Application.class, resourceFilePath("config.yml"));

	@Test
	public void testGetPing() {
		String accessToken = authenticate("username", "password", 1);
		ClientResponse response = get("/ping", accessToken);

		assertThat(response.getEntity(String.class)).isEqualTo("{\"answer\": \"pong for user 1\"}");
		assertThat(response.getStatus()).isEqualTo(200);
	}

	protected ClientResponse get(final String endPoint, final String accessToken) {
		String uri = String.format("http://localhost:%d%s", RULE.getLocalPort(), endPoint);
		WebResource.Builder builder = new Client().resource(uri).getRequestBuilder();

		if (accessToken != null) {
			builder.header("Authorization", String.format("Bearer %s", accessToken));
		}

		return builder.get(ClientResponse.class);
	}

	protected String authenticate(final String username, final String password, final int clientId) {
		MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
		formData.add("grant_type", "password");
		formData.add("username", username);
		formData.add("password", password);
		formData.add("client_id", Integer.toString(clientId));
		Client client = new Client();

		ClientResponse response = client
				.resource(String.format("http://localhost:%d/oauth2/token", RULE.getLocalPort()))
				.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(ClientResponse.class, formData);
		assertThat(response.getStatus()).isEqualTo(200);
		return (String) response.getEntity(HashMap.class).get("token");
	}

	protected static String resourceFilePath(final String resourceClassPathLocation) {
		try {
			return new File(Resources.getResource(resourceClassPathLocation).toURI()).getAbsolutePath();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
