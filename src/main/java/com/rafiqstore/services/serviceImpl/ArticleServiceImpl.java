
package com.rafiqstore.services.serviceImpl;
import com.rafiqstore.dto.frontUser.ArticleRequestDTO;
import com.rafiqstore.dto.frontUser.ArticleResponseDTO;
import com.rafiqstore.entity.Article;
import com.rafiqstore.entity.Menu;
import com.rafiqstore.repository.ArticleRepository;
import com.rafiqstore.repository.MenuRepository;
import com.rafiqstore.services.service.ArticleService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private ModelMapper modelMapper;
    private static final Logger log = LoggerFactory.getLogger(ArticleServiceImpl.class);

    @Override
    public ArticleResponseDTO addArticle(ArticleRequestDTO articleRequest) {
        log.info("Adding article with title: {}", articleRequest.getTitle());
        Menu menu = menuRepository.findById(articleRequest.getMenuId())
                .orElseThrow(() -> {
                    log.error("Menu not found with ID: {}", articleRequest.getMenuId());
                    return new RuntimeException("Menu not found with ID: " + articleRequest.getMenuId());
                });

        Article article = new Article();
        article.setTitle(articleRequest.getTitle());
        article.setContent(articleRequest.getContent());
        article.setMenu(menu);

        Article savedArticle = articleRepository.save(article);
        log.info("Article saved with ID: {}", savedArticle.getId());
        return modelMapper.map(savedArticle, ArticleResponseDTO.class);
    }

    @Override
    public List<ArticleResponseDTO> getArticlesByMenuId(Long menuId) {
        List<Article> articles = articleRepository.findByMenuId(menuId);

        return articles.stream()
                .map(article -> modelMapper.map(article, ArticleResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public ArticleResponseDTO updateArticle(Long id, ArticleRequestDTO articleRequest) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found with ID: " + id));

        article.setTitle(articleRequest.getTitle());
        article.setContent(articleRequest.getContent());

        Article updatedArticle = articleRepository.save(article);

        return modelMapper.map(updatedArticle, ArticleResponseDTO.class);
    }

    @Override
    public void deleteArticle(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found with ID: " + id));

        articleRepository.delete(article);
    }
}
