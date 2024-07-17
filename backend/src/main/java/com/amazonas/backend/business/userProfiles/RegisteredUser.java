package com.amazonas.backend.business.userProfiles;

import com.amazonas.backend.business.payment.PaymentMethod;
import com.amazonas.common.abstracts.HasId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import com.amazonas.backend.business.payment.CreditCard;

import java.time.LocalDate;

@Entity
public class RegisteredUser implements User, HasId<String> {

    @Id
    private String userId;
    private String email;
    private LocalDate birthDate;
    @Transient
    private PaymentMethod paymentMethod;

    public RegisteredUser(){
        userId = "";
        email = "";
        birthDate = LocalDate.now();
    }

    public RegisteredUser(String userId, String email, LocalDate birthDate){
        this.userId = userId;
        this.email = email;
        this.birthDate = birthDate;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getBirthDate() {return birthDate.plusDays(0); }

    public PaymentMethod getPaymentMethod() {
        // not supported
        return new CreditCard("","","","","","","");
    }

    @Override
    public String getId() {
        return userId;
    }
}

