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

    public ProductData(Long id_product, String name, Integer total,String img) {
        this.id_product = id_product;
        this.name = name;
        this.total = total;
        this.img = img;
    }

    public ProductData() {
    }
}
