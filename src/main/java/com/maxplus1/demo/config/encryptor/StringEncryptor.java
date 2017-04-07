package com.maxplus1.demo.config.encryptor;

import com.maxplus1.demo.utils.EncryptorUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by xiaolong.qiu on 2017/3/28.
 */
@Configuration
public class StringEncryptor {
    @Bean("jasyptStringEncryptor")
    public org.jasypt.encryption.StringEncryptor stringEncryptor() {
        //实现自己的加密解析器
        return new org.jasypt.encryption.StringEncryptor() {
            @Override
            public String encrypt(String s) {
                return EncryptorUtils.encrypt(s.getBytes());
            }

            @Override
            public String decrypt(String s) {
                return new String(EncryptorUtils.decrypt(s));
            }
        };
    }
}
