package com.restapi.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class AuthServerConfig {

    /**
     * TODO
     *  현재 이 코드는 제대로 작성된 코드가 아님.
     *  강의와 버전이 많이 달라서 OAuth2 서버 만드는 코드 최신화에 실패했음.
     */


    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Bean
    public void authorizationFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient registeredClient = RegisteredClient
                .withId("myApp")
                .authorizationGrantTypes(type -> {
                    type.add(AuthorizationGrantType.PASSWORD);
                    type.add(AuthorizationGrantType.REFRESH_TOKEN);
                })
                .scopes(scope -> {
                    scope.add("read");
                    scope.add("write");
                })
                .clientSecret(passwordEncoder.encode("pass"))
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofSeconds(60 * 10))
                        .refreshTokenTimeToLive(Duration.ofSeconds(6 * 60 * 10))
                        .build())
                .build();

        return new InMemoryRegisteredClientRepository(registeredClient);
    }
}
