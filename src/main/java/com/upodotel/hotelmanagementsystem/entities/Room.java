package com.upodotel.hotelmanagementsystem.entities;

import java.util.List;

public class Room {
    private final int id;
    private final String name;
    private final int capacity;
    private final float price;
    private final List<Feature> features;
    private String featuresString = "";

    public Room(int id, String name, int capacity, float price, List<Feature> features) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.price = price;
        this.features = features;
        for (Feature feature : features) {
            featuresString += feature.getFeatureName() + ", ";
        }
        if (!featuresString.equals("")) {
            featuresString = featuresString.substring(0, featuresString.length() - 2);
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }

    public float getPrice() {
        return price;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public String getFeaturesString() {
        return featuresString;
    }
}
