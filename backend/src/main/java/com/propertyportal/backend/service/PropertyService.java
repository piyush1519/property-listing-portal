package com.propertyportal.backend.service;

import com.propertyportal.backend.entity.Property;
import com.propertyportal.backend.exception.ResourceNotFoundException;
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

    public List<Property> searchByLocation(String location) {
        return propertyRepository.findByLocationContainingIgnoreCase(location);
    }

    public List<Property> searchByType(String type) {
        return propertyRepository.findByTypeIgnoreCase(type);
    }

    public List<Property> searchByStatus(String status) {
        return propertyRepository.findByStatusIgnoreCase(status);
    }

    public List<Property> searchByBedrooms(Integer bedrooms) {
        return propertyRepository.findByBedrooms(bedrooms);
    }

    public List<Property> searchByLocationAndType(String location, String type) {
        return propertyRepository.findByLocationContainingIgnoreCaseAndTypeIgnoreCase(
                location, type
        );
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
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Property not found with id: " + id
                        )
                );
    }

    public void deleteProperty(Long id) {
        if (!propertyRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Property not found with id: " + id
            );
        }

        propertyRepository.deleteById(id);
    }
}