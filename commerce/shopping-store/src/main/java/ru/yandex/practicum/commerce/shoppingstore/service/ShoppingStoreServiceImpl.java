package ru.yandex.practicum.commerce.shoppingstore.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.commerce.interactionapi.dto.ProductCategory;
import ru.yandex.practicum.commerce.interactionapi.dto.ProductDto;
import ru.yandex.practicum.commerce.interactionapi.dto.ProductQuantityState;
import ru.yandex.practicum.commerce.interactionapi.dto.ProductState;
import ru.yandex.practicum.commerce.interactionapi.exception.ProductNotFoundException;
import ru.yandex.practicum.commerce.shoppingstore.mapper.ProductMapper;
import ru.yandex.practicum.commerce.shoppingstore.model.Product;
import ru.yandex.practicum.commerce.shoppingstore.repository.ProductRepository;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ShoppingStoreServiceImpl implements ShoppingStoreService {
    private final ProductRepository productRepository;
    private final ProductMapper mapper;

    @Override
    public ProductDto createNewProduct(ProductDto productDto) {
        Product product = mapper.toProduct(productDto);
        product = productRepository.save(product);
        return mapper.toProductDto(product);
    }

    @Override
    public ProductDto getProduct(UUID productId) {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isEmpty()) {
            throw new ProductNotFoundException(productId);
        }
        return mapper.toProductDto(product.get());
    }

    @Override
    public Page<ProductDto> getProducts(ProductCategory category, Pageable pageable) {
        Page<ProductDto> products = productRepository.findAllByProductCategory(category.toString(), pageable).map(mapper::toProductDto);
        return products;
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        Optional<Product> product = productRepository.findById(productDto.getProductId());
        if (product.isEmpty()) {
            throw new ProductNotFoundException(productDto.getProductId());
        }
        Product updProduct = mapper.toProduct(productDto);
        updProduct = productRepository.save(updProduct);
        return mapper.toProductDto(updProduct);
    }

    @Override
    public boolean removeProduct(UUID productId) {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isEmpty()) {
            throw new ProductNotFoundException(productId);
        }
        product.get().setProductState(ProductState.DEACTIVATE.toString());
        Product updProduct = productRepository.save(product.get());
        return true;
    }

    @Override
    public boolean setProductQuantityState(UUID productId, ProductQuantityState state) {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isEmpty()) {
            throw new ProductNotFoundException(productId);
        }
        product.get().setQuantityState(state.toString());
        Product updProduct = productRepository.save((product.get()));
        return true;
    }
}
