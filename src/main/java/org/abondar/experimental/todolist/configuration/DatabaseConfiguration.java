package org.abondar.experimental.todolist.configuration;

import org.abondar.experimental.todolist.datamodel.Item;
import org.abondar.experimental.todolist.datamodel.TodoList;
import org.abondar.experimental.todolist.datamodel.User;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.core.io.Resource;

import javax.sql.DataSource;


@Configuration
@PropertySource("classpath:db.properties")
@MapperScan("org.abondar.experimental.todolist.mappers")
public class DatabaseConfiguration {

//    @Value("${db.driverClassName}")
//    public String driverClassName;
//
//    @Value("${db.url}")
//    public String dbUrl;
//
//    @Value("${db.username}")
//    public String username;
//
//    @Value("${db.password}")
//    public String password;


//    @Bean
//    public DataSource dataSource() {
//        BasicDataSource dataSource = new BasicDataSource();
//        dataSource.setDriverClassName(driverClassName);
//        dataSource.setUrl(dbUrl);
//        dataSource.setUsername(username);
//        dataSource.setPassword(password);
//
//        return dataSource;
//    }

    @Bean
    public DataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://127.0.0.1:3306/todo_list");
        dataSource.setUsername("root");
        dataSource.setPassword("alex21");

        return dataSource;
    }



    @Bean
    public DataSourceTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());

    }


    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        Resource resource = new ClassPathResource("/mapper.xml");
        Resource[] resources = new Resource[]{resource};
        sessionFactory.setMapperLocations(resources);

        Class<?>[] aliases = new Class<?>[]{
                User.class,
                TodoList.class,
                Item.class
        };
        sessionFactory.setTypeAliases(aliases);

        return sessionFactory.getObject();
    }

    @Bean
    MapperScannerConfigurer configurer() {
        MapperScannerConfigurer configurer = new MapperScannerConfigurer();

        configurer.setBasePackage("org.abondar.experimental.todolist.mappers");
        return configurer;
    }

}
