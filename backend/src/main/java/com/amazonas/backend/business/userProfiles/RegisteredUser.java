package com.amazonas.backend.business.userProfiles;

import com.amazonas.common.abstracts.HasId;

import java.time.LocalDate;

public class RegisteredUser extends User implements HasId<String> {

    private String email;
    private LocalDate birthDate;

    public RegisteredUser(){
        super("");
    }

    public RegisteredUser(String userId, String email, LocalDate birthDate){
        super(userId);
        this.email = email;
        this.birthDate = birthDate;
    }

    @Override
    public String getUserId() {
        return super.getUserId();
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getBirthDate() {return birthDate.plusDays(0); }

    @Override
    public String getId() {
        return getUserId();
    }
}

