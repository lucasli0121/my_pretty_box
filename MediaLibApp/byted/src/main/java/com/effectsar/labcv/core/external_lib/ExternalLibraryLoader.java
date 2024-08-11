package com.effectsar.labcv.core.external_lib;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExternalLibraryLoader {
    static String TAG = "ExternalLibraryLoader";

    static private final ArrayList<String> loadedLibraryNames = new ArrayList<>();
    private final LibrarySource librarySource;

    public ExternalLibraryLoader(LibrarySource librarySource) {
        this.librarySource = librarySource;
    }

    public void loadLib() {
        List<String> list = Arrays.asList("c++_shared", "effect");
        if (!onLoadNativeLibs(list)) {
            Log.e(TAG, "dynamically load library failed!");
        }
    }

    public boolean onLoadNativeLibs(List<String> list) {
        for (int i = 0; i < list.size(); i++) {
            if (!load(list.get(i))) {
                return false;
            }
        }
        return true;
    }

    private boolean load(String libraryName) {
        if (loadedLibraryNames.contains(libraryName)) {
            return true;
        }
        String transformedLibraryPath = librarySource.transformLibrary(libraryName);
        if (transformedLibraryPath == null) {
            Log.e(TAG, "Get " + libraryName + " path failded");
            return false;
        } else {
            try {
                Log.i(TAG, "Loading " + libraryName + " from external storage " + transformedLibraryPath);
                System.load(transformedLibraryPath);
                loadedLibraryNames.add(libraryName);
                return true;
            } catch (UnsatisfiedLinkError e) {
                e.printStackTrace();
                return false;
            } finally {
                librarySource.afterLibraryLoaded(libraryName);
            }
        }
    }
}
