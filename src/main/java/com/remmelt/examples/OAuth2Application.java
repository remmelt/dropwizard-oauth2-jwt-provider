package com.remmelt.examples;

import com.github.toastshaman.dropwizard.auth.jwt.JWTAuthProvider;
import com.github.toastshaman.dropwizard.auth.jwt.JsonWebTokenValidator;
import com.github.toastshaman.dropwizard.auth.jwt.hmac.HmacSHA512Verifier;
import com.github.toastshaman.dropwizard.auth.jwt.parser.DefaultJsonWebTokenParser;
import com.github.toastshaman.dropwizard.auth.jwt.validator.ExpiryValidator;
import com.remmelt.examples.auth.JWTAuthenticator;
import com.remmelt.examples.health.PingHealthCheck;
import com.remmelt.examples.resources.OAuth2Resource;
import com.remmelt.examples.resources.PingResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.joda.time.DateTimeZone;

public class OAuth2Application extends Application<Oauth2Configuration> {
	public static void main(String[] args) throws Exception {
		new OAuth2Application().run(args);
	}

	@Override
	public String getName() {
		return "oauth2-provider";
	}

	@Override
	public void initialize(Bootstrap<Oauth2Configuration> oauth2ConfigurationBootstrap) {
		DateTimeZone.setDefault(DateTimeZone.UTC);
	}

	@Override
	public void run(Oauth2Configuration configuration, Environment environment) throws Exception {
		final JsonWebTokenValidator expiryValidator = new ExpiryValidator();

		environment.jersey().register(
				new JWTAuthProvider<>(
						new JWTAuthenticator(expiryValidator),
						new DefaultJsonWebTokenParser(),
						new HmacSHA512Verifier(configuration.getJwtTokenSecret()),
						configuration.getSecurityRealm()
				)
		);
		environment.jersey().register(new OAuth2Resource(configuration.getJwtTokenSecret()));
		environment.jersey().register(new PingResource());
		environment.healthChecks().register("Ping health check", new PingHealthCheck());
	}
}
