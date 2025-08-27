import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { EventOffer } from '../models/EventOffer';

@Injectable({
  providedIn: 'root'
})
export class BackendService {

  baseUrl = 'http://localhost:8080/api/';

  constructor(private http: HttpClient) { }

  // ========== USER ==========
  register(user: any) {
    return this.http.post(`${this.baseUrl}users/register`, user);
  }

  login(username: string, password: string) {
    return this.http.post(`${this.baseUrl}users/login?username=${username}&password=${password}`, {});
  }

  updateUser(id: number, updated: any) {
    return this.http.post(`${this.baseUrl}users/${id}/update`, updated);
  }

  changePassword(id: number, newPassword: string) {
    return this.http.post(`${this.baseUrl}users/${id}/password?newPassword=${newPassword}`, {});
  }

  // ========== EVENTS ==========
  getEvents() {
    return this.http.get(`${this.baseUrl}events`);
  }

  getEvent(id: number) {
    return this.http.get(`${this.baseUrl}events/${id}`);
  }

  addEvent(event: any) {
    return this.http.post(`${this.baseUrl}events`, event);
  }

  // ========== OFFERS ==========
  getOffers() {
    return this.http.get(`${this.baseUrl}offers`);
  }

  getOffer(id: number) {
    return this.http.get(`${this.baseUrl}offers/${id}`);
  }

  // ========== PROMOTIONS ==========
  getPromotions() {
    return this.http.get(`${this.baseUrl}promotions`);
  }

  // ========== PHOTOS ==========
  getPhoto(eventId: number) {
    return this.http.get(`${this.baseUrl}photos/${eventId}`);
  }

  // ========== COMMENTS & RATINGS ==========
  addComment(eventId: number, comment: string, rating: number) {
    return this.http.post(`${this.baseUrl}events/${eventId}/comment?comment=${encodeURIComponent(comment)}&rating=${rating}`, {});
  }

  // ========== CART / SCHEDULING ==========
  addToCart(userId: number, offerId: number, numberOfPeople: number, date: Date) {
    const dateString = date instanceof Date ? date.toISOString().split('T')[0] : date;
  
    const params = new HttpParams()
      .set('offerId', offerId)
      .set('numberOfPeople', numberOfPeople)
      .set('date', dateString);
  
    return this.http.post(`${this.baseUrl}users/${userId}/cart`, {}, { params });
  }
  

  getCart(userId: number) {
    return this.http.get(`${this.baseUrl}users/${userId}/cart`);
  }

  removeFromCart(eventId: number) {
    const url = `${this.baseUrl}cart/${eventId}/delete`;
    return this.http.post(url, {}); 
  }
  
  confirmCart(userId: number) {
    return this.http.post(`${this.baseUrl}users/${userId}/cart/confirm`, {});
  }

  // ========== ORGANIZER ACTIONS ==========
  approveEvent(eventId: number) {
    return this.http.post(`${this.baseUrl}organizer/events/${eventId}/approve`, {});
  }

  declineEvent(eventId: number) {
    return this.http.post(`${this.baseUrl}organizer/events/${eventId}/decline`, {});
  }

  getEventsByStatus(statusId: number) {
    return this.http.get(`${this.baseUrl}events/status/${statusId}`);
  }

  addPhoto(base64File: string) {
    return this.http.post(`${this.baseUrl}photos`, { file: base64File });
  }
  
  addEventOffer(eventOffer: EventOffer) {
    return this.http.post(`${this.baseUrl}offers`, eventOffer);
  }
  

  // ========== NOTIFICATIONS ==========
  getNotifications(userId: number) {
    return this.http.get(`${this.baseUrl}users/${userId}/notifications`);
  }

  markNotificationAsRead(notificationId: number) {
    return this.http.post(`${this.baseUrl}notifications/${notificationId}/read`, {});
  }
}
