package com.example.artsphere.backend.service;

import com.example.artsphere.backend.dto.AddressRequest;
import com.example.artsphere.backend.dto.AddressResponse;
import com.example.artsphere.backend.model.Address;
import com.example.artsphere.backend.model.User;
import com.example.artsphere.backend.repository.AddressRepository;
import com.example.artsphere.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    public List<AddressResponse> getUserAddresses(Long userId) {
        return addressRepository.findByUserId(userId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public AddressResponse getAddressById(Long id) {
        Address address = addressRepository.findById(id).orElseThrow(() -> new RuntimeException("Nie znaleziono adresu"));
        return convertToResponse(address);
    }

    public AddressResponse createAddress(Long userId, AddressRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));
        Address address = new Address();
        address.setUser(user);
        address.setCity(request.getCity());
        address.setPostalCode(request.getPostalCode());
        address.setStreet(request.getStreet());
        address.setHouseNumber(request.getHouseNumber());
        address.setApartmentNumber(request.getApartmentNumber());

        return convertToResponse(addressRepository.save(address));
    }

    public AddressResponse updateAddress(Long addressId, Long userId, AddressRequest request) {
        Address address = addressRepository.findById(addressId).orElseThrow(() -> new RuntimeException("Adres nie znaleziony"));
        if (!address.getUser().getId().equals(userId)) throw new RuntimeException("Brak uprawnień");

        address.setCity(request.getCity());
        address.setPostalCode(request.getPostalCode());
        address.setStreet(request.getStreet());
        address.setHouseNumber(request.getHouseNumber());
        address.setApartmentNumber(request.getApartmentNumber());

        return convertToResponse(addressRepository.save(address));
    }

    public void deleteAddress(Long addressId, Long userId) {
        Address address = addressRepository.findById(addressId).orElseThrow(() -> new RuntimeException("Adres nie znaleziony"));
        if (!address.getUser().getId().equals(userId)) throw new RuntimeException("Brak uprawnień");
        addressRepository.delete(address);
    }

    public List<AddressResponse> getAllAddresses() {
        return addressRepository.findAll().stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    public AddressResponse adminUpdateAddress(Long addressId, AddressRequest request) {
        Address address = addressRepository.findById(addressId).orElseThrow(() -> new RuntimeException("Adres nie znaleziony"));
        address.setCity(request.getCity());
        address.setPostalCode(request.getPostalCode());
        address.setStreet(request.getStreet());
        address.setHouseNumber(request.getHouseNumber());
        address.setApartmentNumber(request.getApartmentNumber());
        return convertToResponse(addressRepository.save(address));
    }

    public void adminDeleteAddress(Long addressId) {
        addressRepository.delete(addressRepository.findById(addressId).orElseThrow(() -> new RuntimeException("Adres nie znaleziony")));
    }

    private AddressResponse convertToResponse(Address address) {
        return new AddressResponse(
                address.getId(), address.getUser().getId(), address.getUser().getUsername(),
                address.getCity(), address.getPostalCode(), address.getStreet(),
                address.getHouseNumber(), address.getApartmentNumber()
        );
    }
}