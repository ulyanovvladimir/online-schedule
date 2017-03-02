package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlUpdate;
import play.Logger;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;


@Entity
public class WeekDays extends Model {

    @Id
    public Integer id;

    @Required
    public String upperStarts;

    @Required
    public String upperEnds;

    @Required
    public String lowerStarts;

    @Required
    public String lowerEnds;

    public static Model.Finder<Integer, WeekDays> find = new Model.Finder(
            Integer.class, WeekDays.class
    );

    public void setUpperStarts(String upperStarts) {
        this.upperStarts = upperStarts;
    }

    public void setUpperEnds(String upperEnds) {
        this.upperEnds = upperEnds;
    }

    public static void clearAll() {
        SqlUpdate rebuildTable = Ebean.createSqlUpdate("TRUNCATE TABLE week_days");
        try {
            rebuildTable.execute();
        } catch (Exception e) {
            Logger.error(e.getMessage(), e);
        }

    }

    public String getUpperStarts() {
        return upperStarts;
    }

    public String getUpperEnds() {
        return upperEnds;
    }

    public String getLowerStarts() {
        return lowerStarts;
    }

    public void setLowerStarts(String lowerStarts) {
        this.lowerStarts = lowerStarts;
    }

    public String getLowerEnds() {
        return lowerEnds;
    }

    public void setLowerEnds(String lowerEnds) {
        this.lowerEnds = lowerEnds;
    }
}