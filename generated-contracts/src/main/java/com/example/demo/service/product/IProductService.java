package com.example.demo.service.product;
import com.example.demo.model.ProductEntity;
import com.example.demo.service.IGenericService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

public interface IProductService extends IGenericService<ProductEntity> {
    Page<ProductEntity> findAllByCategoryId(Long categoryId, Pageable pageable);
    void updateStatus(Long id, Boolean isShow);
    Page<ProductEntity> fullTextSearch(@Param("categoryId") Long categoryId,
                                            @Param("keyword") String keyword,
                                            Pageable pageable);
}
