package ru.yandex.practicum.commerce.shoppingstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "ru.yandex.practicum.commerce.interactionapi.client")
public class ShoppingStore {
    public static void main(String[] args) {
        SpringApplication.run(ShoppingStore.class, args);
    }

}
