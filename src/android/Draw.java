package in.co.geekninja.plugin;

import android.content.Context;
import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import android.widget.Toast;
import android.content.Intent;

public class Draw extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {

        if (action.equals("greet")) {

            String name = data.getString(0);
            String message = "Hello, " + name;
            callbackContext.success(message);
            Context context=this.cordova.getActivity().getApplicationContext();
            Toast.makeText(context, "Toasting for the,"+message, Toast.LENGTH_SHORT).show();
            return true;

        } else if("draw".equals(action)){
            Context context=this.cordova.getActivity().getApplicationContext();
            context.startActivity(new Intent(context,SketchActivity.class));
            return true;

        }else{
          return false;
        }
    }
}
