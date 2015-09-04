package com.remmelt.examples;

import io.dropwizard.Configuration;
import lombok.Getter;
import org.hibernate.validator.constraints.NotEmpty;

import java.io.UnsupportedEncodingException;

@Getter
public class Oauth2Configuration extends Configuration {
	@NotEmpty
	private String jwtTokenSecret = "dfwzsdzwh823zebdwdz772632gdsbd";

	@NotEmpty
	private String securityRealm = "realm";

	public byte[] getJwtTokenSecret() throws UnsupportedEncodingException {
		return jwtTokenSecret.getBytes("UTF-8");
	}
}
