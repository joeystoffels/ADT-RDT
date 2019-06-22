package nl.nickhartjes.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DatabaseTestConfig {

    private final long nrDataPoints;
    private final int batchSize;
    private final double startValue;
    private final double upperBoundValue;

}
