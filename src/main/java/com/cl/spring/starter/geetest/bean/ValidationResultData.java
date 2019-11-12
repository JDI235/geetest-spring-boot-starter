package com.cl.spring.starter.geetest.bean;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ValidationResultData
{

    private String userId;

    private String jsonStr;

    private boolean success;
}
