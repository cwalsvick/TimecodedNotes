package com.walsvick.christopher.timecodenotes.view;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.walsvick.christopher.timecodenotes.R;
import com.walsvick.christopher.timecodenotes.model.Project;

import org.joda.time.LocalDate;

/**
 * Created on 1/2/2015 by Christopher.
 */
public class NewProjectDialog implements DatePickerDialog.OnDateSetListener {

    private Context context;
    private Project newProject;
    private NewProjectDialogListener callback;
    private int flags;

    public static int EDIT = 1;
    public static int CLONE = 2;

    public NewProjectDialog(Context context, NewProjectDialogListener callback) {
        this.context = context;
        this.callback = callback;
        newProject = new Project();
        flags = 0;
    }

    public NewProjectDialog(Context context, NewProjectDialogListener callback, Project p, int flags) {
        this.context = context;
        this.callback = callback;
        this.newProject = p;
        this.flags = flags;
    }

    public void begin() {
        launchProjectNameDialog(newProject.getName());
    }

    public void launchProjectNameDialog(String projectName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        final EditText editText = new EditText(context);
        if (projectName == null) {
            editText.setHint(context.getResources().getString(R.string.dialog_new_project_name_hint));
        }
        else {
            editText.setText(projectName);
        }

        editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS
                | InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE
                | InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE
                | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                editText.post(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                    }
                });
            }
        });
        editText.requestFocus();
        builder.setView(editText);

        builder.setTitle(context.getResources().getString(R.string.dialog_new_project_name_title));
        builder.setPositiveButton(context.getResources().getString(R.string.dialog_new_project_next), null);
        builder.setNegativeButton(context.getResources().getString(R.string.dialog_new_project_name_cancel),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        String name = editText.getText().toString();

                        if (name == null || name.isEmpty()) {
                            Toast.makeText(context, "Name cannot be empty.", Toast.LENGTH_SHORT).show();
                        } else {
                            newProject.setName(editText.getText().toString());
                            launchProjectStartDateDialog();
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
        dialog.show();
    }

    private void launchProjectStartDateDialog() {
        DatePickerDialog dialog = new DatePickerDialog(context, this, newProject.getStartDate().getYear(),
                newProject.getStartDate().getMonthOfYear() - 1, newProject.getStartDate().getDayOfMonth());

        dialog.setButton(DialogInterface.BUTTON_POSITIVE,
                context.getResources().getString(R.string.dialog_new_project_next), dialog);

        dialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                context.getResources().getString(R.string.dialog_new_project_back),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        launchProjectNameDialog(newProject.getName());
                    }
                });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void launchProjectCameraListDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        final ListView cameraList = new ListView(context);
        ArrayAdapter<String> cameraListAdapter = new ArrayAdapter<>(context,
                R.layout.list_view_item_camera, newProject.getCameras());
        cameraList.setAdapter(cameraListAdapter);
        builder.setView(cameraList);

        builder.setTitle(
                context.getResources().getString(R.string.dialog_new_project_cameras_partial_title) + newProject.getName());

        builder.setPositiveButton(context.getResources().getString(R.string.dialog_new_project_next), null);


        builder.setNegativeButton(context.getResources().getString(R.string.dialog_new_project_back),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        launchProjectStartDateDialog();
                    }
                });

        builder.setNeutralButton(context.getResources().getString(R.string.dialog_new_project_add_camera),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        launchAddCameraInputDialog();
                    }
                });

        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(
                    new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            if (newProject.getCameras().size() < 1) {
                                Toast.makeText(context, "You must add at least one camera.", Toast.LENGTH_SHORT).show();
                            } else {
                                launchAdditionalInfoDialog();
                                dialog.dismiss();
                            }
                        }
                    });
            }
        });
        dialog.show();
    }

    private void launchAddCameraInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        final EditText editText = new EditText(context);
        editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS
                | InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE
                | InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE
                | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                editText.post(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                    }
                });
            }
        });
        editText.requestFocus();
        builder.setView(editText);

        builder.setTitle(context.getResources().getString(R.string.dialog_new_project_camera_input_title));
        builder.setPositiveButton(context.getResources().getString(R.string.dialog_new_project_done), null);
        builder.setNegativeButton(context.getResources().getString(R.string.dialog_new_project_back),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        launchProjectCameraListDialog();
                        dialog.cancel();
                    }
                });

        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        String cameraName = editText.getText().toString();

                        if (cameraName == null || cameraName.isEmpty()) {
                            Toast.makeText(context, "Camera name cannot be empty.", Toast.LENGTH_SHORT).show();
                        } else {
                            newProject.addCamera(editText.getText().toString());
                            launchProjectCameraListDialog();
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
        dialog.show();
    }

    private void launchAdditionalInfoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        final EditText editText = new EditText(context);
        if (newProject.getAddInfo() == null) {
            editText.setHint(context.getResources().getString(R.string.dialog_new_project_add_info_hint));
        }
        else {
            editText.setText(newProject.getAddInfo());
        }

        editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        editText.setSingleLine(false);
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                editText.post(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                    }
                });
            }
        });
        editText.requestFocus();
        builder.setView(editText);

        builder.setTitle(context.getResources().getString(R.string.dialog_new_project_add_info_title));
        builder.setPositiveButton(context.getResources().getString(R.string.dialog_new_project_done),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        newProject.setAddInfo(editText.getText().toString());
                        if (flags == 0 || flags == CLONE) {
                            callback.onProjectCreate(newProject);
                        }
                        else if (flags == EDIT) {
                            callback.onProjectChange(newProject);
                        }

                        dialog.cancel();
                    }
                });
        builder.setNegativeButton(context.getResources().getString(R.string.dialog_new_project_back),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        launchProjectCameraListDialog();
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Log.d("NewProjectDialog", "onDateSet");
        newProject.setStartDate(new LocalDate(year, monthOfYear + 1, dayOfMonth));
        launchProjectCameraListDialog();
    }

    public interface NewProjectDialogListener {
        public void onProjectCreate(Project p);
        public void onProjectChange(Project p);
    }
}
