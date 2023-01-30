package com.nle.shared.service.form;

import java.io.ByteArrayOutputStream;

public interface FormService {
    ByteArrayOutputStream exportInvoice(Long id);

    ByteArrayOutputStream exportBon(Long id);
    ByteArrayOutputStream exportInvoiceOrder(Long id);
}
