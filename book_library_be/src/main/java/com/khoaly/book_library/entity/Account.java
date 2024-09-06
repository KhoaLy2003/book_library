package com.khoaly.book_library.entity;

import com.khoaly.book_library.enumeration.AccountRoleEnum;
import com.khoaly.book_library.enumeration.AccountStatusEnum;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
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

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "account")
@EntityListeners(AuditingEntityListener.class)
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id", nullable = false)
    private Integer id;

    @Size(max = 200)
    @Column(name = "first_name", length = 200)
    private String firstName;

    @Size(max = 200)
    @Column(name = "last_name", length = 200)
    private String lastName;

    @Size(max = 350)
    @NotNull
    @Column(name = "email", nullable = false, length = 350, unique = true)
    private String email;

    @Size(max = 20)
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Size(max = 255)
    @Column(name = "address")
    private String address;

    @Column(name = "status", length = 20)
    @Enumerated(EnumType.STRING)
    private AccountStatusEnum status;

    @Lob
    @Column(name = "notes")
    private String notes;

    @Column(name = "role", length = 20)
    @Enumerated(EnumType.STRING)
    private AccountRoleEnum role;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
    private Member member;
}