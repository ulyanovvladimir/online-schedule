package models;

import java.util.*;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlUpdate;
import play.db.ebean.*;
import play.data.validation.Constraints.*;
import javax.persistence.*;


@Entity
public class Lesson extends Model {

  @Id
  public Integer id;
  public String groupNumber;
  public String day;
  public String hours;
  public String lecture;
  public String teacher;
  public String room;
  
  public static Finder<Integer,Lesson> find = new Finder(
    Integer.class, Lesson.class
  );

  public static List<Lesson> all() {
    return find.all();
  }

  public static void clearBase() {
    SqlUpdate rebuildTable = Ebean.createSqlUpdate("TRUNCATE TABLE lesson");
    try {
      rebuildTable.execute();
    } catch (Exception e) {
      System.out.println("LOGGGGG"+e.getMessage());
    }

  }
  
}