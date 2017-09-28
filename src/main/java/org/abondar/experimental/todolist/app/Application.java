package org.abondar.experimental.todolist.app;

import org.abondar.experimental.todolist.configuration.CXFConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
@Import({CXFConfig.class})
@MapperScan({"org.abondar.experimental.todolist"})
public class Application extends SpringBootServletInitializer {

    private static Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {

        SpringApplicationBuilder builder = new SpringApplicationBuilder(Application.class);
        builder.web(true).bannerMode(Banner.Mode.OFF).run(args);
        logger.info("Application has started");
    }
}
