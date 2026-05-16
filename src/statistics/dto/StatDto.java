package statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 통계 범용 DTO
 * label  : 그룹 기준 이름(카테고리명, 연령대, 등급 등)
 * count  : 구매 건수
 * amount : 구매 총액
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatDto {
    private String label;
    private long count;
    private long amount;
}

