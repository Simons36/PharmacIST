package pt.ulisboa.tecnico.cmov.pharmacist.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import pt.ulisboa.tecnico.cmov.pharmacist.R;

public class ConfigClass {

    private static final String TAG = "ConfigClass";



    public static String getUrl(Context context){
        return getConfigValue(context, "api_url");
    }

    private static String getConfigValue(Context context, String name) {
        Resources resources = context.getResources();

        try {
            InputStream rawResource = resources.openRawResource(R.raw.config);
            Properties properties = new Properties();
            properties.load(rawResource);
            return properties.getProperty(name);
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Unable to find the config file: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "Failed to open config file.");
        }

        return null;
    }

    public static String getValueFromAndroidManifest(Context context, String name) {
        try {
            return context.getPackageManager().getApplicationInfo(context.getPackageName(), 128).metaData.getString(name);
        } catch (Exception e) {
            Log.e(TAG, "Failed to get value from AndroidManifest: " + e.getMessage());
        }
        return null;
    }

}
