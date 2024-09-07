package inha.git.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${user.file}")
    private String fileUrl;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("http://localhost:3001","http://localhost", "http://localhost:80",  "http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true);
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/banner/**")
                .addResourceLocations(fileUrl + "/banner/");
        registry.addResourceHandler("/evidence/**")
                .addResourceLocations(fileUrl + "/evidence/");
        registry.addResourceHandler("/problem-file/**")
                .addResourceLocations(fileUrl + "/problem-file/");
        registry.addResourceHandler("/project/**")
                .addResourceLocations(fileUrl + "/project/");
        registry.addResourceHandler("/project-zip/**")
                .addResourceLocations(fileUrl + "/project-zip/");
    }
}