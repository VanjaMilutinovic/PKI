/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package backend.request;

/**
 *
 * @author vamilutinovic
 */
public class CreateUserRequest {
    
    private String email;
    private String name;
    private String surname;
    private String phone;
    private String address;
    private String idToken;

    public CreateUserRequest() {
    }

    public CreateUserRequest(String idToken, String name, String surname, String email, String phone, String address) {
        this.idToken = idToken;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

    public boolean checkCreateUserRequest(){
        return idToken!=null &&
               name!=null &&
               surname!=null &&
               email!=null &&
               phone!=null &&
               address!=null;
    }

    @Override
    public String toString() {
        return "CreateUserRequest{" + "name=" + name + ", surname=" + surname + ", email=" + email + '}';
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
}
