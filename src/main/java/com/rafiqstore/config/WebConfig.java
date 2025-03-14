package com.rafiqstore.config;

import com.rafiqstore.converter.StringToLocalDateTimeConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addFormatters(FormatterRegistry registry) {
                registry.addConverter(new StringToLocalDateTimeConverter());
            }



            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins("http://localhost:3000") // Allow requests from this origin
                        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS") // Allowed HTTP methods
                        .allowedHeaders("*") // Allow all headers
                        .exposedHeaders("Authorization", "Content-Disposition") // Expose specific headers to the client
                        .allowCredentials(true) // Allow credentials (e.g., cookies)
                        .maxAge(3600); // Cache preflight responses for 1 hour
            }
            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry.addResourceHandler("/images/item/**")
                        .addResourceLocations("file:src/main/resources/static/images/item/");
               // registry.addResourceHandler("/invoices/**").addResourceLocations(invoicesPath);
                // Serve invoices from uploads folder
//                registry.addResourceHandler("/invoices/**")
//                        .addResourceLocations("file:src/main/resources/static/invoices/");
            }

        };
    }
}
