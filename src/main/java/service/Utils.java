package service;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    public List<String> amenityNames;

    // constructor
    public Utils() {
        setupAmenityNames();
    }
    private void setupAmenityNames() {
        amenityNames = new ArrayList<>();
        amenityNames.add("Wifi");
        amenityNames.add("Kitchen");
        amenityNames.add("Washer");
        amenityNames.add("Dryer");
        amenityNames.add("Air conditioning");
        amenityNames.add("Heating");
        amenityNames.add("Dedicated workspace");
        amenityNames.add("TV");
        amenityNames.add("Hair dryer");
        amenityNames.add("Iron");
        amenityNames.add("Pool");
        amenityNames.add("Hot tub");
        amenityNames.add("Free parking");
        amenityNames.add("EV charger");
        amenityNames.add("Crib");
        amenityNames.add("Gym");
        amenityNames.add("BBQ grill");
        amenityNames.add("Breakfast");
        amenityNames.add("Indoor fireplace");
        amenityNames.add("Smoking allowed");
        amenityNames.add("Beachfront");
        amenityNames.add("Waterfront");
        amenityNames.add("Smoke alarm");
        amenityNames.add("Carbon monoxide alarm");
    }

    public Boolean isValidAmenity(String amenityName) {
        return amenityNames.contains(amenityName);
    }
}
