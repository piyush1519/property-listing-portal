package com.propertyportal.backend.repository;

import com.propertyportal.backend.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropertyRepository extends JpaRepository<Property, Long> {

    List<Property> findByLocationContainingIgnoreCase(String location);

    List<Property> findByTypeIgnoreCase(String type);

    List<Property> findByStatusIgnoreCase(String status);

    List<Property> findByBedrooms(Integer bedrooms);

    List<Property> findByLocationContainingIgnoreCaseAndTypeIgnoreCase(
            String location,
            String type
    );
}