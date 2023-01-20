package com.nle.shared.service.bankDepo;


import com.nle.ui.model.request.BankDepoRequest;
import com.nle.ui.model.response.BankDepoResponse;

import java.util.List;

public interface BankDepoService {

    BankDepoResponse insertBankCode(BankDepoRequest request);

    List<BankDepoResponse> getAllBankDepo();
}
