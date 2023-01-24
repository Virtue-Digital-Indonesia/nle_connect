package com.nle.shared.service.bankDepo;

import com.nle.exception.BadRequestException;
import com.nle.io.entity.BankDepo;
import com.nle.io.entity.DepoOwnerAccount;
import com.nle.io.repository.BankDepoRepository;
import com.nle.io.repository.DepoOwnerAccountRepository;
import com.nle.security.SecurityUtils;
import com.nle.ui.model.request.BankDepoRequest;
import com.nle.ui.model.response.BankDepoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class BankDepoServiceImpl implements BankDepoService {

    private final DepoOwnerAccountRepository depoOwnerAccountRepository;
    private final BankDepoRepository bankDepoRepository;

    @Override
    public BankDepoResponse changeBankCode(BankDepoRequest request) {
        Optional<String> companyEmail = SecurityUtils.getCurrentUserLogin();

        if (companyEmail.isEmpty())
            throw new BadRequestException("invalid token");

        Optional<DepoOwnerAccount> depoOwnerAccount = depoOwnerAccountRepository.findByCompanyEmail(companyEmail.get());

        if (depoOwnerAccount.isEmpty())
            throw new BadRequestException("cannot find depo");

        //if not have will create
        Optional<BankDepo> bankDefault = bankDepoRepository.findDefaultDepoByCompanyEmail(companyEmail.get());
        if (bankDefault.isEmpty()) {
            return insertBankCode(request);
        }

        //update
        BankDepo bankDepo = bankDefault.get();
        if (request.getBank_code() != null || !request.getBank_code().trim().isEmpty())
            bankDepo.setBank_code(bankDepo.getBank_code());

        if (request.getAccount_holder_name() != null || !request.getAccount_holder_name().trim().isEmpty())
            bankDepo.setAccount_holder_name(request.getAccount_holder_name());

        if (request.getAccount_number() != null || !request.getAccount_number().trim().isEmpty())
            bankDepo.setAccount_number(request.getAccount_number());

        if (request.getDescription_bank() != null || !request.getDescription_bank().trim().isEmpty())
            bankDepo.setDescription_bank(request.getDescription_bank());

        bankDepo.setDefault_bank(true);

        return convertToResponse(bankDepoRepository.save(bankDepo));
    }

    @Override
    public BankDepoResponse insertBankCode(BankDepoRequest request) {
        Optional<String> companyEmail = SecurityUtils.getCurrentUserLogin();

        if (companyEmail.isEmpty())
            throw new BadRequestException("invalid token");

        Optional<DepoOwnerAccount> depoOwnerAccount = depoOwnerAccountRepository.findByCompanyEmail(companyEmail.get());

        if (depoOwnerAccount.isEmpty())
            throw new BadRequestException("cannot find depo");

        BankDepo bankDepo = new BankDepo();
        BeanUtils.copyProperties(request, bankDepo);
        bankDepo.setBank_code(request.getBank_code().toUpperCase());
        bankDepo.setDepoOwnerAccount(depoOwnerAccount.get());

        //change default bank
        if (request.getDefault_bank() == null || request.getDefault_bank() == false) {
            bankDepo.setDefault_bank(false);
        }
        else if (request.getDefault_bank() == true) {
            bankDepo.setDefault_bank(true);
            Optional<BankDepo> bankDefault = bankDepoRepository.findDefaultDepoByCompanyEmail(companyEmail.get());

            if (bankDefault.isPresent()) {
                BankDepo temp = bankDefault.get();
                temp.setDefault_bank(false);
                bankDepoRepository.save(temp);
            }

        }

        //if depo not have any default this bank will be default
        Optional<BankDepo> bankDefault = bankDepoRepository.findDefaultDepoByCompanyEmail(companyEmail.get());
        if (bankDefault.isEmpty()) {
            bankDepo.setDefault_bank(true);
        }

        BankDepo saved = bankDepoRepository.save(bankDepo);
        return this.convertToResponse(saved);
    }

    @Override
    public List<BankDepoResponse> getAllBankDepo() {

        Optional<String> companyEmail = SecurityUtils.getCurrentUserLogin();
        if (companyEmail.isEmpty())
            throw new BadRequestException("invalid token");

        Optional<DepoOwnerAccount> depoOwnerAccount = depoOwnerAccountRepository.findByCompanyEmail(companyEmail.get());
        if (depoOwnerAccount.isEmpty())
            throw new BadRequestException("cannot find depo");

        List<BankDepo> bankDepoList = bankDepoRepository.getAllBankDepoByCompanyEmail(companyEmail.get());

        List<BankDepoResponse> responseList = new ArrayList<>();
        for (BankDepo entity : bankDepoList) {
            responseList.add(this.convertToResponse(entity));
        }

        return responseList;
    }

    private BankDepoResponse convertToResponse (BankDepo bankDepo) {
        BankDepoResponse response = new BankDepoResponse();
        BeanUtils.copyProperties(bankDepo, response);
        return response;
    }
}
