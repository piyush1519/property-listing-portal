package com.propertyportal.backend.controller;

import com.propertyportal.backend.entity.Property;
import com.propertyportal.backend.service.PropertyService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/properties")
@CrossOrigin(origins = "*")
public class PropertyController {

    private final PropertyService propertyService;

    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    // Get all properties
    @GetMapping
    public List<Property> getAllProperties() {
        return propertyService.getAllProperties();
    }

    // Search/filter properties
    @GetMapping("/search")
    public List<Property> searchProperties(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer bedrooms
    ) {

        if (location != null && type != null) {
            return propertyService.searchByLocationAndType(location, type);
        }

        if (location != null) {
            return propertyService.searchByLocation(location);
        }

        if (type != null) {
            return propertyService.searchByType(type);
        }

        if (status != null) {
            return propertyService.searchByStatus(status);
        }

        if (bedrooms != null) {
            return propertyService.searchByBedrooms(bedrooms);
        }

        return propertyService.getAllProperties();
    }

    // Get property by ID
    @GetMapping("/{id}")
    public ResponseEntity<Property> getPropertyById(@PathVariable Long id) {
        return propertyService.getPropertyById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create property
    @PostMapping
    public ResponseEntity<Property> createProperty(
            @Valid @RequestBody Property property) {

        return ResponseEntity.ok(propertyService.createProperty(property));
    }

    // Update property
    @PutMapping("/{id}")
    public ResponseEntity<Property> updateProperty(
            @PathVariable Long id,
            @Valid @RequestBody Property property) {

        try {
            return ResponseEntity.ok(
                    propertyService.updateProperty(id, property)
            );
        } catch (RuntimeException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete property
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long id) {

        propertyService.deleteProperty(id);

        return ResponseEntity.noContent().build();
    }
}