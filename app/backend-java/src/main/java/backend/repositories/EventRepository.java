/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package backend.repositories;

import backend.entities.Event;
import backend.entities.EventStatus;
import backend.entities.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Vanjy
 */
public interface EventRepository extends JpaRepository<Event, Integer>{
    List<Event> findByCustomerIdAndEventStatusId(User customer, EventStatus status);
    List<Event> findByEventStatusId(EventStatus eventStatusId);

}
