package org.example.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import org.example.entity.Service;
import org.example.repository.base.BaseRepository;
import org.example.repository.base.BaseRepositoryImpl;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class ServiceRepositoryImpl
        extends BaseRepositoryImpl<Service, Long>
        implements ServiceRepository  {


    public ServiceRepositoryImpl() {
        this.domainClass = Service.class;
    }


    @Override
    public long countByNameParentService(String name, Service parentService) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Service> root = query.from(Service.class);

        Predicate condition = cb.equal(root.get("name"), name);

        if (parentService != null) {
            condition = cb.and(condition, cb.equal(root.get("parentService"), parentService));
        } else {
            condition = cb.and(condition, cb.isNull(root.get("parentService")));
        }

        query.select(cb.count(root)).where(condition);
        return em.createQuery(query).getSingleResult();
    }


    @Override
    public List<Service> findByParentService(Service parentService) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Service> query = cb.createQuery(Service.class);

        Root<Service> root = query.from(Service.class);
        if (parentService != null) {
            query.where(cb.equal(root.get("parentService"), parentService));
        } else {
            query.where(cb.isNull(root.get("parentService")));
        }


        return em.createQuery(query).getResultList();
    }


}
