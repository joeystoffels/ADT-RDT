package nl.nickhartjes.statistics;

import java.util.ArrayList;
import java.util.List;

public class Exporter {

    private List<ExportAdapter> exportAdapters;

    public Exporter() {
        exportAdapters = new ArrayList<>();
    }

    public void add(ExportAdapter exportAdapter) {
        this.exportAdapters.add(exportAdapter);
    }

    public List<ExportAdapter> getExportAdapters() {
        return exportAdapters;
    }
}


