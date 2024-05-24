package com.glohow.genealogy.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
;
import java.util.List;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "member")
@Builder
public class Member implements Comparable<Member>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "dob")
    @Temporal(TemporalType.DATE)
    private LocalDate dob;

    @Column(name = "dod")
    @Temporal(TemporalType.DATE)
    private LocalDate dod;

    private String gender;

    @Column(name = "is_adopted")
    private boolean isAdopted;

    @OneToMany(mappedBy = "member1", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Marriage> marriages1;

    @OneToMany(mappedBy = "member2", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Marriage> marriages2;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Marriage parent;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Family family;

    @Override
    public int compareTo(Member member) {
        return this.getDob().compareTo(member.getDob());
    }
}
