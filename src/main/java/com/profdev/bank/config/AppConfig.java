package com.profdev.bank.config;

import com.profdev.bank.data.load.DataLoader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    private final AppProperties appProperties;
    private final ApplicationContext applicationContext;

    public AppConfig(AppProperties appProperties, ApplicationContext applicationContext) {
        this.appProperties = appProperties;
        this.applicationContext = applicationContext;
    }

    @Bean
    public DataLoader dataLoader() {
        return (DataLoader) applicationContext.getBean(appProperties.dataType());
    }
}
