package com.effectsar.labcv.common.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

//vehook_package don't delete this line
import com.effectsar.labcv.core.Config;
import com.effectsar.labcv.core.util.LogUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.concurrent.Callable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static android.os.Environment.DIRECTORY_MOVIES;

public class FileUtils {

    /** {zh} 
     * 递归拷贝Asset目录中的文件到rootDir中
     * Recursively copy the files in the Asset directory to rootDir
     * @param assets
     * @param path
     * @param rootDir
     * @throws IOException
     */
    /** {en} 
     * Recursively copy files in the Asset directory to rootDir
     * Recursively copy the files in the Asset directory to rootDir
     * @param assets
     * @param path
     * @param rootDir
     * @throws IOException
     */

    public static void copyAssets(AssetManager assets, String path, String rootDir) throws IOException {
        if (isAssetsDir(assets, path)) {
            File dir = new File(rootDir + File.separator + path);
            if (!dir.exists() && !dir.mkdirs()) {
                throw new IllegalStateException("mkdir failed");
            }
            for (String s : assets.list(path)) {
                copyAssets(assets, path + "/" + s, rootDir);
            }
        } else {
            InputStream input = assets.open(path);
            File dest = new File(rootDir, path);
            copyToFileOrThrow(input, dest);
        }

    }


    public static void copyAssets(AssetManager assets, String srcRootDir, String path, String rootDir) throws IOException {
        String fullAssetPath = new File(srcRootDir, path).toString();
        if (isAssetsDir(assets, fullAssetPath)) {
            File dir = new File(rootDir + File.separator + path);
            if (!dir.exists() && !dir.mkdirs()) {
                throw new IllegalStateException("mkdir failed");
            }
            String[] pathList = assets.list(fullAssetPath);
            if (pathList != null) {
                for (String s : pathList) {
                    copyAssets(assets, srcRootDir, path + "/" + s, rootDir);
                }
            }
        } else {
            InputStream input = assets.open(fullAssetPath);
            File dest = new File(rootDir, path);
            copyToFileOrThrow(input, dest);
        }

    }

    public static void copyAssetsContent(AssetManager assets, String srcRootDir, String path, String rootDir) throws IOException {
        String fullAssetPath = new File(srcRootDir, path).getPath();
        String[] filePathList = assets.list(fullAssetPath);
        if (filePathList != null) {
            for (String filePath : filePathList) {
                if (filePath.equals("online_model")){
                    if (!Config.IS_ONLINE_MODEL) {
                        String onlineAssetPath = new File(fullAssetPath, filePath).getPath();
                        String[] onlinePathList = assets.list(onlineAssetPath);
                        for (String online_path : onlinePathList) {
                            copyAssets(assets, onlineAssetPath, online_path , rootDir);
                        }
                    }
                }
                else {
                    copyAssets(assets, fullAssetPath, filePath , rootDir);
                }
            }
        }
    }

    public static void copyAssetsContentFiltered(AssetManager assets, String srcRootDir, String path, String rootDir, HashSet<String> copyAllowList) throws IOException {
        String fullAssetPath = new File(srcRootDir, path).getPath();
        String[] filePathList = assets.list(fullAssetPath);
        if (filePathList != null) {
            for (String filePath : filePathList) {
                if (copyAllowList != null && copyAllowList.contains(filePath)) {
                    copyAssets(assets, fullAssetPath, filePath , rootDir);
                }
            }
        }
    }

    public static void copyAssets(AssetManager assets, String dir) throws IOException {
        String[] paths = assets.list("");
        for (String s : paths) {
            copyAssets(assets, s, dir);
        }
    }

    public static boolean isAssetsDir(AssetManager assets, String path) {
        try {

            String[] files = assets.list(path);
            return files != null && files.length > 0;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void copyToFileOrThrow(InputStream inputStream, File destFile)
            throws IOException {
        if (destFile.exists()) {
            return;

        }
        File file = destFile.getParentFile();
        if (file != null && !file.exists()) {
            file.mkdirs();
        }
        FileOutputStream out = new FileOutputStream(destFile);
        try {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) >= 0) {
                out.write(buffer, 0, bytesRead);
            }
        } finally {
            out.flush();
            try {
                out.getFD().sync();
            } catch (IOException e) {
            }
            out.close();
        }
    }


