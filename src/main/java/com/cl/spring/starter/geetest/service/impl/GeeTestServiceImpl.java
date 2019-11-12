package com.cl.spring.starter.geetest.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import com.cl.spring.starter.geetest.bean.ValidationData;
import com.cl.spring.starter.geetest.bean.ValidationResultData;
import com.cl.spring.starter.geetest.consts.GeeTestConst;
import com.cl.spring.starter.geetest.properties.GeeTestProperties;
import com.cl.spring.starter.geetest.sdk.GeetestLib;
import com.cl.spring.starter.geetest.service.GeeTestService;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

@Slf4j
public class GeeTestServiceImpl implements GeeTestService
{

    private GeetestLib geetestLib;

    public GeeTestServiceImpl(GeetestLib geetestLib)
    {
        this.geetestLib = geetestLib;
    }
}
