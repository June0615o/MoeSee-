package com.moesee.moeseedemo;

import com.moesee.moeseedemo.service.RandomUserDataGenerateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MoeSeeDemoApplicationTests {

    @Autowired
    private RandomUserDataGenerateService randomUserDataGenerateService;

    @Test
    void testGenerateRandomUserData() {
        randomUserDataGenerateService.generateRandomUserData();
    }
}
