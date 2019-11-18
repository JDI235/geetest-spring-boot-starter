package com.cl.spring.starter.geetest.sdk;

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
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import com.cl.spring.starter.geetest.bean.GeetestData;
import com.cl.spring.starter.geetest.bean.RegisterData;
import com.cl.spring.starter.geetest.bean.RegisterResultData;
import com.cl.spring.starter.geetest.consts.GeeTestConst;
import com.cl.spring.starter.geetest.properties.GeetestProperties;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

@Slf4j
public class GeetestLib
{

    private GeetestProperties geeTestProperties;

    private OkHttpClient okHttpClient;

    public GeetestLib(GeetestProperties geeTestProperties)
    {
        this.geeTestProperties = geeTestProperties;
        this.okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
    }

    public String getVersionInfo()
    {
        return GeeTestConst.VERSION;
    }

    /**
     * 验证初始化预处理
     *
     * @param registerData
     * @return
     */
    public RegisterResultData preProcess(RegisterData registerData)
    {
        RegisterResultData validationResultData = registerChallenge(registerData);
        validationResultData.setRegisterData(registerData);
        if (!validationResultData.isSuccess())
        {
            validationResultData.setGeetestData(GeetestData.formJson(this.getFailPreProcessRes()));
        }
        return validationResultData;
    }

    /**
     * 服务正常的情况下使用的验证方式,向gt-server进行、“”二次验证,获取验证结果
     *
     * @param geetestData
     * @param validationData
     * @return
     * @throws UnsupportedEncodingException
     */
    public boolean enhencedValidateRequest(GeetestData geetestData,
            RegisterData validationData)
    {

        if (!resquestIsLegal(geetestData.getChallenge(),
                geetestData.getValidate(), geetestData.getSeccode()))
        {

            return false;

        }

        gtlog("request legitimate");

        String param = setParamString(validationData, String.format(
                "challenge=%s&validate=%s&seccode=%s&json_format=%s", geetestData.getChallenge(),
                geetestData.getValidate(), geetestData.getSeccode(),
                GeeTestConst.JSON_FORMAT));

        gtlog("param:" + param);

        String response = "";
        try
        {

            if (geetestData.getValidate().length() <= 0 || !checkResultByPrivate(geetestData.getChallenge(),
                    geetestData.getValidate()))
            {

                return false;

            }

            gtlog("checkResultByPrivate");

            response = readContentFromPost(GeeTestConst.API_URL + GeeTestConst.VALIDATE_URL, param);

            gtlog("response: " + response);

        }
        catch (Exception e)
        {

            e.printStackTrace();

        }

        String return_seccode = "";

        try
        {

            JSONObject return_map = new JSONObject(response);
            return_seccode = return_map.getString("seccode");
            gtlog("md5: " + md5Encode(return_seccode));

            if (return_seccode.equals(md5Encode(geetestData.getSeccode())))
            {

                return true;

            }
            else
            {

                return false;

            }

        }
        catch (JSONException e)
        {
            gtlog("json load error");
            return false;
        }

    }

    /**
     * failback使用的验证方式
     *
     * @param geetestData
     * @return
     */
    public boolean failbackValidateRequest(GeetestData geetestData)
    {
        gtlog("in failback validate");
        boolean flag = resquestIsLegal(geetestData.getChallenge(),
                geetestData.getValidate(), geetestData.getSeccode());
        gtlog("request legitimate");
        return flag;
    }

    @CachePut(value = "geetest", key = "validationResultData.userId")
    public void cacheValidationResultData(RegisterResultData validationResultData)
    {
    }

    @Cacheable(value = "geetest", key = "#userId")
    public RegisterResultData findValidationResultData(String userId)
    {
        return null;
    }

    /**
     * 输出debug信息，需要开启debugCode
     *
     * @param message
     */
    private void gtlog(String message)
    {
        if (geeTestProperties.isDebugCode())
        {
            log.info("gtlog: " + message);
        }
    }

    /**
     * 检查客户端的请求是否合法,三个只要有一个为空，则判断不合法
     *
     * @param challenge
     * @param validate
     * @param seccode
     * @return
     */
    private boolean resquestIsLegal(String challenge, String validate, String seccode)
    {
        return StringUtils.isAnyBlank(challenge, validate, seccode);

    }

    private boolean checkResultByPrivate(String challenge, String validate)
    {
        String encodeStr = md5Encode(geeTestProperties.getPrivateKey() + "geetest" + challenge);
        return validate.equals(encodeStr);
    }

    /**
     * @param plainText
     * @return
     * @time 2014年7月10日 下午3:30:01
     */
    private String md5Encode(String plainText)
    {
        String re_md5 = "";
        try
        {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte[] b = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (byte value : b)
            {
                i = value;
                if (i < 0)
                {
                    i += 256;
                }
                if (i < 16)
                {
                    buf.append("0");
                }
                buf.append(Integer.toHexString(i));
            }

            re_md5 = buf.toString();

        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return re_md5;
    }

    /**
     * 发送POST请求，获取服务器返回结果
     *
     * @param URL
     * @return 服务器返回结果
     * @throws IOException
     */
    private String readContentFromPost(String URL, String data) throws IOException
    {

        gtlog(data);
        java.net.URL postUrl = new URL(URL);
        HttpURLConnection connection = (HttpURLConnection) postUrl
                .openConnection();

        connection.setConnectTimeout(2000);// 设置连接主机超时（单位：毫秒）
        connection.setReadTimeout(2000);// 设置从主机读取数据超时（单位：毫秒）
        connection.setRequestMethod("POST");
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        // 建立与服务器的连接，并未发送数据
        connection.connect();

        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream(), "utf-8");
        outputStreamWriter.write(data);
        outputStreamWriter.flush();
        outputStreamWriter.close();

        if (connection.getResponseCode() == 200)
        {

            return getResopnseString(connection);
        }
        else
        {

            return "fail";
        }
    }

