package statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 월별 추이용 DTO
 * label  : 그룹명(카테고리명, 등급 등)
 * month  : 1 ~ 12
 * count  : 구매 건수
 * amount : 총 결제금액(final_price 합산)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyStatDto {
    private String label;
    private int    month;
    private long   count;
    private long   amount;
}

