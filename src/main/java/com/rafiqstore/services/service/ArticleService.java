package com.rafiqstore.services.service;

import com.rafiqstore.dto.frontUser.ArticleRequestDTO;
import com.rafiqstore.dto.frontUser.ArticleResponseDTO;

import java.util.List;

public interface ArticleService {
    ArticleResponseDTO addArticle(ArticleRequestDTO articleRequest);
    List<ArticleResponseDTO> getArticlesByMenuId(Long menuId);
    ArticleResponseDTO updateArticle(Long id, ArticleRequestDTO articleRequest);
    void deleteArticle(Long id);
}