    private String getResopnseString(HttpURLConnection connection) throws IOException
    {
        // 发送数据到服务器并使用Reader读取返回的数据
        StringBuilder sBuffer = new StringBuilder();
        InputStream inStream = null;
        byte[] buf = new byte[1024];
        inStream = connection.getInputStream();
        for (int n; (n = inStream.read(buf)) != -1; )
        {
            sBuffer.append(new String(buf, 0, n, StandardCharsets.UTF_8));
        }
        inStream.close();
        connection.disconnect();// 断开连接
        return sBuffer.toString();
    }

    /**
     * 用captchaID进行注册，更新challenge
     *
     * @return 1表示注册成功，0表示注册失败
     */
    private RegisterResultData registerChallenge(RegisterData validationData)
    {
        RegisterResultData validationResultData = RegisterResultData.builder().userId(validationData.getUserId())
                .build();
        try
        {
            String getUrl = GeeTestConst.API_URL + GeeTestConst.REGISTER_URL + "?";
            String param = setParamString(validationData,
                    "gt=" + geeTestProperties.getCaptchaId() + "&json_format=" + GeeTestConst.JSON_FORMAT);

            gtlog("GET_URL:" + getUrl + param);
            String result_str = readContentFromGet(getUrl + param);
            if (result_str != "fail")
            {
                gtlog("result:" + result_str);
                JSONObject jsonObject = new JSONObject(result_str);
                String return_challenge = jsonObject.getString("challenge");

                gtlog("return_challenge:" + return_challenge);

                if (return_challenge.length() == 32)
                {
                    validationResultData.setGeetestData(GeetestData.formJson(this
                            .getSuccessPreProcessRes(
                                    this.md5Encode(return_challenge + geeTestProperties.getPrivateKey()))));
                    validationResultData.setSuccess(true);
                }
                else
                {
                    gtlog("gtServer register challenge error");
                }

            }
            else
            {
                gtlog("gtServer register challenge failed");
            }
        }
        catch (Exception e)
        {

            gtlog(e.toString());
            gtlog("exception:register api");

        }
        return validationResultData;
    }

    private String setParamString(RegisterData validationData, String param)
    {
        try
        {
            if (validationData.getUserId() != null)
            {
                param = param + "&user_id=" + URLEncoder.encode(validationData.getUserId(), "utf-8");
            }
            if (validationData.getClientType() != null)
            {
                param = param + "&client_type=" + URLEncoder.encode(validationData.getClientType(), "utf-8");
            }
            if (validationData.getIpAddress() != null)
            {
                param = param + "&ip_address=" + URLEncoder.encode(validationData.getIpAddress(), "utf-8");
            }
        }
        catch (UnsupportedEncodingException e)
        {
            //log.error("", e);
        }
        return param;
    }

    /**
     * 预处理成功后的标准串
     */
    private String getSuccessPreProcessRes(String challenge)
    {

        gtlog("challenge:" + challenge);

        JSONObject jsonObject = new JSONObject();
        try
        {

            jsonObject.put("success", 1);
            jsonObject.put("gt", geeTestProperties.getCaptchaId());
            jsonObject.put("challenge", challenge);

        }
        catch (JSONException e)
        {

            gtlog("json dumps error");

        }

        return jsonObject.toString();

    }

    /**
     * 发送GET请求，获取服务器返回结果
     *
     * @param URL
     * @return 服务器返回结果
     * @throws IOException
     */
    private String readContentFromGet(String URL) throws IOException
    {

        java.net.URL getUrl = new URL(URL);
        HttpURLConnection connection = (HttpURLConnection) getUrl
                .openConnection();

        connection.setConnectTimeout(2000);// 设置连接主机超时（单位：毫秒）
        connection.setReadTimeout(2000);// 设置从主机读取数据超时（单位：毫秒）

        // 建立与服务器的连接，并未发送数据
        connection.connect();

        if (connection.getResponseCode() == 200)
        {
            return getResopnseString(connection);
        }
        else
        {

            return "fail";
        }
    }

    /**
     * 预处理失败后的返回格式串
     *
     * @return
     */
    private String getFailPreProcessRes()
    {
        String md5Str1 = md5Encode(Math.round(Math.random() * 100) + "");
        String md5Str2 = md5Encode(Math.round(Math.random() * 100) + "");
        String challenge = md5Str1 + md5Str2.substring(0, 2);

        JSONObject jsonObject = new JSONObject();
        try
        {

            jsonObject.put("success", 0);
            jsonObject.put("gt", geeTestProperties.getCaptchaId());
            jsonObject.put("challenge", challenge);
            jsonObject.put("new_captcha", geeTestProperties.isNewFailback());

        }
        catch (JSONException e)
        {

            gtlog("json dumps error");

        }

        return jsonObject.toString();

    }
}
