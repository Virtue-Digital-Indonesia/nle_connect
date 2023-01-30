package com.nle.util;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class DecodeUtil {

    public static Map<String, String> decodeToken (String token) {
        String[] chunks = token.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(chunks[1]));
        String [] pairs = payload.substring(1, payload.length()-1).split(",");

        Map<String, String> decodeToken = new HashMap<>();

        for (String pair : pairs) {
            String [] tempPair = pair.split(":");
            String key = tempPair[0].substring(1, tempPair[0].length() - 1);
            String value = tempPair[1];

            if (value.startsWith("\"") && value.endsWith("\""))
                value = tempPair[1].substring(1, tempPair[1].length() - 1);
            decodeToken.put(key, value);
        }

        return decodeToken;
    }
}
