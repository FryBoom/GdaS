package com.pinyougou.service;

public interface SmsService {
    public boolean sendSms(String phone,String signName,
                           String templateCode,String templateParam);
}
