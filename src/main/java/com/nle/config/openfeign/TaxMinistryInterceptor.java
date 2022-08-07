package com.nle.config.openfeign;

import com.nle.config.prop.AppProperties;
import com.nle.constant.AppConstant;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Autowired;

public class TaxMinistryInterceptor implements RequestInterceptor {

    @Autowired
    private AppProperties appProperties;

    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header(AppConstant.TAX_MINISTRY_HEADER_KEY, appProperties.getSecurity().getTaxMinistry().getApiKey());
    }
}
