package com.rahul.generativeai.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "http://springaiassist.s3-website.eu-north-1.amazonaws.com",
                        "https://delightful-tree-058892c0f.5.azurestaticapps.net",
                        "https://main.d1roziyo3zgxn7.amplifyapp.com",
                        "http://localhost:5173",
                        "http://localhost:3000",
                        "https://main.d23bkhxjqbyoh7.amplifyapp.com/",
                        "https://ashy-sky-0ba15c40f.5.azurestaticapps.net/"
                        ,"https://main.dt6i6q82xp8vm.amplifyapp.com/",
                        "https://main.d1b7c33z3by11m.amplifyapp.com/"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
