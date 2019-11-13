package com.cl.spring.starter.geetest.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "geetest")
public class GeetestProperties
{
    /**
     * 公钥
     */
    private String captchaId = "";

    /**
     * 私钥
     */
    private String privateKey = "";

    /**
     * 是否开启新的failback
     */
    private boolean newFailback = false;

    /**
     * 调试开关，是否输出调试日志
     */
    public boolean debugCode = true;

}
