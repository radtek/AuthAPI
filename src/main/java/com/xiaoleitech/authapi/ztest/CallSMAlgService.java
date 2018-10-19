package com.xiaoleitech.authapi.ztest;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Data
class SM3Response {
    private int errorCode;
    private String hash;
}

@Component
public class CallSMAlgService {
    private final RestTemplate restTemplate;

    @Autowired
    public CallSMAlgService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String callsm3(String url, String request) {
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, request, String.class);
//        SM3Response response = restTemplate.postForObject(url, request, SM3Response.class);
        String sm3Response = responseEntity.getBody();
        return sm3Response;
    }
}
