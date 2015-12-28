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
                    parser.parsing();
                } catch(Exception e) {
                    System.out.println("UNACCEPTABLE EXCEL!!!!" + e.getMessage());
                }
            }
        },
        Akka.system().dispatcher()
      );
    }

    public static int nextExecutionInSeconds(int hour, int minute){
        return Seconds.secondsBetween(
                new DateTime(),
                nextExecution(hour, minute)
        ).getSeconds();
    }

    public static DateTime nextExecution(int hour, int minute){
        DateTime next = new DateTime()
                .withHourOfDay(hour)
                .withMinuteOfHour(minute)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0);

        return (next.isBeforeNow())
                ? next.plusHours(24)
                : next;
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