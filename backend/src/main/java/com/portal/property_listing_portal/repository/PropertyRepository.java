package com.portal.property_listing_portal.repository;

import com.portal.property_listing_portal.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyRepository extends JpaRepository<Property, Long> {
}