package com.podiatry.controller.product;

import com.podiatry.model.Product;
import com.podiatry.pojo.ProductMetaData;
import com.podiatry.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.util.Base64;

@Controller
public class ProductController {

    private ProductRepository productRepository;


    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    public ProductController(ProductRepository productRepository){
        this.productRepository= productRepository;
    }
    @GetMapping("/product")
    public String productView(Model model,@ModelAttribute("product") ProductMetaData productMetaData){

        return "product/product";
    }

    @PostMapping("/product")
    public String productSave(Model model,@Validated @ModelAttribute("product") ProductMetaData productMetaData){

        try {
            Product product= mapPojoToEntity(productMetaData);
            this.productRepository.save(product);
            model.addAttribute("status",true);
            model.addAttribute("messageProduct","Producto guardado con éxito.");

        }catch (IOException ex){
            model.addAttribute("status",false);
            model.addAttribute("messageProduct","Error al guardar el archivo, hubo un error con la imágen");
            LOGGER.error("Error al guardar el producto {}",ex);

        }catch (Exception ex){
            model.addAttribute("status",false);
            model.addAttribute("messageProduct","La imagen es demasiado grande");
            LOGGER.error("Imagen demasiado pesada {}",ex);

        }

        return "product/product";
    }

    private Product mapPojoToEntity(ProductMetaData productMetaData) throws IOException {
        return new Product(productMetaData.getName(), productMetaData.getTotal(), productMetaData.getPrice(), Base64.getEncoder().encodeToString(productMetaData.getImg().getBytes()));
    }
}
