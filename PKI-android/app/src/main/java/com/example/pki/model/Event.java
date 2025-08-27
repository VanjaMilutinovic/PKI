package com.example.pki.model;

import androidx.annotation.Nullable;
import java.util.Date;
import java.util.List;

public class Event {
    @Nullable
    public Integer eventId;
    public int numberOfPeople;
    public Date date;
    @Nullable
    public String comment;
    @Nullable
    public Integer grade;
    @Nullable
    public List<Notification> notificationList;
    public User customerId;
    public EventOffer eventOfferId;
    public EventStatus eventStatusId;
    public int hoverGrade = 0;
}
