package com.podiatry.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class ProductData implements Serializable {

    private Long id_product;
    private String name;
    private String img;
    private Integer total;
    private Double price;
}
