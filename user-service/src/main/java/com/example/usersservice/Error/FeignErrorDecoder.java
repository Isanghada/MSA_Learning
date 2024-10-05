package com.example.usersservice.Error;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
public class FeignErrorDecoder implements ErrorDecoder {
    private final Environment env;
    @Override
    public Exception decode(String methodKey, Response response) {
        if(response.status() == 400){
        }else if(response.status() == 404){
            if(methodKey.contains("getOrders")){
                return new ResponseStatusException(HttpStatus.valueOf(response.status()),
                        env.getProperty("order_service.exception.orders_is_empty"));
            }
        }else return new Exception(response.reason());

        return null;
    }
}
