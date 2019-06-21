package nl.nickhartjes.statistics;

import nl.nickhartjes.models.StatisticEntry;

public interface ExportAdapter {

    void addStatistiscsEntry(StatisticEntry statisticEntry);

    void finish();
}
