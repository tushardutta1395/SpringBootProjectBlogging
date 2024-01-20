package org.studyeasy.SpringBlog.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.studyeasy.SpringBlog.models.Post;
import org.studyeasy.SpringBlog.repository.PostRepository;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    public Optional<Post> getById(final Long id) {
        return postRepository.findById(id);
    }

    public List<Post> findAll() {
        return postRepository.findAll();
    }

    public Page<Post> findAll(final Integer offset, final Integer pageSize, final String field) {
        return postRepository.findAll(PageRequest.of(offset, pageSize).withSort(Direction.ASC, field));
    }

    public void delete(final Post post) {
        postRepository.delete(post);
    }

    public void save(final Post post) {
        if (post.getId() == null) {
            post.setCreatedAt(LocalDateTime.now());
        }
        post.setUpdatedAt(LocalDateTime.now());
        postRepository.save(post);
    }
}
