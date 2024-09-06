package com.khoaly.book_library.repository;

import com.khoaly.book_library.entity.Account;
import com.khoaly.book_library.enumeration.AccountStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    boolean existsByEmail(String email);
    Optional<Account> findAccountByIdAndStatusEquals(int accountId, AccountStatusEnum accountStatus);
//    Optional<Account> findAccountByMembershipNumber(String membershipNumber);
    long countByStatus(AccountStatusEnum status);
}