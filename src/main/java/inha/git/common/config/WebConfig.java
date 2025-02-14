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
                .allowedOriginPatterns("http://localhost:3000","http://localhost", "http://165.246.21.232", "http://165.246.21.231", "https://oss.inha.ac.kr")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/api/v1/banner/**")
                .addResourceLocations(fileUrl + "/banner/");
        registry.addResourceHandler("/api/v1/evidence/**")
                .addResourceLocations(fileUrl + "/evidence/");
        registry.addResourceHandler("/api/v1/problem-file/**")
                .addResourceLocations(fileUrl + "/problem-file/");
        registry.addResourceHandler("/api/v1/project/**")
                .addResourceLocations(fileUrl + "/project/");
        registry.addResourceHandler("/api/v1/project-zip/**")
                .addResourceLocations(fileUrl + "/project-zip/");
        registry.addResourceHandler("/api/v1/image/**")
                .addResourceLocations(fileUrl + "/image/");
        registry.addResourceHandler("/api/v1/attachment/**")
                .addResourceLocations(fileUrl + "/attachment/");
        registry.addResourceHandler("/api/v1/patent/**")
                .addResourceLocations(fileUrl + "/patent/");
    }
}
