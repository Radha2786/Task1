package com.example.Task1.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    Tells JPA how to generate the value for the primary key when a new record is inserted into the database.
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    @Email(message = "Please provide a valid email address")
//    This annotation is used to validate that the value of the annotated field is a valid email address.
    private String email;

    @Column(name = "user_type", nullable = false)
    @Enumerated(EnumType.STRING)
//    This annotation is used to specify that a persistent property or field should be persisted as an enumerated type.
    private UserType userType;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "contact_number", nullable = false)
    private String contactNumber;

}
