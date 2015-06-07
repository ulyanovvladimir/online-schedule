package controllers;

import models.Lesson;
import play.*;
import play.mvc.*;
import views.html.*;
import play.data.*;
import java.io.*;
import static play.data.Form.form;
import java.util.*;
import play.db.ebean.*;
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


public class Parsing extends Controller {

  public static Result pars() throws Exception {

    org.apache.poi.poifs.filesystem.POIFSFileSystem fs =
            new org.apache.poi.poifs.filesystem.POIFSFileSystem(new FileInputStream("sched.xls"));
    org.apache.poi.hssf.usermodel.HSSFWorkbook workbook =
            new org.apache.poi.hssf.usermodel.HSSFWorkbook(fs);
    org.apache.poi.hssf.usermodel.HSSFSheet sheet1 = workbook.getSheetAt(0);
    org.apache.poi.hssf.usermodel.HSSFRow row;
    org.apache.poi.hssf.usermodel.HSSFCell cell;

    String[][] dataBase = new String[240][60]; //240 и 60 - примерный размер используемого пространства
    // разбираем файл Excel в массив
    for(int i = 0; i < 240; i++){
      row = sheet1.getRow(i+1);
      for(int j = 0; j < 60; j++){
        cell = row.getCell(j);
        if(cell != null){
          switch (cell.getCellType()){
            case HSSFCell.CELL_TYPE_STRING:
              dataBase[i][j] = cell.getStringCellValue();
			break;
          };
        };
      };
    };
    // массив уже парсим в БД
	

    // ищем, где начинается расписание
	// startLine - строка, где начинается "чистое" расписание. x и y - для обхода файла
    int startLine = 1;
    int x = 1;
	int y = 2;
	String d = ""; //d -> day
	
    for(int i=0; i<100; i++){
      if("Дни".equals(dataBase[i][0])){
        startLine = i;
		x = startLine;
        break;
      };
    };


    while(true){
      String gN = dataBase[x][y]; //gN -> groupNumber

      while(true){
		x = x + 1;
        if(x > 200){
			break;
		};
        if(dataBase[x][0] != null){
          d = dataBase[x][0];
		};
          while(true){
            if(dataBase[x][1] != null){
              Lesson lesson = new Lesson();
              lesson.groupNumber = gN;
              lesson.day = d;
              lesson.hours = dataBase[x][1];
              lesson.lecture = dataBase[x][y];
              lesson.teacher = dataBase[x][y + 1];
              lesson.room = dataBase[x][y + 2];
              lesson.save();
            } else{
              if(dataBase[x][y] != null){
                Lesson lesson = new Lesson();
                lesson.groupNumber = gN;
                lesson.day = d;
                lesson.hours = dataBase[x][1];
                lesson.lecture = dataBase[x][y];
                lesson.teacher = dataBase[x][y + 1];
                lesson.room = dataBase[x][y + 2];
                lesson.save();
              };
            };
            if(dataBase[x + 1][1] != null){
              break;
            } else{
              x = x + 1;
              if(x > 200){
                break;
              };
            };
          };
      };
      y = y + 3;
      x = startLine;
      if("Часы".equals(dataBase[x][y])){
        break;
      };
    };
	
	return redirect(controllers.routes.Application.admin());
  };
  
}