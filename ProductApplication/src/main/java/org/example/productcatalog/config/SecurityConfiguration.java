package org.example.productcatalog.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.HttpHeaders;
import org.example.productcatalog.exception.ApplicationException;
import org.example.productcatalog.mapper.UserMapper;
import org.example.productcatalog.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService service) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(service);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authenticationProvider);
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository, UserMapper userMapper) {
        return username -> userRepository.getByKey(username).map(userMapper::toDto)
                .orElseThrow(() -> new ApplicationException("User not found"));
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration().applyPermitDefaultValues();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowCredentials(true);

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain apiFilterChain(HttpSecurity http, AuthenticationManager manager, UserDetailsService service) {
        http.authorizeHttpRequests(requests -> requests
                        .requestMatchers("/api-docs", "/api-docs/**", "/openapi/**", "/proxy/**",
                                "/swagger-ui/**", "/swagger-initializer.js", "/openapi.json",
                                "/favicon.ico", "/error").permitAll().anyRequest().authenticated())
                .cors(configurer -> configurer.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(httpSecuritySessionManagementConfigurer ->
                        httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationManager(manager)
                .formLogin(AbstractHttpConfigurer::disable)
                .securityContext(securityContext ->
                        securityContext.securityContextRepository(new HttpSessionSecurityContextRepository()))
                .addFilterAt((request, response, filterChain) -> {
                    HttpServletRequest httpRequest = (HttpServletRequest) request;
                    String credentials = getCredentials(httpRequest);
                    String login = Optional.ofNullable(credentials)
                            .map(x -> x.substring(0, credentials.indexOf(':'))).orElse("");
                    String password = Optional.ofNullable(credentials)
                            .map(x -> x.substring(credentials.indexOf(':') + 1)).orElse("");
                    if (!login.isEmpty() && !password.isEmpty()) {
                        var authentication = new UsernamePasswordAuthenticationToken(login, password,
                                service.loadUserByUsername(login).getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                    filterChain.doFilter(request, response);
                }, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    private String getCredentials(HttpServletRequest request) {
        String headerAuth = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(StringUtils.hasText(headerAuth) && headerAuth.startsWith("Basic ")) {
            return new String(Base64.getDecoder().decode(headerAuth.substring(6)), StandardCharsets.UTF_8);
        }
        return null;
    }
}
