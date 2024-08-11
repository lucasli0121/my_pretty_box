package com.effectsar.labcv.core.external_lib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

final class Files {
    private Files() {}

    static boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }

    /**
     * Creates an empty file or updates the last updated timestamp on the same as the unix command of
     * the same name.
     *
     * @param file the file to create or update
     * @throws IOException if an I/O error occurs
     */
    static void touch(File file) throws IOException {
        checkNotNull(file);
        if (!file.createNewFile() && !file.setLastModified(System.currentTimeMillis())) {
            throw new IOException("Unable to update modification time of " + file);
        }
    }

    static long copy(File input, File output) throws IOException {
        long bytesCopied = 0;
        int defaultBufferSize = 8 * 1024;
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(output);
            FileInputStream in = null;
            try {
                in = new FileInputStream(input);
                byte[] buffer = new byte[defaultBufferSize];
                int bytes = in.read(buffer);
                while (bytes >= 0) {
                    out.write(buffer, 0, bytes);
                    bytesCopied += bytes;
                    bytes = in.read(buffer);
                }
            } finally {
                if (in != null) {
                    in.close();
                }
            }
        } finally {
            if (out != null) {
                out.close();
            }
        }
        return bytesCopied;
    }

    static <T> T checkNotNull(T reference) {
        if (reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }

}
