/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package backend.repositories;

import backend.entities.Notification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author vamilutinovic
 */
public interface NotificationRepository extends JpaRepository<Notification, Integer>{
    @Query(value = """
        SELECT n.* 
        FROM notification n 
        JOIN event e ON n.event_id = e.event_id 
        WHERE e.customer_id = :userId 
        ORDER BY n.date_time DESC""", nativeQuery = true)
    List<Notification> findByUserId(@Param("userId") Integer userId);
}
