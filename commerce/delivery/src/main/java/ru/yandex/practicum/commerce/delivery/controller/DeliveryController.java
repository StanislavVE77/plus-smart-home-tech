package ru.yandex.practicum.commerce.delivery.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.delivery.service.DeliveryService;
import ru.yandex.practicum.commerce.interactionapi.dto.delivery.DeliveryDto;
import ru.yandex.practicum.commerce.interactionapi.dto.order.OrderDto;
import ru.yandex.practicum.commerce.interactionapi.interfase.DeliveryOperations;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/delivery")
public class DeliveryController implements DeliveryOperations {
    private final DeliveryService deliveryService;

    @Override
    public DeliveryDto planDelivery(DeliveryDto deliveryDto) {
        log.info("--> PUT запрос для создания новой доставки: {}", deliveryDto);
        DeliveryDto newDeliveryDto = deliveryService.planDelivery(deliveryDto);
        log.info("<-- PUT ответ newDelivery={}", newDeliveryDto);
        return newDeliveryDto;
    }

    @Override
    public void deliverySuccessful(UUID orderId) {
        log.info("--> POST запрос для эмуляции успешной доставки товара: {}", orderId);
        deliveryService.deliverySuccessful(orderId);
        log.info("<-- POST ответ об успешной оплате");
    }

    @Override
    public void deliveryPicked(UUID orderId) {
        log.info("--> POST запрос для эмуляции получения товара в доставку: {}", orderId);
        deliveryService.deliveryPicked(orderId);
        log.info("<-- POST ответ об успешной оплате");
    }

    @Override
    public void deliveryFailed(UUID orderId) {
        log.info("--> POST запрос для эмуляции неудачного вручения товара: {}", orderId);
        deliveryService.deliveryFailed(orderId);
        log.info("<-- POST ответ об успешной оплате");
    }

    @Override
    public BigDecimal deliveryCost(OrderDto order) {
        log.info("--> POST запрос для расчёта полной стоимости доставки заказа: {}", order);
        BigDecimal result = deliveryService.deliveryCost(order);
        log.info("<-- POST ответ result={}", result);
        return result;
    }
}
