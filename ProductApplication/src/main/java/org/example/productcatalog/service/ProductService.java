package org.example.productcatalog.service;

import jakarta.transaction.Transactional;
import org.example.productcatalog.dto.ProductDto;
import org.example.productcatalog.entity.Product;
import org.example.productcatalog.exception.ApplicationException;
import org.example.productcatalog.mapper.ProductMapper;
import org.example.productcatalog.repository.CrudRepository;
import org.example.productcatalog.util.cache.ProductCacheManager;
import org.example.productcatalog.util.specification.GetSpecification;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.example.productcatalog.preset.ProductCatalogInit.*;

@Service("ProductService")
public class ProductService implements CrudService<ProductDto, String> {
    private final CrudRepository<Product> repository;
    private final ProductMapper mapper;
    private final ProductCacheManager productCacheManager;

    @Autowired
    public ProductService(CrudRepository<Product> repository, ProductMapper mapper, ProductCacheManager productCacheManager) {
        this.repository = repository;
        this.mapper = mapper;
        this.productCacheManager = productCacheManager;
    }

    /**
     * Requests product details database for new record creation.
     * Main product details database record creation.
     * @param data   product details data transfer object
     *
     * @return  product details data transfer object
     */
    @Transactional
    @Override
    public ProductDto create(ProductDto data) {
        return Optional.ofNullable(data).map(mapper::fromDto)
                .map(repository::add).map(productCacheManager::put).map(mapper::toDto)
                .orElseThrow(() -> new ApplicationException(PRODUCT_NOT_SPECIFIED));
    }

    /**
     * Requests product details database to update existing record.
     * Main product details database record update method.
     * @param data   product details data transfer object
     *
     * @return  product details data transfer object
     */
    @Override
    public ProductDto update(ProductDto data) {
        return Optional.ofNullable(data).map(ProductDto::getItem).flatMap(repository::getByKey)
                .map(x -> {
                    x.setBrand(Optional.ofNullable(data.getBrand()).orElse(x.getBrand()));
                    x.setTitle(Optional.ofNullable(data.getTitle()).orElse(x.getTitle()));
                    x.setCategory(Optional.ofNullable(data.getCategory()).orElse(x.getCategory()));
                    x.setPrice(Optional.ofNullable(data.getPrice()).orElse(x.getPrice()));
                    return repository.update(x);
                }).map(productCacheManager::put).map(mapper::toDto)
                .orElseThrow(() -> new ApplicationException(PRODUCT_NOT_SPECIFIED));
    }

    /**
     * Requests product details database to delete existing record.
     * Main product details database record delete method.
     * @param data  product details data transfer object
     *
     * @return  product details data transfer object
     */
    @Transactional
    @Override
    public ProductDto remove(ProductDto data) {
        Optional.ofNullable(data).ifPresent(x -> productCacheManager.clear(x.getItem()));
        return Optional.ofNullable(data).map(mapper::fromDto)
                .map(repository::delete).map(mapper::toDto)
                .orElseThrow(() -> new ApplicationException(PRODUCT_NOT_SPECIFIED));
    }

    /**
     * Requests product database for a filtered list.
     * Main product database constrained sample list method.
     * @param criteria   set of sought values for filter attributes
     * @param pageable  product list pagination criteria object
     *
     * @return  set of product database record representation objects
     */
    @Override
    public Slice<ProductDto> findFiltered(Map<String, ? extends Comparable<?>> criteria, Pageable pageable) {
        List<ProductDto> result = repository.get(new GetSpecification<>(criteria), pageable).stream()
                .map(mapper::toDto).toList();
        return new SliceImpl<>(result, pageable, result.iterator().hasNext());
    }

    /**
     * Requests product details database for all records.
     * Supplementary product details database record receiving method.
     *
     * @return  set of details data transfer objects
     */
    @Override
    public Collection<ProductDto> findAll() {
        return repository.get(new GetSpecification<>(Map.of()), PageRequest.of(0, 20)).stream()
                .map(mapper::toDto).toList();
    }

    /**
     * Requests product details database for the record matching specified product item number.
     * Supplementary product details database record receiving method.
     * @param item  sought product details product item number
     *
     * @return  product details data transfer object
     */
    @Override
    public ProductDto find(String item) {
        return Optional.ofNullable(item).map(x -> productCacheManager.get(x)
                        .orElse(repository.getByKey(x).orElseThrow(() -> new ApplicationException(PRODUCT_NOT_FOUND))))
                .map(mapper::toDto).orElseThrow(() -> new ApplicationException(PRODUCT_NOT_SPECIFIED));
    }

    /**
     * Requests product details database for the record matching specified record id.
     * Supplementary product details database record receiving method.
     * @param id  sought product details id
     *
     * @return  product details data transfer object
     */
    @Override
    public ProductDto findById(long id) {
        return repository.getById(id).map(mapper::toDto).orElseThrow(() -> new ApplicationException(PRODUCT_NOT_FOUND));
    }
}
