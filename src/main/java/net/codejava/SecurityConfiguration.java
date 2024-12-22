package net.codejava;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.ignoringRequestMatchers("/logout")) // Ignore CSRF for logout endpoint
				.authorizeHttpRequests(auth -> auth.requestMatchers("/welcome", "/css/**", "/js/**", "/img/**", "/sound/**", "/health")
						.permitAll().anyRequest().authenticated())
				.oauth2Login(oauth2 -> oauth2.loginPage("/welcome").defaultSuccessUrl("/", true))
				.logout(logout -> logout.logoutUrl("/logout") // URL to trigger logout
						.logoutSuccessUrl("/welcome") // Redirect after logout
						.invalidateHttpSession(true) // Invalidate session
						.deleteCookies("JSESSIONID") // Ensure all cookies are deleted
				);

		return http.build();
	}
}