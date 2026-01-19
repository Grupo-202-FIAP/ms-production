package com.nextimefood.msproduction.integration.config;

import com.nextimefood.msproduction.MsproductionApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@CucumberContextConfiguration
@SpringBootTest(
    classes = {
        MsproductionApplication.class,
        SqsTestConfig.class
    },
    webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@ActiveProfiles("test")
public class CucumberSpringConfig {
}
