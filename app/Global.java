import controllers.Parser;
import scala.concurrent.duration.Duration;
import org.joda.time.DateTime;
import org.joda.time.Seconds;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.libs.Akka;
import java.util.concurrent.TimeUnit;

public class Global extends GlobalSettings {

    @Override
    public void onStart(Application application) {
      Akka.system().scheduler().schedule(
        Duration.create(0, TimeUnit.SECONDS),
        Duration.create(24, TimeUnit.HOURS),
        new Runnable() {
            @Override
            public void run() {
                Parser parser = new Parser();
                try{
                    parser.parseSchedule();
                } catch(Exception e) {
                    System.out.println("UNACCEPTABLE EXCEL! + " + e.getMessage());
                }
            }
        },
        Akka.system().dispatcher()
      );
    }

}

/*Akka.system().scheduler().schedule(
                Duration.create(0, TimeUnit.MILLISECONDS),
                Duration.create(10, TimeUnit.SECONDS),
                new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("EVERY DAY AT 0:00 ---    " + System.currentTimeMillis());
                    }
                },
                Akka.system().dispatcher()
        );*/