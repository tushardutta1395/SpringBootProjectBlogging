package org.studyeasy.SpringBlog.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.studyeasy.SpringBlog.models.Authority;
import org.studyeasy.SpringBlog.repository.AuthorityRepository;

@Service
public class AuthorityService {

    @Autowired
    private AuthorityRepository authorityRepository;

    public void save(final Authority authority) {
        authorityRepository.save(authority);
    }

    public Optional<Authority> findById(final Long id) {
        return authorityRepository.findById(id);
    }
}
