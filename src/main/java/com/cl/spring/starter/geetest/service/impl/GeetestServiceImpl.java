package com.cl.spring.starter.geetest.service.impl;

import java.io.UnsupportedEncodingException;

import com.cl.spring.starter.geetest.bean.GeetestData;
import com.cl.spring.starter.geetest.bean.RegisterData;
import com.cl.spring.starter.geetest.bean.RegisterResultData;
import com.cl.spring.starter.geetest.sdk.GeetestLib;
import com.cl.spring.starter.geetest.service.GeetestService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GeetestServiceImpl implements GeetestService
{

    private GeetestLib geetestLib;

    public GeetestServiceImpl(GeetestLib geetestLib)
    {
        this.geetestLib = geetestLib;
    }

    @Override
    public RegisterResultData register(RegisterData registerData)
    {
        geetestLib.cacheValidationResultData(geetestLib.preProcess(registerData));
        return geetestLib.findValidationResultData(registerData.getUserId());
    }

    @Override
    public Boolean validate(String userId, GeetestData geetestData)
    {
        boolean flag = false;
        RegisterResultData registerResultData = geetestLib.findValidationResultData(userId);
        if (registerResultData.isSuccess())
        {
            flag = geetestLib.enhencedValidateRequest(geetestData, registerResultData.getRegisterData());
        }
        else
        {
            flag = geetestLib.failbackValidateRequest(geetestData);
        }
        return flag;
    }

}
