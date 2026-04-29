package com.securefileshare;

import javax.crypto.SecretKey;
import java.io.*;
import java.nio.file.*;
import java.util.Base64;

public class FileService {

    public void uploadFile(User user, String password, String filePath) {
        try {
            File inputFile = new File(filePath);

            if (!inputFile.exists()) {
                System.out.println("File not found.");
                return;
            }

            byte[] fileData = Files.readAllBytes(inputFile.toPath());

            byte[] salt = Base64.getDecoder().decode(user.getSalt());
            SecretKey key = CryptoUtil.getAESKeyFromPassword(password, salt);

            byte[] encryptedData = CryptoUtil.encrypt(fileData, key);

            String outputPath = "storage/" + user.getUsername() + "/" + inputFile.getName() + ".enc";

            Files.write(Paths.get(outputPath), encryptedData);

            System.out.println("File uploaded and encrypted successfully.");
            System.out.println("Saved at: " + outputPath);

        } catch (Exception e) {
            System.out.println("Upload failed: " + e.getMessage());
        }
    }

    public void downloadFile(User user, String password, String encryptedFileName, String outputPath) {
        try {
            String encryptedPath = "storage/" + user.getUsername() + "/" + encryptedFileName;

            File encryptedFile = new File(encryptedPath);

            if (!encryptedFile.exists()) {
                System.out.println("Encrypted file not found.");
                return;
            }

            byte[] encryptedData = Files.readAllBytes(encryptedFile.toPath());

            byte[] salt = Base64.getDecoder().decode(user.getSalt());
            SecretKey key = CryptoUtil.getAESKeyFromPassword(password, salt);

            byte[] decryptedData = CryptoUtil.decrypt(encryptedData, key);

            Files.write(Paths.get(outputPath), decryptedData);

            System.out.println("File decrypted and downloaded successfully.");
            System.out.println("Saved at: " + outputPath);

        } catch (Exception e) {
            System.out.println("Download failed. Wrong password or corrupted file.");
        }
    }

    public void listFiles(User user) {
        File folder = new File("storage/" + user.getUsername());

        if (!folder.exists()) {
            System.out.println("No files found.");
            return;
        }

        File[] files = folder.listFiles();

        if (files == null || files.length == 0) {
            System.out.println("No uploaded files.");
            return;
        }

        System.out.println("\nYour encrypted files:");

        for (File file : files) {
            System.out.println("- " + file.getName());
        }
    }

    public void shareFile(User user, String encryptedFileName, String receiverUsername) {
        try {
            File sourceFile = new File("storage/" + user.getUsername() + "/" + encryptedFileName);

            if (!sourceFile.exists()) {
                System.out.println("File not found.");
                return;
            }

            File receiverFolder = new File("storage/" + receiverUsername);

            if (!receiverFolder.exists()) {
                System.out.println("Receiver user does not exist.");
                return;
            }

            File sharedFolder = new File("storage/" + receiverUsername + "/shared_from_" + user.getUsername());
            sharedFolder.mkdirs();

            Files.copy(
                    sourceFile.toPath(),
                    Paths.get(sharedFolder.getPath(), encryptedFileName),
                    StandardCopyOption.REPLACE_EXISTING
            );

            System.out.println("Encrypted file shared successfully.");
            System.out.println("Receiver can access it from: " + sharedFolder.getPath());

        } catch (Exception e) {
            System.out.println("Sharing failed: " + e.getMessage());
        }
    }
}