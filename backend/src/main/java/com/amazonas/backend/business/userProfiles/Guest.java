package com.amazonas.backend.business.userProfiles;

import com.amazonas.backend.business.payment.PaymentMethod;

public class Guest implements User{

    private String id;
    private PaymentMethod paymentMethod;

    public Guest(String id){
        this.id = id;
    }

    @Override
    public String getUserId() {
        return id;
    }

    @Override
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    @Override
    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }


}
