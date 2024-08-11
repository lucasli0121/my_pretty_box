package com.effectsar.labcv.core.external_lib;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Locale;

public class SdCardLibrarySource implements LibrarySource {
    static final String TAG = "SdCardLibrarySource";
    private final Context context;

    public SdCardLibrarySource(Context context) {
        this.context = context;
    }

    @Override
    public String transformLibrary(String libraryName) {
        return getNativeLibraryPathOnExternal(libraryName);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void afterLibraryLoaded(String libraryName) {
        File tmp = new File(getTempFilesDir(), libraryName);
        tmp.delete();
    }

    private String getNativeLibraryPathOnExternal(String libraryName) {
        final String targetLibraryName = String.format(Locale.US, "lib%s.so", libraryName);

        File[] files = null;

        File[] baseDirs = getBaseDirs();

        for (File baseDir : baseDirs) {
            try {

                files = baseDir.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.equals(targetLibraryName);
                    }
                });
                if (files != null && files.length > 0) {
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, Log.getStackTraceString(e));
            }
        }

        File tempFilesDir = getTempFilesDir();
        if (!tempFilesDir.exists()) {
            if (!tempFilesDir.mkdirs()) {
                Log.e(TAG, "Error mkdirs " + tempFilesDir.getPath());
                return null;
            }
        }

        if (files != null && files.length > 0) {
            File dest = new File(tempFilesDir, targetLibraryName);
            try {
                Files.copy(files[0], dest);
            } catch (IOException e) {
                Log.e(TAG, "Error copying file from external storage: " + files[0].getName());
                return null;
            }
            return dest.getPath();
        } else {
            return null;
        }
    }

    private File getTempFilesDir() {
        return new File(context.getFilesDir(), "external_libs");
    }

    private File[] getBaseDirs() {
        return new File[] {
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                new File("/data/local/tmp/cv")
        };
    }

}
