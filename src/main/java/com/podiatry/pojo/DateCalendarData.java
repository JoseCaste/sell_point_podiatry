package com.podiatry.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class DateCalendarData implements Serializable {
    private Long id;
    private Integer paymentId;
    private String title;
    private String start;
    private String end;
}
