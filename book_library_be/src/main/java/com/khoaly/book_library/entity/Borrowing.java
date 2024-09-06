package com.khoaly.book_library.entity;

import com.khoaly.book_library.enumeration.BorrowingStatusEnum;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "borrowing")
public class Borrowing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "borrowing_id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_borrow_id", nullable = false)
    private Member memberBorrow;

    @Column(name = "status", length = 20)
    @Enumerated(EnumType.STRING)
    private BorrowingStatusEnum status;

    @NotNull
    @Column(name = "borrowing_date", nullable = false)
    private LocalDate borrowingDate;

    @Column(name = "return_date")
    private LocalDate returnDate;

    @NotNull
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @NotNull
    @Column(name = "total_book", nullable = false)
    private Integer totalBook;

    @OneToMany(mappedBy = "borrowing", cascade = CascadeType.ALL)
    private Set<BorrowingDetail> borrowingDetails = new LinkedHashSet<>();
}