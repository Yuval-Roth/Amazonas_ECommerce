package com.amazonas.backend.business.userProfiles;

import com.amazonas.backend.business.payment.PaymentMethod;

public interface User {

    String getUserId();

    PaymentMethod getPaymentMethod();

    void setPaymentMethod(PaymentMethod paymentMethod);
}
