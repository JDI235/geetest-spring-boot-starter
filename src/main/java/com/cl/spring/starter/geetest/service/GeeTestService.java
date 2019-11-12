package com.cl.spring.starter.geetest.service;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import com.cl.spring.starter.geetest.bean.ValidationData;
import com.cl.spring.starter.geetest.bean.ValidationResultData;

public interface GeeTestService
{

    /**
     * 获取版本信息
     *
     * @return
     */
    public String getVersionInfo();

    /**
     * 验证初始化预处理
     *
     * @param validationData
     * @return
     */
    public ValidationResultData preProcess(ValidationData validationData);

    /**
     * 服务正常的情况下使用的验证方式,向gt-server进行二次验证,获取验证结果
     *
     * @param challenge
     * @param validate
     * @param seccode
     * @return 验证结果, 1表示验证成功0表示验证失败
     */
    public boolean enhencedValidateRequest(String challenge, String validate, String seccode,
            ValidationData validationData)
            throws UnsupportedEncodingException;

    /**
     * failback使用的验证方式
     *
     * @param challenge
     * @param validate
     * @param seccode
     * @return 验证结果, 1表示验证成功0表示验证失败
     */
    public boolean failbackValidateRequest(String challenge, String validate, String seccode);

    public ValidationResultData cacheValidationResultData(ValidationResultData validationResultData);

    public ValidationResultData findValidationResultData(String userId);

}
