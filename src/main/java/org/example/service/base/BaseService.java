package org.example.service.base;

import org.example.entity.base.BaseEntity;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface BaseService<T extends BaseEntity, ID extends Serializable> {

    T save(T entity);

    Optional<T> findById(ID id);

    List<T> findAll();

    long countAll();

    void deleteById(ID id);

    boolean existsById(ID id);
}
