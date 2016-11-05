package controllers;

import models.Lesson;
import models.ScheduleURL;
import play.*;
import play.mvc.*;
import views.html.*;
import play.data.*;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;
import java.net.URL;

import play.db.ebean.*;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressBase;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;


public class Parser {

    public static void parseSchedule(File f) throws Exception {

        org.apache.poi.poifs.filesystem.POIFSFileSystem fs =
                new org.apache.poi.poifs.filesystem.POIFSFileSystem(new FileInputStream(f));
        org.apache.poi.hssf.usermodel.HSSFWorkbook workbook =
                new org.apache.poi.hssf.usermodel.HSSFWorkbook(fs);
        org.apache.poi.hssf.usermodel.HSSFSheet sheet1 = workbook.getSheetAt(0);
        org.apache.poi.hssf.usermodel.HSSFRow row;
        org.apache.poi.hssf.usermodel.HSSFCell cell;

        String[][] dataBase = new String[240][60]; //240 и 60 - примерный размер используемого пространства
        // разбираем файл Excel в массив
        for (int i = 0; i < 240; i++) {
            row = sheet1.getRow(i + 1);
            for (int j = 0; j < 60; j++) {
                cell = row.getCell(j);
                if (cell != null) {
                    switch (cell.getCellType()) {
                        case HSSFCell.CELL_TYPE_STRING:
                            dataBase[i][j] = cell.getStringCellValue().trim();
                            break;
                    }
                }
            }
        }
        // массив уже парсим в БД

        int x;
        // ищем, где начинается расписание
        for (x = 0; x < 100; x++) {
            if ("Дни".equals(dataBase[x][0])) {
                break;
            }
        }

        // startLine - строка, где начинается "чистое" расписание. x и y - для обхода файла
        int startLine = x;
        int y = 2;
        String d; //d -> day

        while (true) {

            String dbxy = dataBase[x][y];
            String gN = ""; //gN -> groupNumber
            //System.out.println("111 UNACCEPTABLE!!!! + " + dataBase[x][y]);
            //System.out.println("222 UNACCEPTABLE!!!! + " + dataBase[x+1][y]);
            //System.out.println("333 UNACCEPTABLE!!!! + " + x);
            if (dataBase[x][y] != null) {
                if ("02".equals(dbxy.substring(0, 2))) {
                    gN = dataBase[x][y];
                    //System.out.println("444 UNACCEPTABLE!!!! + " + dataBase[x][y]);
                } else {
                    gN = dataBase[x + 1][y];
                    //System.out.println("555 UNACCEPTABLE!!!! + " + dataBase[x+1][y]);
                }
            } else {
                gN = dataBase[x + 1][y];
                //System.out.println("555 UNACCEPTABLE!!!! + " + dataBase[x+1][y]);
            }

            while (true) {
                if (x > 200) {
                    break;
                }

                x = x + 1;
                if (dataBase[x][0] != null) {
                    d = dataBase[x][0];
                    while (true) {
                        if (!"".equals(dataBase[x][1]) && !"".equals(dataBase[x][y + 1]) && dataBase[x][1] != null && dataBase[x][y + 1] != null) {
                            Lesson lesson = new Lesson();
                            lesson.groupNumber = gN;
                            lesson.day = d;
                            lesson.hours = dataBase[x][1];
                            lesson.lecture = dataBase[x][y];
                            lesson.teacher = dataBase[x][y + 1];
                            lesson.room = dataBase[x][y + 2];
                            lesson.save();
                        } else {
                            if (!"".equals(dataBase[x][y]) && dataBase[x][y] != null && !"".equals(dataBase[x][y + 1]) && dataBase[x][y + 1] != null) {
                                Lesson lesson = new Lesson();
                                lesson.groupNumber = gN;
                                lesson.day = d;
                                lesson.hours = dataBase[x][1];
                                lesson.lecture = dataBase[x][y];
                                lesson.teacher = dataBase[x][y + 1];
                                lesson.room = dataBase[x][y + 2];
                                lesson.save();
                            }
                        }

                        if (dataBase[x + 1][0] != null) {
                            break;
                        } else {
                            x = x + 1;
                            if (x > 200) {
                                break;
                            }
                        }
                    }
                }
            }

            y = y + 3;
            x = startLine;
            if ("Часы".equals(dataBase[x][y])) {
                break;
            }
        }

    }


    public static void parseAndStore(File file) throws IOException {
        List<Lesson> ret = parseFile(file);
        for (Lesson lesson : ret) {
            lesson.save();
        }
    }

