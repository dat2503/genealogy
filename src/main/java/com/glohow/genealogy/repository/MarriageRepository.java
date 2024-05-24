package com.glohow.genealogy.repository;

import com.glohow.genealogy.model.Marriage;
import com.glohow.genealogy.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarriageRepository extends JpaRepository<Marriage, Integer> {
    List<Marriage> findByMember1OrMember2(Member Member1, Member Member2);
}
