package com.glohow.genealogy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberDTO{
    private Integer id;
    private Integer familyId;
    private String firstName;
    private String lastName;
    private String gender;
    private boolean isAdopted;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dod;
    private Integer parentId;
    private List<MarriageDTO> marriages;
}
