package com.example.artsphere.backend.service;

import com.example.artsphere.backend.dto.AddressRequest;
import com.example.artsphere.backend.dto.AddressResponse;
import com.example.artsphere.backend.model.Address;
import com.example.artsphere.backend.model.User;
import com.example.artsphere.backend.repository.AddressRepository;
import com.example.artsphere.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AddressService addressService;

    private User testUser;
    private Address testAddress;
    private AddressRequest addressRequest;

    @BeforeEach
    void setUp() {
        // Przygotowanie testowego użytkownika
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("jan_kowalski");
        testUser.setEmail("jan@example.com");

        // Przygotowanie testowego adresu
        testAddress = new Address();
        testAddress.setId(1L);
        testAddress.setUser(testUser);
        testAddress.setCity("Warszawa");
        testAddress.setPostalCode("00-001");
        testAddress.setStreet("Marszałkowska");
        testAddress.setHouseNumber("10");
        testAddress.setApartmentNumber("5");

        // Przygotowanie request DTO
        addressRequest = new AddressRequest();
        addressRequest.setCity("Kraków");
        addressRequest.setPostalCode("30-001");
        addressRequest.setStreet("Floriańska");
        addressRequest.setHouseNumber("15");
        addressRequest.setApartmentNumber("3");
    }

    //  getUserAddresses

    @Test
    @DisplayName("Should return list of user addresses")
    void shouldReturnListOfUserAddresses() {
        // Arrange
        Address address2 = new Address();
        address2.setId(2L);
        address2.setUser(testUser);
        address2.setCity("Gdańsk");
        address2.setPostalCode("80-001");
        address2.setStreet("Długa");
        address2.setHouseNumber("20");
        address2.setApartmentNumber("7");

        when(addressRepository.findByUserId(1L)).thenReturn(Arrays.asList(testAddress, address2));

        // Act
        List<AddressResponse> addresses = addressService.getUserAddresses(1L);

        // Assert
        assertEquals(2, addresses.size());
        assertEquals("Warszawa", addresses.get(0).getCity());
        assertEquals("Gdańsk", addresses.get(1).getCity());
        verify(addressRepository, times(1)).findByUserId(1L);
    }

    @Test
    @DisplayName("Should return empty list when user has no addresses")
    void shouldReturnEmptyListWhenUserHasNoAddresses() {
        // Arrange
        when(addressRepository.findByUserId(1L)).thenReturn(Collections.emptyList());

        // Act
        List<AddressResponse> addresses = addressService.getUserAddresses(1L);

        // Assert
        assertTrue(addresses.isEmpty());
        verify(addressRepository, times(1)).findByUserId(1L);
    }

    // getAddressById

    @Test
    @DisplayName("Should return address by ID")
    void shouldReturnAddressById() {
        // Arrange
        when(addressRepository.findById(1L)).thenReturn(Optional.of(testAddress));

        // Act
        AddressResponse response = addressService.getAddressById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Warszawa", response.getCity());
        assertEquals("00-001", response.getPostalCode());
        assertEquals("Marszałkowska", response.getStreet());
        assertEquals("10", response.getHouseNumber());
        assertEquals("5", response.getApartmentNumber());
        assertEquals(1L, response.getUserId());
        assertEquals("jan_kowalski", response.getUsername());
        verify(addressRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when address not found by ID")
    void shouldThrowExceptionWhenAddressNotFoundById() {
        // Arrange
        when(addressRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> addressService.getAddressById(99L));
        assertEquals("Nie znaleziono adresu", exception.getMessage());
    }

    // createAddress

    @Test
    @DisplayName("Should create new address successfully")
    void shouldCreateNewAddressSuccessfully() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> {
            Address savedAddress = invocation.getArgument(0);
            savedAddress.setId(3L);
            return savedAddress;
        });

        // Act
        AddressResponse response = addressService.createAddress(1L, addressRequest);

        // Assert
        assertNotNull(response);
        assertEquals("Kraków", response.getCity());
        assertEquals("30-001", response.getPostalCode());
        assertEquals("Floriańska", response.getStreet());
        assertEquals("15", response.getHouseNumber());
        assertEquals("3", response.getApartmentNumber());
        assertEquals(1L, response.getUserId());
        verify(userRepository, times(1)).findById(1L);
        verify(addressRepository, times(1)).save(any(Address.class));
    }

    @Test
    @DisplayName("Should throw exception when user not found during address creation")
    void shouldThrowExceptionWhenUserNotFoundDuringCreation() {
        // Arrange
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> addressService.createAddress(99L, addressRequest));
        assertEquals("Użytkownik nie znaleziony", exception.getMessage());
        verify(addressRepository, never()).save(any());
    }

    // updateAddress

    @Test
    @DisplayName("Should update address successfully when user is owner")
    void shouldUpdateAddressSuccessfullyWhenUserIsOwner() {
        // Arrange
        when(addressRepository.findById(1L)).thenReturn(Optional.of(testAddress));
        when(addressRepository.save(any(Address.class))).thenReturn(testAddress);

        // Act
        AddressResponse response = addressService.updateAddress(1L, 1L, addressRequest);

        // Assert
        assertNotNull(response);
        assertEquals("Kraków", response.getCity());
        assertEquals("30-001", response.getPostalCode());
        assertEquals("Floriańska", response.getStreet());
        verify(addressRepository, times(1)).findById(1L);
        verify(addressRepository, times(1)).save(testAddress);
    }

    @Test
    @DisplayName("Should throw exception when address not found during update")
    void shouldThrowExceptionWhenAddressNotFoundDuringUpdate() {
        // Arrange
        when(addressRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> addressService.updateAddress(99L, 1L, addressRequest));
        assertEquals("Adres nie znaleziony", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when user is not address owner")
    void shouldThrowExceptionWhenUserIsNotAddressOwner() {
        // Arrange
        when(addressRepository.findById(1L)).thenReturn(Optional.of(testAddress));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> addressService.updateAddress(1L, 99L, addressRequest));
        assertEquals("Brak uprawnień", exception.getMessage());
        verify(addressRepository, never()).save(any());
    }

    // deleteAddress

    @Test
    @DisplayName("Should delete address successfully when user is owner")
    void shouldDeleteAddressSuccessfullyWhenUserIsOwner() {
        // Arrange
        when(addressRepository.findById(1L)).thenReturn(Optional.of(testAddress));
        doNothing().when(addressRepository).delete(testAddress);

        // Act
        addressService.deleteAddress(1L, 1L);

        // Assert
        verify(addressRepository, times(1)).findById(1L);
        verify(addressRepository, times(1)).delete(testAddress);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent address")
    void shouldThrowExceptionWhenDeletingNonExistentAddress() {
        // Arrange
        when(addressRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> addressService.deleteAddress(99L, 1L));
        assertEquals("Adres nie znaleziony", exception.getMessage());
        verify(addressRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should throw exception when user tries to delete address they don't own")
    void shouldThrowExceptionWhenUserTriesToDeleteAddressTheyDontOwn() {
        // Arrange
        when(addressRepository.findById(1L)).thenReturn(Optional.of(testAddress));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> addressService.deleteAddress(1L, 99L));
        assertEquals("Brak uprawnień", exception.getMessage());
        verify(addressRepository, never()).delete(any());
    }

    // getAllAddresses

    @Test
    @DisplayName("Should return all addresses in system")
    void shouldReturnAllAddressesInSystem() {
        // Arrange
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("anna_nowak");

        Address address2 = new Address();
        address2.setId(2L);
        address2.setUser(user2);
        address2.setCity("Poznań");
        address2.setPostalCode("60-001");
        address2.setStreet("Półwiejska");
        address2.setHouseNumber("30");

        when(addressRepository.findAll()).thenReturn(Arrays.asList(testAddress, address2));

        // Act
        List<AddressResponse> addresses = addressService.getAllAddresses();

        // Assert
        assertEquals(2, addresses.size());
        assertEquals("Warszawa", addresses.get(0).getCity());
        assertEquals("Poznań", addresses.get(1).getCity());
        verify(addressRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no addresses exist")
    void shouldReturnEmptyListWhenNoAddressesExist() {
        // Arrange
        when(addressRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<AddressResponse> addresses = addressService.getAllAddresses();

        // Assert
        assertTrue(addresses.isEmpty());
    }

    // adminUpdateAddress

    @Test
    @DisplayName("Should update address as admin without ownership check")
    void shouldUpdateAddressAsAdminWithoutOwnershipCheck() {
        // Arrange
        when(addressRepository.findById(1L)).thenReturn(Optional.of(testAddress));
        when(addressRepository.save(any(Address.class))).thenReturn(testAddress);

        // Act
        AddressResponse response = addressService.adminUpdateAddress(1L, addressRequest);

        // Assert
        assertNotNull(response);
        assertEquals("Kraków", response.getCity());
        assertEquals("30-001", response.getPostalCode());
        assertEquals("Floriańska", response.getStreet());
        assertEquals("15", response.getHouseNumber());
        assertEquals("3", response.getApartmentNumber());
        verify(addressRepository, times(1)).findById(1L);
        verify(addressRepository, times(1)).save(testAddress);
    }

    @Test
    @DisplayName("Should throw exception when admin tries to update non-existent address")
    void shouldThrowExceptionWhenAdminTriesToUpdateNonExistentAddress() {
        // Arrange
        when(addressRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> addressService.adminUpdateAddress(99L, addressRequest));
        assertEquals("Adres nie znaleziony", exception.getMessage());
        verify(addressRepository, never()).save(any());
    }

    //  adminDeleteAddress

    @Test
    @DisplayName("Should delete address as admin without ownership check")
    void shouldDeleteAddressAsAdminWithoutOwnershipCheck() {
        // Arrange
        when(addressRepository.findById(1L)).thenReturn(Optional.of(testAddress));
        doNothing().when(addressRepository).delete(testAddress);

        // Act
        addressService.adminDeleteAddress(1L);

        // Assert
        verify(addressRepository, times(1)).findById(1L);
        verify(addressRepository, times(1)).delete(testAddress);
    }

    @Test
    @DisplayName("Should throw exception when admin tries to delete non-existent address")
    void shouldThrowExceptionWhenAdminTriesToDeleteNonExistentAddress() {
        // Arrange
        when(addressRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> addressService.adminDeleteAddress(99L));
        assertEquals("Adres nie znaleziony", exception.getMessage());
        verify(addressRepository, times(1)).findById(99L);
    }

    // onvertToResponse (implicit testing)

    @Test
    @DisplayName("Should correctly map Address entity to AddressResponse DTO")
    void shouldCorrectlyMapAddressEntityToDTO() {
        // Arrange
        when(addressRepository.findById(1L)).thenReturn(Optional.of(testAddress));

        // Act
        AddressResponse response = addressService.getAddressById(1L);

        // Assert
        assertEquals(testAddress.getId(), response.getId());
        assertEquals(testAddress.getUser().getId(), response.getUserId());
        assertEquals(testAddress.getUser().getUsername(), response.getUsername());
        assertEquals(testAddress.getCity(), response.getCity());
        assertEquals(testAddress.getPostalCode(), response.getPostalCode());
        assertEquals(testAddress.getStreet(), response.getStreet());
        assertEquals(testAddress.getHouseNumber(), response.getHouseNumber());
        assertEquals(testAddress.getApartmentNumber(), response.getApartmentNumber());
    }
}