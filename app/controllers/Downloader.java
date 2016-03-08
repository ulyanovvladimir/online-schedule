package controllers;

import models.ScheduleURL;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

public class Downloader {

    public static Boolean downloadSchedule() {
        try{
            List<ScheduleURL> urlList = ScheduleURL.all();
            int i = 1;
            Iterator iterator = urlList.iterator();
            while (iterator.hasNext()) {
                ScheduleURL scheduleUrl = (ScheduleURL) iterator.next();
                URL url = new URL(scheduleUrl.url);
                File destination = new File("sched" + i + ".xls");
                FileUtils.copyURLToFile(url, destination);
                i++;
            }
            return true;
        } catch(Exception e) {
            System.out.println("ERROR WHILE DOWNLOADING! " + e.getMessage());
            return false;
        }
    }

}
