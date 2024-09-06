package com.khoaly.book_library.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class ReportDataDto implements Serializable {
    private Long totalBook;
    private Long totalMember;
    private Long totalBorrowing;
    private Map<Integer, Long> borrowingStats;
}
