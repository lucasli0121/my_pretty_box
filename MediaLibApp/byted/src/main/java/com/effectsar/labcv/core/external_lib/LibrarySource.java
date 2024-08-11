package com.effectsar.labcv.core.external_lib;

public interface LibrarySource {

    String transformLibrary(String libraryName);

    void afterLibraryLoaded(String libraryName);
}
