package com.project.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class JasyptDecryptionTest {

    @Autowired
    private Environment env;

    @Test
    public void testDecryption() {
        assertDecryptedValue("spring.datasource.url");
        assertDecryptedValue("spring.datasource.username");
        assertDecryptedValue("spring.datasource.password");
        assertDecryptedValue("jwt.secret.key");
    }

    private void assertDecryptedValue(String propertyKey) {
        String decryptedValue = env.getProperty(propertyKey);
        assertNotNull(decryptedValue, "Decrypted value for " + propertyKey + " should not be null");
        assertTrue(!decryptedValue.contains("ENC("), "Decrypted value for " + propertyKey + " should not contain 'ENC('");
        System.out.println(propertyKey + ": " + decryptedValue); // 로그 출력 (필요에 따라 제거 가능)
    }
}
