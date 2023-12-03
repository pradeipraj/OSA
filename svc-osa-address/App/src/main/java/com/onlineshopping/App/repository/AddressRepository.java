package com.onlineshopping.App.repository;


import com.onlineshopping.App.entity.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<AddressEntity,Long> {

    List<AddressEntity> findAll();
    Optional<AddressEntity> findById(Long Id);
    List<AddressEntity> findByUserId(String userId);

}
