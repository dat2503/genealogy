package com.glohow.genealogy.model;

import com.glohow.genealogy.enumerate.EnumMaritalStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "marriage")
@Builder
public class Marriage implements Comparable<Marriage>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "marriage_id")
    private Integer marriageId;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "member1_id")
    @ToString.Exclude
    private Member member1;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "member2_id")
    private Member member2;

    @Column(name = "wedding_date")
    @Temporal(TemporalType.DATE)

    private LocalDate marriageDate;
    @Column(name = "divorce_date")

    @Temporal(TemporalType.DATE)
    private LocalDate divorceDate;

    @OneToMany(mappedBy = "parent")
    @ToString.Exclude
    private List<Member> children;

    @Enumerated(EnumType.STRING)
    private EnumMaritalStatus maritalStatus;

    @Override
    public int compareTo(Marriage marriage) {
        return this.getMarriageDate().compareTo(marriage.getMarriageDate());
    }
//    private String maritalStatus;
}
