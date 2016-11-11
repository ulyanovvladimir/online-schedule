package controllers;

import models.Lesson;
import models.ScheduleURL;
import parser.Parser;
import play.Logger;
import play.libs.Akka;
import play.mvc.Controller;
import play.mvc.*;
import scala.concurrent.duration.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Vladimir Ulyanov
 */
public class Admin extends Controller {
    public static Result reload() {
        startReload();
        return ok("reloaded");//redirect(controllers.routes.Application.adminPage());
    }

    public static void startReload(){
        Akka.system().scheduler().scheduleOnce(
                Duration.create(0, TimeUnit.SECONDS),
                new Runnable() {
                    public void run() {
                        long before = System.currentTimeMillis();
                        List<ScheduleURL> urls = ScheduleURL.all();
                        Lesson.clearBase();
                        for (ScheduleURL schedule : urls) {
                            try {
                                List<parser.Lesson> list = Parser.parseURL(new URL(schedule.url));
                                for (parser.Lesson lesson : list) {
                                    models.Lesson.from(lesson).save();
                                }
                            } catch (IOException e) {
                                Logger.error("Can not parse the URL:" + schedule.url);
                            }
                        }
                        System.out.println("downloaded and processed for "+(System.currentTimeMillis()-before) +" ms.");
                    }
                },
                Akka.system().dispatcher()
        );
    }
}
