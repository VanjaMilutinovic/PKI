/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Vanjy
 */
@Entity
@Table(name = "event_offer")
@NamedQueries({
    @NamedQuery(name = "EventOffer.findAll", query = "SELECT e FROM EventOffer e"),
    @NamedQuery(name = "EventOffer.findByEventOfferId", query = "SELECT e FROM EventOffer e WHERE e.eventOfferId = :eventOfferId"),
    @NamedQuery(name = "EventOffer.findByName", query = "SELECT e FROM EventOffer e WHERE e.name = :name"),
    @NamedQuery(name = "EventOffer.findByPrice", query = "SELECT e FROM EventOffer e WHERE e.price = :price")})
public class EventOffer implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "event_offer_id")
    private Integer eventOfferId;
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    @Lob
    @Column(name = "description")
    private String description;
    @Basic(optional = false)
    @Lob
    @Column(name = "short_description")
    private String shortDescription;
    @Basic(optional = false)
    @Column(name = "price")
    private int price;
    @JoinColumn(name = "photo_id", referencedColumnName = "photo_id")
    @ManyToOne
    private Photo photoId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "eventOfferId")
    private List<Event> eventList;

    public EventOffer() {
    }

    public EventOffer(Integer eventOfferId) {
        this.eventOfferId = eventOfferId;
    }

    public EventOffer(Integer eventOfferId, String name, String shortDescription, int price) {
        this.eventOfferId = eventOfferId;
        this.name = name;
        this.shortDescription = shortDescription;
        this.price = price;
    }

    public Integer getEventOfferId() {
        return eventOfferId;
    }

    public void setEventOfferId(Integer eventOfferId) {
        this.eventOfferId = eventOfferId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Photo getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Photo photoId) {
        this.photoId = photoId;
    }

    public List<Event> getEventList() {
        return eventList;
    }

    public void setEventList(List<Event> eventList) {
        this.eventList = eventList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (eventOfferId != null ? eventOfferId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof EventOffer)) {
            return false;
        }
        EventOffer other = (EventOffer) object;
        if ((this.eventOfferId == null && other.eventOfferId != null) || (this.eventOfferId != null && !this.eventOfferId.equals(other.eventOfferId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "backend.entities.EventOffer[ eventOfferId=" + eventOfferId + " ]";
    }
    
}
