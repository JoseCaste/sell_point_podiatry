package com.podiatry.controller.adminController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.podiatry.model.Citas;
import com.podiatry.pojo.DateCalendarData;
import com.podiatry.repository.CitasRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class AdministratorController {

    private CitasRepository citasRepository;
    public AdministratorController(CitasRepository citasRepository){
        this.citasRepository= citasRepository;
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

    private String mapObjectToFullCalendar(List<Citas> citas) throws JsonProcessingException {
        List<DateCalendarData> parseDate = citas.stream().map(cita->{
            LocalDateTime date= LocalDateTime.of(cita.getDate(),cita.getTime());
            return new DateCalendarData(cita.getId(),String.format("%s %s - %s",cita.getPacientName(),cita.getLastName(),cita.getPlace()),date.toString());
        }).collect(Collectors.toCollection(ArrayList::new));

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Hibernate5Module());
        return mapper.writeValueAsString(parseDate);
    }
}
