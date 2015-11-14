package com.davidtschida.purduemenu.models;

import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by david on 10/22/2015.
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class Meal extends SugarRecord<Meal> {
    public static String BREAKFAST = "breakfast";
    public static String LUNCH = "lunch";
    public static String LATE_LUNCH = "late lunch";
    public static String DINNER = "dinner";

    public static String STATUS_CLOSED = "Closed";

    @SerializedName("Name")
    String name;

    @SerializedName("Order")
    int mealOrder;

    @SerializedName("Status")
    String status;

    @SerializedName("Hours")
    MealHours hours;

    @SerializedName("Stations")
    List<Station> stations;

    public List<FoodItem> getAllFoodItems() {
        List<FoodItem> foodItems = new ArrayList<>();
        for(Station station : getStations()) {
            foodItems.addAll(station.getItems());
        }
        return foodItems;
    }

    /**
     * Returns true if the given time is within the operating hours for the meal
     *
     * Assumptions: The date is not checked, only the time.
     * @param localTime the time to be checked against.
     * @return true|false
     */
    public boolean containsTime(LocalTime localTime) {
        return getHours() != null &&
                localTime.isAfter(getHours().getStartLocalTime()) &&
                localTime.isBefore(getHours().getEndLocalTime());
    }

    /**
     * Returns true if the given time is after the end of the meal
     *
     * Assumptions: The date is not checked, only the time.
     * @param localTime the time to be checked against.
     * @return true|false
     */
    public boolean endsBefore(LocalTime localTime) {
        return getHours() != null &&
                localTime.isAfter(getHours().getEndLocalTime());
    }

    public boolean startsAfter(LocalTime localTime) {
        return getHours() != null &&
                localTime.isBefore(getHours().getStartLocalTime());
    }

    public long timeUntilStartFrom(LocalTime localTime) {
        if(getHours() == null)
            return Long.MAX_VALUE; //Return "infinity" for time until if there are no hours.
        //TODO: Should this be negative?
        return getHours().getStartLocalTime().getMillisOfDay() - localTime.getMillisOfDay();
    }
}
