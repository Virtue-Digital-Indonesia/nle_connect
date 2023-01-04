package com.nle.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import com.xendit.exception.XenditException;
import com.xendit.model.FixedVirtualAccount;
import com.xendit.model.Invoice;

@Component
public class XenditUtil {

    public static Invoice getInvoice(String forUserId, String invoiceId) {
        Map<String, String> headers = new HashMap<>();
        headers.put("for-user-id", forUserId);
        try {
            Invoice invoice = Invoice.getById(headers, invoiceId);
            return invoice;
        } catch (XenditException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static FixedVirtualAccount getVA(String forUserId, String vaId) {
        Map<String, String> headers = new HashMap<>();
        headers.put("for-user-id", forUserId);
        try {
            FixedVirtualAccount fvAccount = FixedVirtualAccount.getFixedVA(headers, vaId);
            return fvAccount;
        } catch (XenditException e) {
            e.printStackTrace();
        }
        return null;
    }
}
