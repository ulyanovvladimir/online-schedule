package models;

import java.util.*;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlUpdate;
import play.db.ebean.*;

import javax.persistence.*;


@Entity
public class Lesson extends Model {

    @Id
    private Integer id;
    private String groupNumber;
    private Integer dayOfWeek;
    private String lecture;
    private String instructor;
    private String room;

    private Integer fromHours;
    private Integer fromMinutes;
    private Integer toHours;
    private Integer toMinutes;

    public static Finder<Integer, Lesson> find = new Finder(
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
            System.out.println("LOGGGGG" + e.getMessage());
        }

    }

    public String getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(String groupNumber) {
        this.groupNumber = groupNumber;
    }

    public String getDay() {
        switch(dayOfWeek){
            case 1: return "Пн";
            case 2: return "Вт";
            case 3: return "Ср";
            case 4: return "Чт";
            case 5: return "Пт";
            case 6: return "Сб";
            case 7: return "Вс";
            default: return "";
        }
    }

    public void setDay(String day) {
        switch(day){
            case "ПОНЕДЕЛЬНИК":
                this.dayOfWeek=1;
                break;
            case "ВТОРНИК":
                this.dayOfWeek=2;
                break;
            case "СРЕДА":
                this.dayOfWeek=3;
                break;
            case "ЧЕТВЕРГ":
                this.dayOfWeek=4;
                break;
            case "ПЯТНИЦА":
                this.dayOfWeek=5;
                break;
            case "СУББОТА":
                this.dayOfWeek=6;
                break;
            case "ВОСКРЕСЕНЬЕ":
                this.dayOfWeek=6;
                break;
            default:
                throw new IllegalArgumentException("Unknown day of week:"+day);
        }
    }

    public String getHours() {
        if (fromMinutes == null) return ""; //todo BUG FIX
        return fromHours+":"+fromMinutes+"-"+toHours+":"+toMinutes;
    }

    public void setHours(String hours) {
        //this.hours = hours;
        if (hours == null) {
            //System.out.println("NULL HOURS"); //todo remove STUB!!!! DEBUG null hours
            return;
        }
        //parse from and to hours "08.30-10.00"
        String[] fromTo = hours.split("-");
        String from = fromTo[0];
        int point = from.indexOf(".");
        if (point != -1){
            this.fromHours = Integer.valueOf(from.substring(0,point).trim());
            this.fromMinutes= Integer.valueOf(from.substring(point+1).trim());
        }
        String to = fromTo[1];
        point = to.indexOf(".");
        if (point != -1){
            this.toHours = Integer.valueOf(to.substring(0,point).trim());
            this.toMinutes= Integer.valueOf(to.substring(point+1).trim());
        }
    }

    public String getLecture() {
        return lecture;
    }

    public void setLecture(String lecture) {
        this.lecture = lecture;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public Integer getFromHours() {
        return fromHours;
    }

    public void setFromHours(Integer fromHours) {
        this.fromHours = fromHours;
    }

    public Integer getFromMinutes() {
        return fromMinutes;
    }

    public void setFromMinutes(Integer fromMinutes) {
        this.fromMinutes = fromMinutes;
    }

    public Integer getToHours() {
        return toHours;
    }

    public void setToHours(Integer toHours) {
        this.toHours = toHours;
    }

    public Integer getToMinutes() {
        return toMinutes;
    }

    public void setToMinutes(Integer toMinutes) {
        this.toMinutes = toMinutes;
    }

    public Integer getDayOfWeek() {
        return dayOfWeek;
    }
}