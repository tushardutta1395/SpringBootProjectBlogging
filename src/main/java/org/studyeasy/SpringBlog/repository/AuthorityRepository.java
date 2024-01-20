package org.studyeasy.SpringBlog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.studyeasy.SpringBlog.models.Authority;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Long> {
}
