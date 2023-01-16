package hhvitek.documentmanager.configuration.authentication;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * HTTP basic auth & InMemory user management
 */
@Configuration
public class BasicSecurityConfiguration {
	@Autowired
	private BasicEntryPoint entryPoint;

	@Bean
	public InMemoryUserDetailsManager userDetailsManager() {
		UserDetails user1 = User
				.withUsername("user1")
				.password(passwordEncoder().encode("passwd1"))
				.roles("USER")
				.build();

		UserDetails user2 = User
				.withUsername("user2")
				.password(passwordEncoder().encode("passwd2"))
				.roles("USER")
				.build();

		UserDetails userTest = User // for tests with @WithMockUser
				.withUsername("user")
				.password(passwordEncoder().encode("password"))
				.roles("USER")
				.build();

		return new InMemoryUserDetailsManager(Arrays.asList(user1, user2, userTest));
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf().disable()
			.authorizeRequests()
			.anyRequest().authenticated()
				.and()
			.httpBasic()
			.authenticationEntryPoint(entryPoint);
		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
