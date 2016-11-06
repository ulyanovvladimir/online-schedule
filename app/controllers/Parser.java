package controllers;

import models.Lesson;

import java.io.*;
import java.util.*;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;


public class Parser {

    public static void parseAndStore(File file) throws IOException {
        List<Lesson> ret = parseFile(file);
        for (Lesson lesson : ret) {
            lesson.save();
        }
    }

    public static List<Lesson> parseFile(File file) throws IOException {
        List<Lesson> list = new ArrayList<Lesson>();

        POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(file));
        HSSFWorkbook workbook = new HSSFWorkbook(fs);

        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            HSSFSheet sheet = workbook.getSheetAt(i);
            list.addAll(parseSheet(sheet));
            System.out.println("Sheet " + i + " processed");
        }
        return list;
    }

    private static List<Lesson> parseSheet(HSSFSheet sheet) {
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
        String day; //day -> day

        while (true) {

            String dbxy = dataBase[x][y];
            String group = ""; //group -> groupNumber
            if (dataBase[x][y] != null) {
                if (isGroupTitle(dbxy)) {
                    group = dataBase[x][y];
                    //System.out.println("444 UNACCEPTABLE!!!! + " + dataBase[x][y]);
                } else {
                    group = dataBase[x + 1][y];
                    //System.out.println("555 UNACCEPTABLE!!!! + " + dataBase[x+1][y]);
                }
            } else {
                group = dataBase[x + 1][y];
                //System.out.println("555 UNACCEPTABLE!!!! + " + dataBase[x+1][y]);
            }
            System.out.println("Group " + group);
            if (group == null) break;

            while (x<200) {
                x = x + 1;
                if (dataBase[x][0] != null) {
                    day = dataBase[x][0];
                    while (true) {
                        if (notEmpty(dataBase[x][1]) && notEmpty(dataBase[x][y + 1])) {
                            Lesson lesson = new Lesson();
                            lesson.setGroupNumber(group);
                            lesson.setDay(day);
                            String hours = getLectureHours(x, dataBase);
                            lesson.setHours(hours);
                            lesson.setLecture(dataBase[x][y]);
                            lesson.setInstructor(dataBase[x][y + 1]);
                            lesson.setRoom(dataBase[x][y + 2]);
                            list.add(lesson);
                        } else {
                            if (!"".equals(dataBase[x][y]) && dataBase[x][y] != null && !"".equals(dataBase[x][y + 1]) && dataBase[x][y + 1] != null) {
                                Lesson lesson = new Lesson();
                                lesson.setGroupNumber(group);
                                lesson.setDay(day);
                                lesson.setHours(getLectureHours(x, dataBase));
                                lesson.setLecture(dataBase[x][y]);
                                lesson.setInstructor(dataBase[x][y + 1]);
                                lesson.setRoom(dataBase[x][y + 2]);
                                list.add(lesson);
                            }
                        }

                        if (dataBase[x + 1][0] != null) {  //todo ???
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
            if (endOfColumns(x,y, dataBase)) {
                break;
            }
        }
        return list;
    }

    private static boolean endOfColumns(int x, int y, String[][] dataBase) {
        String s =  dataBase[x][y];
        return "Часы".equals(s) || y + 2 >= dataBase[x].length;
    }

    private static String getLectureHours(int row, String[][] db) {
        String ret = db[row][1];
        if (notEmpty(ret)) return ret; else return getLectureHours(row-1, db);
    }

    private static boolean isGroupTitle(String cell) {
        return "02".equals(cell.substring(0, 2));
    }

    private static boolean notEmpty(String cell){
        return cell != null && !"".equals(cell);
    }
}
