package com.podiatry.pojo;

import com.mercadopago.core.annotations.validation.NotNull;
import com.mercadopago.core.annotations.validation.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class ProductMetaData implements Serializable {

    @NotNull
    private  String name;

    @NotNull
    private Integer total;

    @NotNull
    private Double price;

    @NotNull
    @Size(min = 20, max = 2000)
    private MultipartFile img;
}
