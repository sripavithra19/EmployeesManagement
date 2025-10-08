package com.ciberspring.api.employee_mgmt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class OAuth2SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                // Use group-based authorization
                .requestMatchers("/employees/**").hasAuthority("HR_EMPLOYEES_ACCESS")
                .anyRequest().denyAll()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            );
        return http.build();
    }

	private JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
		converter.setJwtGrantedAuthoritiesConverter(this::extractAuthorities);
		return converter;
	}

	private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
		Collection<GrantedAuthority> authorities = new ArrayList<>();

		// Extract groups from the token
		List<String> groups = jwt.getClaimAsStringList("groups");
		System.out.println("=== EXTRACTING AUTHORITIES FROM JWT ===");
		System.out.println("All claims: " + jwt.getClaims());
		System.out.println("Groups found: " + groups);

		if (groups != null) {
			// Convert group names to authorities
			groups.stream().map(group -> new SimpleGrantedAuthority(group)).forEach(authorities::add);
		}

		// Extract scopes and add them as authorities
		List<String> scopes = jwt.getClaimAsStringList("scp");
		System.out.println("Scopes found: " + scopes);

		if (scopes != null) {
			scopes.stream().map(scope -> new SimpleGrantedAuthority("SCOPE_" + scope)).forEach(authorities::add);
		}

		System.out.println("Final authorities: " + authorities);
		return authorities;
	}
}