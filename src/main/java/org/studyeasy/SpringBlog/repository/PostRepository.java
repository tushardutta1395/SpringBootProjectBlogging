package org.studyeasy.SpringBlog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.studyeasy.SpringBlog.models.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
}
