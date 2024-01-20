package org.studyeasy.SpringBlog.services;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.studyeasy.SpringBlog.models.Account;
import org.studyeasy.SpringBlog.repository.AccountRepository;
import org.studyeasy.SpringBlog.util.constants.Roles;

@Service
public class AccountService implements UserDetailsService {

    @Value("${photo_prefix}")
    private String photo_prefix;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void save(final Account account) {
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        if (account.getRole() == null) {
            account.setRole(Roles.USER.getRole());
        }
        if (account.getPhoto() == null) {
            final var path = photo_prefix.replace("**", "images/person.png");
            account.setPhoto(path);
        }
        accountRepository.save(account);
    }

    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
        final var optionalAccount = accountRepository.findOneByEmailIgnoreCase(email);
        if (optionalAccount.isEmpty()) {
            throw new UsernameNotFoundException("Account not found");
        }
        final var account = optionalAccount.get();
        final var grantedAuthority = new ArrayList<GrantedAuthority>();
        grantedAuthority.add(new SimpleGrantedAuthority(account.getRole()));

        for (final var _auth : account.getAuthorities()) {
            grantedAuthority.add(new SimpleGrantedAuthority(_auth.getName()));
        }

        return new User(account.getEmail(), account.getPassword(), grantedAuthority);
    }

    public Optional<Account> findOneByEmail(final String email) {
        return accountRepository.findOneByEmailIgnoreCase(email);
    }

    public Optional<Account> findById(final Long id) {
        return accountRepository.findById(id);
    }

    public Optional<Account> findByToken(final String token) {
        return accountRepository.findByToken(token);
    }
}
