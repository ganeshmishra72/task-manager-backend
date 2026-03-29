package com.rdmishra.backend_tms.config;

import java.util.HashMap;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;

@Configuration
public class ProjectConfig {

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apikey;

    @Value("${cloudinary.api-secret}")
    private String apisecret;

    @Bean
    ModelMapper mapper() {
        return new ModelMapper();
    }

    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", cloudName);
        config.put("api_key", apikey);
        config.put("api_secret", apisecret);
        return new Cloudinary(config);
    }

}
