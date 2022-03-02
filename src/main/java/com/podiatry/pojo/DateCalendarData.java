package com.podiatry.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class DateData implements Serializable {
    private Long id;
    private String title;
    private String start;
    private String end;

}
