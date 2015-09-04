package com.remmelt.examples.auth;

import com.github.toastshaman.dropwizard.auth.jwt.exceptions.JsonWebTokenException;
import com.github.toastshaman.dropwizard.auth.jwt.model.JsonWebToken;
import com.github.toastshaman.dropwizard.auth.jwt.validator.ExpiryValidator;
import com.google.common.base.Optional;
import io.dropwizard.auth.Authenticator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * This class only provides extra validation on the token, like expiry etc.
 * It has already been verified by the JWT framework.
 */
@AllArgsConstructor
@Slf4j
public class JWTAuthenticator implements Authenticator<JsonWebToken, User> {
	@Override
	public Optional<User> authenticate(JsonWebToken token) {
		new ExpiryValidator().validate(token);

		Integer claimedId;
		try {
			claimedId = Integer.parseInt(token.claim().subject());
		} catch (NumberFormatException e) {
			throw new JsonWebTokenException("no access", e);
		}

		User user = new User(claimedId.toString());
		// Or something like
		// User user = db.getUser(claimedId);

		return Optional.fromNullable(user);
	}
}
