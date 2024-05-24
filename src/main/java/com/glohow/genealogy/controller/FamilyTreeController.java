package com.glohow.genealogy.controller;

import com.glohow.genealogy.dto.MemberDTO;
import com.glohow.genealogy.dto.MemberHierachyDTO;
import com.glohow.genealogy.model.Member;
import com.glohow.genealogy.service.FamilyTreeService;
import com.glohow.genealogy.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class FamilyTreeController {

    private final FamilyTreeService familyTreeService;
    private final MemberService memberService;

    @Operation(summary = "add new member to family")
    @ApiResponses(value = {
            @ApiResponse(responseCode ="201", description = "successfully added new member"),
            @ApiResponse(responseCode = "500", description = "failed to add new member")
    })
    @PostMapping("/member/add")
    public ResponseEntity<Boolean> addNewMember(@RequestBody MemberDTO memberDTO){
        return new ResponseEntity<>(memberService.addNewMember(memberDTO, memberDTO.getFamilyId()), HttpStatus.CREATED);
    }


    @Operation(summary = "edit family member")
    @ApiResponses(value = {
            @ApiResponse(responseCode ="200", description = "successfully edited member"),
            @ApiResponse(responseCode = "500", description = "failed to edit member")
    })
    @PostMapping("/member/update-detail")
    public ResponseEntity<Boolean> updateMember(@RequestBody MemberDTO memberDTO){
        return new ResponseEntity<>(memberService.updateMember(memberDTO, memberDTO.getFamilyId()), HttpStatus.OK);
    }

    @Operation(summary = "get family tree")
    @ApiResponses(value = {
            @ApiResponse(responseCode ="200", description = "successfully get family tree"),
            @ApiResponse(responseCode = "500", description = "failed to get family tree")
    })
    @GetMapping("/family/{familyId}")
    public ResponseEntity<MemberHierachyDTO> getByTreeId(@PathVariable Integer familyId){
        return new ResponseEntity<>(familyTreeService.getFamilyTree(familyId), HttpStatus.OK);
    }

    @Operation(summary = "get member's family tree")
    @ApiResponses(value = {
            @ApiResponse(responseCode ="200", description = "successfully get member's family tree"),
            @ApiResponse(responseCode = "500", description = "failed to get member's family tree")
    })
    @GetMapping("/family/{familyId}/{memberId}")
    public ResponseEntity<MemberHierachyDTO>getMemberFamilyInfo(
            @PathVariable(name = "familyId") Integer familyId,
            @PathVariable(name = "memberId") Integer memberId){
        return new ResponseEntity<>(familyTreeService.getMemberFamily(familyId, memberId), HttpStatus.OK);
    }
}
