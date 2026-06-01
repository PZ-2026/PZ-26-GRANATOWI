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

/**
 * Serwis logiki biznesowej dla adresów.
 */
@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Zwraca listę adresów przypisanych do użytkownika.
     *
     * @param userId identyfikator użytkownika, którego adresy mają zostać pobrane.
     * @return lista adresów użytkownika w formacie {@link AddressResponse}.
     */
    public List<AddressResponse> getUserAddresses(Long userId) {
        return addressRepository.findByUserId(userId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Pobiera pojedynczy adres po identyfikatorze.
     *
     * @param id identyfikator adresu.
     * @return adres w formacie {@link AddressResponse}.
     * @throws RuntimeException gdy adres nie istnieje.
     */
    public AddressResponse getAddressById(Long id) {
        Address address = addressRepository.findById(id).orElseThrow(() -> new RuntimeException("Nie znaleziono adresu"));
        return convertToResponse(address);
    }

    /**
     * Tworzy nowy adres dla użytkownika.
     *
     * @param userId identyfikator użytkownika, do którego ma zostać przypisany adres.
     * @param request dane adresu wprowadzone przez użytkownika.
     * @return nowo utworzony adres w formacie {@link AddressResponse}.
     * @throws RuntimeException gdy użytkownik nie istnieje.
     */
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

    /**
     * Aktualizuje istniejący adres użytkownika.
     * Weryfikuje, czy adres należy do wskazanego użytkownika.
     *
     * @param addressId identyfikator adresu do aktualizacji.
     * @param userId identyfikator użytkownika, który jest właścicielem adresu.
     * @param request nowe dane adresu.
     * @return zaktualizowany adres w formacie {@link AddressResponse}.
     * @throws RuntimeException gdy adres nie istnieje lub nie należy do użytkownika.
     */
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

    /**
     * Usuwa adres użytkownika po weryfikacji właściciela.
     *
     * @param addressId identyfikator adresu do usunięcia.
     * @param userId identyfikator użytkownika będącego właścicielem adresu.
     * @throws RuntimeException gdy adres nie istnieje lub nie należy do użytkownika.
     */
    public void deleteAddress(Long addressId, Long userId) {
        Address address = addressRepository.findById(addressId).orElseThrow(() -> new RuntimeException("Adres nie znaleziony"));
        if (!address.getUser().getId().equals(userId)) throw new RuntimeException("Brak uprawnień");
        addressRepository.delete(address);
    }

    /**
     * Zwraca wszystkie adresy w systemie (widok administracyjny).
     *
     * @return lista wszystkich adresów w formacie {@link AddressResponse}.
     */
    public List<AddressResponse> getAllAddresses() {
        return addressRepository.findAll().stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    /**
     * Aktualizuje adres w trybie administracyjnym (bez weryfikacji właściciela).
     *
     * @param addressId identyfikator adresu do aktualizacji.
     * @param request nowe dane adresu.
     * @return zaktualizowany adres w formacie {@link AddressResponse}.
     * @throws RuntimeException gdy adres nie istnieje.
     */
    public AddressResponse adminUpdateAddress(Long addressId, AddressRequest request) {
        Address address = addressRepository.findById(addressId).orElseThrow(() -> new RuntimeException("Adres nie znaleziony"));
        address.setCity(request.getCity());
        address.setPostalCode(request.getPostalCode());
        address.setStreet(request.getStreet());
        address.setHouseNumber(request.getHouseNumber());
        address.setApartmentNumber(request.getApartmentNumber());
        return convertToResponse(addressRepository.save(address));
    }

    /**
     * Usuwa adres w trybie administracyjnym.
     *
     * @param addressId identyfikator adresu do usunięcia.
     * @throws RuntimeException gdy adres nie istnieje.
     */
    public void adminDeleteAddress(Long addressId) {
        addressRepository.delete(addressRepository.findById(addressId).orElseThrow(() -> new RuntimeException("Adres nie znaleziony")));
    }

    /**
     * Mapuje encję {@link Address} na obiekt DTO odpowiedzi.
     *
     * @param address encja adresu pobrana z bazy danych.
     * @return adres w formacie {@link AddressResponse}.
     */
    private AddressResponse convertToResponse(Address address) {
        return new AddressResponse(
                address.getId(), address.getUser().getId(), address.getUser().getUsername(),
                address.getCity(), address.getPostalCode(), address.getStreet(),
                address.getHouseNumber(), address.getApartmentNumber()
        );
    }
}