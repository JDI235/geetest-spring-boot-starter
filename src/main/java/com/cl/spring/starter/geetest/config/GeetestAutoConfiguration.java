package com.cl.spring.starter.geetest.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cl.spring.starter.geetest.properties.GeetestProperties;
import com.cl.spring.starter.geetest.sdk.GeetestLib;
import com.cl.spring.starter.geetest.service.GeetestService;
import com.cl.spring.starter.geetest.service.impl.GeetestServiceImpl;

@Configuration
@EnableConfigurationProperties(GeetestProperties.class)
public class GeetestAutoConfiguration
{

    @Autowired
    @Bean
    @ConditionalOnMissingBean(GeetestLib.class)
    public GeetestLib setGeeTestService(GeetestProperties geetestProperties)
    {
        return new GeetestLib(geetestProperties);
    }

    @Autowired
    @Bean
    @ConditionalOnBean(GeetestLib.class)
    public GeetestService createGeeTestService(GeetestLib geetestLib)
    {
        return new GeetestServiceImpl(geetestLib);
    }

}