    public static  List<Lesson> parseFile(File file) throws IOException {
        List<Lesson> list = new ArrayList<Lesson>();

        POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(file));
        HSSFWorkbook workbook = new HSSFWorkbook(fs);

        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            HSSFSheet sheet = workbook.getSheetAt(i);
            try {
                list.addAll(parseSheet(sheet));
                System.out.println("Sheet "+i+" processed");
            } catch (ArrayIndexOutOfBoundsException ex){
                System.out.println("Error ARRAY INDEX OUT OF BOUNDS WHILE PROCESSING SHEET №"+ i);
                ex.printStackTrace();
            }
        }
        return list;
    }

    private static List<Lesson> parseSheet(HSSFSheet sheet){
        List<Lesson> list = new ArrayList<Lesson>();
        org.apache.poi.hssf.usermodel.HSSFRow row;
        org.apache.poi.hssf.usermodel.HSSFCell cell;

        int ROWS_COUNT = 240;   //todo Избавиться от магических чисел
        int COLUMNS_COUNT = 60; //todo Избавиться от магических чисел
        String[][] dataBase = new String[ROWS_COUNT][COLUMNS_COUNT]; // todo примерный размер используемого пространства ???
        // разбираем файл Excel в массив
        for (int i = 0; i < ROWS_COUNT; i++) {
            row = sheet.getRow(i + 1);
            for (int j = 0; j < COLUMNS_COUNT; j++) {
                cell = row.getCell(j);
                if (cell != null) {
                    switch (cell.getCellType()) {
                        case HSSFCell.CELL_TYPE_STRING:
                            dataBase[i][j] = cell.getStringCellValue().trim();
                            break;
                    }
                }
            }
        }
        // массив уже парсим в БД

        int x;
        // ищем, где начинается расписание
        for (x = 0; x < 100; x++) {
            if ("Дни".equals(dataBase[x][0])) {
                break;
            }
        }

        // startLine - строка, где начинается "чистое" расписание. x и y - для обхода файла
        int startLine = x;
        int y = 2;
        String d; //d -> day

        while (true) {

            String dbxy = dataBase[x][y];
            String gN = ""; //gN -> groupNumber
            //System.out.println("111 UNACCEPTABLE!!!! + " + dataBase[x][y]);
            //System.out.println("222 UNACCEPTABLE!!!! + " + dataBase[x+1][y]);
            //System.out.println("333 UNACCEPTABLE!!!! + " + x);
            if (dataBase[x][y] != null) {
                if ("02".equals(dbxy.substring(0, 2))) {  //todo группа начинается с 02 ???
                    gN = dataBase[x][y];
                    //System.out.println("444 UNACCEPTABLE!!!! + " + dataBase[x][y]);
                } else {
                    gN = dataBase[x + 1][y];
                    //System.out.println("555 UNACCEPTABLE!!!! + " + dataBase[x+1][y]);
                }
            } else {
                gN = dataBase[x + 1][y];
                //System.out.println("555 UNACCEPTABLE!!!! + " + dataBase[x+1][y]);
            }

            while (true) {
                if (x > 200) {
                    break;
                }

                x = x + 1;
                if (dataBase[x][0] != null) {
                    d = dataBase[x][0];
                    while (true) {
                        if (!"".equals(dataBase[x][1]) && !"".equals(dataBase[x][y + 1]) && dataBase[x][1] != null && dataBase[x][y + 1] != null) { //todo BUG: COLUMN y OVER 60
                            Lesson lesson = new Lesson();
                            lesson.groupNumber = gN;
                            lesson.day = d;
                            lesson.hours = dataBase[x][1];
                            lesson.lecture = dataBase[x][y];
                            lesson.teacher = dataBase[x][y + 1];
                            lesson.room = dataBase[x][y + 2];
                            list.add(lesson);
                        } else {
                            if (!"".equals(dataBase[x][y]) && dataBase[x][y] != null && !"".equals(dataBase[x][y + 1]) && dataBase[x][y + 1] != null) {
                                Lesson lesson = new Lesson();
                                lesson.groupNumber = gN;
                                lesson.day = d;
                                lesson.hours = dataBase[x][1];
                                lesson.lecture = dataBase[x][y];
                                lesson.teacher = dataBase[x][y + 1];
                                lesson.room = dataBase[x][y + 2];
                                list.add(lesson);
                            }
                        }

                        if (dataBase[x + 1][0] != null) {
                            break;
                        } else {
                            x = x + 1;
                            if (x > 200) {
                                break;
                            }
                        }
                    }
                }
            }

            y = y + 3;
            x = startLine;
            if ("Часы".equals(dataBase[x][y])) {
                break;
            }
        }
        return list;
    }
}
