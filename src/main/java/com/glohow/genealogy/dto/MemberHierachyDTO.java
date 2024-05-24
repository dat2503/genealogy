package com.glohow.genealogy.dto;

import com.glohow.genealogy.model.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberHierachyDTO {
    private Integer id;
    private Integer familyId;
    private String firstName;
    private String lastName;
    private String gender;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate DOB;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate DOD;

    private List<SpouseChildrenDTO> spouses;

    private List<MemberHierachyDTO> parents;
    public MemberHierachyDTO(Member member) {
        this.id = member.getId();
        this.firstName = member.getFirstName();
        this.lastName = member.getLastName();
        this.DOB = member.getDob();
        this.DOD = member.getDod();
        this.gender = member.getGender();
    }
}
