package com.example.pki.service;

import com.example.pki.model.*;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface BackendService {

    // ========== USER ==========
    @POST("users/register")
    Call<Void> register(@Body User user);

    @POST("users/login")
    Call<User> login(@Query("username") String username, @Query("password") String password);

    @POST("users/{id}/update")
    Call<Void> updateUser(@Path("id") int id, @Body User updated);

    @POST("users/{id}/password")
    Call<Void> changePassword(@Path("id") int id, @Query("newPassword") String newPassword);

    // ========== EVENTS ==========
    @GET("events")
    Call<List<Event>> getEvents();

    @GET("events/{id}")
    Call<Event> getEvent(@Path("id") int id);

    @POST("events")
    Call<Void> addEvent(@Body Event event);

    // ========== OFFERS ==========
    @GET("offers")
    Call<List<EventOffer>> getOffers();

    @GET("offers/{id}")
    Call<EventOffer> getOffer(@Path("id") int id);

    @POST("offers")
    Call<Void> addEventOffer(@Body EventOffer eventOffer);

    // ========== PROMOTIONS ==========
    @GET("promotions")
    Call<List<Promotion>> getPromotions();

    // ========== PHOTOS ==========
    @GET("photos/{eventId}")
    Call<Photo> getPhoto(@Path("eventId") int eventId);

    @POST("photos")
    Call<Photo> addPhoto(@Body Photo photo);

    // ========== COMMENTS & RATINGS ==========
    @POST("events/{eventId}/comment")
    Call<Void> addComment(@Path("eventId") int eventId,
                          @Query("comment") String comment,
                          @Query("rating") int rating);

    // ========== CART / SCHEDULING ==========
    @POST("users/{userId}/cart")
    Call<Void> addToCart(@Path("userId") int userId,
                         @Query("offerId") int offerId,
                         @Query("numberOfPeople") int numberOfPeople,
                         @Query("date") String date); // date u ISO formatu yyyy-MM-dd

    @GET("users/{userId}/cart")
    Call<List<Event>> getCart(@Path("userId") int userId);

    @POST("cart/{eventId}/delete")
    Call<Void> removeFromCart(@Path("eventId") int eventId);

    @POST("users/{userId}/cart/confirm")
    Call<Void> confirmCart(@Path("userId") int userId);

    // ========== ORGANIZER ACTIONS ==========
    @POST("organizer/events/{eventId}/approve")
    Call<Void> approveEvent(@Path("eventId") int eventId);

    @POST("organizer/events/{eventId}/decline")
    Call<Void> declineEvent(@Path("eventId") int eventId);

    @GET("events/status/{statusId}")
    Call<List<Event>> getEventsByStatus(@Path("statusId") int statusId);

    // ========== NOTIFICATIONS ==========
    @GET("users/{userId}/notifications")
    Call<List<Notification>> getNotifications(@Path("userId") int userId);

    @POST("notifications/{notificationId}/read")
    Call<Void> markNotificationAsRead(@Path("notificationId") int notificationId);
}
