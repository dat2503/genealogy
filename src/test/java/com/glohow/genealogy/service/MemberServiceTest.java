package com.glohow.genealogy.service;

import com.glohow.genealogy.dto.MarriageDTO;
import com.glohow.genealogy.dto.MemberDTO;
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

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
class MemberServiceTest {
    private  MemberRepository memberRepository;
    private  FamilyRepository familyRepository;
    private  MarriageRepository marriageRepository;
    private  FamilyTreeService familyTreeService;
    private  MemberService memberService;

    @BeforeEach
    public void setUp(){
        memberRepository = mock(MemberRepository.class);
        familyRepository = mock(FamilyRepository.class);
        marriageRepository = mock(MarriageRepository.class);
        familyTreeService = mock(FamilyTreeService.class);
        memberService = new MemberService(memberRepository, familyRepository, marriageRepository,familyTreeService);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void givenMemberId_whenFindById_thenSuccess() {
        // given - precondition or setup
        Member member = Member.builder()
                .id(1)
                .build();
        // when - action or the behavior being tested
        when(memberRepository.findById(any())).thenReturn(Optional.of(member));
        Member queiredMember = memberService.findByMemberId(member.getId());
        //then - verify the output
        assertThat(queiredMember.getId()).isEqualTo(member.getId());
    }

    @Test
    public void givenInvalidMemberId_whenFindById_thenThrowError() {
        // given - precondition or setup
        Integer id = 1;
        // when - action or the behavior being tested
        when(memberRepository.findById(id)).thenReturn(Optional.empty());
        //then - verify the output
        assertThrows(EntityNotFoundException.class, () -> memberService.findByMemberId(id));
    }

    @Test
    public void givenMarriageId_whenFindById_thenSuccess() {
        // given - precondition or setup
        Member spouse1 = Member.builder()
                .id(1)
                .build();
        Member spouse2 = Member.builder()
                .id(2)
                .build();
        Marriage marriage = Marriage.builder()
                .marriageId(1)
                .member1(spouse1)
                .member2(spouse2)
                .maritalStatus(EnumMaritalStatus.MARRIED)
                .marriageDate(LocalDate.now())
                .build();
        // when - action or the behavior being tested
        when(marriageRepository.findById(any())).thenReturn(Optional.of(marriage));
        Marriage queriedMarriage = memberService.findByMarriageId(marriage.getMarriageId());
        //then - verify the output
        assertThat(queriedMarriage.getMarriageId()).isEqualTo(marriage.getMarriageId());
        assertThat(queriedMarriage.getMember1().getId()).isEqualTo(marriage.getMember1().getId());
        assertThat(queriedMarriage.getMember2().getId()).isEqualTo(marriage.getMember2().getId());
    }

    @Test
    public void givenInvalidMarriageId_whenFindById_thenThrowError() {
        // given - precondition or setup
        Integer id = 1;
        // when - action or the behavior being tested
        when(marriageRepository.findById(id)).thenReturn(Optional.empty());
        //then - verify the output
        assertThrows(EntityNotFoundException.class, () -> memberService.findByMarriageId(id));
    }

    @Test
    public void givenRootMemberDTO_whenAddNewMember_thenSuccess() {
        // given - precondition or setup
        MemberDTO memberDTO = MemberDTO.builder()
                .firstName("John")
                .lastName("Smith")
                .isAdopted(false)
                .gender("male")
                .dob(LocalDate.parse("1674-01-01"))
                .build();

        Member member = Member.builder()
                .id(1)
                .firstName("John")
                .lastName("Smith")
                .isAdopted(false)
                .gender("male")
                .dob(LocalDate.parse("1674-01-01"))
                .build();
        // when - action or the behavior being tested
        when(memberRepository.findById(any())).thenReturn(Optional.of(member));
        Boolean isNewMemberCreated = memberService.addNewMember(memberDTO, null);
        //then - verify the output
        assertThat(isNewMemberCreated).isEqualTo(true);
        verify(familyRepository).saveAndFlush(any(Family.class));
    }

    @Test
    public void givenMemberDTO_whenAddNewMember_thenSuccess() {
        // given - precondition or setup
        Family familySmith = Family.builder()
                .id(1)
                .name("Smith")
                .build();

        Family familyThrone = Family.builder()
                .id(2)
                .name("Throne")
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

        Member spouse = Member.builder()
                .id(3)
                .family(familyThrone)
                .firstName("Ada")
                .lastName("Thorne")
                .isAdopted(false)
                .gender("female")
                .dob(LocalDate.parse("1701-01-01"))
                .build();

        Member member = Member.builder()
                .id(4)
                .family(familySmith)
                .firstName("John")
                .lastName("Smith")
                .isAdopted(false)
                .gender("male")
                .dob(LocalDate.parse("1701-01-01"))
                .parent(parentMarriage)
                .build();

        Marriage marriage = Marriage.builder()
                .member1(member)
                .member2(spouse)
                .maritalStatus(EnumMaritalStatus.MARRIED)
                .marriageDate(LocalDate.parse("1720-01-01"))
                .build();

        MarriageDTO marriageDTO = MarriageDTO.builder()
                .marriageDate(LocalDate.parse("1720-01-01"))
                .spouseId(spouse.getId())
                .maritalStatus(EnumMaritalStatus.MARRIED)
                .build();

        MemberDTO memberDTO = MemberDTO.builder()
                .firstName("John")
                .lastName("Smith")
                .isAdopted(false)
                .gender("male")
                .dob(LocalDate.parse("1674-01-01"))
                .parentId(parentMarriage.getMarriageId())
                .marriages(Collections.singletonList(marriageDTO))
                .build();

        // when - action or the behavior being tested
        when(familyRepository.findById(any())).thenReturn(Optional.of(familySmith));
        when(memberRepository.saveAndFlush(any())).thenReturn(member);
        when(marriageRepository.findById(any())).thenReturn(Optional.of(parentMarriage));
        when(memberRepository.findAllById(any())).thenReturn(Collections.singletonList(spouse));
        when(marriageRepository.saveAllAndFlush(any())).thenReturn(Collections.singletonList(marriage));
        boolean isNewMemberAdded = memberService.addNewMember(memberDTO, familySmith.getId());
        //then - verify the output
        assertThat(isNewMemberAdded).isEqualTo(true);
        verify(memberRepository).saveAndFlush(any(Member.class));
        verify(marriageRepository).saveAllAndFlush(any(List.class));
    }

    @Test
    public void givenMemberDTO_whenEditMember_thenSuccess() {
        // given - precondition or setup
        Family familySmith = Family.builder()
                .id(1)
                .name("Smith")
                .build();

        Family familyThrone = Family.builder()
                .id(2)
                .name("Throne")
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

        Member spouse = Member.builder()
                .id(3)
                .family(familyThrone)
                .firstName("Ada")
                .lastName("Thorne")
                .isAdopted(false)
                .gender("female")
                .dob(LocalDate.parse("1701-01-01"))
                .build();

        Member member = Member.builder()
                .id(4)
                .family(familySmith)
                .firstName("John")
                .lastName("Smith")
                .isAdopted(false)
                .gender("male")
                .dob(LocalDate.parse("1701-01-01"))
                .parent(parentMarriage)
                .build();

        Marriage marriage = Marriage.builder()
                .member1(member)
                .member2(spouse)
                .maritalStatus(EnumMaritalStatus.MARRIED)
                .marriageDate(LocalDate.parse("1720-01-01"))
                .build();

        MarriageDTO marriageDTO = MarriageDTO.builder()
                .marriageDate(LocalDate.parse("1720-01-01"))
                .spouseId(spouse.getId())
                .maritalStatus(EnumMaritalStatus.MARRIED)
                .build();

        MemberDTO memberDTO = MemberDTO.builder()
                .id(4)
                .firstName("John")
                .lastName("Smith")
                .isAdopted(false)
                .gender("male")
                .dob(LocalDate.parse("1674-01-01"))
                .parentId(parentMarriage.getMarriageId())
                .marriages(Collections.singletonList(marriageDTO))
                .build();

        // when - action or the behavior being tested
        when(familyRepository.findById(any())).thenReturn(Optional.of(familySmith));
        when(memberRepository.findById(any())).thenReturn(Optional.of(member));
        when(marriageRepository.findById(any())).thenReturn(Optional.of(parentMarriage));
        when(memberRepository.findAllById(any())).thenReturn(Collections.singletonList(spouse));
        when(marriageRepository.saveAllAndFlush(any())).thenReturn(Collections.singletonList(marriage));
        boolean isNewMemberAdded = memberService.updateMember(memberDTO, familySmith.getId());
        //then - verify the output
        assertThat(isNewMemberAdded).isEqualTo(true);
        verify(memberRepository).saveAndFlush(any(Member.class));
        verify(marriageRepository).saveAllAndFlush(any(List.class));
    }
}