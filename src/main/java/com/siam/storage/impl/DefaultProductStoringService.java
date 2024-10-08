package com.siam.storage.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.siam.enteties.Product;
import com.siam.enteties.impl.DefaultProduct;
import com.siam.storage.ProductStoringService;

public class DefaultProductStoringService implements ProductStoringService {

    private static final String PRODUCTS_INFO_STORAGE = "products.csv";
    private static final String CURRENT_TASK_RESOURCE_FOLDER = "finaltask";
    private static final String DATA_STORAGE_FOLDER = "data";  // New data storage folder
    private static final int PRODUCT_PRICE_INDEX = 3;
    private static final int PRODUCT_CATEGORY_INDEX = 2;
    private static final int PRODUCT_NAME_INDEX = 1;
    private static final int PRODUCT_ID_INDEX = 0;

    private static DefaultProductStoringService instance;

    @Override
    public List<Product> loadProducts() {
        try {
            // Load from the classpath
            Path productFilePath = Paths.get(getClass().getClassLoader()
                    .getResource(CURRENT_TASK_RESOURCE_FOLDER + "/" + PRODUCTS_INFO_STORAGE).toURI());

            try (var stream = Files.lines(productFilePath)) {
                return stream
                        .filter(Objects::nonNull)
                        .filter(line -> !line.isEmpty())
                        .map(line -> {
                            String[] productElements = line.split(",");
                            return new DefaultProduct(Integer.valueOf(productElements[PRODUCT_ID_INDEX]),
                                    productElements[PRODUCT_NAME_INDEX],
                                    productElements[PRODUCT_CATEGORY_INDEX],
                                    Double.valueOf(productElements[PRODUCT_PRICE_INDEX]));
                        }).collect(Collectors.toList());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public static DefaultProductStoringService getInstance() {
        if (instance == null) {
            instance = new DefaultProductStoringService();
        }
        return instance;
    }
}
