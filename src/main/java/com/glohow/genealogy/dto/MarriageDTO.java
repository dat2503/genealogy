package com.glohow.genealogy.dto;

import com.glohow.genealogy.enumerate.EnumMaritalStatus;
import com.glohow.genealogy.model.Marriage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MarriageDTO {
    private Integer id;
    private Integer spouseId;
    private EnumMaritalStatus maritalStatus;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate marriageDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate divorcedDate;

}

