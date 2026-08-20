package com.portal.property_listing_portal.service;

import com.portal.property_listing_portal.entity.Property;
import com.portal.property_listing_portal.entity.PropertyStatus;
import com.portal.property_listing_portal.repository.PropertyRepository;
import org.springframework.stereotype.Service;

@Service
public class PropertyService {

    private final PropertyRepository propertyRepository;

    public PropertyService(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    public Property createProperty(Property property) {
        if (property.getTitle() == null || property.getTitle().isBlank()) {
            throw new IllegalArgumentException("Title is required");
        }
        if (property.getLocation() == null || property.getLocation().isBlank()) {
            throw new IllegalArgumentException("Location is required");
        }
        if (property.getPropertyType() == null || property.getPropertyType().isBlank()) {
            throw new IllegalArgumentException("Property type is required");
        }
        if (property.getPrice() == null || property.getPrice() <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero");
        }
        property.setStatus(PropertyStatus.DRAFT);
        return propertyRepository.save(property);
    }
}