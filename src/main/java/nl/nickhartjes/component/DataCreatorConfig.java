package nl.nickhartjes.component;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DataCreatorConfig {

    private final long nrDataPoints;
    private final int batchSize;
    private final double startValue;
    private final double upperBoundValue;

}
