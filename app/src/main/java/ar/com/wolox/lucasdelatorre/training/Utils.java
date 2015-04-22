package ar.com.wolox.lucasdelatorre.training;

import android.app.Activity;
import android.graphics.Color;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public final static String CODE_INVALID_AUTH = "101";
    public final static String CODE_INVALID_TOKEN = "209";

    public static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public static final int[] TAB_IMAGES = {
            R.drawable.icon_news,
            R.drawable.icon_profile
    };

    public static boolean validate(final String hex, final String stringPattern) {
        Pattern pattern = Pattern.compile(stringPattern);
        Matcher matcher = pattern.matcher(hex);
        return matcher.matches();
    }

    public static void showToast(Activity activity, int id) {
        Toast.makeText(activity.getApplicationContext(),
                activity.getResources().getString(id),
                Toast.LENGTH_SHORT).show();
    }

    //TODO: Unhardcode
    public static int getColorState(boolean state) {
        if (state) {
            return Color.parseColor("#88B85E");
        }
        return Color.parseColor("#959692");
    }
}
