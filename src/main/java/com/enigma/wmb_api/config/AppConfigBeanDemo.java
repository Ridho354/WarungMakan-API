package com.enigma.wmb_api.config;
import com.enigma.wmb_api.service.MenuDemoService;
import com.enigma.wmb_api.service.impl.MenuDemoServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class AppConfigBeanDemo {
    @Bean
    public MenuDemoService menuDemoService() {
        return new MenuDemoServiceImpl();
    }

}
