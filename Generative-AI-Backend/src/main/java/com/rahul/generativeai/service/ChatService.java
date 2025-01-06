package com.rahul.generativeai.service;

import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final AzureOpenAiChatModel azureOpenAiChatModel;
    private final JavaMailSender mailSender;


    public ChatService(AzureOpenAiChatModel azureOpenAiChatModel, JavaMailSender mailSender) {
        this.azureOpenAiChatModel = azureOpenAiChatModel;
        this.mailSender = mailSender;
    }

    public String getResponse(String prompt){

        return azureOpenAiChatModel.call(prompt);
    }

    public ChatResponse getResponseOptions(String prompt){
        return  azureOpenAiChatModel.call(new Prompt(prompt));
    }

    public void sendEmail(String to, String subject, String message) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message);
        simpleMailMessage.setFrom("rahulvirat9795@gmail.com");
        mailSender.send(simpleMailMessage);
    }

}
