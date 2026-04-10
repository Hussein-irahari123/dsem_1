package dsem.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemandSummaryDTO {
    private Long explosiveId;
    private String explosiveName;
    private String explosiveUnit;

    private Double totalRequested;     // Sum of approved requests in period
    private Double totalReturned;      // Sum of returns in period
    private Double netRequest;         // totalRequested - totalReturned
    private Double availableSubStock;  // Current sub-magazine stock
    private Double rutogoRequest;      // netRequest - availableSubStock (if > 0)

    private String fromDate;
    private String toDate;
}
