/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.mavenproject1;

/**
 *
 * @author a
 */
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;

public class Mavenproject1 {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java MultiThreadedCompressor <file_path>");
            return;
        }

        String filePath = args[0];
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("File not found: " + filePath);
            return;
        }

       
        ExecutorService executor = Executors.newFixedThreadPool(3);

        executor.submit(() -> compressToZip(file));
        executor.submit(() -> compressToGzip(file));
        executor.submit(() -> compressTo7z(file));

        
        executor.shutdown();
    }

    private static void compressToZip(File file) {
        String zipFileName = file.getAbsolutePath() + ".zip";

        try (FileOutputStream fos = new FileOutputStream(zipFileName);
             ZipOutputStream zos = new ZipOutputStream(fos);
             FileInputStream fis = new FileInputStream(file)) {

            ZipEntry zipEntry = new ZipEntry(file.getName());
            zos.putNextEntry(zipEntry);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) >= 0) {
                zos.write(buffer, 0, length);
            }

            zos.closeEntry();
            System.out.println("ZIP compression completed: " + zipFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void compressToGzip(File file) {
        String gzipFileName = file.getAbsolutePath() + ".gz";

        try (FileInputStream fis = new FileInputStream(file);
             FileOutputStream fos = new FileOutputStream(gzipFileName);
             GZIPOutputStream gzipOS = new GZIPOutputStream(fos)) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) >= 0) {
                gzipOS.write(buffer, 0, length);
            }

            System.out.println("GZIP compression completed: " + gzipFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void compressTo7z(File file) {
        String sevenZFileName = file.getAbsolutePath() + ".7z";

        try (SevenZOutputFile sevenZOutput = new SevenZOutputFile(new File(sevenZFileName));
             FileInputStream fis = new FileInputStream(file)) {

            SevenZArchiveEntry entry = sevenZOutput.createArchiveEntry(file, file.getName());
            sevenZOutput.putArchiveEntry(entry);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                sevenZOutput.write(buffer, 0, length);
            }

            sevenZOutput.closeArchiveEntry();
            System.out.println("7z compression completed: " + sevenZFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
