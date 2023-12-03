package com.onlineshopping.App.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {

    private Long id;
    private String user_id;
    private String street_address;
    private String city;
    private String state;
    private String postal_code;
    private String country;
    private Boolean defaultAddress;
}
