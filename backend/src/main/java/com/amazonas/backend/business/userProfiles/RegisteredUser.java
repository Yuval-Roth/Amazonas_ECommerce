package com.amazonas.backend.business.userProfiles;

public class RegisteredUser extends User{

    private String email;

    public RegisteredUser(){
        super("");
    }

    public RegisteredUser(String userId, String email){
        super(userId);
        this.email = email;
    }

    @Override
    public String getUserId() {
        return super.getUserId();
    }

    public String getEmail() {
        return email;
    }
}

