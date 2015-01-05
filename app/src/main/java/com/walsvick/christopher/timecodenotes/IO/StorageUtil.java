package com.walsvick.christopher.timecodenotes.IO;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.walsvick.christopher.timecodenotes.model.Project;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Created by Christopher on 1/4/2015.
 */
public class StorageUtil {

    public static String APP_DIRECTORY = "/TimeCodeNotes";
    public static String FILE_SUFFIX = ".txt";
    public static String LOG_TAG = "STORAGE_SAVE_PROJECT";

    public String saveProject(Project p) {
        if (!isExternalStorageWritable()) {
            return new String("Cannot write to external storage");
        }

        File root = Environment.getExternalStorageDirectory();
        Log.d(LOG_TAG, "root dir: " + root.getAbsolutePath());
        File appDir = new File(root.getAbsolutePath() + APP_DIRECTORY);
        Log.d(LOG_TAG, "appDir: " + appDir.getAbsolutePath());

        appDir.mkdirs();
        if (!appDir.isDirectory()) {
            return new String("Cannot create app directory");
        }

        File outputFile = new File(appDir, getFileName(p));
        BufferedOutputStream stream = null;
        try {
            stream = new BufferedOutputStream(new FileOutputStream(outputFile));
            stream.write(getJsonString(p).getBytes());
            stream.flush();
            stream.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d(LOG_TAG, "FileNotFoundException");
        }
        catch (IOException e) {
            e.printStackTrace();
            Log.d(LOG_TAG, "IOException");
        }
        finally {
        }

        return null;
    }

    public File[] getSavedProjects() {
        File root = Environment.getExternalStorageDirectory();
        File appDir = new File(root.getAbsolutePath() + APP_DIRECTORY);

        if (appDir.isDirectory()) {
            return appDir.listFiles();
        }
        return null;
    }

    public String getFileName(Project p) {
        return new String(p.getName() + "_" + p.getStartDate().toString("yyyy-MM-dd"));
    }

    /* Checks if external storage is available for read and write */
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    private Gson getGson() {
        GsonBuilder builder = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
                .registerTypeAdapter(LocalTime.class, new LocalTimeSerializer())
                .serializeNulls();
        return builder.create();
    }

    private String getJsonString(Project p) {
        Gson gson = getGson();
        return gson.toJson(p);
    }

    public Project readProject(File f) {
        try {
            Reader r = new InputStreamReader(new FileInputStream(f));
            Gson gson = getGson();
            return gson.fromJson(r, Project.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
