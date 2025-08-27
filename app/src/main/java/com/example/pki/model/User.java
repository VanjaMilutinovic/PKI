package com.example.pki.model;

import androidx.annotation.Nullable;

public class User {
    @Nullable
    public Integer userId;
    public String username;
    @Nullable
    public String firstName;
    @Nullable
    public String lastName;
    @Nullable
    public String phone;
    @Nullable
    public String address;
    public UserType userTypeId;
    public String password;
    @Nullable
    public Integer unreadNotificationCount = 0;
    @Nullable
    public Integer cartCount = 0;
}
