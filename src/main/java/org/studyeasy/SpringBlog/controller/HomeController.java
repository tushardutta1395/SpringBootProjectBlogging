package org.studyeasy.SpringBlog.controller;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.studyeasy.SpringBlog.services.PostService;

@Controller
public class HomeController {

    @Autowired
    private PostService postService;

    @GetMapping("/")
    public String home(final Model model,
            @RequestParam(required = false, name = "sort_by", defaultValue = "createdAt") final String sort_by,
            @RequestParam(required = false, name = "per_page", defaultValue = "2") final String per_page,
            @RequestParam(required = false, name = "page", defaultValue = "1") final String page) {

        final var posts_on_page = postService.findAll(Integer.parseInt(page) - 1, Integer.parseInt(per_page), sort_by);
        final var total_pages = posts_on_page.getTotalPages();
        final var pages = (total_pages > 0)
                ? IntStream.rangeClosed(0, total_pages - 1).boxed().toList()
                : null;
        final var links = new ArrayList<String>();
        if (pages != null) {
            for (final var link : pages) {
                final var active = (link == posts_on_page.getNumber()) ? "active" : "";
                final var _temp_link = "/?per_page=" + per_page + "&page=" + (link + 1) + "&sort_by=" + sort_by;
                links.add("<li class=\"page-item " + active + "\"><a href=\"" + _temp_link + "\" class='page-link'>"
                        + (link + 1) + "</a></li>");
            }
            model.addAttribute("links", links);
        }
        model.addAttribute("posts", posts_on_page);
        return "home_views/home";
    }
}
