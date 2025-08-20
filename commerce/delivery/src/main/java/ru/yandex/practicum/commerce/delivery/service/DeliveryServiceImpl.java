package ru.yandex.practicum.commerce.delivery.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.commerce.delivery.mapper.DeliveryMapper;
import ru.yandex.practicum.commerce.delivery.model.Delivery;
import ru.yandex.practicum.commerce.delivery.model.DeliveryAddress;
import ru.yandex.practicum.commerce.delivery.repository.DeliveryRepository;
import ru.yandex.practicum.commerce.interactionapi.client.OrderClient;
import ru.yandex.practicum.commerce.interactionapi.client.WarehouseClient;
import ru.yandex.practicum.commerce.interactionapi.dto.delivery.DeliveryDto;
import ru.yandex.practicum.commerce.interactionapi.dto.delivery.DeliveryState;
import ru.yandex.practicum.commerce.interactionapi.dto.order.OrderDto;
import ru.yandex.practicum.commerce.interactionapi.dto.warehouse.ShippedToDeliveryRequest;
import ru.yandex.practicum.commerce.interactionapi.exception.NoDeliveryFoundException;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@AllArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {
    private static final BigDecimal BASE_PRICE = BigDecimal.valueOf(5.0);
    private static final BigDecimal OTHER_STREET_RATE = new BigDecimal("1.2");

    private final DeliveryRepository deliveryRepository;
    private final DeliveryMapper mapper;
    private final OrderClient orderClient;
    private final WarehouseClient warehouseClient;


    @Override
    @Transactional
    public DeliveryDto planDelivery(DeliveryDto deliveryDto) {
        Delivery delivery = mapper.toDelivery(deliveryDto);
        Delivery newDelivery = deliveryRepository.save(delivery);
        warehouseClient.shippedToDelivery(new ShippedToDeliveryRequest(newDelivery.getDeliveryId(), newDelivery.getOrderId()));
        return mapper.toDeliveryDto(newDelivery);
    }

    @Override
    @Transactional
    public void deliverySuccessful(UUID orderId) {
        Delivery delivery = deliveryRepository.findByOrderId(orderId).orElseThrow(NoDeliveryFoundException::new);
        delivery.setDeliveryState(DeliveryState.DELIVERED);
        Delivery updDelivery = deliveryRepository.save(delivery);
        orderClient.delivery(orderId);
    }

    @Override
    @Transactional
    public void deliveryPicked(UUID orderId) {
        Delivery delivery = deliveryRepository.findByOrderId(orderId).orElseThrow(NoDeliveryFoundException::new);
        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
        Delivery updDelivery = deliveryRepository.save(delivery);
        orderClient.complete(orderId);
    }

    @Override
    @Transactional
    public void deliveryFailed(UUID orderId) {
        Delivery delivery = deliveryRepository.findByOrderId(orderId).orElseThrow(NoDeliveryFoundException::new);
        delivery.setDeliveryState(DeliveryState.FAILED);
        Delivery updDelivery = deliveryRepository.save(delivery);
        orderClient.deliveryFailed(orderId);
    }

    @Override
    public BigDecimal deliveryCost(OrderDto order) {
        BigDecimal deliveryPrice = BigDecimal.ZERO;
        BigDecimal addressRate = new BigDecimal("1");
        Delivery delivery = deliveryRepository.findByOrderId(order.getOrderId()).orElseThrow(NoDeliveryFoundException::new);
        DeliveryAddress fromAddress = delivery.getDeliveryFromAddress();
        DeliveryAddress toAddress = delivery.getDeliveryToAddress();

        if (fromAddress.toString().contains("ADDRESS_2")) {
            addressRate = BigDecimal.valueOf(2);
        }
        deliveryPrice = BASE_PRICE.add(addressRate.multiply(BASE_PRICE));
        if (delivery.isFragile()) {
            deliveryPrice = deliveryPrice.add(deliveryPrice.multiply(BigDecimal.valueOf(0.2)));
        }
        deliveryPrice = deliveryPrice.add(BigDecimal.valueOf(0.3 * delivery.getDeliveryWeight()));
        deliveryPrice = deliveryPrice.add(BigDecimal.valueOf(0.2 * delivery.getDeliveryVolume()));
        if (!fromAddress.getStreet().equals(toAddress.getStreet())) {
            deliveryPrice = deliveryPrice.multiply(OTHER_STREET_RATE);
        }
        return deliveryPrice;
    }
}
