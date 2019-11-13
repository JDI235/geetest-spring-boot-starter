package com.cl.spring.starter.geetest.service;

import com.cl.spring.starter.geetest.bean.GeetestData;
import com.cl.spring.starter.geetest.bean.RegisterData;
import com.cl.spring.starter.geetest.bean.RegisterResultData;

public interface GeetestService
{

    RegisterResultData register(RegisterData registerData);

    Boolean validate(String userId, GeetestData geetestData);

}
