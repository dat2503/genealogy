package com.glohow.genealogy.service;

import com.glohow.genealogy.dto.MemberHierachyDTO;
import com.glohow.genealogy.dto.SpouseChildrenDTO;
import com.glohow.genealogy.enumerate.EnumErrorMessage;
import com.glohow.genealogy.model.*;
import com.glohow.genealogy.repository.FamilyRepository;
import com.glohow.genealogy.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FamilyTreeService {

    private final MemberRepository memberRepository;
    private final FamilyRepository familyRepository;

    /**
     * find family by id
     *
     * @param familyId
     * @return
     */
    public Family findByFamilyId(Integer familyId){
        return familyRepository
                .findById(familyId)
                .orElseThrow(() ->
                        new EntityNotFoundException(EnumErrorMessage.FAMILY_NOT_FOUND.name()));
    }

    @Cacheable(value = "family", key = "#familyId")
    public MemberHierachyDTO getFamilyTree(Integer familyId){
        List<Member> members =getFamilyMember(familyId);
        MemberHierachyDTO result = new MemberHierachyDTO();
        if(!CollectionUtils.isEmpty(members)){
            Map<Integer, List<Member>> childrenMap = getChildMap(members);
            Member root = getRootMember(members);
            result = buildHierarchy(root, childrenMap, 4);
        }
        return result;
    }

    @Cacheable(value = "member-family", key = "#familyId")
    public MemberHierachyDTO getMemberFamily(Integer familyId, Integer memberId){
        List<Member> members = getFamilyMember(familyId);
        MemberHierachyDTO result = new MemberHierachyDTO();
        if(!CollectionUtils.isEmpty(members)){
            Member currentMember = getMemberFromFamily(memberId, members);
            setCurrentMemberParent(currentMember, members, result);
            Map<Integer, List<Member>> childrenMap = getChildMap(members);
            result = buildHierarchy(currentMember, childrenMap, 0);
        }
        return result;
    }

    /**
     * recursively build up family hierarchy
     *
     * @param member
     * @param childrenMap
     * @param counter
     * @return
     */
    private MemberHierachyDTO buildHierarchy(Member member, Map<Integer, List<Member>> childrenMap, int counter){
        try{
            MemberHierachyDTO result = new MemberHierachyDTO(member);

            // take to the 3rd generation if getting a member family (grand children)
            if(counter == 3){
                return result;
            }
            Set<Marriage> marriages = new HashSet<>();
            if(!CollectionUtils.isEmpty(member.getMarriages1())){
                marriages.addAll(member.getMarriages1());
            }
            if(!CollectionUtils.isEmpty(member.getMarriages2())){
                marriages.addAll(member.getMarriages2());
            }

            if(!CollectionUtils.isEmpty(marriages)){
                List<SpouseChildrenDTO> spouses = marriages
                        .stream()
                        .sorted()
                        .map(marriage -> {
                            // get spouse from marriages
                            Member spouse = marriage.getMember1().equals(member)
                                    ? marriage.getMember2()
                                    : marriage.getMember1();
                            List<Member> children = childrenMap.getOrDefault(marriage.getMarriageId(), Collections.emptyList());
                            List<MemberHierachyDTO> childDTOs = children.stream()
                                    .sorted()
                                    .map(child -> buildHierarchy(child, childrenMap, counter+1))
                                    .toList();

                            return new SpouseChildrenDTO(new MemberHierachyDTO(spouse), childDTOs);
                        })
                        .toList();
                    result.setSpouses(spouses);
            }
            return result;
        }catch (Exception ex){
            throw  new RuntimeException(EnumErrorMessage.ERROR_BUILD_HIERARCHY.name() + ": " + ex.getMessage());
        }

    }

    /**
     * get member by id from list of family members
     *
     * @param memberId
     * @param members
     * @return
     */
    private Member getMemberFromFamily(Integer memberId, List<Member> members){
        return members.stream()
                .filter(member
                        -> member.getId().equals(memberId))
                .findFirst()
                .orElseThrow(()
                        -> new EntityNotFoundException(EnumErrorMessage.MEMBER_NOT_FOUND.name()));
    }

    /**
     * get list of family members from family id
     *
     * @param familyId
     * @return
     */
    private List<Member> getFamilyMember(Integer familyId){
        findByFamilyId(familyId);
        List<Member> members = memberRepository.findAllByFamilyId(familyId);
        if(!CollectionUtils.isEmpty(members)){
            return members;
        }else{
            return Collections.emptyList();
        }
    }

    /**
     * get parent-child map from list of family members
     *
     * @param members
     * @return
     */
    private Map<Integer, List<Member>> getChildMap(List<Member> members){
        try{
            return members.stream()
                    .filter(member -> member.getParent() != null)
                    .collect(Collectors.groupingBy(member -> member.getParent().getMarriageId()));
        }catch (Exception ex){
            throw new RuntimeException(EnumErrorMessage.ERROR_CHILD_MAP.name() + ": " + ex.getMessage());
        }

    }

    /**
     * Set parent for current member from family members
     *
     * @param currentMember
     * @param members
     * @param memberHierachyDTO
     */
    private void setCurrentMemberParent(Member currentMember, List<Member> members, MemberHierachyDTO memberHierachyDTO){
        try{
            Marriage parents = currentMember.getParent();
            if(parents != null){
                List<MemberHierachyDTO> parentDTOs = members.stream()
                        .filter(member ->
                                member.equals(parents.getMember1())
                                        || member.equals(parents.getMember2()))
                        .map(MemberHierachyDTO::new)
                        .toList();
                memberHierachyDTO.setParents(parentDTOs);
            }
        }catch (Exception ex){
            throw new RuntimeException(EnumErrorMessage.ERROR_SET_PARENT.name() + ": " + ex.getMessage());
        }
    }

    /**
     * get family root member
     *
     * @param members
     * @return
     */
    private Member getRootMember(List<Member> members){
        return members.stream()
                .sorted()
                .filter(member -> member.getParent() == null)
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(EnumErrorMessage.ERROR_NO_ROOT_DETECTED.name()));
    }
}
