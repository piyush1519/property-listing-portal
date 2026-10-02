package com.propertyportal.backend.service;

import com.propertyportal.backend.entity.Property;
import com.propertyportal.backend.repository.PropertyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PropertyService {

    private final PropertyRepository propertyRepository;

    public PropertyService(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    public Optional<Property> getPropertyById(Long id) {
        return propertyRepository.findById(id);
    }

    public Property createProperty(Property property) {
        return propertyRepository.save(property);
    }

    public Property updateProperty(Long id, Property updatedProperty) {
        return propertyRepository.findById(id)
                .map(existingProperty -> {
                    existingProperty.setTitle(updatedProperty.getTitle());
                    existingProperty.setDescription(updatedProperty.getDescription());
                    existingProperty.setLocation(updatedProperty.getLocation());
                    existingProperty.setType(updatedProperty.getType());
                    existingProperty.setPrice(updatedProperty.getPrice());
                    existingProperty.setBedrooms(updatedProperty.getBedrooms());
                    existingProperty.setArea(updatedProperty.getArea());
                    existingProperty.setOwnerAgent(updatedProperty.getOwnerAgent());
                    existingProperty.setStatus(updatedProperty.getStatus());

                    return propertyRepository.save(existingProperty);
                })
                .orElseThrow(() -> new RuntimeException("Property not found with id: " + id));
    }

    public void deleteProperty(Long id) {
        propertyRepository.deleteById(id);
    }
}