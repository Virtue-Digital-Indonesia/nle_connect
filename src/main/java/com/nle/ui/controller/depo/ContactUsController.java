package com.nle.ui.controller.depo;


import com.nle.shared.service.email.EmailService;
import com.nle.ui.model.request.ContactUsFormRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contact-us")
@RequiredArgsConstructor
public class ContactUsController {

    private final EmailService emailService;

    @PostMapping
    public void sendUserFeedback(@RequestBody ContactUsFormRequest contactUsFormRequest){
        emailService.sendMessageForContactUs(contactUsFormRequest);
    }
}
