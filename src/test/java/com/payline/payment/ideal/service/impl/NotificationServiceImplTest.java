package com.payline.payment.ideal.service.impl;

import com.payline.pmapi.bean.notification.response.NotificationResponse;
import com.payline.pmapi.bean.notification.response.impl.IgnoreNotificationResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class NotificationServiceImplTest {

    @InjectMocks
    private NotificationServiceImpl service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    void parse() {
        NotificationResponse response = service.parse(null);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(IgnoreNotificationResponse.class, response.getClass());
    }

    @Test
    void notifyTransactionStatus() {
    }
}