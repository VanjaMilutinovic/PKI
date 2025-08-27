package com.example.pki.model;

import androidx.annotation.Nullable;

import java.util.Date;

public class Notification {
    @Nullable
    public Integer notificationId;
    public Date dateTime;
    public boolean isRead;
    public String message;
    public Event eventId;
}
