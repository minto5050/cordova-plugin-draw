package in.co.geekninja.plugin;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import android.widget.Toast;

public class Draw extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {

        if (action.equals("greet")) {

            String name = data.getString(0);
            String message = "Hello, " + name;
            callbackContext.success(message);
            Context context=this.cordova.getActivity().getApplicationContext();
            Toast.makeText(Context, "Toasting for the,"+message, Toast.LENGTH_SHORT).show();
            return true;

        } else {

            return false;

        }
    }
}
