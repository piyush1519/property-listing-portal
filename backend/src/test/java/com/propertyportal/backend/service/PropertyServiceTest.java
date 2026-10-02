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
}