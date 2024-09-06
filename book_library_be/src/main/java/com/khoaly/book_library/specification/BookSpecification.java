package com.khoaly.book_library.specification;

import com.khoaly.book_library.entity.Book;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookSpecification {
    public static Specification<Book> titleLike(String title) {
        return (
                Root<Book> root,
                CriteriaQuery<?> query,
                CriteriaBuilder builder
        ) -> {
            if (Objects.isNull(title)) {
                return null;
            }
            return builder.like(root.get("title"), "%" + title + "%");
        };
    }
}
