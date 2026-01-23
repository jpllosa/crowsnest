package net.codesamples.crowsnest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class IndexTransformationConfigurer implements WebMvcConfigurer {

    @Autowired
    IndexTransformer indexTransformer;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("index.html")
                .addResourceLocations("classpath:/static/")
                .resourceChain(false)
                .addTransformer(indexTransformer);
    }
}
