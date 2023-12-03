package com.onlineshopping.App.controller;

import com.onlineshopping.App.dto.AddressDTO;
import com.onlineshopping.App.entity.AddressEntity;
import com.onlineshopping.App.exception.AddressNotFoundException;
import com.onlineshopping.App.exception.InvalidAddressException;
import com.onlineshopping.App.service.AddressService;
import com.sun.jdi.InvalidTypeException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;


@ExtendWith(MockitoExtension.class)
class AddressControllerTest {

    @Mock
    private AddressService addressService;

    @InjectMocks
    private AddressController addressController;


    @Test
    public void testcreateAddress_ValidInput() throws Exception {
        AddressDTO validAddressDTO = new AddressDTO();
        validAddressDTO.setUser_id("1");
        validAddressDTO.setCity("Bangalore");
        validAddressDTO.setState("Karnataka");
        validAddressDTO.setStreet_address("Bomanahalli");
        validAddressDTO.setCountry("India");
        validAddressDTO.setPostal_code("560068");
        validAddressDTO.setDefaultAddress(Boolean.valueOf("True"));
        AddressEntity createdAddress = new AddressEntity(/* mock created address */);
        when(addressService.addAddress(any(AddressDTO.class))).thenReturn(createdAddress);

        ResponseEntity<?> response = addressController.createAddress(validAddressDTO);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(createdAddress, response.getBody());
    }

    @Test
    public void testCreateAddress_InvalidInput_ReturnsBadRequest() {
        // Given
        AddressDTO invalidAddressDTO = new AddressDTO();
        String errorMessage = "Invalid address details";
        doThrow(new InvalidAddressException(errorMessage)).when(addressService).addAddress(any(AddressDTO.class));

        // When
        ResponseEntity<?> response = addressController.createAddress(invalidAddressDTO);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
    }

    @Test
    public void testCreateAddress_InternalServerError_ReturnsInternalServerError() {
        // Given
        AddressDTO validAddressDTO = new AddressDTO(/* provide valid data */);
        when(addressService.addAddress(any(AddressDTO.class))).thenThrow(new RuntimeException("Something went wrong"));

        // When
        ResponseEntity<?> response = addressController.createAddress(validAddressDTO);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An error occurred while creating the address.", response.getBody());
    }

    @Test
    public void testGetAllAddresses_AddressesPresent() {
        List<AddressEntity> addressList = new ArrayList<>();
        AddressEntity address1 = new AddressEntity(1L, "1", "D.No-104,Rainbowoaks", "Bangalore", "Karnataka", "560068", "India", true);
        AddressEntity address2 = new AddressEntity(2L, "2", "D.No-110 Aratt Felicita", "Bangalore", "Karnataka", "560068", "India", false);
        addressList.add(address1);
        addressList.add(address2);

        when(addressService.getAllAddress()).thenReturn(addressList);

        ResponseEntity<List<AddressEntity>> response = addressController.getAllAddresses();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(addressList.size(), response.getBody().size());

        verify(addressService, times(1)).getAllAddress();

    }

    @Test
    public void testGetAllAddresses_AddressNotPresent() {
        List<AddressEntity> emptyList = new ArrayList<>();
        when(addressService.getAllAddress()).thenReturn(emptyList);

        ResponseEntity<List<AddressEntity>> response = addressController.getAllAddresses();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().size());

