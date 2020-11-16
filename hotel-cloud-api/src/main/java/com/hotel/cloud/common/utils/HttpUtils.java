package com.hotel.cloud.common.utils;

import cn.hutool.core.io.IoUtil;
import com.alibaba.fastjson.JSON;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

public class HttpUtils {

    private static RestTemplate restTemplate;

    static {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(10000);
        requestFactory.setReadTimeout(8000);
        restTemplate = new RestTemplate(requestFactory);
    }

    public static InputStream get(String url) throws IOException {
        ResponseEntity<Resource> resource = restTemplate.getForEntity(url, Resource.class);
        return null != resource.getBody() ? resource.getBody().getInputStream() : null;
    }

    public static String get(String url, Map<String, Object> param){
        ResponseEntity<String> responseEntity;
        if (param !=null){
            url = makeUriFormParam(url,param);
        }
        responseEntity = restTemplate.getForEntity(url, String.class);
        return getBody(responseEntity);
    }

    private static String makeUriFormParam(String url, Map<String, Object> param) {
        StringBuffer stringBuffer = new StringBuffer(url);
        Iterator iterator = ((Map)param).entrySet().iterator();
        if (iterator.hasNext()) {
            stringBuffer.append("?");
            Object element;
            while (iterator.hasNext()) {
                element = iterator.next();
                Map.Entry<String, Object> entry = (Map.Entry) element;
                //过滤value为null，value为null时进行拼接字符串会变成 "null"字符串
                if (entry.getValue() != null) {
                    stringBuffer.append(element).append("&");
                }
                url = stringBuffer.substring(0, stringBuffer.length() - 1);
            }
        }
        return  url;
    }

    public static  String post(String url, Map<String, Object> param){
        HttpEntity<Map<String, Object>> httpEntity = makeHttpEntityForXW(param);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        return getBody(responseEntity);
    }

    public static String post(String url, String body){
        HttpEntity<Map<String, Object>> httpEntity = makeHttpEntityForXW(body);
        ResponseEntity<String> responseEntity = null;
        responseEntity = restTemplate.postForEntity(url, httpEntity, String.class);
        return getBody(responseEntity);
    }

    public static String put(String url, String body){
        HttpEntity<Map<String, Object>> httpEntity = makeHttpEntityForXW(body);
        ResponseEntity<String> responseEntity = null;
        responseEntity = restTemplate.exchange(url, HttpMethod.PUT, httpEntity, String.class);
        return getBody(responseEntity);
    }

    public static String delete(String url, String body){
        HttpEntity<Map<String, Object>> httpEntity = makeHttpEntityForXW(body);
        ResponseEntity<String> responseEntity = null;
        responseEntity = restTemplate.exchange(url, HttpMethod.DELETE, httpEntity, String.class);
        return getBody(responseEntity);
    }

    private static String getBody(ResponseEntity<String> responseEntity){
        int status = responseEntity.getStatusCodeValue();
        if ((status >= 200 && status < 300) || status == 302) {
            String body = responseEntity.getBody();
            if (body != null) {
                return body;
            }
        }
        return null;
    }

    private static HttpEntity<Map<String, Object>> makeHttpEntityForXW(String body) {
        Map<String, Object> param;
        param = (Map<String, Object>) JSON.parse(body);
        return makeHttpEntityForXW(param);
    }

    private static HttpEntity<Map<String, Object>> makeHttpEntityForXW(Map<String, Object> map) {
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        if (map == null){
            return new HttpEntity<>(null, headers);
        }
        return new HttpEntity<>(map, headers);
    }
}
