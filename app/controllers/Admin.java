package controllers;

import models.Lesson;
import models.ScheduleURL;
import play.libs.Akka;
import play.mvc.Controller;
import play.mvc.*;
import scala.concurrent.duration.Duration;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * @author Vladimir Ulyanov
 */
public class Admin extends Controller {

    public static Result reload(){
        Akka.system().scheduler().scheduleOnce(
                Duration.create(0, TimeUnit.SECONDS),
                new Runnable() {
                    public void run() {
                        Downloader downloader = new Downloader();
                        if (downloader.downloadSchedule()) {
                            Lesson.clearBase();
                            System.out.println("Base cleared");
                            for (int i = 1; i <= ScheduleURL.all().size(); i++) {
                                try {
                                    Parser parser = new Parser();
                                    File destination = new File("sched" + i + ".xls");
                                    parser.parseAndStore(destination);
                                    System.out.println("File "+ destination.getName()+" has been parsed");
                                } catch (Exception e) {
                                    System.out.println("UNACCEPTABLE EXCEL! PLACED IN URL #" + i + ". ERROR: " + e.getMessage());
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                },
                Akka.system().dispatcher()
        );


        return redirect(controllers.routes.Application.adminPage());
    }

}
