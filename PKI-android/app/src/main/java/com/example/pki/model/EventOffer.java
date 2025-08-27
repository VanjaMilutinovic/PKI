package com.example.pki.model;

import androidx.annotation.Nullable;
import java.util.List;

public class EventOffer {
    @Nullable
    public Integer eventOfferId;
    public String name;
    @Nullable
    public String description;
    public String shortDescription;
    public double price;
    @Nullable
    public Photo photoId;
    @Nullable
    public List<Event> eventList;
}
