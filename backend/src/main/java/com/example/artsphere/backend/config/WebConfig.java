package com.example.artsphere.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Konfiguracja Web MVC dla aplikacji ArtSphere.
 * Odpowiada za mapowanie zasobów statycznych, takich jak przesłane zdjęcia dzieł sztuki,
 * na ścieżki fizyczne w systemie plików serwera.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Konstruktor domyślny.
     */
    public WebConfig() {}

    /**
     * Konfiguruje obsługę zasobów statycznych.
     * Mapuje żądania zaczynające się od "/uploads/**" na fizyczny folder "uploads" 
     * znajdujący się w głównym katalogu projektu.
     *
     * @param registry rejestr handlerów zasobów.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadDir = Paths.get("uploads");
        String uploadPath = uploadDir.toFile().getAbsolutePath();
        
        // Zapewniamy, że ścieżka kończy się separatorem
        if (!uploadPath.endsWith("/") && !uploadPath.endsWith("\\")) {
            uploadPath += "/";
        }

        // Mapujemy URL /uploads/** na fizyczną lokalizację na dysku
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath);
    }
}
