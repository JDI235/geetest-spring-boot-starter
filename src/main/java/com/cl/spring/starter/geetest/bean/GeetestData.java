package com.cl.spring.starter.geetest.bean;

import com.google.gson.Gson;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GeetestData
{
    private String challenge;

    private String validate;

    private String seccode;

    public static GeetestData formJson(String json)
    {
        return new Gson().fromJson(json, GeetestData.class);
    }

}
