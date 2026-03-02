package com.kernel360.utils.jasypt;

import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Profile;

import java.util.LinkedHashMap;
import java.util.Map;

//@Profile("local")
@SpringBootApplication
public class JasyptEncryptor implements CommandLineRunner {
    private final StringEncryptor stringEncryptor;

    public JasyptEncryptor(@Qualifier("washpediaEncryptorBean") StringEncryptor stringEncryptor) {
        this.stringEncryptor = stringEncryptor;
    }

    public static void main(String[] args) {
//        SpringApplication.run(JasyptEncryptor.class, args);
        new SpringApplicationBuilder(JasyptEncryptor.class)
                .web(WebApplicationType.NONE) // 웹 서버를 띄우지 않도록 설정
                .run(args);
    }

    @Override
    public void run(String... args) {
//        String url = "암호화 할 문자열을 입력하세요";
//        String encryptedUrl = stringEncryptor.encrypt(url);
//
//        System.out.println("[Original] : " + url);
//        System.out.println("[Encrypted] : " + "ENC(" + encryptedUrl + ")");
//        System.out.println("[Decrypted] : " + stringEncryptor.decrypt(encryptedUrl));

        Map<String, String> targetMap = new LinkedHashMap<>();
        targetMap.put("redis.pw", "test");

        System.out.println("\n" + "=".repeat(50));
        System.out.println("✨ Jasypt 일괄 암호화 결과 (yml용) ✨");
        System.out.println("=".repeat(50));

        targetMap.forEach((key, value) -> {
            String encryptedValue = stringEncryptor.encrypt(value);

            System.out.printf("%s: ENC(%s)%n", key, encryptedValue);
        });

        System.out.println("=".repeat(50) + "\n");
    }
}