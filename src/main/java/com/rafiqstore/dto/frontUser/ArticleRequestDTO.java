package com.rafiqstore.dto.frontUser;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleRequestDTO {
    private String title;
    private String content;
    private Long menuId;
}
