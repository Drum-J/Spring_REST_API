package com.restapi.config;

import com.restapi.accounts.Account;
import com.restapi.accounts.AccountRole;
import com.restapi.accounts.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class AppConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public ApplicationRunner applicationRunner() {
        return new ApplicationRunner() {

            @Autowired AccountService accountService;

            @Override
            public void run(ApplicationArguments args) throws Exception {
                Account testAccount = Account.builder()
                        .email("test2@test.com")
                        .password("test123")
                        .roles(Set.of(AccountRole.ADMIN,AccountRole.USER))
                        .build();

                accountService.saveAccount(testAccount);
            }
        };
    }
}
