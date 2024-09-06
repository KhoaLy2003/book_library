package com.khoaly.book_library.entity;

import com.khoaly.book_library.enumeration.BookStatusEnum;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
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
@Table(name = "book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id", nullable = false)
    private Integer id;

    @Size(max = 400)
    @Column(name = "title", length = 400)
    private String title;

    @Column(name = "authors", columnDefinition = "TEXT")
    private String authors;

    @Column(name = "average_rating")
    private Double averageRating;

    @Size(max = 10)
    @Column(name = "isbn", length = 10)
    private String isbn;

    @Size(max = 13)
    @Column(name = "isbn13", length = 13)
    private String isbn13;

    @Column(name = "language_code")
    private String languageCode;

    @Column(name = "num_pages")
    private Integer numPages;

    @Column(name = "publication_date")
    private LocalDate publicationDate;

    @JoinColumn(name = "publisher")
    private String publisher;

    @Column(name = "status", length = 20)
    @Enumerated(EnumType.STRING)
    private BookStatusEnum status;

    @OneToOne(mappedBy = "book", cascade = CascadeType.ALL)
    private BookInventory bookInventory;

    @OneToMany(mappedBy = "book")
    private Set<BorrowingDetail> borrowingDetails = new LinkedHashSet<>();
}