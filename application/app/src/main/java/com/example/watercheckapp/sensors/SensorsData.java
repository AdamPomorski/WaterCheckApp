package com.example.watercheckapp.sensors;

public class SensorsData {
    private int sensor_number;
    private String sensor_id;
    private String timestamp;
    private String ph;
    private String water_level;
    private String too_high_ph_value_private = "9";
    private String too_low_ph_value_private = "5";
    private String too_high_water_level_value_private = "9";
    private String too_low_water_level_value_private = "100";
    private String too_fast_water_level_change_private = "15";
    private String measure_period;
    private String lang_s;
    private String long_s;
    private String description;



    //sensor data with settings
    public SensorsData(String sensor_id, String too_high_ph_value_private, String too_low_ph_value_private, String too_high_water_level_value_private, String too_low_water_level_value_private, String too_fast_water_level_change_private, String measure_period) {
        this.sensor_id = sensor_id;
        this.too_high_ph_value_private = too_high_ph_value_private;
        this.too_low_ph_value_private = too_low_ph_value_private;
        this.too_high_water_level_value_private = too_high_water_level_value_private;
        this.too_low_water_level_value_private = too_low_water_level_value_private;
        this.too_fast_water_level_change_private = too_fast_water_level_change_private;
        this.measure_period = measure_period;
    }

    public SensorsData(String sensor_id, String timestamp, String ph, String water_level) {
        this.sensor_id = sensor_id;
        this.timestamp = timestamp;
        this.ph = ph;
        this.water_level = water_level;
    }

    public SensorsData(String sensor_id, String measure_period, String lang_s, String long_s, String description) {

        this.sensor_id = sensor_id;
        this.measure_period = measure_period;
        this.lang_s = lang_s;
        this.long_s = long_s;
        this.description = description;
    }
    public SensorsData(int sensor_number,String sensor_id, String measure_period, String lang_s, String long_s, String description) {
        this.sensor_number = sensor_number;
        this.sensor_id = sensor_id;
        this.measure_period = measure_period;
        this.lang_s = lang_s;
        this.long_s = long_s;
        this.description = description;
    }

    public SensorsData(String sensor_id, String ph, String water_level) {
        this.sensor_id = sensor_id;
        this.ph = ph;
        this.water_level = water_level;
    }
    public SensorsData(int sensor_number,String sensor_id, String ph, String water_level) {
        this.sensor_number = sensor_number;
        this.sensor_id = sensor_id;
        this.ph = ph;
        this.water_level = water_level;
    }


    public int getSensor_number() {
        return sensor_number;
    }

    public void setSensor_number(int sensor_number) {
        this.sensor_number = sensor_number;
    }


    public String getSensor_id() {
        return sensor_id;
    }

    public void setSensor_id(String sensor_id) {
        this.sensor_id = sensor_id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getPh() {
        return ph;
    }

    public void setPh(String ph) {
        this.ph = ph;
    }

    public String getWater_level() {
        return water_level;
    }

    public void setWater_level(String water_level) {
        this.water_level = water_level;
    }

    public String getToo_high_ph_value_private() {
        return too_high_ph_value_private;
    }

    public void setToo_high_ph_value_private(String too_high_ph_value_private) {
        this.too_high_ph_value_private = too_high_ph_value_private;
    }

    public String getToo_low_ph_value_private() {
        return too_low_ph_value_private;
    }

    public void setToo_low_ph_value_private(String too_low_ph_value_private) {
        this.too_low_ph_value_private = too_low_ph_value_private;
    }

    public String getToo_high_water_level_value_private() {
        return too_high_water_level_value_private;
    }

    public void setToo_high_water_level_value_private(String too_high_water_level_value_private) {
        this.too_high_water_level_value_private = too_high_water_level_value_private;
    }

    public String getToo_low_water_level_value_private() {
        return too_low_water_level_value_private;
    }

    public void setToo_low_water_level_value_private(String too_low_water_level_value_private) {
        this.too_low_water_level_value_private = too_low_water_level_value_private;
    }

    public String getToo_fast_water_level_change_private() {
        return too_fast_water_level_change_private;
    }

    public void setToo_fast_water_level_change_private(String too_fast_water_level_change_private) {
        this.too_fast_water_level_change_private = too_fast_water_level_change_private;
    }
    public String getLang_s() {
        return lang_s;
    }

    public void setLang_s(String lang_s) {
        this.lang_s = lang_s;
    }

    public String getLong_s() {
        return long_s;
    }

    public void setLong_s(String long_s) {
        this.long_s = long_s;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMeasure_period() {
        return measure_period;
    }

    public void setMeasure_period(String measure_period) {
        this.measure_period = measure_period;
    }

    @Override
    public String toString() {
        return "SensorsData{" +
                "sensor_id='" + sensor_id + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", ph='" + ph + '\'' +
                ", water_level='" + water_level + '\'' +
                '}';
    }
}
