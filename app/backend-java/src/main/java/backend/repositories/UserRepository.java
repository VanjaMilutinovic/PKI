/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package backend.repositories;

import backend.entities.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Vanjy
 */
public interface UserRepository extends JpaRepository<User, Integer>{
    
    Optional<User> findByUsernameAndPassword(String username, String password);    
}
