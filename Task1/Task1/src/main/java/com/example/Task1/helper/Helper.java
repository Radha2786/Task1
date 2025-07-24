package com.example.Task1.helper;

import com.example.Task1.entity.User;
import com.example.Task1.entity.UserType;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.apache.commons.math3.stat.descriptive.summary.Product;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Helper {

    // check that file is of excel file or not
    public static boolean checkExcelFormat(MultipartFile file){
        String contentType = file.getContentType();

        if(contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")){
            System.out.println("true inside helper check");
            return true;
        }else{
            System.out.println("false inside helper check");
            return false;
        }
    }

    // check that file is of CSV format or not
    public static boolean checkCSVFormat(MultipartFile file){
        String contentType = file.getContentType();
        
        if(contentType.equals("text/csv") || contentType.equals("application/csv")){
            System.out.println("true inside CSV helper check");
            return true;
        }else{
            System.out.println("false inside CSV helper check");
            return false;
        }
    }

    // check if file is either Excel or CSV
    public static boolean checkFileFormat(MultipartFile file){
        return checkExcelFormat(file) || checkCSVFormat(file);
    }

    // converts excel to list of users
    public static List<User> convertExcelToListOfUser(InputStream is) throws IOException {
//        System.out.println("input stream is...." + is);
        List<User> list = new ArrayList<>();

//        XSSFWorkbook workbook = new XSSFWorkbook(is);
//        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
//            System.out.println("Sheet " + i + ": " + workbook.getSheetName(i));
//        }

        try {
//            System.out.println("inside helper convert method..");

            XSSFWorkbook workbook = new XSSFWorkbook(is);

            // Get the first sheet (more dynamic approach)
            XSSFSheet sheet = workbook.getSheetAt(0);

            if (sheet == null) {
                System.err.println("No sheets found in the workbook.");
                return list;
            }

            System.out.println("sheet is......" + sheet);

            int rowNumber = 0;

            Iterator<Row> iterator = sheet.iterator();

            while (iterator.hasNext()) {
                Row row = iterator.next();
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                Iterator<Cell> cells = row.iterator();

                int cid = 0;

                User u = new User();
                boolean isValidUser = true; // Track validity

                while (cells.hasNext()) {
                    Cell cell = cells.next();
                    System.out.println("cid is...." + cid);
                    System.out.println("cell is...." + cell);
                    switch (cid) {
//                        case 0:
//                            u.setId((int) cell.getNumericCellValue());
//                            break;
                        case 0:
                            u.setName(cell.getStringCellValue());
                            break;
                        case 1:
                            u.setEmail(cell.getStringCellValue());
                            break;
                        case 2:
                            u.setPassword(cell.getStringCellValue());
                            break;
                        case 3:
                            u.setContactNumber(cell.getStringCellValue());
                            break;
//                      case 4:
//                          u.setUserType(cell.getCellType());
                        case 4:
                            String userTypeStr = cell.getStringCellValue().toUpperCase();
                            System.out.println("userTypeStr is...." + userTypeStr);
//                            System.out.println("UserType.valueOf(userTypeStr) is...." + UserType.valueOf(userTypeStr));
                            try {
                                u.setUserType(UserType.valueOf(userTypeStr));
                            } catch (IllegalArgumentException ex) {
                                System.err.println("Invalid userType at row " + row.getRowNum() + ": " + userTypeStr);
                                isValidUser = false; // Mark invalid
//                                continue; // skip this user or handle as invalid
                            }
                            break;

                        default:
                            break;
                    }
                    cid++;
                }
                System.out.println(u.getUserType());
                if (isValidUser) {
                    list.add(u);
                }
//                list.add(u);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // converts CSV to list of users
    public static List<User> convertCSVToListOfUser(InputStream is) throws IOException {
        List<User> list = new ArrayList<>();
        
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(is))) {
            List<String[]> records = csvReader.readAll();
            
            // Skip header row if present
            boolean isFirstRow = true;
            
            for (String[] record : records) {
                if (isFirstRow) {
                    isFirstRow = false;
                    // Check if first row contains headers (non-numeric data in first column)
                    if (record.length > 0 && !record[0].matches("\\d+")) {
                        continue; // Skip header row
                    }
                }
                
                if (record.length >= 5) { // Ensure we have all required fields
                    User user = new User();
                    boolean isValidUser = true;
                    
                    try {
                        // CSV format: name, email, userType, password, contactNumber
                        user.setName(record[0].trim());
                        user.setEmail(record[1].trim());
                        
                        String userTypeStr = record[2].trim().toUpperCase();
                        user.setUserType(UserType.valueOf(userTypeStr));
                        
                        user.setPassword(record[3].trim());
                        user.setContactNumber(record[4].trim());
                        
                    } catch (IllegalArgumentException ex) {
                        System.err.println("Invalid userType in CSV: " + record[2]);
                        isValidUser = false;
                    } catch (Exception ex) {
                        System.err.println("Error parsing CSV row: " + String.join(",", record));
                        isValidUser = false;
                    }
                    
                    if (isValidUser) {
                        list.add(user);
                    }
                } else {
                    System.err.println("Insufficient data in CSV row: " + String.join(",", record));
                }
            }
            
        } catch (CsvException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
            e.printStackTrace();
        }
        
        return list;
    }
}
