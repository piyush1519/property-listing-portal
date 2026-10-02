package com.propertyportal.backend.repository;

import com.propertyportal.backend.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyRepository extends JpaRepository<Property, Long> {
}