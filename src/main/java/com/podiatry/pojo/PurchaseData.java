package com.podiatry.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@ToString
public class PurchaseData implements Serializable {

    private Long id;
    private Integer paymentId;
    private String buyersName;
    private String status;
    private Date localDateTime;
    private Double totalPaid;
    private String address;

}
