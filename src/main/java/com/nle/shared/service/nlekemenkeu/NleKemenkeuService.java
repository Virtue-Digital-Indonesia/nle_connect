package com.nle.shared.service.nlekemenkeu;


import com.nle.ui.model.JWTToken;

public interface NleKemenkeuService {
    JWTToken getConvertUserToken(String token);
}
