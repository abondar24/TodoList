package org.abondar.experimental.todolist.app;

import org.abondar.experimental.todolist.configuration.CXFConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;

@SpringBootApplication

@Import({CXFConfig.class})
//@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, MybatisAutoConfiguration.class})
@MapperScan({"org.abondar.experimental.todolist"})
public class Application extends SpringBootServletInitializer {

    private static Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {

        SpringApplicationBuilder builder = new SpringApplicationBuilder(Application.class);
        builder.web(true).bannerMode(Banner.Mode.OFF).run(args);
//        SpringApplication.run(Application.class,args);
        logger.info("Application has started");
    }
}
