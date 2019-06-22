package nl.nickhartjes.exporter;

import nl.nickhartjes.models.StatisticEntry;

public interface ExportAdapter {

    void addStatisticsEntry(StatisticEntry statisticEntry);

    void finish();
}
