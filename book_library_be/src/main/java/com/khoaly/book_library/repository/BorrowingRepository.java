package com.khoaly.book_library.repository;

import com.khoaly.book_library.entity.Borrowing;
import com.khoaly.book_library.enumeration.BorrowingStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BorrowingRepository extends JpaRepository<Borrowing, Integer>, JpaSpecificationExecutor<Borrowing> {
    @Query("SELECT b FROM Borrowing b WHERE b.memberBorrow.id = :memberBorrowId " +
            "AND (:status IS NULL OR b.status = :status)")
    Page<Borrowing> findByMemberBorrow_IdAndOptionalStatus(
            @Param("memberBorrowId") int memberBorrowId,
            @Param("status") BorrowingStatusEnum status, Pageable pageable);

    List<Borrowing> findAllByDueDate(LocalDate dueDate);

    @Query("SELECT EXTRACT(MONTH FROM b.borrowingDate) AS month, " +
            "COUNT(b) AS count " +
            "FROM Borrowing b " +
            "WHERE EXTRACT(YEAR FROM b.borrowingDate) = :year " +
            "GROUP BY EXTRACT(MONTH FROM b.borrowingDate)" +
            "ORDER BY EXTRACT(MONTH FROM b.borrowingDate)")
    List<Object[]> findBorrowingCountByMonthAndYear(@Param("year") int year);
}