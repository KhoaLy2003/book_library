package com.khoaly.book_library.specification;

import com.khoaly.book_library.entity.Borrowing;
import com.khoaly.book_library.enumeration.BorrowingStatusEnum;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BorrowingSpecification {
    public static Specification<Borrowing> hasStatus(BorrowingStatusEnum status) {
        return (
                Root<Borrowing> root,
                CriteriaQuery<?> query,
                CriteriaBuilder builder
        ) -> {
            if (Objects.isNull(status)) {
                return null;
            }
            return builder.equal(root.get("status"), status);
        };
    }

    public static Specification<Borrowing> borrowingDateBetween(LocalDate startDate, LocalDate endDate) {
        return (
                Root<Borrowing> root,
                CriteriaQuery<?> query,
                CriteriaBuilder builder
        ) -> {
            if (Objects.isNull(startDate) || Objects.isNull(endDate)) {
                return null;
            }
            return builder.between(root.get("borrowingDate"), startDate, endDate);
        };
    }

    public static Specification<Borrowing> borrowingReportInYear(int year) {
        return (
                Root<Borrowing> root,
                CriteriaQuery<?> query,
                CriteriaBuilder builder
        ) -> {
            if (year == NumberUtils.INTEGER_ZERO) {
                return null;
            }

            return builder.equal(root.get("borrowingDate"), year);
        };
    }
}
