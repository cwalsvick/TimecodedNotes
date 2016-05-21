package com.walsvick.christopher.timecodenotes.io;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

import com.walsvick.christopher.timecodenotes.db.NoteDAO;
import com.walsvick.christopher.timecodenotes.model.Note;
import com.walsvick.christopher.timecodenotes.model.Project;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created on 1/4/2015 by Christopher.
 */
public class StorageUtil {

    public static String APP_DIRECTORY = "/TimeCodeNotes";
    public static String LOG_TAG = "STORAGE_SAVE_PROJECT";

    public static String exportToTSV(final Context context, Project project) {
        int fileCopyNum = 0;

        if (!isExternalStorageWritable()) {
            createErrorDialog(context, "External storage is unavailable.");
            return null;
        }

        File root = Environment.getExternalStorageDirectory();
        File appDir = new File(root.getAbsolutePath() + APP_DIRECTORY);

        appDir.mkdirs();
        if (!appDir.isDirectory()) {
            createErrorDialog(context, "Problem creating application directory.");
            return null;
        }

        File outputFile = new File (appDir, generateFileName(project, fileCopyNum));

//        while (outputFile.exists()) {
//            outputFile = new File(appDir, generateFileName(project, fileCopyNum));
//            fileCopyNum++;
//            Log.d(LOG_TAG, "newOutputFileName: " + outputFile.getName());
//        }

        writeFile(context, project, outputFile);

        return outputFile.getPath();
    }

    private static void writeFile(Context context, Project project, File outputFile) {
        NoteDAO dao = new NoteDAO(context);
        ArrayList<Note> notes = dao.getAllNotes(project);

        BufferedOutputStream stream;
        try {
            stream = new BufferedOutputStream(new FileOutputStream(outputFile));
            stream.write(new String("Project\t" + project.getName()).getBytes());
            stream.write("\nDate\t".getBytes());
            stream.write(new String(project.getStartDate().toString()).getBytes());
            stream.write("\nCameras\t".getBytes());
            for (String camera : project.getCameras()) {
                stream.write(new String(camera + "\t").getBytes());
            }
            stream.write(new String("\nAdditional Info\t" + project.getAddInfo() + "\n").getBytes());

            stream.write("\n\nTime\tCamera\tNote\n".getBytes());
            for (Note n : notes) {
                stream.write(n.getTimeCode().getBytes());
                stream.write("\t".getBytes());
                stream.write(n.getCamera().getBytes());
                stream.write("\t".getBytes());
                stream.write(n.getNote().getBytes());
                stream.write("\n".getBytes());
            }
            stream.flush();
            stream.close();

            MediaScannerConnection.scanFile(context,
                    new String[] { outputFile.toString() }, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }
                    });
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d(LOG_TAG, "FileNotFoundException");
        }
        catch (IOException e) {
            e.printStackTrace();
            Log.d(LOG_TAG, "IOException");
        }
    }

    private static void createErrorDialog(Context context, String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        final TextView textView = new TextView(context);
        textView.setPadding(16, 16, 16, 16);
        textView.setTextSize(16);
        textView.setText(s);
        textView.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        builder.setView(textView);

        builder.setTitle("Error");
        builder.setNeutralButton("Close",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static String generateFileName(Project p, int fileCopyNum) {
        String fileCopyString = "";

        if (fileCopyNum > 0) {
            fileCopyString = "(" + fileCopyNum + ")";
        }
        return new String("[TimeCodeNodes]" + p.getName().replaceAll("[^a-zA-Z0-9.-]", "_") + "_" + fileCopyString + ".tsv");
    }

   // Checks if external storage is available for read and write
    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
}
