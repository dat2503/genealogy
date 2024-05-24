package com.glohow.genealogy.service;

import com.glohow.genealogy.dto.MarriageDTO;
import com.glohow.genealogy.dto.MemberDTO;
import com.glohow.genealogy.enumerate.EnumErrorMessage;
import com.glohow.genealogy.model.Family;
import com.glohow.genealogy.model.Marriage;
import com.glohow.genealogy.model.Member;
import com.glohow.genealogy.repository.FamilyRepository;
import com.glohow.genealogy.repository.MarriageRepository;
import com.glohow.genealogy.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final FamilyRepository familyRepository;
    private final MarriageRepository marriageRepository;
    private final FamilyTreeService familyTreeService;

    /**
     * find marriage by id
     *
     * @param marriageId
     * @return
     */
    public Marriage findByMarriageId(Integer marriageId){
        return marriageRepository
                .findById(marriageId)
                .orElseThrow(() ->
                        new EntityNotFoundException(EnumErrorMessage.MARRIAGE_NOT_FOUND.name()));
    }

    /**
     * find member by id
     *
     * @param memberId
     * @return
     */
    public Member findByMemberId(Integer memberId){
        return memberRepository
                .findById(memberId)
                .orElseThrow(() ->
                        new EntityNotFoundException(EnumErrorMessage.MEMBER_NOT_FOUND.name()));
    }

    /**
     * Add new member
     *
     * @param memberDTO
     * @return
     */
    @CacheEvict(value = {"family", "member-family"}, key = "#familyId")
    @Transactional(value = Transactional.TxType.REQUIRED,
            rollbackOn = RuntimeException.class)
    public boolean addNewMember(MemberDTO memberDTO, Integer familyId) {
        try {
            // add member family or root member
            Family family = getOrAddFamily(memberDTO);
            // get parents
            Marriage parents = null;
            if(memberDTO.getParentId() != null){
                parents = findByMarriageId(memberDTO.getParentId());
            }

            // add Member information
            Member member = Member.builder()
                    .firstName(memberDTO.getFirstName())
                    .family(family)
                    .lastName(memberDTO.getLastName())
                    .dob(memberDTO.getDob())
                    .dod(memberDTO.getDod())
                    .gender(memberDTO.getGender())
                    .isAdopted(memberDTO.isAdopted())
                    .parent(parents)
                    .build();

            memberRepository.saveAndFlush(member);
            if(!CollectionUtils.isEmpty(memberDTO.getMarriages())){
                addMarriages(member, memberDTO.getMarriages());
            }
            return true;
        }catch(RuntimeException ex){
            throw new RuntimeException(EnumErrorMessage.ERROR_ADD_USER.name() + ": " + ex.getMessage());
        }

    }

    /**
     * Edit member
     *
     * @param memberDTO
     * @return
     */
    @CacheEvict(value = {"family", "member-family"}, key = "#familyId")
    @Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = RuntimeException.class)
    public boolean updateMember(MemberDTO memberDTO, Integer familyId) {
        try{
            Member member = findByMemberId(memberDTO.getId());

            member.setDob(memberDTO.getDob());
            member.setDod(memberDTO.getDod());
            member.setFirstName(memberDTO.getFirstName());
            member.setLastName(memberDTO.getLastName());
            member.setAdopted(memberDTO.isAdopted());
            member.setGender(memberDTO.getGender());

            if(!member.getParent().getMarriageId()
                    .equals(memberDTO.getParentId())){
                Marriage parents = findByMarriageId(memberDTO.getParentId());
                member.setParent(parents);
            }

            memberRepository.saveAndFlush(member);

            List<MarriageDTO> marriageDTOs = memberDTO.getMarriages();
            addOrEditMarriages(member, marriageDTOs);

            return true;
        }catch (RuntimeException ex){
            throw new RuntimeException(EnumErrorMessage.ERROR_UPDATE_USER.name() + ": " + ex.getMessage());
        }
    }

    /**
     * Get family by family id or add new one
     *
     * @param memberDTO
     * @return
     */
    private Family getOrAddFamily(MemberDTO memberDTO){
        Family family = new Family();
        if(memberDTO.getFamilyId() == null){
            family.setName(memberDTO.getLastName());
            familyRepository.saveAndFlush(family);
        }else{
            family = familyTreeService.findByFamilyId(memberDTO.getFamilyId());
        }
        return  family;
    }

    /**
     * Add or edit a member marital status
     *
     * @param member
     * @param marriageDTOs
     */
    private void addOrEditMarriages(Member member, List<MarriageDTO> marriageDTOs){
        if(!CollectionUtils.isEmpty(marriageDTOs)){
            addMarriages(member, marriageDTOs);
            editMarriages(marriageDTOs);
        }
    }

    /**
     * edit current marriages of the Member
     *
     * @param marriageDTOs
     */
    private void editMarriages(List<MarriageDTO> marriageDTOs){
        Map<Integer, MarriageDTO> currentMarriageDTOMap = marriageDTOs.stream()
                .filter(marriage -> marriage.getId() != null)
                .collect(Collectors.toMap(MarriageDTO::getId, marriage -> marriage));
        if(!CollectionUtils.isEmpty(currentMarriageDTOMap)){
            List<Marriage> currentMarriages = marriageRepository.findAllById(currentMarriageDTOMap.keySet());
            if(!CollectionUtils.isEmpty(currentMarriages)){
                currentMarriages.forEach(marriage -> {
                    MarriageDTO marriageDTO = currentMarriageDTOMap.get(marriage.getMarriageId());
                    if(marriageDTO != null){
                        marriage.setMaritalStatus(marriageDTO.getMaritalStatus());
                        marriage.setMarriageDate(marriageDTO.getMarriageDate());
                        marriage.setDivorceDate(marriageDTO.getDivorcedDate());
                    }
                });
                marriageRepository.saveAllAndFlush(currentMarriages);
            }
        }
    }

    /**
     * Add new marriages to Member
     *
     * @param member
     * @param marriageDTOS
     */
    private void addMarriages(Member member, List<MarriageDTO> marriageDTOS){
        List<MarriageDTO> newMarriageDTOs = marriageDTOS
                .stream()
                .filter(marriage -> marriage.getId() == null)
                .toList();
        List<Marriage> newMarriages = new ArrayList<>();
        List<Integer> spousesIds = newMarriageDTOs.stream().map(MarriageDTO::getSpouseId).toList();
        List<Member> spouses = memberRepository.findAllById(spousesIds);
        if(!CollectionUtils.isEmpty(spouses)){
            Map<Integer, Member> spouseMap = spouses
                    .stream()
                    .collect(Collectors.toMap(
                            Member::getId,
                            spouse -> spouse));

            newMarriageDTOs.forEach(marriageDTO -> {
                if(spouseMap.get(marriageDTO.getSpouseId()) != null){

                    Marriage marriage = Marriage.builder()
                            .member1(member)
                            .member2(spouseMap.get(marriageDTO.getSpouseId()))
                            .marriageDate(marriageDTO.getMarriageDate())
                            .divorceDate(marriageDTO.getDivorcedDate())
                            .maritalStatus(marriageDTO.getMaritalStatus())
                            .build();
                    newMarriages.add(marriage);
                }
            });
            marriageRepository.saveAllAndFlush(newMarriages);
        }
    }
}
