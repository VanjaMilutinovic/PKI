/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package backend.repositories;

import backend.entities.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author vamilutinovic
 */
public interface EventStatusRepository extends JpaRepository<EventStatus, Integer>{
    
}
