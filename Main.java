package com.securefileshare;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        AuthService authService = new AuthService();
        FileService fileService = new FileService();

        while (true) {
            System.out.println("\n===== SECURE FILE SHARING SYSTEM =====");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Enter username: ");
                    String regUsername = scanner.nextLine();

                    System.out.print("Enter password: ");
                    String regPassword = scanner.nextLine();

                    authService.register(regUsername, regPassword);
                    break;

                case 2:
                    System.out.print("Enter username: ");
                    String loginUsername = scanner.nextLine();

                    System.out.print("Enter password: ");
                    String loginPassword = scanner.nextLine();

                    User loggedInUser = authService.login(loginUsername, loginPassword);

                    if (loggedInUser != null) {
                        userMenu(scanner, fileService, loggedInUser, loginPassword);
                    }

                    break;

                case 3:
                    System.out.println("Thank you for using Secure File Sharing System.");
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private static void userMenu(
            Scanner scanner,
            FileService fileService,
            User user,
            String password
    ) {
        while (true) {
            System.out.println("\n===== USER MENU =====");
            System.out.println("1. Upload File");
            System.out.println("2. Download File");
            System.out.println("3. List My Files");
            System.out.println("4. Share File");
            System.out.println("5. Logout");
            System.out.print("Choose option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Enter full file path to upload: ");
                    String filePath = scanner.nextLine();

                    fileService.uploadFile(user, password, filePath);
                    break;

                case 2:
                    System.out.print("Enter encrypted file name: ");
                    String encryptedFileName = scanner.nextLine();

                    System.out.print("Enter output path with file name: ");
                    String outputPath = scanner.nextLine();

                    fileService.downloadFile(user, password, encryptedFileName, outputPath);
                    break;

                case 3:
                    fileService.listFiles(user);
                    break;

                case 4:
                    System.out.print("Enter encrypted file name to share: ");
                    String shareFileName = scanner.nextLine();

                    System.out.print("Enter receiver username: ");
                    String receiverUsername = scanner.nextLine();

                    fileService.shareFile(user, shareFileName, receiverUsername);
                    break;

                case 5:
                    System.out.println("Logged out successfully.");
                    return;

                default:
                    System.out.println("Invalid option.");
            }
        }
    }
}