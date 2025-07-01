package org.example.repository.base;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.example.entity.base.BaseEntity;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;


@Repository
public class BaseRepositoryImpl
        <T extends BaseEntity,
        ID extends Serializable>
        implements BaseRepository<T, ID> {

    @PersistenceContext
    private EntityManager em;

    protected Class<T> domainClass;

    public BaseRepositoryImpl(EntityManager em, Class<T> domainClass) {
        this.em = em;
        this.domainClass = domainClass;
    }


    public BaseRepositoryImpl() {

    }

    @Override
    public T save(T entity) {

        if (entity.getId() == null) {
            em.persist(entity);
        } else {
            entity = em.merge(entity);
        }
        return entity;
    }


    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(
                em.find(
                        domainClass,
                        id
                )
        );
    }


    @Override
    public List<T> findAll() {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(domainClass);
        Root<T> from = query.from(domainClass);
        query.select(from);
        return em.createQuery(query).getResultList();
    }



    @Override
    public long countAll() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<T> from = query.from(domainClass);
        query.select(cb.count(from));
        return em.createQuery(query).getSingleResult();
    }

    @Override
    public void deleteById(ID id) {
        findById(id).ifPresent(em::remove);
    }



    @Override
    public boolean existsById(ID id) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<T> from = query.from(domainClass);
        query.select(cb.count(from));
        query.where(
                cb.equal(from.get(BaseEntity.ID), id)
        );
        return em.createQuery(query).getSingleResult() > 0;

    }


    @Override
    public void beginTransaction() {
        em.getTransaction().begin();
    }

    @Override
    public void commitTransaction() {
        em.getTransaction().commit();
    }



}
