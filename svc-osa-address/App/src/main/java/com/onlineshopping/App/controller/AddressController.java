package com.onlineshopping.App.controller;

import com.onlineshopping.App.dto.AddressDTO;
import com.onlineshopping.App.entity.AddressEntity;
import com.onlineshopping.App.exception.AddressNotFoundException;
import com.onlineshopping.App.exception.InvalidAddressException;
import com.onlineshopping.App.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/")
public class AddressController {

    @Autowired
    private AddressService addressService;


    @PostMapping("/create")
public ResponseEntity<?> createAddress(@RequestBody AddressDTO addressDTO) {
        try {
            AddressEntity createdAddress = addressService.addAddress(addressDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAddress);
        } catch (InvalidAddressException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while creating the address.");
        }

    }

    @GetMapping("/addresses")
    public ResponseEntity<List<AddressEntity>> getAllAddresses() {
        List<AddressEntity> address = addressService.getAllAddress();
        return ResponseEntity.ok(address);
    }

    @PutMapping("/addresses/{id}")
    public ResponseEntity<?> updateAddressById(@PathVariable("id") Long Id, @RequestBody AddressDTO addressDTO) {
try {
    AddressEntity updatedAddress = addressService.updateAddressById(Id, addressDTO);
    return ResponseEntity.ok(updatedAddress);
} catch (AddressNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating the address.");
}
}

@DeleteMapping("address/{id}")
    public ResponseEntity<String> deleteAddressById(@PathVariable("id") Long id){
        try{
        addressService.deleteAddressById(id);
        return ResponseEntity.ok("Address deleted succesfully");
}catch (AddressNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (Exception e){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete address");
    }
    }

    @GetMapping("/{id}")
    public ResponseEntity<AddressEntity> getAddressById(@PathVariable("id") Long Id){
        AddressEntity address = addressService.getAddressById(Id);
        return ResponseEntity.ok(address);
    }

    @GetMapping("/users/{userId}/addresses")
    public ResponseEntity<List<AddressEntity>> getAddressesByUserId(@PathVariable("userId") String userId){
        List<AddressEntity> userAddresses = addressService.getAddressesByUserId(userId);
        return ResponseEntity.ok(userAddresses);
    }

    @PutMapping("/{id}/default")
    public ResponseEntity<String> setAsDefaultAddress(@PathVariable("id") Long addressId) {
        try {
            addressService.setAsDefaultAddress(addressId);
            return ResponseEntity.ok("Address set as default successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to set address as default");
        }
    }
}
