package org.example.service.base;

import lombok.RequiredArgsConstructor;
import org.example.entity.base.BaseEntity;
import org.example.repository.base.BaseRepository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class BaseServiceImpl<T extends BaseEntity, ID extends Serializable,R extends BaseRepository<T, ID>>
        implements BaseService<T, ID> {


    protected final R repository;

    @Override
    public T save(T entity) {
        repository.beginTransaction();
        entity = repository.save(entity);
        repository.commitTransaction();
        return entity;
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
        repository.beginTransaction();
        repository.deleteById(id);
        repository.commitTransaction();
    }


    @Override
    public boolean existsById(ID id) {
        return repository.existsById(id);
    }
}
