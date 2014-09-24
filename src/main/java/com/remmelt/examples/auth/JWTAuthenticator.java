package com.remmelt.examples.auth;

import com.github.toastshaman.dropwizard.auth.jwt.JsonWebTokenValidator;
import com.github.toastshaman.dropwizard.auth.jwt.exceptions.TokenExpiredException;
import com.github.toastshaman.dropwizard.auth.jwt.model.JsonWebToken;
import com.google.common.base.Optional;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import lombok.AllArgsConstructor;

/**
 * This class only provides extra validation on the token, like expiry etc.
 * It has already been verified by the JWT framework.
 */
@AllArgsConstructor
public class JWTAuthenticator implements Authenticator<JsonWebToken, Long> {
	final private JsonWebTokenValidator expiryValidator;

	@Override
	public Optional<Long> authenticate(JsonWebToken token) throws AuthenticationException {
		try {
			expiryValidator.validate(token);
		} catch (TokenExpiredException e) {
			return Optional.absent();
		}

		Long userId;
		try {
			userId = Long.valueOf((String) token.claim().getParameter("principal"));
		} catch (NumberFormatException e) {
			throw new AuthenticationException("Principal is not a userId, cannot proceed.");
		}

		return Optional.fromNullable(userId);
	}
}
