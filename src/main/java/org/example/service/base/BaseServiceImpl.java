package org.example.service.base;

import org.example.entity.base.BaseEntity;
import org.example.repository.base.BaseRepository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;



public class BaseServiceImpl
        <T extends BaseEntity,
                ID extends Serializable,
                R extends BaseRepository<T, ID>>
        implements BaseService<T, ID> {

    protected final R repository;

    public BaseServiceImpl(R repository) {
        this.repository = repository;
    }

    @Override
    public T save(T entity) {
        return repository.save(entity);
    }

    @Override
    public Optional<T> findById(ID id) {
        return repository.findById(id);
    }

    @Override
    public List<T> findAll() {
        return repository.findAll();
    }

    @Override
    public long countAll() {
        return repository.countAll();
    }

    @Override
    public void deleteById(ID id) {
        repository.deleteById(id);
    }

    @Override
    public boolean existsById(ID id) {
        return repository.existsById(id);
    }
}