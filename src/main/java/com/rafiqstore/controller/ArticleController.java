package com.rafiqstore.controller;


import com.rafiqstore.dto.frontUser.ArticleRequestDTO;
import com.rafiqstore.dto.frontUser.ArticleResponseDTO;
import com.rafiqstore.services.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    @Autowired
    private ArticleService articleService; // Use the interface

    @PostMapping
    public ArticleResponseDTO addArticle(@RequestBody ArticleRequestDTO articleRequest) {
        return articleService.addArticle(articleRequest);
    }

    @GetMapping("/menu/{menuId}")
    public List<ArticleResponseDTO> getArticlesByMenuId(@PathVariable Long menuId) {
        return articleService.getArticlesByMenuId(menuId);
    }

    @PutMapping("/{id}")
    public ArticleResponseDTO updateArticle(@PathVariable Long id, @RequestBody ArticleRequestDTO articleRequest) {
        return articleService.updateArticle(id, articleRequest);
    }

    @DeleteMapping("/{id}")
    public void deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
    }
}