package backend.controllers;

import backend.entities.*;
import backend.repositories.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class Controller {

    @Autowired private UserRepository userRepo;
    @Autowired private EventRepository eventRepo;
    @Autowired private EventOfferRepository offerRepo;
    @Autowired private EventStatusRepository statusRepo;
    @Autowired private NotificationRepository notificationRepo;
    @Autowired private PhotoRepository photoRepo;
    @Autowired private PromotionRepository promoRepo;

    // ========== USER ==========

    @PostMapping("/users/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        return ResponseEntity.status(201).body(userRepo.saveAndFlush(user));
    }

    @PostMapping("/users/login")
    public ResponseEntity<User> login(@RequestParam String username, @RequestParam String password) {
        User user = userRepo.findByUsernameAndPassword(username, password)
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));
        return ResponseEntity.ok(user);
    }

    @PostMapping("/users/{id}/update")
    public ResponseEntity<User> updateUser(@PathVariable Integer id, @RequestBody User updated) {
        User u = userRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found"));

        u.setUsername(updated.getUsername());
        u.setFirstName(updated.getFirstName());
        u.setLastName(updated.getLastName());
        u.setPhone(updated.getPhone());
        u.setAddress(updated.getAddress());

        return ResponseEntity.ok(userRepo.saveAndFlush(u));
    }

    @PostMapping("/users/{id}/password")
    public ResponseEntity<User> changePassword(@PathVariable Integer id, @RequestParam String newPassword) {
        User u = userRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found"));
        u.setPassword(newPassword);
        return ResponseEntity.ok(userRepo.saveAndFlush(u));
    }

    // ========== EVENTS ==========

    @GetMapping("/events")
    public ResponseEntity<List<Event>> getEvents() {
        EventStatus status = statusRepo.findById(2)
                .orElseThrow(() -> new RuntimeException("Status not found"));
        List<Event> events = eventRepo.findByEventStatusId(status);
        return ResponseEntity.ok(events);
    }


    @GetMapping("/events/{id}")
    public ResponseEntity<Event> getEvent(@PathVariable Integer id) {
        Event e = eventRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event with id " + id + " not found"));
        return ResponseEntity.ok(e);
    }

    @PostMapping("/events")
    public ResponseEntity<Event> addEvent(@RequestBody Event event) {
        return ResponseEntity.status(201).body(eventRepo.saveAndFlush(event));
    }

    // ========== OFFERS ==========

    @GetMapping("/offers")
    public ResponseEntity<List<EventOffer>> getOffers() {
        return ResponseEntity.ok(offerRepo.findAll());
    }

    @GetMapping("/offers/{id}")
    public ResponseEntity<EventOffer> getOffer(@PathVariable Integer id) {
        EventOffer offer = offerRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Offer with id " + id + " not found"));
        return ResponseEntity.ok(offer);
    }
    
    @PostMapping("/offers")
    public ResponseEntity<EventOffer> createEventOffer(@RequestBody EventOffer offer) {
        if (offer.getName() == null || offer.getShortDescription() == null || offer.getPrice() == null) {
            return ResponseEntity.badRequest().build();
        }

        // opcionalno: proveriti da li photo postoji
        if (offer.getPhotoId() != null && offer.getPhotoId().getPhotoId() != null) {
            Photo photo = photoRepo.findById(offer.getPhotoId().getPhotoId())
                    .orElseThrow(() -> new EntityNotFoundException("Photo not found with id " + offer.getPhotoId().getPhotoId()));
            offer.setPhotoId(photo);
        }

        EventOffer savedOffer = offerRepo.saveAndFlush(offer);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedOffer);
    }

    // ========== PROMOTIONS ==========

    @GetMapping("/promotions")
    public ResponseEntity<List<Promotion>> getPromotions() {
        return ResponseEntity.ok(promoRepo.findAll());
    }

    // ========== PHOTOS ==========

    @GetMapping("/photos/{eventId}")
    public ResponseEntity<Photo> getEventPhotos(@PathVariable Integer eventId) {
        Photo photo = photoRepo.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Photo with id " + eventId + " not found"));
        return ResponseEntity.ok(photo);
    }
    
    @PostMapping("/photos")
    public ResponseEntity<Photo> uploadPhoto(@RequestBody Map<String, String> payload) {
        String fileBase64 = payload.get("file");
        if (fileBase64 == null || fileBase64.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Photo photo = new Photo();
        photo.setFile(fileBase64);
        photo.setPath("uploaded"); // opcionalno
        photo = photoRepo.saveAndFlush(photo);

        return ResponseEntity.status(HttpStatus.CREATED).body(photo);
    }

    // ========== COMMENTS & RATINGS ==========

    @PostMapping("/events/{id}/comment")
    public ResponseEntity<Event> addComment(@PathVariable Integer id,
                                            @RequestParam String comment,
                                            @RequestParam Integer rating) {
        Event e = eventRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event with id " + id + " not found"));
        e.setComment(comment);
        e.setGrade(rating);
        return ResponseEntity.ok(eventRepo.saveAndFlush(e));
    }

    // ========== CART / SCHEDULING ==========

    @PostMapping("/users/{userId}/cart")
    public ResponseEntity<Event> addToCart(
            @PathVariable Integer userId,
            @RequestParam Integer offerId,
            @RequestParam int numberOfPeople,
            @RequestParam String date) { // promenjeno u String

        User customer = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));

        EventOffer offer = offerRepo.findById(offerId)
                .orElseThrow(() -> new EntityNotFoundException("Offer with id " + offerId + " not found"));

        EventStatus status = statusRepo.findById(1)
                .orElseThrow(() -> new EntityNotFoundException("Status IN_CART (id=1) not found"));

        // Parsiranje stringa u Date
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date parsedDate;
        try {
            parsedDate = formatter.parse(date);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date format. Expected yyyy-MM-dd");
        }

        Event event = new Event();
        event.setCustomerId(customer);
        event.setEventOfferId(offer);
        event.setEventStatusId(status);
        event.setNumberOfPeople(numberOfPeople);
        event.setDate(parsedDate);
        event.setComment(null);
        event.setGrade(null);

        return ResponseEntity.status(201).body(eventRepo.saveAndFlush(event));
    }

    @GetMapping("/users/{userId}/cart")
    public ResponseEntity<List<Event>> getCart(@PathVariable Integer userId) {
        User customer = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));
        EventStatus status = statusRepo.findById(1)
                .orElseThrow(() -> new EntityNotFoundException("Status IN_CART (id=1) not found"));

        return ResponseEntity.ok(eventRepo.findByCustomerIdAndEventStatusId(customer, status));
    }
    
    @PostMapping("/cart/{eventId}/delete")
    public ResponseEntity<Void> removeFromCart(@PathVariable Integer eventId) {
        if (!eventRepo.existsById(eventId)) {
            return ResponseEntity.notFound().build();
        }

        eventRepo.deleteById(eventId);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/users/{userId}/cart/confirm")
    public ResponseEntity<List<Event>> confirmCart(@PathVariable Integer userId) {
        User customer = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));
        EventStatus inCartStatus = statusRepo.findById(1)
                .orElseThrow(() -> new EntityNotFoundException("Status IN_CART (id=1) not found"));
        EventStatus pendingStatus = statusRepo.findById(4)
                .orElseThrow(() -> new EntityNotFoundException("Status PENDING (id=4) not found"));

        List<Event> inCartEvents = eventRepo.findByCustomerIdAndEventStatusId(customer, inCartStatus);
        inCartEvents.forEach(e -> e.setEventStatusId(pendingStatus));

        return ResponseEntity.ok(eventRepo.saveAllAndFlush(inCartEvents));
    }

    // ========== ORGANIZER ACTIONS ==========

    @PostMapping("/organizer/events/{eventId}/approve")
    public ResponseEntity<Event> approveEvent(@PathVariable Integer eventId) {
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id " + eventId + " not found"));
        EventStatus approved = statusRepo.findById(2)
                .orElseThrow(() -> new EntityNotFoundException("Status APPROVED (id=2) not found"));

        event.setEventStatusId(approved);

        Notification n = new Notification();
        n.setEventId(event);
        n.setMessage("Vaše zakazivanje za događaj " + event.getEventOfferId().getName() + " je ODOBRENO.");
        n.setDateTime(new Date());
        n.setIsRead(false);
        notificationRepo.saveAndFlush(n);

        return ResponseEntity.ok(eventRepo.saveAndFlush(event));
    }

    @PostMapping("/organizer/events/{eventId}/decline")
    public ResponseEntity<Event> declineEvent(@PathVariable Integer eventId) {
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id " + eventId + " not found"));
        EventStatus declined = statusRepo.findById(3)
                .orElseThrow(() -> new EntityNotFoundException("Status DECLINED (id=3) not found"));

        event.setEventStatusId(declined);

        Notification n = new Notification();
        n.setEventId(event);
        n.setMessage("Vaše zakazivanje za događaj " + event.getEventOfferId().getName() + " je ODBIJENO.");
        n.setDateTime(new Date());
        n.setIsRead(false);
        notificationRepo.saveAndFlush(n);

        return ResponseEntity.ok(eventRepo.saveAndFlush(event));
    }

    // ========== NOTIFICATIONS ==========

    @GetMapping("/users/{userId}/notifications")
    public ResponseEntity<List<Notification>> getNotifications(@PathVariable Integer userId) {
        User customer = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));
        return ResponseEntity.ok(notificationRepo.findByUserId(userId));
    }
    
    @PostMapping("/notifications/{id}/read")
    public ResponseEntity<Notification> markNotificationAsRead(@PathVariable Integer id) {
        Notification notification = notificationRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found with id " + id));

        notification.setIsRead(true);
        notificationRepo.saveAndFlush(notification);

        return ResponseEntity.ok(notification);
    }
    
    @GetMapping("/events/status/{statusId}")
    public ResponseEntity<List<Event>> getEventsByStatus(@PathVariable Integer statusId) {
        EventStatus status = statusRepo.findById(statusId)
                .orElseThrow(() -> new EntityNotFoundException("Status with id " + statusId + " not found"));
        List<Event> events = eventRepo.findByEventStatusId(status);
        return ResponseEntity.ok(events);
    }

}
