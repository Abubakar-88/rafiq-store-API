package com.rafiqstore.repository;

import com.rafiqstore.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    @Query("SELECT a FROM Article a JOIN FETCH a.menu WHERE a.menu.id = :menuId")
    List<Article> findByMenuId(@Param("menuId") Long menuId);
}
