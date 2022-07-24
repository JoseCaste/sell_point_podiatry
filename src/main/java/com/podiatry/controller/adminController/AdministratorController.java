package com.podiatry.controller.adminController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.podiatry.model.Citas;
import com.podiatry.model.Purchase;
import com.podiatry.pojo.DateCalendarData;
import com.podiatry.pojo.ProductData;
import com.podiatry.pojo.PurchaseData;
import com.podiatry.repository.CitasRepository;
import com.podiatry.repository.PurchaseRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class AdministratorController {

    private CitasRepository citasRepository;
    private PurchaseRepository purchaseRepository;

    public AdministratorController(CitasRepository citasRepository, PurchaseRepository purchaseRepository){
        this.citasRepository= citasRepository;
        this.purchaseRepository = purchaseRepository;
    }

    @GetMapping("/dates")
    public String showDates(Model model){
        List<Citas> citas= this.citasRepository.findAll();
        try {
            String json = mapObjectToFullCalendar(citas);
            model.addAttribute("citasJson",json);
        }catch (JsonProcessingException e){
            model.addAttribute("error", "Hubo un error al recuperar las citas");
        }
        return "admin/dates";
    }

    @GetMapping("/purchases")
    public String showPurchases(Model model){

        try {
            final UserDetails userLogged = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if(userLogged.isAccountNonExpired()){
                List<Purchase> purchaseList = purchaseRepository.findAll();
                model.addAttribute("purchases", mapEntityToDTO(purchaseList));
            }
        }catch (Exception ex){
            model.addAttribute("error","La sesión ha expirado o cerrado");
            ex.printStackTrace();
        }
        return "admin/purchases_list";
    }

    @GetMapping("/purchases/detail")
    public String showPurchaseDetail(Model model, @RequestParam("idPurchase") final Long idCarSales){
        Purchase purchase = purchaseRepository.findById(idCarSales).get();
        model.addAttribute("purchasesDetail", mapProductToDTO(purchase));
        return "admin/purchases_detail_list";
    }

    private List<PurchaseData> mapEntityToDTO(List<Purchase> purchaseList) {
        return purchaseList.stream().map(item->
            new PurchaseData(item.getId_purchase(), item.getUser().getUserName().concat(item.getUser().getLastName()),item.getStatus(),item.getDate_(), item.getTotal())
        ).collect(Collectors.toList());
    }

    private List<ProductData> mapProductToDTO(Purchase purchase){
        return purchase.getProducts().stream().map(item->
                new ProductData(item.getId_product(),item.getName(), Base64.getEncoder().encodeToString(item.getImg()))
                ).collect(Collectors.toList());
    }

    private String mapObjectToFullCalendar(List<Citas> citas) throws JsonProcessingException {
        List<DateCalendarData> parseDate = citas.stream().map(cita->{

            LocalDateTime date= LocalDateTime.of(cita.getDate(),cita.getTime());
            LocalDateTime dateEnd= LocalDateTime.of(cita.getDate(),cita.getTime().plusHours(1));

            final String address = cita.getAddress() != null ?
                                   cita.getPlace().concat(" | ".concat(cita.getAddress())) : cita.getPlace();

            return new DateCalendarData(cita.getId(),cita.getPayment_id(), String.format("Orden de pago: %s | %s %s - %s",cita.getPayment_id() != null ? cita.getPayment_id() : "",
                                        cita.getPacientName(),cita.getLastName(),address),date.toString(), dateEnd.toString());
        }).collect(Collectors.toCollection(ArrayList::new));

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Hibernate5Module());
        return mapper.writeValueAsString(parseDate);
    }
}
