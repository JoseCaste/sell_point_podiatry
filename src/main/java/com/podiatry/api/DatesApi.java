package com.podiatry.api;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
			//List<LocalTime> filter= new ArrayList<>();
			Set<LocalTime> filter= new HashSet<LocalTime>();
			for (LocalTime localTime : work_hours) {
				Optional<LocalTime> filter_= citas.stream().filter(item-> !item.getTime().equals(localTime)).map(item->item.getTime()).findFirst();
				if(!filter_.isPresent()) {
					filter.add(localTime);
				}
			}
			return new ResponseEntity<>(filter,HttpStatus.ACCEPTED);
		}
	}
}
