package ru.yandex.practicum.commerce.interactionapi.dto;


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
    Long newQuantity;
    UUID productId;

}
