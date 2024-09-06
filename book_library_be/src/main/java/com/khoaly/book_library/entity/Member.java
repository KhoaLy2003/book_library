package com.khoaly.book_library.entity;

import com.khoaly.book_library.enumeration.MembershipTypeEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "member")
@EntityListeners(AuditingEntityListener.class)
public class Member extends BaseEntity{
    @Id
    @Column(name = "member_id", nullable = false)
    private Integer id;

    @Size(max = 50)
    @NotNull
    @Column(name = "membership_number", nullable = false, length = 50, unique = true)
    private String membershipNumber;

    @NotNull
    @Column(name = "membership_start_date", nullable = false)
    private LocalDate membershipStartDate;

    @Column(name = "membership_end_date")
    private LocalDate membershipEndDate;

    @Column(name = "membership_type", length = 50)
    @Enumerated(EnumType.STRING)
    private MembershipTypeEnum membershipType;

    @Column(name = "total_books_borrowed")
    private Integer totalBooksBorrowed;

    @Column(name = "total_late_returns")
    private Integer totalLateReturns;

    @Column(name = "current_borrowed_books")
    private Integer currentBorrowedBooks;

    @OneToMany(mappedBy = "memberBorrow")
    private Set<Borrowing> borrowings = new LinkedHashSet<>();

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id", nullable = false)
    @MapsId
    private Account account;
}
