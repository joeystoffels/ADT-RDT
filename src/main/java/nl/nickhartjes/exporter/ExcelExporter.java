package nl.nickhartjes.exporter;

import lombok.extern.slf4j.Slf4j;
import nl.nickhartjes.models.StatisticEntry;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


@Slf4j
public class ExcelExporter implements ExportAdapter {

    private Workbook workbook;

    public ExcelExporter() {
        workbook = new XSSFWorkbook();
    }

    @Override
    public void addStatisticsEntry(StatisticEntry statisticEntry) {
        Sheet sheet;
        String sheetName = statisticEntry.getName() + '-' + statisticEntry.getAction();
        if (statisticEntry.getBatch() == 1) {
            sheet = workbook.createSheet(sheetName);
            sheet.setColumnWidth(0, 6000);
            sheet.setColumnWidth(1, 4000);
            sheet.setColumnWidth(2, 4000);
            sheet.setColumnWidth(3, 4000);
            sheet.setColumnWidth(4, 4000);

            Row header = sheet.createRow(0);
            Cell headerCell = header.createCell(0);
            headerCell.setCellValue("Batch nr.");

            headerCell = header.createCell(1);
            headerCell.setCellValue("Batch Total");

            headerCell = header.createCell(2);
            headerCell.setCellValue("Seconds");

            headerCell = header.createCell(3);
            headerCell.setCellValue("Milliseconds");

            headerCell = header.createCell(4);
            headerCell.setCellValue("Nanoseconds");
        } else {
            sheet = workbook.getSheet(sheetName);
        }

        Row row = sheet.createRow(statisticEntry.getBatch() + 1);
        Cell cell = row.createCell(0);
        cell.setCellValue(statisticEntry.getBatch());

        cell = row.createCell(1);
        cell.setCellValue(statisticEntry.getBatch() * (double) statisticEntry.getBatchSize());

        cell = row.createCell(2);
        cell.setCellValue(statisticEntry.getSeconds());

        cell = row.createCell(3);
        cell.setCellValue(statisticEntry.getMilliseconds());

        cell = row.createCell(4);
        cell.setCellValue(statisticEntry.getNanoseconds());
    }

    @Override
    public void finish() {
        File currDir = new File(".");
        String path = currDir.getAbsolutePath();
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
        String fileLocation = path.substring(0, path.length() - 1) + "files" + File.separator + "data-" + timeStamp + ".xlsx";

        try {
            FileOutputStream outputStream = new FileOutputStream(fileLocation);
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            log.error(e.toString());

        }
    }
}
