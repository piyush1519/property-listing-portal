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

    // Get all properties
    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    // Search by location
    public List<Property> searchByLocation(String location) {
        return propertyRepository.findByLocationContainingIgnoreCase(location);
    }

    // Search by property type
    public List<Property> searchByType(String type) {
        return propertyRepository.findByTypeIgnoreCase(type);
    }

    // Search by status
    public List<Property> searchByStatus(String status) {
        return propertyRepository.findByStatusIgnoreCase(status);
    }

    // Search by number of bedrooms
    public List<Property> searchByBedrooms(Integer bedrooms) {
        return propertyRepository.findByBedrooms(bedrooms);
    }

    // Search by location and property type
    public List<Property> searchByLocationAndType(String location, String type) {
        return propertyRepository.findByLocationContainingIgnoreCaseAndTypeIgnoreCase(
                location,
                type
        );
    }

    // Get property by ID
    public Optional<Property> getPropertyById(Long id) {
        return propertyRepository.findById(id);
    }

    // Create property
    public Property createProperty(Property property) {
        return propertyRepository.save(property);
    }

    // Update property
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
                .orElseThrow(() ->
                        new RuntimeException("Property not found with id: " + id)
                );
    }

    // Delete property
    public void deleteProperty(Long id) {
        propertyRepository.deleteById(id);
    }
}