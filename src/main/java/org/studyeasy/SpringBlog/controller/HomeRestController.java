package org.studyeasy.SpringBlog.controller;

// import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
// import org.studyeasy.SpringBlog.models.Post;
// import org.studyeasy.SpringBlog.services.PostService;

@RestController
@RequestMapping("/api/v1")
public class HomeRestController {

    // @Autowired
    // private PostService postService;

    private final Logger logger = LoggerFactory.getLogger(HomeRestController.class);

    @GetMapping("/")
    public /*List<Post>*/ String home() {
        logger.error("This is a test error log");
        return "sample response";
        // return postService.findAll();
    }
}
