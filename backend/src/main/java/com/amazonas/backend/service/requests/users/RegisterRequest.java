package com.amazonas.backend.service.requests.users;

public record RegisterRequest(String email, String userId, String password) {
}