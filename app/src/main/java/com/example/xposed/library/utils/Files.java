package com.example.xposed.library.utils;

import com.example.xposed.library.log.Log2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

public class Files {

    /**
     * 写文件
     *
     * @param file 文件
     * @param text 内容
     */
    public static void writeFile(File file, String text) {
        try {
            makeParent(file);

            if (!file.exists()) {
                boolean r = file.createNewFile();
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(text);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 续写文件
     *
     * @param file 文件
     * @param text 内容
     */
    public static void appendFile(File file, String text) {
        try {
            if (!file.exists()) {
                if (!file.getParentFile().exists()) {
                    boolean r = file.getParentFile().mkdirs();
                }
                boolean r = file.createNewFile();
            }
            RandomAccessFile randomFile = new RandomAccessFile(file.getPath(), "rw");
            long fileLength = randomFile.length();
            randomFile.seek(fileLength);
            randomFile.write((text + "\r\n").getBytes());
            randomFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 续写文件
     *
     * @param file 文件
     * @param text 内容
     */
    public static void appendFileFirst(File file, String text) {
        try {
            if (!file.exists()) {
                if (!file.getParentFile().exists()) {
                    boolean r = file.getParentFile().mkdirs();
                }
                boolean r = file.createNewFile();
            }
            String oldContent = readFile(file);
            RandomAccessFile randomFile = new RandomAccessFile(file.getPath(), "rw");
            long fileLength = randomFile.length();
            randomFile.seek(0);
            randomFile.write((text + "\r\n").getBytes());
            randomFile.write(oldContent.getBytes());
            randomFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读文件
     *
     * @param file 文件
     * @return 内容
     */
    public static String readFile(File file) {
        StringBuilder builder = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append("\r\n");
            }
            builder.delete(builder.length() - 2, builder.length());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    /**
     * 读文件
     *
     * @param file 文件
     * @return 内容
     */
    public static byte[] readFileBytes(File file) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            int len = 0;
            byte[] buffer = new byte[1024];
            while ((len = fileInputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, len);
                byteArrayOutputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * 删除文件
     *
     * @param file 文件
     */
    public static void delete(File file) {
        if (file == null) {
            return;
        }

        if (file.isFile()) {
            boolean r = file.delete();
        } else {
            File[] listFiles = file.listFiles();
            if (listFiles != null) {
                for (File listFile : listFiles) {
                    delete(listFile);
                }
            }
        }
    }

    /**
     * 写入文件
     *
     * @param file 文件
     * @param data 字节数据
     */
    public static void writeByte(File file, byte[] data) {
        try {
            makeParent(file);

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            Log2.e(e);
        }
    }

    /**
     * 如果父文件不存在则创建
     *
     * @param file 文件
     */
    public static void makeParent(File file) {
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                boolean r = file.getParentFile().mkdirs();
            }
        }
    }

    /**
     * 复制文件
     *
     * @param source 源文件
     * @param dst    目标文件
     */
    public static void copyFile(File source, File dst) {
        makeParent(dst);

        InputStream input = null;
        OutputStream output = null;
        try {
            input = new FileInputStream(source);
            output = new FileOutputStream(dst);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buf)) != -1) {
                output.write(buf, 0, bytesRead);
            }
            output.close();
            input.close();
        } catch (Exception e) {
            Log2.e(e);
        }
    }
}
