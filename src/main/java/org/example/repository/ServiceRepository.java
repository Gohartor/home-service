package org.example.repository;

import org.example.entity.Service;
import org.example.entity.User;
import org.example.repository.base.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface ServiceRepository
        extends BaseRepository<Service, Long> {

    long countByNameAndParentService(String name, Service parentService);

    List<Service> findByParentServiceIsNull();

    // ۱. بررسی یکتا بودن نام خدمت در سطح والد
    boolean existsByNameAndParentService(String name, Service parentService);

    // ۲. جستجو خدمات با نام و آی‌دی والد (برای کنترل یکتا)
    Optional<Service> findByNameAndParentService(String name, Service parentService);

    // ۳. پیدا کردن خدمات (والد/بدون والد/لیست خدمات)
    List<Service> findByParentService(Service parentService);

}