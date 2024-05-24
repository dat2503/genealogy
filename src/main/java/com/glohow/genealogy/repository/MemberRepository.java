package com.glohow.genealogy.repository;

import com.glohow.genealogy.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Integer> {
    List<Member> findAllByFamilyId(Integer id);
}
