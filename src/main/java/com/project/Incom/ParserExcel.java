package com.project.Incom;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class ParserExcel {

    private final ArrayList<String> _sheetParser = new ArrayList<>();
    private final ArrayList<String> _textPrint = new ArrayList<>();
    private String _nameFiles;
    private Workbook _workbook;
    private final JProgressBar _progressBar;

    public ParserExcel(JProgressBar bar) {
        this._progressBar = bar;
    }

    /**
     * Loading file excel
     */
    public void loadingExcelFile(File nameFile) {
        setNameFiles(nameFile.getName());
        _progressBar.setString("Загрузка файла");
        try {
            FileInputStream inputStream = new FileInputStream(nameFile);
            _workbook = getWorkBook(nameFile.getName(), inputStream);
            _sheetParser.clear();

            _progressBar.setMaximum(_workbook.getNumberOfSheets());

            for (int i = 0; i < _workbook.getNumberOfSheets(); i++) {
                Sheet sheet = _workbook.getSheetAt(i);
                String shellName = findNameFileTN(sheet);
                if(shellName != null){
                    _sheetParser.add(shellName);
                    _progressBar.setValue(i);
                }
            }
            _progressBar.setValue(_workbook.getNumberOfSheets());
            _progressBar.setString("Загрузка завершина");
            _workbook.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * set name files is Save folder
     */
    private void setNameFiles(String nameFiles) {
        String[] nameList = nameFiles.split("-");
        _nameFiles = nameList[0].substring(3) + "-" + nameList[1].substring(4);
    }

    /**
     * Get file loader excel xlsx - xls
     */
    private Workbook getWorkBook(String nameFiles, InputStream inputStream) throws IOException {
        Workbook workbook;
        if (nameFiles.endsWith(".xlsx")) {
            workbook = new XSSFWorkbook(inputStream);
        }
        else {
            workbook = new HSSFWorkbook(inputStream);
        }
        return workbook;
    }

    /**
     * Find sheet is TH
     */
    private String findNameFileTN(Sheet sheet) {
        String temp = null;
        Iterator<Row> rowIterator = sheet.rowIterator();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();

            Iterator<Cell> cellIterator = row.cellIterator();

            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();


                for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
                    CellRangeAddress region = sheet.getMergedRegion(i);

                    int colIndex = region.getFirstColumn();
                    int rowNum = region.getFirstRow();

                    if (rowNum == cell.getRowIndex() && colIndex == cell.getColumnIndex()) {
                        if (sheet.getRow(rowNum).getCell(colIndex).getCellType() == CellType.STRING) {
                            if (sheet.getRow(rowNum).getCell(colIndex).getStringCellValue().contains("ТН")) {
                                if(!Objects.equals(temp, sheet.getSheetName())){
                                    temp = sheet.getSheetName();
                                    return temp;
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Parser excel sheet
     */
    public void parserExcel() {
        _textPrint.clear();
        for (int i = 0; i <= _sheetParser.size() -1; i++) {
            Sheet sheet = _workbook.getSheetAt(i);
            for (int countRow = 0; countRow <= sheet.getLastRowNum(); countRow++) {
                if (sheet.getRow(countRow) != null) {
                    Cell cell = sheet.getRow(countRow).getCell(1);
                    if (cell != null) {
                        if (cell.getCellType() == CellType.STRING) {// marker text wire
                            String normalNameText = cell.getStringCellValue();
                            if (normalNameText.startsWith("W") && !normalNameText.contains("1.") &&
                                !normalNameText.contains("Раз")) {
                                _textPrint.add(normalNameText);
                            }
                        }
                    }
                }
            }
        }
    }

    public String getNameFiles() {
        return _nameFiles;
    }

    public ArrayList<String> getTextPrint() {
        return _textPrint;
    }
}