        verify(addressService, times(1)).getAllAddress();
    }

   /* @Test
    public void testGetAllAddresses_InternalServerError() throws Exception {


        when(addressService.getAllAddress()).thenThrow(new RuntimeException("Internal Server Error"));

        ResponseEntity<List<AddressEntity>> response = addressController.getAllAddresses();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());

        verify(addressService, times(1)).getAllAddress();

    }*/

    @Test
    public void testUpdateAddressById_ValidId() {
        Long id = 1L;
        AddressDTO validAddressDTO = new AddressDTO();
        validAddressDTO.setUser_id("1");
        validAddressDTO.setCity("Bangalore");
        validAddressDTO.setState("Karnataka");
        validAddressDTO.setStreet_address("Bomanahalli");
        validAddressDTO.setCountry("India");
        validAddressDTO.setPostal_code("560068");
        validAddressDTO.setDefaultAddress(Boolean.valueOf("True"));
        AddressEntity updatedAddressEntity = new AddressEntity();


        when(addressService.updateAddressById(id, validAddressDTO)).thenReturn(updatedAddressEntity);
        ResponseEntity<?> response = addressController.updateAddressById(id, validAddressDTO);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedAddressEntity, response.getBody());
        verify(addressService, times(1)).updateAddressById(id, validAddressDTO);
    }

    @Test
    public void testUpdateAddressById_AddressNotFound() {
        Long id = 1L;
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setUser_id("1");
        addressDTO.setCity("Bangalore");
        addressDTO.setState("Karnataka");
        addressDTO.setStreet_address("Bomanahalli");
        addressDTO.setCountry("India");
        addressDTO.setPostal_code("560068");
        addressDTO.setDefaultAddress(Boolean.valueOf("True"));
        when(addressService.updateAddressById(id, addressDTO)).thenThrow(new AddressNotFoundException("Address not found"));

        // When
        ResponseEntity<?> response = addressController.updateAddressById(id, addressDTO);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Address not found", response.getBody());
        verify(addressService, times(1)).updateAddressById(id, addressDTO);
    }

    @Test
    public void testUpdateAddressById_InternalServerError() {

        Long id = 1L;
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setUser_id("1");
        addressDTO.setCity("Bangalore");
        addressDTO.setState("Karnataka");
        addressDTO.setStreet_address("Bomanahalli");
        addressDTO.setCountry("India");
        addressDTO.setPostal_code("560068");
        addressDTO.setDefaultAddress(Boolean.valueOf("True"));
        when(addressService.updateAddressById(id, addressDTO)).thenThrow(new RuntimeException("Internal Server Error"));
        ResponseEntity<?> response = addressController.updateAddressById(id, addressDTO);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An error occurred while updating the address.", response.getBody());
        verify(addressService, times(1)).updateAddressById(id, addressDTO);
    }

    @Test
    public void testDeleteAddressById_ValidDeletion() {
        Long id = 1L;

        ResponseEntity<String> response = addressController.deleteAddressById(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Address deleted succesfully", response.getBody());
        verify(addressService, times(1)).deleteAddressById(id);
    }

    @Test
    public void testDeleteAddressById_AddressNotFound() {
        Long id = 1L;
        doThrow(new AddressNotFoundException("Address not found")).when(addressService).deleteAddressById(id);

        ResponseEntity<String> response = addressController.deleteAddressById(id);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Address not found", response.getBody());
        verify(addressService, times(1)).deleteAddressById(id);

    }

    @Test
    public void testDeleteAddressById_InternalServerError() {
        Long id = 1L;
        doThrow(new RuntimeException("Internal Server Error")).when(addressService).deleteAddressById(id);

        ResponseEntity<String> response = addressController.deleteAddressById(id);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Failed to delete address", response.getBody());
        verify(addressService, times(1)).deleteAddressById(id);

    }

    @Test
    public void testGetAddressById_ValidId() {
        Long id = 1L;
        AddressEntity mockAddress = new AddressEntity(1L, "1", "D.No-104,Rainbowoaks", "Bangalore", "Karnataka", "560068", "India", true);;
        when(addressService.getAddressById(id)).thenReturn(mockAddress);

        ResponseEntity<AddressEntity> response = addressController.getAddressById(id);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockAddress, response.getBody());
        verify(addressService, times(1)).getAddressById(id);

    }

   /* @Test
    public void testGetAddressById_AddressNotFound() {
        Long id = 1L;
        when(addressService.getAddressById(id)).thenThrow(new AddressNotFoundException("Address not found"));
        ResponseEntity<AddressEntity> response = addressController.getAddressById(id);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody()); // Body should be null in case of not found
        verify(addressService, times(1)).getAddressById(id);
    }

    @Test
    public void testGetAddressById_InternalServerError() {

        Long id = 1L;
        when(addressService.getAddressById(id)).thenThrow(new RuntimeException("Internal Server Error"));

        ResponseEntity<AddressEntity> response = addressController.getAddressById(id);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody()); // Body should be null in case of internal server error
        verify(addressService, times(1)).getAddressById(id);
    }*/

    @Test
    public void testGetAddressesByUserId_ValidUserId() {
        String userId = "user123";
        List<AddressEntity> mockAddresses = new ArrayList<>();
        mockAddresses.add(new AddressEntity(/* address details */));
        mockAddresses.add(new AddressEntity(/* address details */));
        when(addressService.getAddressesByUserId(userId)).thenReturn(mockAddresses);
        ResponseEntity<List<AddressEntity>> response = addressController.getAddressesByUserId(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockAddresses, response.getBody());
        verify(addressService, times(1)).getAddressesByUserId(userId);
    }

    @Test
    public void testGetAddressesByUserId_NoAddresses() {
        String userId = "user456";
        when(addressService.getAddressesByUserId(userId)).thenReturn(Collections.emptyList());

        ResponseEntity<List<AddressEntity>> response = addressController.getAddressesByUserId(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
        verify(addressService, times(1)).getAddressesByUserId(userId);
    }


  /*  @Test
    public void testGetAddressesByUserId_InvalidUserId(){
        String userId = "nonexistentUser";
        when(addressService.getAddressesByUserId(userId)).thenThrow(new InvalidTypeException("User not found"));
        ResponseEntity<List<AddressEntity>> response = addressController.getAddressesByUserId(userId);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(addressService, times(1)).getAddressesByUserId(userId);
    }*/


    @Test
    public void testSetAsDefaultAddress_Successful() {

        Long addressId = 1L;
        doNothing().when(addressService).setAsDefaultAddress(addressId);

        ResponseEntity<String> response = addressController.setAsDefaultAddress(addressId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Address set as default successfully", response.getBody());
        verify(addressService, times(1)).setAsDefaultAddress(addressId);
    }

    @Test
    public void testSetAsDefaultAddress_Failure() {
        Long addressId = 2L;
        doThrow(new RuntimeException("Failed to set default")).when(addressService).setAsDefaultAddress(addressId);
        ResponseEntity<String> response = addressController.setAsDefaultAddress(addressId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Failed to set address as default", response.getBody());
        verify(addressService, times(1)).setAsDefaultAddress(addressId);
    }
}