    /** {zh} 
     * 解压压缩包
     * 解压后删除zip文件
     * unzip the package and delete thd zip file
     * @return
     */
    /** {en} 
     * Unzip package
     *  Delete zip file after decompression
     * unzip the package and delete thd zip file
     * @return
     */

    public static boolean unzipAssetFile(Context context, String zipFilePath, File dstDir) {
        ZipInputStream zipInputStream = null;
        try {
            zipInputStream = new ZipInputStream(context.getAssets().open(zipFilePath));
            return unzipFile(zipInputStream, dstDir);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (zipInputStream != null) {
                try {
                    zipInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public static boolean unzipFile(String filePath, File dstDir) {
        ZipInputStream zipInputStream = null;
        try {
            zipInputStream = new ZipInputStream(new FileInputStream(new File(filePath)));
            boolean ret = unzipFile(zipInputStream, dstDir);
            LogUtils.d("unzipFile ret =" + ret);
            return ret;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (zipInputStream != null) {
                try {
                    zipInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        LogUtils.d("unzipFile ret =" + false);

        return false;
    }

    public static boolean unzipFile(ZipInputStream zipInputStream, File dstDir) {

        try {
            if (dstDir.exists()) {
                dstDir.delete();
            }
            dstDir.mkdirs();
            if (null == zipInputStream) {
                return false;
            }
            ZipEntry entry;
            String name;
            do {
                entry = zipInputStream.getNextEntry();
                if (null != entry) {
                    name = entry.getName();
                    if (entry.isDirectory()) {
                        name = name.substring(0, name.length() - 1);
                        File folder = new File(dstDir, name);
                        folder.mkdirs();

                    } else {
                        //   {zh} 否则创建文件,并输出文件的内容     {en} Otherwise create the file and output the contents of the file 
                        File file = new File(dstDir, name);
                        file.getParentFile().mkdirs();
                        file.createNewFile();
                        FileOutputStream out = new FileOutputStream(file);
                        int len;
                        byte[] buffer = new byte[1024];
                        while ((len = zipInputStream.read(buffer)) != -1) {
                            out.write(buffer, 0, len);
                            out.flush();
                        }
                        out.close();

                    }
                }

            } while (null != entry);

        } catch (Exception e) {
            e.printStackTrace();
            return false;

        } finally {
            if (zipInputStream != null) {
                try {
                    zipInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;

    }

    public static boolean clearDir(File dir) {
        if (!dir.exists()) {
            return true;
        }
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                clearDir(file);
                file.delete();
            } else {
                if (file.exists()) {
                    file.delete();
                }
            }
        }
        return true;
    }

    public static String generateVideoFile() {
        String directory = Environment.getExternalStoragePublicDirectory(DIRECTORY_MOVIES).getAbsolutePath();
        String name = createtFileName(".mp4");
        return directory + "/" + name;
    }

    public static String generateCacheFile(String fileName) {
        String directory = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).getAbsolutePath();
        return directory + File.separator + fileName;
    }

    public static synchronized String createtFileName(String suffix) {
        java.util.Date dt = new java.util.Date(System.currentTimeMillis());
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String fileName= fmt.format(dt);
        fileName = fileName + suffix; //extension, you can change it.
        return fileName;
    }


    //vehook_add don't delete this line

    public static boolean validateFileMD5(String strMD5, String filePath){
        File fileToCheck = new File(filePath);
        if (strMD5.isEmpty() || !fileToCheck.exists()) {
            LogUtils.e("MD5 string empty or File not exists");
            return false;
        }
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
        InputStream is = null;
        try {
            is = new FileInputStream(fileToCheck);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            LogUtils.e("Exception while getting FileInputStream");
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ie) {
                    ie.printStackTrace();
                }
            }
            return false;
        }

        byte[] buffer = new byte[8192];
        int read;
        String calculatedDigest = null;
        try {
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            byte[] md5sum = digest.digest();
            BigInteger bigInt = new BigInteger(1, md5sum);
            calculatedDigest = bigInt.toString(16);
            calculatedDigest = String.format("%32s", calculatedDigest).replace(' ', '0');
        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.e("Unable to process file for ");
            return false;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    LogUtils.e("Exception on closing inputstream:" );
                }
            }
        }
        if (calculatedDigest.isEmpty()) {
            LogUtils.d("calculatedDigest null");
            return false;
        }
        return calculatedDigest.equalsIgnoreCase(strMD5);
    }

    public static boolean checkFileMD5(String strMD5, String filePath){
        try{
            String string = getHash(filePath);
            return string == strMD5;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static char[] hexChar = { '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    public static String getHash(String fileName)
            throws Exception {
        InputStream fis = null;
        String md5Str = null;
        try {
            fis = new FileInputStream(fileName);
            byte[] buffer = new byte[1024];
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            int numRead = 0;
            while ((numRead = fis.read(buffer)) > 0) {
                md5.update(buffer, 0, numRead);
            }

            md5Str = toHexString(md5.digest());
        } catch (IOException e) {
            throw e;
        } finally {
            if (fis != null) {
                fis.close();
            }
        }

        return md5Str;
    }

    public static String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(hexChar[(b[i] & 0xf0) >>> 4]);
            sb.append(hexChar[b[i] & 0x0f]);
        }
        return sb.toString();
    }

    public static String removeFileNameExtension(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot >-1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }


    public static String loadStringFromFile(String filePath){
        String content = "";
        FileReader fileReader = null;
        try{
            String str;
            fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while((str = bufferedReader.readLine()) != null){
                content = content + str + "\n";
            }
//            while((str = bufferedReader.read())!=null){
//                content = content + str + "\n";
//            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return content;
    }

    public static String loadStringFromFile(File file){
        String content = null;
        FileInputStream fis = null;
        try{
            fis = new FileInputStream(file);
            byte[] buffer = new byte[10];
            StringBuilder sb = new StringBuilder();
            while (fis.read(buffer) != -1) {
                sb.append(new String(buffer));
                buffer = new byte[10];
            }
            content = sb.toString();
        } catch (Exception e){
            e.printStackTrace();
            LogUtils.e(e.getMessage());
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return content;
    }
    public static boolean saveStringToFile(String content, String filePath) {
        if (content.isEmpty()) {
            LogUtils.e("Content failed saved to "+filePath);
            return false;
        } else {
            File file = new File(filePath);
            if(!file.exists()){
                try {
                    file.createNewFile();
                } catch (IOException e){
                    e.printStackTrace();
                    LogUtils.e(e.getMessage());
                    return false;
                }
            }

            FileWriter fileWritter = null;
            boolean ret = false;
            try {
                fileWritter = new FileWriter(filePath, false);
                BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
                bufferWritter.write(content);
                bufferWritter.close();
                ret = true;
            } catch (IOException e) {
                e.printStackTrace();
                LogUtils.e(e.getMessage());
                ret = false;
            } finally {
                if (fileWritter != null) {
                    try {
                        fileWritter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return ret;
        }
    }

//
//        if(content.isEmpty()){
//            return false;
//        }else{
//            try{
//                File file = new File(fileFullPath);
//                if(!file.exists()){
//                    file.createNewFile();
//                }
//                InputStream is = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
//                FileOutputStream fos = null;
//                try {
//                    fos = new FileOutputStream(file);
//                    int consumed;
//                    byte[] bytes = new byte[1024 * 8];
//                    while ((consumed = is.read(bytes)) > 0) {
//                        fos.write(bytes, 0, consumed);
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    return false;
//                } finally {
//                    if (is != null) {
//                        is.close();
//                    }
//                    if (fos != null) {
//                        fos.close();
//                    }
//                }
//            }catch (IOException e){
//                e.printStackTrace();
//                LogUtils.e(e.getMessage());
//                LogUtils.e("save "+ fileFullPath +" error");
//                return false;
//            }
//            return true;
//        }

    //  {zh} 文件读取任务，将二进制文件转成float数组  {en} File reading task, convert binary files into floating array
    public static class FileReaderTask implements Callable<float[]> {
        private final File file;

        public FileReaderTask(String filename) {
            file = new File(filename);
        }

        public float[] call() throws Exception {
            RandomAccessFile raf = null;
            float[] data = null;

            try {
                raf = new RandomAccessFile(file, "r");
                FileChannel channel = raf.getChannel();
                MappedByteBuffer buffer = (MappedByteBuffer) channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size()).order(ByteOrder.LITTLE_ENDIAN);
                //  {zh} 转换成float数组  {en} Convert to float array
                FloatBuffer floatBuffer = buffer.asFloatBuffer();
                data = new float[floatBuffer.limit()];
                floatBuffer.get(data);
                channel.close();
            } catch (IOException e) {
                throw e;
            } finally {
                if (raf != null) {
                    raf.close();
                }
            }

            return data;
        }
    }
}
