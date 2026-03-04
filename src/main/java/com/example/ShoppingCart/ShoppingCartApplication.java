package com.example.ShoppingCart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ShoppingCartApplication {
	public static void main(String[] args) {
		Logger.getInstance().info("Starting ShoppingCart application...");
		SpringApplication.run(ShoppingCartApplication.class, args);
	}
}