package ru.yandex.practicum.commerce.payment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.interactionapi.dto.order.OrderDto;
import ru.yandex.practicum.commerce.interactionapi.dto.payment.PaymentDto;
import ru.yandex.practicum.commerce.interactionapi.interfase.PaymentOperations;
import ru.yandex.practicum.commerce.payment.service.PaymentService;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/payment")
public class PaymentController implements PaymentOperations {
    private final PaymentService paymentService;

    @Override
    @PostMapping
    public PaymentDto payment(OrderDto order) {
        log.info("--> POST запрос для формирования оплаты: {}", order);
        PaymentDto payment = paymentService.payment(order);
        log.info("<-- POST ответ payment={}", payment);
        return payment;
    }

    @Override
    @PostMapping("/totalCost")
    public BigDecimal getTotalCost(OrderDto order) {
        log.info("--> POST запрос для расчёта полной стоимости заказа: {}", order);
        BigDecimal result = paymentService.getTotalCost(order);
        log.info("<-- POST ответ result={}", result);
        return result;
    }

    @Override
    @PostMapping("/refund")
    public void paymentSuccess(UUID paymentId) {
        log.info("--> POST запрос для эмуляции успешной оплаты в платежном шлюзе: {}", paymentId);
        paymentService.paymentSuccess(paymentId);
        log.info("<-- POST ответ об успешной оплате");
    }

    @Override
    @PostMapping("/productCost")
    public BigDecimal productCost(OrderDto order) {
        log.info("--> POST запрос для расчёта стоимости товаров в заказе: {}", order);
        BigDecimal result = paymentService.productCost(order);
        log.info("<-- POST ответ result={}", result);
        return result;
    }

    @Override
    @PostMapping("/failed")
    public void paymentFailed(UUID paymentId) {
        log.info("--> POST запрос для эмуляции отказа в оплате платежного шлюза: {}", paymentId);
        paymentService.paymentFailed(paymentId);
        log.info("<-- POST ответ об успешной оплате");

    }
}
