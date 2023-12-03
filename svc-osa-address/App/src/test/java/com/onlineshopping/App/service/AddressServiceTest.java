package com.onlineshopping.App.service;

import com.onlineshopping.App.dto.AddressDTO;
import com.onlineshopping.App.entity.AddressEntity;
import com.onlineshopping.App.exception.AddressNotFoundException;
import com.onlineshopping.App.exception.InternalServerErrorException;
import com.onlineshopping.App.exception.InvalidAddressException;
import com.onlineshopping.App.repository.AddressRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private AddressService addressService;

   @Test

    public void testAddValidAddress() {
       // Prepare a valid AddressDTO
       AddressDTO validAddressDTO = new AddressDTO();
       validAddressDTO.setUser_id("123L");
       validAddressDTO.setStreet_address("Valid Street Address");
       validAddressDTO.setCity("Valid City");
       validAddressDTO.setState("Valid State");
       validAddressDTO.setPostal_code("Valid Postal Code");
       validAddressDTO.setCountry("Valid Country");
       validAddressDTO.setDefaultAddress(true);
       when(addressRepository.save(any())).thenReturn(new AddressEntity());

       // Call the method under test
       AddressEntity result = addressService.addAddress(validAddressDTO);

       // Assertions
       assertNotNull(result);

       verify(addressRepository, times(1)).save(any());

   }

    @Test
    public void testAddInvalidAddress() {
        // Prepare an invalid AddressDTO
        AddressDTO invalidAddressDTO = new AddressDTO(); // Create an invalid DTO without required fields

        // Call the method under test and expect an InvalidAddressException
        assertThrows(InvalidAddressException.class, () -> addressService.addAddress(invalidAddressDTO));

        // Verify that repository save method was not called
        verify(addressRepository, never()).save(any());
    }
  @Test
  public void testGetAllAddress() {
      AddressEntity address1 = new AddressEntity(1L, "1", "D.No-104,Rainbowoaks", "Bangalore", "Karnataka", "560068", "India", true);
      AddressEntity address2 = new AddressEntity(2L, "2", "D.No-110 Aratt Felicita", "Bangalore", "Karnataka", "560068", "India", false);

      List<AddressEntity> mockAddressList = new ArrayList<>();
      mockAddressList.add(address1);
      mockAddressList.add(address2);

      // Mock the behavior of addressRepository.findAll() to return the mockAddressList
      when(addressRepository.findAll()).thenReturn(mockAddressList);

      // Call the method under test
      List<AddressEntity> result = addressService.getAllAddress();

      // Assertions
      assertNotNull(result); // Check if the result is not null
      assertEquals(2, result.size()); // Check if the result contains two addresses

      // You can further assert the content of the returned list if needed
      assertTrue(result.contains(address1));
      assertTrue(result.contains(address2));

      // Verify that the addressRepository.findAll() method was called exactly once
      verify(addressRepository, times(1)).findAll();
  }
    @Test
    public void testGetAllAddressEmptyList() {
        // Mock behavior for an empty list returned by the repository
        when(addressRepository.findAll()).thenReturn(Collections.emptyList());

        // Call the method under test
        List<AddressEntity> result = addressService.getAllAddress();

        // Assertions
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(addressRepository, times(1)).findAll();
    }

    @Test
    public void testGetAllAddressExceptionHandling() {
        // Mock behavior for repository throwing an exception
        when(addressRepository.findAll()).thenThrow(new RuntimeException("Repository Exception"));

        // Call the method under test and handle the exception
        assertThrows(RuntimeException.class, () -> addressService.getAllAddress());

        // Verify that the repository method was called
        verify(addressRepository, times(1)).findAll();
    }

  @Test
  public void testUpdateAddressById() {

      Long addressId = 1L;

      AddressDTO addressDTO = new AddressDTO();
      addressDTO.setUser_id("1");
      addressDTO.setCity("Bangalore");
      addressDTO.setState("Karnataka");
      addressDTO.setStreet_address("Bomanahalli");
      addressDTO.setCountry("India");
      addressDTO.setPostal_code("560068");
      addressDTO.setDefaultAddress(Boolean.valueOf("True"));

      AddressEntity address = new AddressEntity(1L, "1", "D.No-104,Rainbowoaks", "Bangalore", "Karnataka", "560068", "India", true);
      Optional<AddressEntity> optionalExistingAddressEntity = Optional.of(address);
      when(addressRepository.findById(addressId)).thenReturn(optionalExistingAddressEntity);

      when(addressRepository.save(any(AddressEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

      // Performing the update operation
      AddressEntity updatedAddress = addressService.updateAddressById(addressId, addressDTO);

      // Verifying that findById and save methods were called with the correct arguments
      verify(addressRepository, times(1)).findById(addressId);
      verify(addressRepository, times(1)).save(address);

      // Assertions to check if the update was successful
      assertNotNull(updatedAddress);
      assertEquals(addressDTO.getUser_id(), updatedAddress.getUserId());
      assertEquals(addressDTO.getStreet_address(), updatedAddress.getStreet_address());
      assertEquals(addressDTO.getCity(), updatedAddress.getCity());
      assertEquals(addressDTO.getState(), updatedAddress.getState());
      assertEquals(addressDTO.getPostal_code(), updatedAddress.getPostal_code());
      assertEquals(addressDTO.getCountry(), updatedAddress.getCountry());
      assertEquals(addressDTO.getDefaultAddress(), updatedAddress.getDefaultAddress());
  }
    @Test
    public void testUpdateDefaultAddressFlag() {
        // Mock an existing address entity
        AddressEntity existingAddress = new AddressEntity();
        existingAddress.setId(1L);
        existingAddress.setUserId("123");
        existingAddress.setStreet_address("D.No-111,Aratt Felicta");
        existingAddress.setCity("Bangalore");
        existingAddress.setState("Karnataka");
        existingAddress.setPostal_code("530068");
        existingAddress.setCountry("India");
        existingAddress.setDefaultAddress(false); // Initially not a default address

        // Create a mock AddressDTO with an updated default address flag
        AddressDTO updatedAddressDTO = new AddressDTO();
        updatedAddressDTO.setUser_id("123");
        updatedAddressDTO.setStreet_address("456 New St");
        updatedAddressDTO.setCity("New City");
        updatedAddressDTO.setState("New State");
        updatedAddressDTO.setPostal_code("54321");
        updatedAddressDTO.setCountry("New Country");
        updatedAddressDTO.setDefaultAddress(true); // Updated to be a default address

        // Mock the behavior of the repository's findById method
        Mockito.when(addressRepository.findById(1L)).thenReturn(Optional.of(existingAddress));

        // Mock the behavior of the repository's save method
        Mockito.when(addressRepository.save(existingAddress)).thenReturn(existingAddress);

        // Perform the update
        AddressEntity updatedAddressEntity = addressService.updateAddressById(1L, updatedAddressDTO);

        // Verify that the update was successful
        assertNotNull(updatedAddressEntity);

    }
    //Default method
    @Test
    void testDeleteAddressById_AddressExists() {
        Long id = 1L;
        AddressEntity address = new AddressEntity(1L, "1", "D.No-104,Rainbowoaks", "Bangalore", "Karnataka", "560068", "India", true);
        Optional<AddressEntity> optionalAddressEntity = Optional.of(address);

        when(addressRepository.findById(id)).thenReturn(optionalAddressEntity);

        try {
            addressService.deleteAddressById(id);
        } catch (AddressNotFoundException e) {
            // Not expecting AddressNotFoundException here
            throw new AssertionError("Unexpected exception: " + e.getMessage());
        }

        verify(addressRepository, times(1)).findById(id);
        verify(addressRepository, times(1)).delete(address);
    }

    @Test
    void testDeleteAddressById_AddressNotExists() {
        Long id = 1L;

        Optional<AddressEntity> optionalAddressEntity = Optional.empty();

        when(addressRepository.findById(id)).thenReturn(optionalAddressEntity);

        AddressNotFoundException exception = null;
        try {
            addressService.deleteAddressById(id);
        } catch (AddressNotFoundException e) {
            exception = e;
        }

        assertEquals("Address not found for id: " + id, exception.getMessage());

        verify(addressRepository, times(1)).findById(id);
        verify(addressRepository, times(0)).delete(Mockito.any());
    }


    @Test
    public void testGetAddressById_ExistingId() {
        Long id = 1L;

        AddressEntity address = new AddressEntity(1L, "1", "D.No-104,Rainbowoaks", "Bangalore", "Karnataka", "560068", "India", true);
        when(addressRepository.findById(id)).thenReturn(Optional.of(address));

        AddressEntity resultAddress = addressService.getAddressById(id);

        assertNotNull(resultAddress);
        assertEquals(address.getStreet_address(), resultAddress.getStreet_address());

        verify(addressRepository, times(1)).findById(id);

    }



    @Test
    public void testGetAddressById_NonExistingId() {
        Long nonExistingId = 100L;

        when(addressRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        AddressNotFoundException exception = assertThrows(AddressNotFoundException.class,
                () -> addressService.getAddressById(nonExistingId));

        assertEquals("Address not found with id: " + nonExistingId, exception.getMessage());

        verify(addressRepository, times(1)).findById(nonExistingId);
    }
    @Test
    public void testGetAddressById_IncompleteAddressInfo() {

        AddressEntity incompleteAddress = new AddressEntity(1L, "1", "D.No-104,Rainbowoaks", "Bangalore", "Karnataka", null, "India", true);
        when(addressRepository.findById(1L)).thenReturn(Optional.of(incompleteAddress));
        AddressEntity retrievedAddress = addressService.getAddressById(1L);
        assertNotNull(retrievedAddress);
        assertNull(retrievedAddress.getPostal_code());
        assertEquals("1", retrievedAddress.getUserId());
        assertEquals("D.No-104,Rainbowoaks", retrievedAddress.getStreet_address());
        assertEquals("Bangalore", retrievedAddress.getCity());
        assertEquals("Karnataka", retrievedAddress.getState());
        assertNull(retrievedAddress.getPostal_code()); // Postal code should be null
        assertEquals("India", retrievedAddress.getCountry());
        assertTrue(retrievedAddress.getDefaultAddress());

    }
    @Test
    public void testGetAddressesByUserId() {
        String userId = "user123";


        AddressEntity address1 = new AddressEntity(1L, "1", "D.No-104,Rainbowoaks", "Bangalore", "Karnataka", "560068", "India", true);
        AddressEntity address2 = new AddressEntity(2L, "2", "D.No-110,Aratt Felicita", "Bangalore", "Karnataka", "560068", "India", false);

        List<AddressEntity> mockAddresses = Arrays.asList(address1, address2);

        when(addressRepository.findByUserId(userId)).thenReturn(mockAddresses);
        List<AddressEntity> addresses = addressService.getAddressesByUserId(userId);

        assertNotNull(addresses);
        assertEquals(2, addresses.size());
        assertEquals("D.No-104,Rainbowoaks", addresses.get(0).getStreet_address());
        assertEquals("D.No-110,Aratt Felicita", addresses.get(1).getStreet_address());

        // Verify method invocation on the mock
        verify(addressRepository, times(1)).findByUserId(userId);
    }

    @Test
    public void testGetAddressesByUserId_EmptyList() {
        String userId = "user123";

        when(addressRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

        List<AddressEntity> addresses = addressService.getAddressesByUserId(userId);

        assertNotNull(addresses);
        assertTrue(addresses.isEmpty());

        verify(addressRepository, times(1)).findByUserId(userId);
    }


    @Test
    public void testGetAddressesByUserId_InvalidUserId() {
        String userId = "invalid_user";

        List<AddressEntity> addresses = addressService.getAddressesByUserId(userId);

        assertNotNull(addresses);
        assertTrue(addresses.isEmpty());
    }
    @Test
    public void testGetAddressesByUserId_MultipleAddresses() {
        String userId = "user123";
        AddressEntity address1 = new AddressEntity(1L, userId, "Address 1", "City 1", "State 1", "12345", "Country 1", true);
        AddressEntity address2 = new AddressEntity(2L, userId, "Address 2", "City 2", "State 2", "67890", "Country 2", false);

        when(addressRepository.findByUserId(userId)).thenReturn(Arrays.asList(address1, address2));

        List<AddressEntity> addresses = addressService.getAddressesByUserId(userId);

        assertNotNull(addresses);
        assertEquals(2, addresses.size());

        // Additional assertions as per your requirements
    }

    @Test
    public void testSetAsdefaultAddress() {

        Long addressId = 1L;
        List<AddressEntity> addresses = new ArrayList<>();

        AddressEntity address1 = new AddressEntity(1L, "1", "D.No-104,Rainbowoaks", "Bangalore", "Karnataka", "560068", "India", true);
        AddressEntity address2 = new AddressEntity(2L, "2", "D.No-110 Aratt Felicita", "Bangalore", "Karnataka", "560068", "India", false);
        when(addressRepository.findAll()).thenReturn(Arrays.asList(address1, address2));

        addressService.setAsDefaultAddress(addressId);


        address1.setDefaultAddress(true);
        assertTrue(address1.getDefaultAddress());

        // Set default address to false
        address2.setDefaultAddress(false);
        assertFalse(address2.getDefaultAddress());


        verify(addressRepository, times(1)).findAll();
        verify(addressRepository, times(1)).saveAll(anyList());
    }

    @Test
    public void testSetAsDefaultAddress_InvalidAddressId() {

        AddressEntity address1 = new AddressEntity(1L, "1", "Address 1", "City 1", "State 1", "12345", "Country 1", true);
        AddressEntity address2 = new AddressEntity(2L, "2", "Address 2", "City 2", "State 2", "67890", "Country 2", false);
        List<AddressEntity> addresses = Arrays.asList(address1, address2);

        when(addressRepository.findAll()).thenReturn(addresses);

        Long nonExistingId = 999L;

        addressService.setAsDefaultAddress(nonExistingId);

        boolean found = false;
        for (AddressEntity address : addresses) {
            if (address.getId().equals(nonExistingId)) {
                assertEquals(true, address.getDefaultAddress());
                found = true;
            } else {
                assertEquals(false, address.getDefaultAddress());
            }
        }
        assertEquals(false, found);
    }

    @Test
    public void testInternalServerError() {

        List<AddressEntity> addresses = Arrays.asList(new AddressEntity(1L, "1", "Address 1", "City 1", "State 1", "12345", "Country 1", true),


       new AddressEntity(2L, "2", "Address 2", "City 2", "State 2", "67890", "Country 2", false));

        when(addressRepository.findAll()).thenReturn(addresses);

        doThrow(new InternalServerErrorException("Internal server error")).when(addressRepository).saveAll(any());

        InternalServerErrorException exception = null;
        try {
            addressService.setAsDefaultAddress(1L); // Method that relies on the repository
        } catch (InternalServerErrorException e) {
            exception = e;
        }

        assertEquals(InternalServerErrorException.class, exception.getClass());
        assertEquals("Internal server error", exception.getMessage());

        verify(addressRepository, times(1)).findAll();
        verify(addressRepository, times(1)).saveAll(any());

    }

    @Test
    void testGetAddressById_WhenAddressDoesNotExist() {
        // Arrange
        Long addressId = 1L;
        when(addressRepository.findById(addressId)).thenReturn(Optional.empty());

        // Act and Assert
        AddressNotFoundException exception = assertThrows(AddressNotFoundException.class,
                () -> addressService.getAddressById(addressId));

        assertEquals("Address not found with id: " + addressId, exception.getMessage());

        // Verify that findById method was called on the repository
        verify(addressRepository, times(1)).findById(addressId);
    }
    @Test
    void testUpdateAddressById_WhenAddressDoesNotExist() {
        // Arrange
        Long addressId = 1L;
        AddressDTO addressDTO = new AddressDTO(/* fill with necessary data */);
        when(addressRepository.findById(addressId)).thenReturn(Optional.empty());

        // Act and Assert
        AddressNotFoundException exception = assertThrows(AddressNotFoundException.class,
                () -> addressService.updateAddressById(addressId, addressDTO));

        // Verify that findById method was called on the repository
        verify(addressRepository, times(1)).findById(addressId);

        // Verify the exception message
        String expectedMessage = "Address not found with id: " + addressId;
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
    @Test
    void testMapDtoToEntity() {
        // Arrange
        AddressService addressService = new AddressService();
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setUser_id("user123");
        addressDTO.setStreet_address("123 Main St");
        addressDTO.setCity("Cityville");
        addressDTO.setState("State");
        addressDTO.setPostal_code("12345");
        addressDTO.setCountry("Country");
        addressDTO.setDefaultAddress(true);

        // Act
        AddressEntity addressEntity = addressService.mapDtoToEntity(addressDTO);

        // Assert
        assertEquals(addressDTO.getUser_id(), addressEntity.getUserId());
        assertEquals(addressDTO.getStreet_address(), addressEntity.getStreet_address());
        assertEquals(addressDTO.getCity(), addressEntity.getCity());
        assertEquals(addressDTO.getState(), addressEntity.getState());
        assertEquals(addressDTO.getPostal_code(), addressEntity.getPostal_code());
        assertEquals(addressDTO.getCountry(), addressEntity.getCountry());
        assertEquals(addressDTO.getDefaultAddress(), addressEntity.getDefaultAddress());
    }
}