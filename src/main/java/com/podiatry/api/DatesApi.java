package com.podiatry.api;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.podiatry.model.Citas;
import com.podiatry.repository.CitasRepository;

@RestController
public class DatesApi {
	@Autowired
	private CitasRepository citasRepository;
	
	List<LocalTime> work_hours= Arrays.asList(LocalTime.of(9, 0),LocalTime.of(10, 0),LocalTime.of(11, 0),LocalTime.of(12, 0),
			LocalTime.of(13, 0),LocalTime.of(14, 0),LocalTime.of(15, 0),LocalTime.of(16, 0));
	@GetMapping("/dates/getHours/{date}")
	public ResponseEntity<Object> getHours(@PathVariable("date") @DateTimeFormat(pattern = "yyyy-mm-dd") String date){
		List<Citas> citas= this.citasRepository.findByDate(LocalDate.parse(date));
		if(citas.isEmpty())
		return new ResponseEntity<>(work_hours, HttpStatus.ACCEPTED);
		else {
			List<LocalTime> times_dates= citas.stream().map(item-> item.getTime()).collect(Collectors.toList());
			List<LocalTime> work_hours_= work_hours;
			for (LocalTime localTime : times_dates) {
				work_hours_=work_hours_.stream().filter(item-> !item.equals(localTime)).collect(Collectors.toList());
			}
			return new ResponseEntity<>(work_hours_,HttpStatus.ACCEPTED);
		}
	}
}
