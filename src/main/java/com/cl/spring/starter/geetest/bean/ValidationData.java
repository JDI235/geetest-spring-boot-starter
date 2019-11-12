package com.cl.spring.starter.geetest.bean;

import java.net.InetAddress;
import java.net.UnknownHostException;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@AllArgsConstructor
public class ValidationData
{
    private String userId;

    private String clientType;

    private String ipAddress;

}
