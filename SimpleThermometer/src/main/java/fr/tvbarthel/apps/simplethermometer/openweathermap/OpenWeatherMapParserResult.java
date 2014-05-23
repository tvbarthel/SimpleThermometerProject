package fr.tvbarthel.apps.simplethermometer.openweathermap;

/**
 * A simple class used to store the information
 * retrieved during the parsing of an xml flux from the
 * openWeatherMap api.
 * <p/>
 * See {@link fr.tvbarthel.apps.simplethermometer.openweathermap.OpenWeatherMapParser}
 */
public class OpenWeatherMapParserResult {
    private Integer mCityId;
    private String mCityName;
    private Float mLongitude;
    private Float mLatitude;
    private String mCountry;
    private String mSunRise;
    private String mSunSet;
    private Float mTemperatureValue;
    private Float mTemperatureMax;
    private Float mTemperatureMin;
    private String mTemperatureUnit;
    private Float mHumidityValue;
    private String mHumidityUnit;
    private Float mPressureValue;
    private String mPressureUnit;
    private Float mWindSpeedValue;
    private String mWindSpeedName;
    private Float mWindDirectionValue;
    private String mWindDirectionCode;
    private String mWindDirectionName;
    private Float mCloudValue;
    private String mCloudName;
    private String mPrecipitationMode;
    private Integer mWeatherNumber;
    private String mWeatherValue;
    private String mWeatherIcon;

    public Integer getCityId() {
        return mCityId;
    }

    public void setCityId(Integer cityId) {
        mCityId = cityId;
    }

    public String getCityName() {
        return mCityName;
    }

    public void setCityName(String cityName) {
        mCityName = cityName;
    }

    public Float getLongitude() {
        return mLongitude;
    }

    public void setLongitude(Float longitude) {
        mLongitude = longitude;
    }

    public Float getLatitude() {
        return mLatitude;
    }

    public void setLatitude(Float latitude) {
        mLatitude = latitude;
    }

    public String getCountry() {
        return mCountry;
    }

    public void setCountry(String country) {
        mCountry = country;
    }

    public String getSunRise() {
        return mSunRise;
    }

    public void setSunRise(String sunRise) {
        mSunRise = sunRise;
    }

    public String getSunSet() {
        return mSunSet;
    }

    public void setSunSet(String sunSet) {
        mSunSet = sunSet;
    }

    public Float getTemperatureValue() {
        return mTemperatureValue;
    }

    public void setTemperatureValue(Float temperatureValue) {
        mTemperatureValue = temperatureValue;
    }

    public Float getTemperatureMax() {
        return mTemperatureMax;
    }

    public void setTemperatureMax(Float temperatureMax) {
        mTemperatureMax = temperatureMax;
    }

    public Float getTemperatureMin() {
        return mTemperatureMin;
    }

    public void setTemperatureMin(Float temperatureMin) {
        mTemperatureMin = temperatureMin;
    }

    public String getTemperatureUnit() {
        return mTemperatureUnit;
    }

    public void setTemperatureUnit(String temperatureUnit) {
        mTemperatureUnit = temperatureUnit;
    }

    public Float getHumidityValue() {
        return mHumidityValue;
    }

    public void setHumidityValue(Float humidityValue) {
        mHumidityValue = humidityValue;
    }

    public String getHumidityUnit() {
        return mHumidityUnit;
    }

    public void setHumidityUnit(String humidityUnit) {
        mHumidityUnit = humidityUnit;
    }

    public Float getPressureValue() {
        return mPressureValue;
    }

    public void setPressureValue(Float pressureValue) {
        mPressureValue = pressureValue;
    }

    public String getPressureUnit() {
        return mPressureUnit;
    }

    public void setPressureUnit(String pressureUnit) {
        mPressureUnit = pressureUnit;
    }

    public Float getWindSpeedValue() {
        return mWindSpeedValue;
    }

    public void setWindSpeedValue(Float windSpeedValue) {
        mWindSpeedValue = windSpeedValue;
    }

    public String getWindSpeedName() {
        return mWindSpeedName;
    }

    public void setWindSpeedName(String windSpeedName) {
        mWindSpeedName = windSpeedName;
    }

    public Float getWindDirectionValue() {
        return mWindDirectionValue;
    }

    public void setWindDirectionValue(Float windDirectionValue) {
        mWindDirectionValue = windDirectionValue;
    }

    public String getWindDirectionCode() {
        return mWindDirectionCode;
    }

    public void setWindDirectionCode(String windDirectionCode) {
        mWindDirectionCode = windDirectionCode;
    }

    public String getWindDirectionName() {
        return mWindDirectionName;
    }

    public void setWindDirectionName(String windDirectionName) {
        mWindDirectionName = windDirectionName;
    }

    public Float getCloudValue() {
        return mCloudValue;
    }

    public void setCloudValue(Float cloudValue) {
        mCloudValue = cloudValue;
    }

    public String getCloudName() {
        return mCloudName;
    }

    public void setCloudName(String cloudName) {
        mCloudName = cloudName;
    }

    public String getPrecipitationMode() {
        return mPrecipitationMode;
    }

    public void setPrecipitationMode(String precipitationMode) {
        mPrecipitationMode = precipitationMode;
    }

    public Integer getWeatherNumber() {
        return mWeatherNumber;
    }

    public void setWeatherNumber(Integer weatherNumber) {
        mWeatherNumber = weatherNumber;
    }

    public String getWeatherValue() {
        return mWeatherValue;
    }

    public void setWeatherValue(String weatherValue) {
        mWeatherValue = weatherValue;
    }

    public String getWeatherIcon() {
        return mWeatherIcon;
    }

    public void setWeatherIcon(String weatherIcon) {
        mWeatherIcon = weatherIcon;
    }

    public String getLastUpdate() {
        return LastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        LastUpdate = lastUpdate;
    }

    private String LastUpdate;


}
