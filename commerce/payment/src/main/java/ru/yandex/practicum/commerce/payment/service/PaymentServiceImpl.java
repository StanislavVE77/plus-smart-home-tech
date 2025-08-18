package ru.yandex.practicum.commerce.payment.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.commerce.interactionapi.client.OrderClient;
import ru.yandex.practicum.commerce.interactionapi.client.ShoppingStoreClient;
import ru.yandex.practicum.commerce.interactionapi.dto.order.OrderDto;
import ru.yandex.practicum.commerce.interactionapi.dto.payment.PaymentDto;
import ru.yandex.practicum.commerce.interactionapi.dto.payment.PaymentStatus;
import ru.yandex.practicum.commerce.interactionapi.dto.shoppingstore.ProductDto;
import ru.yandex.practicum.commerce.interactionapi.exception.NoPaymentFoundException;
import ru.yandex.practicum.commerce.payment.mapper.PaymentMapper;
import ru.yandex.practicum.commerce.payment.model.Payment;
import ru.yandex.practicum.commerce.payment.repository.PaymentRepository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper mapper;
    private final ShoppingStoreClient shoppingStoreClient;
    private final OrderClient orderClient;

    @Override
    public PaymentDto payment(OrderDto order) {
        Payment payment = mapper.toPayment(order);
        payment = paymentRepository.save(payment);
        return mapper.toPaymentDto(payment);
    }

    @Override
    public BigDecimal getTotalCost(OrderDto order) {
        BigDecimal feeRate = new BigDecimal("0.1");
        BigDecimal feeTotal = order.getProductPrice().multiply(feeRate);
        return order.getProductPrice().add(feeTotal).add(order.getDeliveryPrice());
    }

    @Override
    @Transactional
    public void paymentSuccess(UUID paymentId) {
        Optional<Payment> payment = paymentRepository.findById(paymentId);
        if (payment.isPresent()) {
            payment.get().setPaymentStatus(PaymentStatus.SUCCESS);
            paymentRepository.save(payment.get());
            orderClient.payment(payment.get().getOrderId());
        } else {
            throw new NoPaymentFoundException(paymentId);
        }
    }

    @Override
    @Transactional
    public BigDecimal productCost(OrderDto order) {
        BigDecimal productsTotalCost = BigDecimal.ZERO;
        Map<UUID, Long> products = order.getProducts();
        for (Map.Entry<UUID, Long> entry : order.getProducts().entrySet()) {
            ProductDto curProduct = shoppingStoreClient.getProduct(entry.getKey());
            BigDecimal curProductCost = curProduct.getPrice().multiply(BigDecimal.valueOf(entry.getValue()));
            productsTotalCost = productsTotalCost.add(curProductCost);
        }
        return productsTotalCost;
    }

    @Override
    @Transactional
    public void paymentFailed(UUID paymentId) {
        Optional<Payment> payment = paymentRepository.findById(paymentId);
        if (payment.isPresent()) {
            payment.get().setPaymentStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment.get());
            orderClient.paymentFailed(payment.get().getOrderId());
        }

    }
}
