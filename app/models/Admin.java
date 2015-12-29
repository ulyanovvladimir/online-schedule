package models;

import java.util.*;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlUpdate;
import play.db.ebean.*;
import play.data.validation.Constraints.*;
import javax.persistence.*;


@Entity
public class Admin extends Model {

  @Id
  public Integer id;
  
  @Required
  public String username;
  
  @Required
  public String userpass;
  
  public static Finder<String,Admin> find = new Finder(
    String.class, Admin.class
  );
  
  public void setUsername(String username){
	this.username = username;
  }
  
  public void setUserpass(String userpass){
	this.userpass = userpass;
  }

  public static void clearBase() {
    SqlUpdate rebuildTable = Ebean.createSqlUpdate("TRUNCATE TABLE admin");
    try {
      rebuildTable.execute();
    } catch (Exception e) {
      System.out.println("LOGGGGG"+e.getMessage());
    }

  }

}