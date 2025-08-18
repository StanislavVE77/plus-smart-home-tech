package ru.yandex.practicum.commerce.interactionapi.dto.shoppingcart;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ChangeProductQuantityRequest {
    private Long newQuantity;
    private UUID productId;

}
