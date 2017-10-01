package org.abondar.experimental.todolist.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.abondar.experimental.todolist.services.RestService;
import org.abondar.experimental.todolist.services.RestServiceImpl;
import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.swagger.Swagger2Feature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PreferencesPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
@ImportResource({"classpath:META-INF/cxf/cxf.xml"})
public class CXFConfig extends WebMvcConfigurerAdapter {

    @Bean(name = Bus.DEFAULT_BUS_ID)
    public SpringBus springBus() {
        return new SpringBus();
    }

    @Bean
    PreferencesPlaceholderConfigurer configurer() {
        return new PreferencesPlaceholderConfigurer();
    }

    @Bean
    RestService webService(){
        return new RestServiceImpl();
    }

    @Autowired
    @Bean
    public Server jaxRsServer(JacksonJsonProvider provider) {


        JAXRSServerFactoryBean factory = new JAXRSServerFactoryBean();
        factory.setBus(springBus());
        factory.setServiceBean(webService());
        factory.setProviders(Collections.singletonList(provider));
        factory.setFeatures(Collections.singletonList(createSwaggerFeature()));
        Map<Object, Object> extMappings = new HashMap<>();
        extMappings.put("json", "application/json");
        extMappings.put("xml", "application/xml");
        factory.setExtensionMappings(extMappings);
        Map<Object, Object> langMappings = new HashMap<>();
        langMappings.put("en", "en-gb");
        factory.setLanguageMappings(langMappings);
        factory.setAddress("/todo_list");

        return factory.create();
    }


    @Bean
    public JacksonJsonProvider jsonProvider() {
        JacksonJsonProvider provider = new JacksonJsonProvider();
        provider.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY,true);
        provider.configure(DeserializationFeature.UNWRAP_ROOT_VALUE,false);
        return provider;
    }


    @Bean
    public Swagger2Feature createSwaggerFeature() {
        Swagger2Feature swagger2Feature = new Swagger2Feature();
        swagger2Feature.setPrettyPrint(true);
        swagger2Feature.setHost("localhost:8080");
        swagger2Feature.setBasePath("/cxf/todo_list");
        return swagger2Feature;
    }
}