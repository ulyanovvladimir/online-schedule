package controllers;

import models.ScheduleURL;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

public class Downloader {

    public static Boolean downloadSchedule() { //todo return List<File>
        try{
            List<ScheduleURL> urlList = ScheduleURL.all();
            int i = 1;
            for (ScheduleURL scheduleURL : urlList) {
                URL url = new URL(scheduleURL.url);
                File destination = new File("sched" + i + ".xls");
                //if (destination.exists()) FileUtils.forceDelete(destination);
                FileUtils.copyURLToFile(url, destination);
                System.out.println("url " +url+" downloaded to file "+destination.getName());
                i++;
            }
            return true;
        } catch(Exception e) {
            System.out.println("ERROR WHILE DOWNLOADING! " + e.getMessage());
            return false;
        }
    }
}
