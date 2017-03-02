package models;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlUpdate;
import play.Logger;
import play.db.ebean.*;

import javax.persistence.*;

import static java.util.Calendar.*;


@Entity
public class Lesson extends Model {
    public static final int EVERY_WEEK = 0;
    public static final int UPPER_WEEK = 1;
    public static final int LOWER_WEEK = 2;

    @Id
    private Integer id;
    private String groupNumber;
    private String groupName;
    private Integer dayOfWeek;
    private String lecture;
    private String instructor;
    private String room;

    private Integer fromHours;
    private Integer fromMinutes;
    private Integer toHours;
    private Integer toMinutes;
    private Integer week = EVERY_WEEK;

    public static Model.Finder<Integer, Lesson> find = new Model.Finder(
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

    public Integer getId() {
        return id;
    }

    public String getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(String groupNumber) {
        this.groupNumber = groupNumber;
    }

    public String getDay() {
        switch (dayOfWeek.intValue()) {
            case MONDAY:
                return "Пн";
            case TUESDAY:
                return "Вт";
            case WEDNESDAY:
                return "Ср";
            case THURSDAY:
                return "Чт";
            case FRIDAY:
                return "Пт";
            case SATURDAY:
                return "Сб";
            case SUNDAY:
                return "Вс";
            default:
                return "";
        }
    }

    public void setDay(String day) {
        switch (day) {
            case "ПОНЕДЕЛЬНИК":
                this.dayOfWeek = MONDAY;
                break;
            case "ВТОРНИК":
                this.dayOfWeek = Calendar.TUESDAY;
                break;
            case "СРЕДА":
                this.dayOfWeek = Calendar.WEDNESDAY;
                break;
            case "ЧЕТВЕРГ":
                this.dayOfWeek = Calendar.THURSDAY;
                break;
            case "ПЯТНИЦА":
                this.dayOfWeek = Calendar.FRIDAY;
                break;
            case "СУББОТА":
                this.dayOfWeek = Calendar.SATURDAY;
                break;
            case "ВОСКРЕСЕНЬЕ":
                this.dayOfWeek = Calendar.SUNDAY;
                break;
            default:
                throw new IllegalArgumentException("Unknown day of week:" + day);
        }
    }

    public String getHours() {
        if (fromMinutes == null) return ""; //todo BUG FIX
        return zs(fromHours) + ":" + zs(fromMinutes) + "-" + zs(toHours) + ":" + zs(toMinutes);
    }

    private String zs(Integer n) {
        return String.format("%02d", n);
    }

    /**
     *
     * @return next date of the same day of week like in this Lesson
     */
    private Calendar nextDay() {
        //TODO start from the first day of the TERM according to upper-lower WeekDays
        //today
        Calendar date = Calendar.getInstance();

        while (date.get(Calendar.DAY_OF_WEEK) != dayOfWeek) {
            date.add(Calendar.DATE, 1);
        }

        return date;
    }

    private Calendar getFirstLessonDate(WeekDays wd){
        Calendar date;
        if (week == LOWER_WEEK) {
            date = getFirstLower(wd);
        } else if (week == UPPER_WEEK){
            date = getFirstUpper(wd);
        }
        else {
            //EVERY_WEEK: WHO STARTS FIRST?
            Calendar lower = getFirstLower(wd);
            Calendar upper = getFirstUpper(wd);
            if (lower.before(upper)) date = lower; else date = upper;
        }

        //find next date according to lesson's day of week
        while (date.get(Calendar.DAY_OF_WEEK) != dayOfWeek) {
            date.add(Calendar.DATE, 1);
        }

        return date;
    }

    private Calendar getLastLessonDate(WeekDays wd){
        Calendar date;
        if (week == LOWER_WEEK) {
            date = parseCalendar(wd.lowerEnds);
        } else if (week == UPPER_WEEK){
            date = parseCalendar(wd.upperEnds);
        }
        else {
            //EVERY_WEEK: WHO EBDS LAST?
            Calendar lower = parseCalendar(wd.lowerEnds);
            Calendar upper = parseCalendar(wd.upperEnds);
            if (lower.after(upper)) date = lower; else date = upper;
        }

        //find next date according to lesson's day of week
        while (date.get(Calendar.DAY_OF_WEEK) != dayOfWeek) {
            date.add(Calendar.DATE, 1);
        }

        return date;
    }


    private Calendar getFirstLower(WeekDays wd) {
        String lsFormatted = wd.getLowerStarts();
        return parseCalendar(lsFormatted);
    }

    private Calendar getFirstUpper(WeekDays wd) {
        return parseCalendar(wd.getUpperStarts());
    }

    //for example 15.01   dd.MM
    public static Calendar parseCalendar(String formatted){
        SimpleDateFormat df = new SimpleDateFormat("dd.MM");
        try {
            Date date = df.parse(formatted);
            Calendar calendar = toCalendar(date);
            calendar.set(YEAR,toCalendar(new Date()).get(YEAR)); //this year
            return calendar;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Calendar toCalendar(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    public Integer getInterval(){
        if (week == EVERY_WEEK) return 1; else return 2;
    }

    public String getFormattedFrom(WeekDays wd){
        return formatICal(getFirstLessonDate(wd), fromHours, fromMinutes);
    }

    public String getFormattedEnd(WeekDays wd){
        return formatICal(getFirstLessonDate(wd), toHours, toMinutes);
    }

    public String getFormattedLast(WeekDays wd){
        return formatICal(getLastLessonDate(wd), toHours,toMinutes);
    }

    public String formatICal(Calendar date, Integer hh, Integer mm){
        DateFormat dateFormat1 = new SimpleDateFormat("yyyyMMdd");
        return dateFormat1.format(date.getTime()) + "T" + zs(hh) + zs(mm) + "00"; //20161106T100000
    }

    public String getNextFrom() {
        Calendar date = nextDay();
        DateFormat dateFormat1 = new SimpleDateFormat("yyyyMMdd");
        return dateFormat1.format(date.getTime()) + "T" + zs(fromHours) + zs(fromMinutes) + "00"; //20161106T100000
    }

    public String getNextTo() {
        Calendar date = nextDay();
        DateFormat dateFormat1 = new SimpleDateFormat("yyyyMMdd");
        return dateFormat1.format(date.getTime()) + "T" + zs(toHours) + zs(toMinutes) + "00"; //20161106T100000
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
        if (point != -1) {
            this.fromHours = Integer.valueOf(from.substring(0, point).trim());
            this.fromMinutes = Integer.valueOf(from.substring(point + 1).trim());
        }
        String to = fromTo[1];
        point = to.indexOf(".");
        if (point != -1) {
            this.toHours = Integer.valueOf(to.substring(0, point).trim());
            this.toMinutes = Integer.valueOf(to.substring(point + 1).trim());
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

    public void setDayOfWeek(Integer dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Integer getWeek() {
        return week;
    }

    public void setWeek(Integer week) {
        this.week = week;
    }

    public static Lesson from(parser.Lesson from) {
        Lesson to = new Lesson();
        to.setDayOfWeek(from.getDayOfWeek());
        to.setWeek(from.getWeek());
        to.setFromHours(from.getFromHours());
        to.setFromMinutes(from.getFromMinutes());
        to.setGroupNumber(from.getGroupNumber());
        to.setGroupName(from.getGroupName());
        to.setInstructor(from.getInstructor());
        to.setLecture(from.getLecture());
        to.setRoom(from.getRoom());
        to.setToHours(from.getToHours());
        to.setToMinutes(from.getToMinutes());
        return to;
    }


}