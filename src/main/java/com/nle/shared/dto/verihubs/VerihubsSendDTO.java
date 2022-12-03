package com.nle.shared.dto.verihubs;

import lombok.Getter;
import lombok.Setter;

import java.net.http.HttpRequest;
import java.nio.ByteBuffer;
import java.util.concurrent.Flow;

@Setter
@Getter
public class VerihubsSendDTO implements HttpRequest.BodyPublisher {
    private String msisdn;
    private String time_limit;

    @Override
    public long contentLength() {
        return 0;
    }

    @Override
    public void subscribe(Flow.Subscriber<? super ByteBuffer> subscriber) {

    }
}
