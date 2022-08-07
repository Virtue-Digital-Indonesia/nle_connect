package com.nle.config.openfeign;

import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;

@Component
public class TaxMinistryClientErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        throw new RetryableException(
            response.status(),
            response.reason(),
            response.request().httpMethod(),
            null,
            response.request());
    }
}
