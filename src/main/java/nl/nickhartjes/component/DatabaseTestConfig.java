package nl.nickhartjes.component;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
class DatabaseTestConfig {

    private final long nrDataPoints;
    private final int batchSize;
    private final double startValue;
    private final double upperBoundValue;

}
