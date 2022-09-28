package com.nle.shared.service.ftp;

import com.nle.config.prop.AppProperties;
import com.nle.exception.CommonException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class SSHService {
    private final AppProperties appProperties;

    public String createFtpUser(String username, String password) {
        try {
            StringBuilder triggerURL = new StringBuilder();
            triggerURL.append(appProperties.getSecurity().getFtp().getTriggerUrl());
            triggerURL.append("&token=");
            triggerURL.append(appProperties.getSecurity().getFtp().getTriggerToken());
            triggerURL.append("&username=");
            triggerURL.append(username);
            triggerURL.append("&password=");
            triggerURL.append(password);
            HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(triggerURL.toString()))
                .GET()
                .build();
            HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new CommonException("Error while trying to create FTP user" + Arrays.toString(e.getStackTrace()));
        }
    }

    public String changePasswordFtpUser (String username, String password) {
        try {
            StringBuilder triggerURL = new StringBuilder();
            triggerURL.append("http://54.169.71.70:8080/buildByToken/buildWithParameters?job=Change-Password-FTP");
            triggerURL.append("&token=");
            triggerURL.append(appProperties.getSecurity().getFtp().getTriggerToken());
            triggerURL.append("&username=");
            triggerURL.append(username);
            triggerURL.append("&password=");
            triggerURL.append(password);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(triggerURL.toString()))
                    .GET()
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new CommonException("Error while change FTP password" + Arrays.toString(e.getStackTrace()));
        }
    }
}
