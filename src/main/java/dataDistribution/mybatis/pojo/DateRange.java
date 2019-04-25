package dataDistribution.mybatis.pojo;

import lombok.Getter;

/**
 * @Author: LX
 * @Date: 2019/4/23 14:33
 * @Version: 1.0
 */

@Getter
public class DateRange {
    String startDate;
    String endDate;

    public DateRange(String startDate, String endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
