package org.studyeasy.SpringBlog.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.studyeasy.SpringBlog.models.Post;
import org.studyeasy.SpringBlog.services.AccountService;
import org.studyeasy.SpringBlog.services.PostService;

import jakarta.validation.Valid;

@Controller
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private AccountService accountService;

    @GetMapping("/posts/{id}")
    public String getPost(@PathVariable final Long id, final Model model, final Principal principal) {
        final var optionalPost = postService.getById(id);
        if (optionalPost.isPresent()) {
            final var post = optionalPost.get();
            model.addAttribute("post", post);
            // get username of current logged in session user
            // final var authUsername =
            // SecurityContextHolder.getContext().getAuthentication().getName();
            final var authUser = principal != null ? principal.getName() : "email";
            if (authUser.equals(post.getAccount().getEmail())) {
                model.addAttribute("isOwner", true);
            } else {
                model.addAttribute("isOwner", false);
            }
            return "post_views/post";
        } else {
            return "404";
        }
    }

    @GetMapping("/posts/add")
    @PreAuthorize("isAuthenticated()")
    public String addPost(final Model model, final Principal principal) {
        final var authUser = principal != null ? principal.getName() : "email";
        final var optionalAccount = accountService.findOneByEmail(authUser);
        if (optionalAccount.isPresent()) {
            final var post = new Post();
            post.setAccount(optionalAccount.get());
            model.addAttribute("post", post);
            return "post_views/post_add";
        } else {
            return "redirect:/";
        }
    }

    @PostMapping("/posts/add")
    @PreAuthorize("isAuthenticated()")
    public String addPostHandler(@Valid @ModelAttribute final Post post, final BindingResult bindingResult,
            final Principal principal) {
        if (bindingResult.hasErrors()) {
            return "post_views/post_add";
        }
        final var authUser = principal != null ? principal.getName() : "email";
        if (post.getAccount().getEmail().compareToIgnoreCase(authUser) < 0) {
            return "redirect:/?error";
        }
        postService.save(post);
        return "redirect:/posts/" + post.getId();
    }

    @GetMapping("/posts/{id}/edit")
    @PreAuthorize("isAuthenticated()")
    public String getPostForEdit(@PathVariable final Long id, final Model model) {
        final var optionalPost = postService.getById(id);
        if (optionalPost.isPresent()) {
            final var post = optionalPost.get();
            model.addAttribute("post", post);
            return "post_views/post_edit";
        } else {
            return "404";
        }
    }

    @PostMapping("/posts/{id}/edit")
    @PreAuthorize("isAuthenticated()")
    public String updatePost(@Valid @ModelAttribute final Post post, final BindingResult bindingResult,
            @PathVariable final long id) {
        if (bindingResult.hasErrors()) {
            return "post_views/post_edit";
        }
        final var optionalPost = postService.getById(id);
        if (optionalPost.isPresent()) {
            final var existingPost = optionalPost.get();
            existingPost.setTitle(post.getTitle());
            existingPost.setBody(post.getBody());
            postService.save(post);
        }
        return "redirect:/posts/" + post.getId();
    }

    @GetMapping("/posts/{id}/delete")
    @PreAuthorize("isAuthenticated()")
    public String deletePost(@PathVariable final Long id) {
        final var optionalPost = postService.getById(id);
        if (optionalPost.isPresent()) {
            final var post = optionalPost.get();
            postService.delete(post);
            return "redirect:/";
        } else {
            return "redirect:/?error";
        }
    }
}
