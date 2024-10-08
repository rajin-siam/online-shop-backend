package com.siam.storage.impl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.siam.enteties.User;
import com.siam.enteties.impl.DefaultUser;
import com.siam.storage.UserStoringService;

public class DefaultUserStoringService implements UserStoringService {

    private static final String USER_INFO_STORAGE = "users.csv";
    private static final String CURRENT_TASK_RESOURCE_FOLDER = "finaltask";
    private static final String DATA_STORAGE_FOLDER = "data";  // New data storage folder
    private static final int USER_EMAIL_INDEX = 4;
    private static final int USER_PASSWORD_INDEX = 3;
    private static final int USER_LASTNAME_INDEX = 2;
    private static final int USER_FIRSTNAME_INDEX = 1;
    private static final int USER_ID_INDEX = 0;

    private static DefaultUserStoringService instance;

    @Override
    public void saveUser(User user) {
        try {
            // Save to a writable directory outside the classpath
            Files.createDirectories(Paths.get(DATA_STORAGE_FOLDER, CURRENT_TASK_RESOURCE_FOLDER));
            Files.writeString(Paths.get(DATA_STORAGE_FOLDER, CURRENT_TASK_RESOURCE_FOLDER, USER_INFO_STORAGE),
                    System.lineSeparator() + convertToStorableString(user),
                    StandardCharsets.UTF_8, StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String convertToStorableString(User user) {
        return user.getId() + "," + user.getFirstName() + "," + user.getLastName() + "," + user.getPassword() + ","
                + user.getEmail();
    }

    @Override
    public List<User> loadUsers() {
        try {
            // Load from the classpath
            Path userFilePath = Paths.get(getClass().getClassLoader()
                    .getResource(CURRENT_TASK_RESOURCE_FOLDER + "/" + USER_INFO_STORAGE).toURI());

            try (var stream = Files.lines(userFilePath)) {
                return stream.filter(Objects::nonNull)
                        .filter(line -> !line.isEmpty())
                        .map(line -> {
                            String[] userElements = line.split(",");
                            return new DefaultUser(Integer.valueOf(userElements[USER_ID_INDEX]),
                                    userElements[USER_FIRSTNAME_INDEX], userElements[USER_LASTNAME_INDEX],
                                    userElements[USER_PASSWORD_INDEX], userElements[USER_EMAIL_INDEX]);
                        }).collect(Collectors.toList());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public static DefaultUserStoringService getInstance() {
        if (instance == null) {
            instance = new DefaultUserStoringService();
        }
        return instance;
    }
}
