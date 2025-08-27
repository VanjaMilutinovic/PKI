/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Vanjy
 */
@Entity
@Table(name = "event")
@NamedQueries({
    @NamedQuery(name = "Event.findAll", query = "SELECT e FROM Event e"),
    @NamedQuery(name = "Event.findByEventId", query = "SELECT e FROM Event e WHERE e.eventId = :eventId"),
    @NamedQuery(name = "Event.findByNumberOfPeople", query = "SELECT e FROM Event e WHERE e.numberOfPeople = :numberOfPeople"),
    @NamedQuery(name = "Event.findByDate", query = "SELECT e FROM Event e WHERE e.date = :date"),
    @NamedQuery(name = "Event.findByGrade", query = "SELECT e FROM Event e WHERE e.grade = :grade"),
    @NamedQuery(name = "Event.findByStatus", query = "SELECT e FROM Event e WHERE e.eventStatusId = :status"),
    @NamedQuery(name = "Event.findByCustomerIdAndEventStatusId", query = "SELECT e FROM Event e WHERE e.customerId = :customer AND e.eventStatusId = :status")})
public class Event implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "event_id")
    private Integer eventId;
    @Basic(optional = false)
    @Column(name = "number_of_people")
    private int numberOfPeople;
    @Basic(optional = false)
    @Column(name = "date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    @Lob
    @Column(name = "comment")
    private String comment;
    @Column(name = "grade")
    private Integer grade;
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "eventId")
    private List<Notification> notificationList;
    @JoinColumn(name = "customer_id", referencedColumnName = "user_id")
    @ManyToOne(optional = false)
    private User customerId;
    @JsonIgnore
    @JoinColumn(name = "event_offer_id", referencedColumnName = "event_offer_id")
    @ManyToOne(optional = false)
    private EventOffer eventOfferId;
    @JoinColumn(name = "event_status_id", referencedColumnName = "event_status_id")
    @ManyToOne(optional = false)
    private EventStatus eventStatusId;

    public Event() {
    }

    public Event(Integer eventId) {
        this.eventId = eventId;
    }

    public Event(Integer eventId, int numberOfPeople, Date date) {
        this.eventId = eventId;
        this.numberOfPeople = numberOfPeople;
        this.date = date;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public int getNumberOfPeople() {
        return numberOfPeople;
    }

    public void setNumberOfPeople(int numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public List<Notification> getNotificationList() {
        return notificationList;
    }

    public void setNotificationList(List<Notification> notificationList) {
        this.notificationList = notificationList;
    }

    public User getCustomerId() {
        return customerId;
    }

    public void setCustomerId(User customerId) {
        this.customerId = customerId;
    }

    public EventOffer getEventOfferId() {
        return eventOfferId;
    }

    public void setEventOfferId(EventOffer eventOfferId) {
        this.eventOfferId = eventOfferId;
    }

    public EventStatus getEventStatusId() {
        return eventStatusId;
    }
    
    @JsonProperty("eventOfferId")
    public Object getTeamBasicInfo() {
        if (eventOfferId == null) 
            return null;

        final Integer id = eventOfferId.getEventOfferId();
        final String offerName = eventOfferId.getName();
        final String offerDesc = eventOfferId.getShortDescription();
        final Photo offerPhotoId = eventOfferId.getPhotoId();

        return new Object() {
            public Integer eventOfferId = id;
            public String name = offerName;
            public String shortDescription = offerDesc;
            public Photo photoId = offerPhotoId;
        };
    }

    public void setEventStatusId(EventStatus eventStatusId) {
        this.eventStatusId = eventStatusId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (eventId != null ? eventId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Event)) {
            return false;
        }
        Event other = (Event) object;
        if ((this.eventId == null && other.eventId != null) || (this.eventId != null && !this.eventId.equals(other.eventId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "backend.entities.Event[ eventId=" + eventId + " ]";
    }
    
}
