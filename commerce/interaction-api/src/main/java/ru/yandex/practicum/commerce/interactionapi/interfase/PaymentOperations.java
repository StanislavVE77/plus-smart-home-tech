package ru.yandex.practicum.commerce.interactionapi.interfase;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.commerce.interactionapi.dto.order.OrderDto;
import ru.yandex.practicum.commerce.interactionapi.dto.payment.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentOperations {

    @PostMapping
    PaymentDto payment(@RequestBody OrderDto order);

    @PostMapping("/totalCost")
    BigDecimal getTotalCost(@RequestBody OrderDto order);

    @PostMapping("/refund")
    void paymentSuccess(@RequestBody UUID paymentId);

    @PostMapping("/productCost")
    BigDecimal productCost(@RequestBody OrderDto order);

    @PostMapping("/failed")
    void paymentFailed(@RequestBody UUID paymentId);

}
