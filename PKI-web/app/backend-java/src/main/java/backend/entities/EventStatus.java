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
@Table(name = "event_status")
@NamedQueries({
    @NamedQuery(name = "EventStatus.findAll", query = "SELECT e FROM EventStatus e"),
    @NamedQuery(name = "EventStatus.findByEventStatusId", query = "SELECT e FROM EventStatus e WHERE e.eventStatusId = :eventStatusId"),
    @NamedQuery(name = "EventStatus.findByName", query = "SELECT e FROM EventStatus e WHERE e.name = :name")})
public class EventStatus implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "event_status_id")
    private Integer eventStatusId;
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "eventStatusId")
    private List<Event> eventList;

    public EventStatus() {
    }

    public EventStatus(Integer eventStatusId) {
        this.eventStatusId = eventStatusId;
    }

    public EventStatus(Integer eventStatusId, String name) {
        this.eventStatusId = eventStatusId;
        this.name = name;
    }

    public Integer getEventStatusId() {
        return eventStatusId;
    }

    public void setEventStatusId(Integer eventStatusId) {
        this.eventStatusId = eventStatusId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        hash += (eventStatusId != null ? eventStatusId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof EventStatus)) {
            return false;
        }
        EventStatus other = (EventStatus) object;
        if ((this.eventStatusId == null && other.eventStatusId != null) || (this.eventStatusId != null && !this.eventStatusId.equals(other.eventStatusId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "backend.entities.EventStatus[ eventStatusId=" + eventStatusId + " ]";
    }
    
}
