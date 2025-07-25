package com.example.Task1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Task1Application {

	public static void main(String[] args) {
		SpringApplication.run(Task1Application.class, args);
	}

}

//	CREATE TABLE users (
//		id SERIAL PRIMARY KEY,
//		name VARCHAR(100) NOT NULL,
//	email VARCHAR(100) UNIQUE NOT NULL,
//	password TEXT NOT NULL,
//	contact_number VARCHAR(15),
//	user_type VARCHAR(20)
//);
//		select * from users;
//		CREATE TYPE user_type_enum AS ENUM ('admin', 'user', 'employee');
