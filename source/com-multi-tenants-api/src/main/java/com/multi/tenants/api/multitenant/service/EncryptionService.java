package com.multi.tenants.api.multitenant.service;

public interface EncryptionService {
    String encrypt(String data, String secret, String salt);
    String decrypt(String data, String secret, String salt);
}
