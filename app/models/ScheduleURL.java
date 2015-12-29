package models;

import play.db.ebean.*;
import java.util.*;
import javax.persistence.Entity;
import javax.persistence.*;
import play.data.validation.Constraints.*;

@Entity
public class ScheduleURL extends Model {

    @Id
    public Integer id;
    public String url;

    public static Finder<Integer,ScheduleURL> find = new Finder(
            Integer.class, ScheduleURL.class
    );

    public static List<ScheduleURL> all() {
        return find.all();
    }

    public static void delete(Integer id) {
        find.ref(id).delete();
    }

    public static void edit(ScheduleURL url) {
        url.update();
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getId() {
        return id;
    }
}
