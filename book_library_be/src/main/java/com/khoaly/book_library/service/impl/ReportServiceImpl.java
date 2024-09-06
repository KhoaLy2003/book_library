package com.khoaly.book_library.service.impl;

import com.khoaly.book_library.dto.ReportDataDto;
import com.khoaly.book_library.enumeration.AccountStatusEnum;
import com.khoaly.book_library.repository.AccountRepository;
import com.khoaly.book_library.repository.BookRepository;
import com.khoaly.book_library.repository.BorrowingRepository;
import com.khoaly.book_library.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportServiceImpl.class);
    private final BookRepository bookRepository;
    private final AccountRepository accountRepository;
    private final BorrowingRepository borrowingRepository;

    @Override
    public ReportDataDto getReportData() {

        List<Object[]> results = borrowingRepository.findBorrowingCountByMonthAndYear(2024);

        long totalBook = bookRepository.count();
        long totalAccount = accountRepository.countByStatus(AccountStatusEnum.ACTIVE);
        long totalBorrowing = borrowingRepository.count();
//        Map<Integer, Long> borrowingStats = results.stream()
//                .collect(Collectors.toMap(
//                        row -> ((Number) row[0]).intValue(),
//                        row -> ((Number) row[1]).longValue()
//                ));

        Map<Integer, Long> monthCounts = IntStream.rangeClosed(1, 12)
                .boxed()
                .collect(Collectors.toMap(Function.identity(), month -> 0L));

        // Populate the map with actual counts
        results.forEach(row -> monthCounts.put(((Number) row[0]).intValue(), ((Number) row[1]).longValue()));

        return ReportDataDto
                .builder()
                .totalBook(totalBook)
                .totalMember(totalAccount)
                .totalBorrowing(totalBorrowing)
                .borrowingStats(monthCounts)
                .build();
    }
}
