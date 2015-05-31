package models;

import java.util.*;
import play.db.ebean.*;
import play.data.validation.Constraints.*;
import javax.persistence.*;


@Entity
public class Lesson extends Model {

  public String groupNumber;
  public String day;
  public String hours;
  public String lecture;
  public String teacher;
  public String room;
  
  public static Finder<String,Lesson> find = new Finder(
    String.class, Lesson.class
  );
  
}