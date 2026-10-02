package com.propertyportal.backend.service;

import com.propertyportal.backend.entity.Property;
import com.propertyportal.backend.repository.PropertyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PropertyServiceTest {

    @Mock
    private PropertyRepository propertyRepository;

    @InjectMocks
    private PropertyService propertyService;

    @Test
    void shouldGetAllProperties() {
        Property property = new Property();
        property.setTitle("Test Property");

        when(propertyRepository.findAll())
                .thenReturn(List.of(property));

        List<Property> result = propertyService.getAllProperties();

        assertEquals(1, result.size());
        assertEquals("Test Property", result.get(0).getTitle());

        verify(propertyRepository).findAll();
    }

    @Test
    void shouldGetPropertyById() {
        Property property = new Property();
        property.setTitle("Test Property");

        when(propertyRepository.findById(1L))
                .thenReturn(Optional.of(property));

        Optional<Property> result = propertyService.getPropertyById(1L);

        assertTrue(result.isPresent());
        assertEquals("Test Property", result.get().getTitle());

        verify(propertyRepository).findById(1L);
    }

    @Test
    void shouldReturnEmptyWhenPropertyDoesNotExist() {
        when(propertyRepository.findById(999L))
                .thenReturn(Optional.empty());

        Optional<Property> result = propertyService.getPropertyById(999L);

        assertTrue(result.isEmpty());

        verify(propertyRepository).findById(999L);
    }

    @Test
    void shouldCreateProperty() {
        Property property = new Property();
        property.setTitle("New Property");

        when(propertyRepository.save(property))
                .thenReturn(property);

        Property result = propertyService.createProperty(property);

        assertNotNull(result);
        assertEquals("New Property", result.getTitle());

        verify(propertyRepository).save(property);
    }

    @Test
    void shouldDeleteProperty() {
        propertyService.deleteProperty(1L);

        verify(propertyRepository).deleteById(1L);
    }

    @Test
    void shouldSearchByLocation() {
        Property property = new Property();
        property.setLocation("Mumbai");

        when(propertyRepository.findByLocationContainingIgnoreCase("Mumbai"))
                .thenReturn(List.of(property));

        List<Property> result = propertyService.searchByLocation("Mumbai");

        assertEquals(1, result.size());
        assertEquals("Mumbai", result.get(0).getLocation());

        verify(propertyRepository)
                .findByLocationContainingIgnoreCase("Mumbai");
    }

    @Test
    void shouldSearchByType() {
        Property property = new Property();
        property.setType("Apartment");

        when(propertyRepository.findByTypeIgnoreCase("Apartment"))
                .thenReturn(List.of(property));

        List<Property> result = propertyService.searchByType("Apartment");

        assertEquals(1, result.size());
        assertEquals("Apartment", result.get(0).getType());

        verify(propertyRepository)
                .findByTypeIgnoreCase("Apartment");
    }

    @Test
    void shouldSearchByStatus() {
        Property property = new Property();
        property.setStatus("AVAILABLE");

        when(propertyRepository.findByStatusIgnoreCase("AVAILABLE"))
                .thenReturn(List.of(property));

        List<Property> result = propertyService.searchByStatus("AVAILABLE");

        assertEquals(1, result.size());
        assertEquals("AVAILABLE", result.get(0).getStatus());

        verify(propertyRepository)
                .findByStatusIgnoreCase("AVAILABLE");
    }

    @Test
    void shouldSearchByBedrooms() {
        Property property = new Property();
        property.setBedrooms(2);

        when(propertyRepository.findByBedrooms(2))
                .thenReturn(List.of(property));

        List<Property> result = propertyService.searchByBedrooms(2);

        assertEquals(1, result.size());
        assertEquals(2, result.get(0).getBedrooms());

        verify(propertyRepository)
                .findByBedrooms(2);
    }

    @Test
    void shouldSearchByLocationAndType() {
        Property property = new Property();
        property.setLocation("Mumbai");
        property.setType("Apartment");

        when(propertyRepository
                .findByLocationContainingIgnoreCaseAndTypeIgnoreCase(
                        "Mumbai",
                        "Apartment"))
                .thenReturn(List.of(property));

        List<Property> result =
                propertyService.searchByLocationAndType(
                        "Mumbai",
                        "Apartment");

        assertEquals(1, result.size());
        assertEquals("Mumbai", result.get(0).getLocation());
        assertEquals("Apartment", result.get(0).getType());

        verify(propertyRepository)
                .findByLocationContainingIgnoreCaseAndTypeIgnoreCase(
                        "Mumbai",
                        "Apartment");
    }

    @Test
    void shouldUpdateProperty() {
        Property existingProperty = new Property();
        existingProperty.setTitle("Old Title");
        existingProperty.setDescription("Old Description");
        existingProperty.setLocation("Mumbai");
        existingProperty.setType("Apartment");
        existingProperty.setPrice(5000000.0);
        existingProperty.setBedrooms(2);
        existingProperty.setArea(900.0);
        existingProperty.setOwnerAgent("Old Agent");
        existingProperty.setStatus("AVAILABLE");

        Property updatedProperty = new Property();
        updatedProperty.setTitle("Updated Title");
        updatedProperty.setDescription("Updated Description");
        updatedProperty.setLocation("Pune");
        updatedProperty.setType("Villa");
        updatedProperty.setPrice(8000000.0);
        updatedProperty.setBedrooms(3);
        updatedProperty.setArea(1500.0);
        updatedProperty.setOwnerAgent("New Agent");
        updatedProperty.setStatus("SOLD");

        when(propertyRepository.findById(1L))
                .thenReturn(Optional.of(existingProperty));

        when(propertyRepository.save(existingProperty))
                .thenReturn(existingProperty);

        Property result =
                propertyService.updateProperty(1L, updatedProperty);

        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Description", result.getDescription());
        assertEquals("Pune", result.getLocation());
        assertEquals("Villa", result.getType());
        assertEquals(8000000.0, result.getPrice());
        assertEquals(3, result.getBedrooms());
        assertEquals(1500.0, result.getArea());
        assertEquals("New Agent", result.getOwnerAgent());
        assertEquals("SOLD", result.getStatus());

        verify(propertyRepository).findById(1L);
        verify(propertyRepository).save(existingProperty);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingMissingProperty() {
        Property updatedProperty = new Property();

        when(propertyRepository.findById(999L))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> propertyService.updateProperty(999L, updatedProperty)
        );

        assertEquals(
                "Property not found with id: 999",
                exception.getMessage()
        );

        verify(propertyRepository).findById(999L);
        verify(propertyRepository, never()).save(any(Property.class));
    }
}