package com.cl.spring.starter.geetest.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cl.spring.starter.geetest.properties.GeeTestProperties;
import com.cl.spring.starter.geetest.sdk.GeetestLib;
import com.cl.spring.starter.geetest.service.GeeTestService;
import com.cl.spring.starter.geetest.service.impl.GeeTestServiceImpl;

@Configuration
@EnableConfigurationProperties(GeeTestProperties.class)
public class GeeTestAutoConfiguration
{

    private GeeTestProperties geeTestProperties;

    @Autowired
    public GeeTestAutoConfiguration(GeeTestProperties geeTestProperties)
    {
        this.geeTestProperties = geeTestProperties;
    }

    @Bean
    @ConditionalOnMissingBean(GeetestLib.class)
    public GeetestLib setGeeTestService()
    {
        return new GeetestLib(geeTestProperties);
    }

    @Autowired
    @Bean
    @ConditionalOnBean(GeetestLib.class)
    public GeeTestService createGeeTestService(GeetestLib geetestLib)
    {
        return new GeeTestServiceImpl(geetestLib);
    }

}