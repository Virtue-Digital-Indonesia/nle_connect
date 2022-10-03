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
import java.util.Base64;

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
                .header("Authorization", getBasicAuthenticationHeader("admin", "TcdYFy1VHJ58"))
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
            triggerURL.append("http://210.247.245.144:8080/job/Change-Password-FTP/buildWithParameters?");
            triggerURL.append("&token=");
            triggerURL.append(appProperties.getSecurity().getFtp().getTriggerToken());
            triggerURL.append("&username=");
            triggerURL.append(username);
            triggerURL.append("&password=");
            triggerURL.append(password);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(triggerURL.toString()))
                    .GET()
                    .header("Authorization", getBasicAuthenticationHeader("admin", "TcdYFy1VHJ58"))
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new CommonException("Error while change FTP password" + Arrays.toString(e.getStackTrace()));
        }
    }

    private final String getBasicAuthenticationHeader(String username, String password) {
        String valueToEncode = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
    }
}
