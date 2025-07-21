package com.example.Task1.helper;

import com.example.Task1.entity.User;
import com.example.Task1.entity.UserType;
import org.apache.commons.math3.stat.descriptive.summary.Product;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
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

    // converts excel to list of users
    public static List<User> convertExcelToListOfUser(InputStream is) throws IOException {
//        System.out.println("input stream is...." + is);
        List<User> list = new ArrayList<>();

//        XSSFWorkbook workbook = new XSSFWorkbook(is);
//        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
//            System.out.println("Sheet " + i + ": " + workbook.getSheetName(i));
//        }

        try {
            System.out.println("inside helper convert method..");

            XSSFWorkbook workbook = new XSSFWorkbook(is);

            XSSFSheet sheet = workbook.getSheet("datanew");

            if (sheet == null) {
                System.err.println("Sheet 'Users' does not exist in the workbook.");
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
}
