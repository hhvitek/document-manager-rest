package hhvitek.documentmanager.configuration.authentication;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import hhvitek.documentmanager.error.ApiError;

/**
 * Customize authentication error response
 */
@Component
public class BasicEntryPoint extends BasicAuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.addHeader("WWW-Authenticate", "Basic realm=\"" + getRealmName() + "\"");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		PrintWriter writer = response.getWriter();
		writer.println(
				new ApiError(
						HttpServletResponse.SC_UNAUTHORIZED,
						authException.getMessage()
				).toJson()
		);
	}

	@Override
	public void afterPropertiesSet() {
		setRealmName("myApiRealm");
		super.afterPropertiesSet();
	}

}
