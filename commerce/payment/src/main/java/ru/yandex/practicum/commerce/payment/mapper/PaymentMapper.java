package ru.yandex.practicum.commerce.payment.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.commerce.interactionapi.dto.order.OrderDto;
import ru.yandex.practicum.commerce.interactionapi.dto.payment.PaymentDto;
import ru.yandex.practicum.commerce.interactionapi.dto.payment.PaymentStatus;
import ru.yandex.practicum.commerce.payment.model.Payment;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentMapper {
    public Payment toPayment(OrderDto order) {
        Payment payment = new Payment();
        payment.setPaymentId(UUID.randomUUID());
        payment.setOrderId(order.getOrderId());
        payment.setProductPrice(order.getProductPrice());
        payment.setDeliverPrice(order.getDeliveryPrice());
        payment.setTotalPrice(order.getTotalPrice());
        payment.setPaymentStatus(PaymentStatus.PENDING);

        return payment;
    }

    public PaymentDto toPaymentDto(Payment payment) {
        BigDecimal feeRate = new BigDecimal("0.1");

        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setPaymentId(payment.getPaymentId());
        paymentDto.setTotalPayment(payment.getTotalPrice());
        paymentDto.setDeliveryTotal(payment.getDeliverPrice());
        paymentDto.setFeeTotal(payment.getProductPrice().multiply(feeRate));

        return paymentDto;
    }
}
