import controllers.App;
import play.Application;
import play.GlobalSettings;

public class Global extends GlobalSettings {

    @Override
    public void onStart(Application application) {
        App.reload();
    }
}