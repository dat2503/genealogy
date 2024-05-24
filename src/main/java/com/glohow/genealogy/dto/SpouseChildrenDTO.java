package com.glohow.genealogy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class SpouseChildrenDTO {
    private MemberHierachyDTO spouse;
    private List<MemberHierachyDTO> children;

}
