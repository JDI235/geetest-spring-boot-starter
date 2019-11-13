package com.cl.spring.starter.geetest.bean;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterResultData
{

    private String userId;

    private GeetestData geetestData;

    private RegisterData registerData;

    private boolean success;
}
