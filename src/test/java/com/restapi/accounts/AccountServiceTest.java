package com.restapi.accounts;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AccountServiceTest {

    @Autowired AccountService accountService;
    @Autowired AccountRepository accountRepository;
    @Autowired PasswordEncoder passwordEncoder;

    @Test
    void findByUsername() throws Exception {
        //given
        String username = "test@test.com";
        String password = "seungho";

        Account account = Account.builder()
                .email(username)
                .password(password)
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();

        accountService.saveAccount(account);

        //when
        UserDetailsService userDetailsService = accountService;
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        //then
        assertTrue(passwordEncoder.matches(password,userDetails.getPassword()));
    }

    @Test
    void findByUsernameFail() throws Exception {
        //given
        String username = "random@test.com";

        //when
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> accountService.loadUserByUsername(username));

        //then
        assertEquals(username, exception.getMessage());
    }
}