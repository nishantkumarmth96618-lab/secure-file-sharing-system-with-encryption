package com.securefileshare;

import java.io.*;
import java.util.*;
import java.util.Base64;

public class AuthService {

    private static final String USERS_FILE = "users.txt";

    public boolean register(String username, String password) {
        try {
            if (userExists(username)) {
                System.out.println("Username already exists.");
                return false;
            }

            byte[] salt = CryptoUtil.generateSalt();
            String passwordHash = CryptoUtil.hashPassword(password, salt);

            try (FileWriter writer = new FileWriter(USERS_FILE, true)) {
                writer.write(username + "," + passwordHash + "," +
                        Base64.getEncoder().encodeToString(salt) + "\n");
            }

            File userFolder = new File("storage/" + username);
            userFolder.mkdirs();

            System.out.println("Registration successful.");
            return true;

        } catch (Exception e) {
            System.out.println("Registration failed: " + e.getMessage());
            return false;
        }
    }

    public User login(String username, String password) {
        try {
            List<User> users = loadUsers();

            for (User user : users) {
                if (user.getUsername().equals(username)) {
                    byte[] salt = Base64.getDecoder().decode(user.getSalt());
                    String enteredHash = CryptoUtil.hashPassword(password, salt);

                    if (enteredHash.equals(user.getPasswordHash())) {
                        System.out.println("Login successful.");
                        return user;
                    }
                }
            }

            System.out.println("Invalid username or password.");

        } catch (Exception e) {
            System.out.println("Login failed: " + e.getMessage());
        }

        return null;
    }

    private boolean userExists(String username) throws Exception {
        List<User> users = loadUsers();

        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }

        return false;
    }

    private List<User> loadUsers() throws Exception {
        List<User> users = new ArrayList<>();

        File file = new File(USERS_FILE);

        if (!file.exists()) {
            file.createNewFile();
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length == 3) {
                    users.add(new User(parts[0], parts[1], parts[2]));
                }
            }
        }

        return users;
    }
}