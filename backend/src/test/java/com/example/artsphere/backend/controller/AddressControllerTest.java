package com.example.artsphere.backend.controller;

import com.example.artsphere.backend.dto.AddressRequest;
import com.example.artsphere.backend.dto.AddressResponse;
import com.example.artsphere.backend.service.AddressService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddressControllerTest {

    @Mock
    private AddressService addressService;

    @InjectMocks
    private AddressController addressController;

    @Test
    void getUserAddressesShouldReturnAddressesList() {
        AddressResponse address = new AddressResponse(1L, 10L, "jan", "Warszawa", "00-001", "Marszalkowska", "10", "1");
        when(addressService.getUserAddresses(10L)).thenReturn(List.of(address));

        ResponseEntity<List<AddressResponse>> result = addressController.getUserAddresses(10L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
        assertEquals("Warszawa", result.getBody().getFirst().getCity());
        verify(addressService).getUserAddresses(10L);
    }

    @Test
    void createAddressShouldReturnBadRequestWhenServiceThrows() {
        AddressRequest request = new AddressRequest();
        request.setCity("Krakow");
        when(addressService.createAddress(10L, request)).thenThrow(new RuntimeException("Bledne dane"));

        ResponseEntity<?> result = addressController.createAddress(10L, request);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertInstanceOf(Map.class, result.getBody());
        assertEquals("Bledne dane", ((Map<?, ?>) result.getBody()).get("error"));
    }

    @Test
    void deleteAddressShouldReturnConfirmationMessage() {
        ResponseEntity<?> result = addressController.deleteAddress(1L, 10L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertInstanceOf(Map.class, result.getBody());
        assertEquals("Usunięto", ((Map<?, ?>) result.getBody()).get("message"));
        verify(addressService).deleteAddress(1L, 10L);
    }

    @Test
    void deleteAddressShouldReturnBadRequestWhenServiceThrows() {
        doThrow(new RuntimeException("Brak uprawnien")).when(addressService).deleteAddress(1L, 10L);

        ResponseEntity<?> result = addressController.deleteAddress(1L, 10L);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Brak uprawnien", ((Map<?, ?>) result.getBody()).get("error"));
    }
}
