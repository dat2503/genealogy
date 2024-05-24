package com.glohow.genealogy.service;

import com.glohow.genealogy.dto.MemberHierachyDTO;
import com.glohow.genealogy.enumerate.EnumMaritalStatus;
import com.glohow.genealogy.model.Family;
import com.glohow.genealogy.model.Marriage;
import com.glohow.genealogy.model.Member;
import com.glohow.genealogy.repository.FamilyRepository;
import com.glohow.genealogy.repository.MarriageRepository;
import com.glohow.genealogy.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.util.CollectionUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FamilyTreeServiceTest {
    private MemberRepository memberRepository;
    private FamilyRepository familyRepository;
    private FamilyTreeService familyTreeService;
    @BeforeEach
    public void setUp(){
        memberRepository = mock(MemberRepository.class);
        familyRepository = mock(FamilyRepository.class);
        familyTreeService = new FamilyTreeService(memberRepository, familyRepository);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void givenFamilyId_whenFindById_thenSuccess() {
        // given - precondition or setup
        Family family = Family.builder()
                .id(1)
                .build();
        // when - action or the behavior being tested
        when(familyRepository.findById(any())).thenReturn(Optional.of(family));
        Family queriedFamily = familyTreeService.findByFamilyId(family.getId());
        //then - verify the output
        assertThat(queriedFamily.getId()).isEqualTo(family.getId());
    }

    @Test
    public void givenInvalidFamilyId_whenFindById_thenThrowError() {
        // given - precondition or setup
        Integer id = 1;
        // when - action or the behavior being tested
        when(familyRepository.findById(id)).thenReturn(Optional.empty());
        //then - verify the output
        assertThrows(EntityNotFoundException.class, () -> familyTreeService.findByFamilyId(id));
    }

    @Test
    public void givenFamilyId_whenGetFamilyTree_thenSuccess() {
        // given - precondition or setup
        Family familySmith = Family.builder()
                .id(1)
                .name("Smith")
                .build();

        Member father = Member.builder()
                .id(1)
                .family(familySmith)
                .firstName("John")
                .lastName("Smith")
                .isAdopted(false)
                .gender("male")
                .dob(LocalDate.parse("1674-01-01"))
                .build();
        Member mother = Member.builder()
                .id(2)
                .family(familySmith)
                .firstName("Mary")
                .lastName("Smith")
                .isAdopted(false)
                .gender("female")
                .dob(LocalDate.parse("1689-01-01"))
                .build();

        Marriage parentMarriage = Marriage.builder()
                .marriageId(1)
                .member1(father)
                .member2(mother)
                .marriageDate(LocalDate.parse("1700-01-01"))
                .maritalStatus(EnumMaritalStatus.MARRIED)
                .build();

        Member member = Member.builder()
                .id(3)
                .family(familySmith)
                .firstName("John")
                .lastName("Smith")
                .isAdopted(false)
                .gender("male")
                .dob(LocalDate.parse("1701-01-01"))
                .parent(parentMarriage)
                .build();
        // when - action or the behavior being tested
        when(familyRepository.findById(any())).thenReturn(Optional.of(familySmith));
        when(memberRepository.findAllByFamilyId(any())).thenReturn(new ArrayList<>(Arrays.asList(father, mother, member)));
        MemberHierachyDTO memberHierachyDTO = familyTreeService.getFamilyTree(familySmith.getId());
        //then - verify the output
        assertThat(memberHierachyDTO.getId()).isEqualTo(father.getId());
    }
}