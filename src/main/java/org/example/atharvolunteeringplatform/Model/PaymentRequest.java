package org.example.atharvolunteeringplatform.Model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {

    private String name;
    private String number;
    private String cvc;
    private String month;
    private String year;
    private double amount;
    private String currency;
    private String description;
    private String callbackUrl;
}
