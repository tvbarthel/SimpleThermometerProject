package fr.tvbarthel.apps.simplethermometer.openweathermap;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;


/**
 * A simple parser used to retrieve data of
 * an xml flux from the OpenWeatherMap Api.
 */
public class OpenWeatherMapParser {

/*
 * A typical xml response from http://api.openweathermap.org/data/2.5/weather?lat=35&lon=139&mode=xml&units=metric
 *
 * <current>
 * 		<city id="1851632" name="Shuzenji">
 * 			<coord lon="139" lat="35"/>
 * 			<country>JP</country>
 * 			<sun rise="2013-09-11T20:24:53" set="2013-09-12T08:55:26"/>
 * 		</city>
 * 		<temperature value="21.47" min="21.47" max="21.47" unit="celsius"/>
 * 		<humidity value="100" unit="%"/>
 * 		<pressure value="1005.09" unit="hPa"/>
 * 		<wind>
 * 			<speed value="1.96" name="Light breeze"/>
 * 			<direction value="42.0003" code="NE" name="NorthEast"/>
 * 		</wind>
 * 		<clouds value="8" name="sky is clear"/>
 * 		<precipitation mode="no"/>
 * 		<weather number="800" value="sky is clear" icon="02n"/>
 * 		<lastupdate value="2013-09-12T17:32:19"/>
 * </current>
 */

    //Names and Attributes used in the openWeatherMap XML
    public static final String NAME_ROOT = "current";
    public static final String NAME_CITY = "city";
    public static final String ATTRIBUTE_CITY_ID = "id";
    public static final String ATTRIBUTE_CITY_NAME = "name";
    public static final String NAME_COORDINATE = "coord";
    public static final String ATTRIBUTE_LONGITUDE = "lon";
    public static final String ATTRIBUTE_LATITUDE = "lat";
    public static final String NAME_COUNTRY = "country";
    public static final String NAME_SUN = "sun";
    public static final String ATTRIBUTE_SUN_RISE = "rise";
    public static final String ATTRIBUTE_SUN_SET = "set";
    public static final String NAME_TEMPERATURE = "temperature";
    public static final String ATTRIBUTE_TEMPERATURE_VALUE = "value";
    public static final String ATTRIBUTE_TEMPERATURE_MIN = "min";
    public static final String ATTRIBUTE_TEMPERATURE_MAX = "max";
    public static final String ATTRIBUTE_TEMPERATURE_UNIT = "unit";
    public static final String NAME_HUMDITY = "humidity";
    public static final String ATTRIBUTE_HUMIDITY_VALUE = "value";
    public static final String ATTRIBUTE_HUMIDITY_UNIT = "unit";
    public static final String NAME_PRESSURE = "pressure";
    public static final String ATTRIBUTE_PRESSURE_VALUE = "value";
    public static final String ATTRIBUTE_PRESSURE_UNIT = "unit";
    public static final String NAME_WIND = "wind";
    public static final String NAME_WIND_SPEED = "speed";
    public static final String ATTRIBUTE_WIND_SPEED_VALUE = "value";
    public static final String ATTRIBUTE_WIND_SPEED_NAME = "name";
    public static final String NAME_WIND_DIRECTION = "direction";
    public static final String ATTRIBUTE_WIND_DIRECTION_VALUE = "value";
    public static final String ATTRIBUTE_WIND_DIRECTION_CODE = "code";
    public static final String ATTRIBUTE_WIND_DIRECTION_NAME = "name";
    public static final String NAME_CLOUD = "clouds";
    public static final String ATTRIBUTE_CLOUD_VALUE = "value";
    public static final String ATTRIBUTE_CLOUD_NAME = "name";
    public static final String NAME_PRECIPITATION = "precipitation";
    public static final String ATTRIBUTE_PRECIPITATION_MODE = "mode";
    public static final String NAME_WEATHER = "weather";
    public static final String ATTRIBUTE_WEATHER_NUMBER = "number";
    public static final String ATTRIBUTE_WEATHER_VALUE = "value";
    public static final String ATTRIBUTE_WEATHER_ICON = "icon";
    public static final String NAME_LAST_UPDATE = "lastupdate";
    public static final String ATTIBUTE_LAST_UPDATE_VALUE = "value";

    private static final String NAME_SPACE = null;

    /**
     * Parse {@code in} a {@link fr.tvbarthel.apps.simplethermometer.openweathermap.OpenWeatherMapParserResult}
     *
     * @param in {@link java.io.InputStream}
     * @return {@link fr.tvbarthel.apps.simplethermometer.openweathermap.OpenWeatherMapParserResult}
     * @throws XmlPullParserException
     * @throws IOException
     */
    public OpenWeatherMapParserResult parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readOpenWeatherMap(parser);
        } finally {
            in.close();
        }
    }

    /**
     * Read an {@link fr.tvbarthel.apps.simplethermometer.openweathermap.OpenWeatherMapParserResult}
     * from {@code parser}
     *
     * @param parser {@link org.xmlpull.v1.XmlPullParser}
     * @return {@link fr.tvbarthel.apps.simplethermometer.openweathermap.OpenWeatherMapParserResult}
     * @throws XmlPullParserException
     * @throws IOException
     */
    private OpenWeatherMapParserResult readOpenWeatherMap(XmlPullParser parser) throws XmlPullParserException, IOException {
        OpenWeatherMapParserResult result = new OpenWeatherMapParserResult();

        parser.require(XmlPullParser.START_TAG, NAME_SPACE, NAME_ROOT);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            final String name = parser.getName();
            if (name.equals(NAME_CITY)) {
                readCity(parser, result);
            } else if (name.equals(NAME_TEMPERATURE)) {
                readTemperature(parser, result);
            } else if (name.equals(NAME_HUMDITY)) {
                readHumidity(parser, result);
            } else if (name.equals(NAME_PRESSURE)) {
                readPressure(parser, result);
            } else if (name.equals(NAME_WIND)) {
                readWind(parser, result);
            } else if (name.equals(NAME_CLOUD)) {
                readCloud(parser, result);
            } else if (name.equals(NAME_PRECIPITATION)) {
                readPrecipitation(parser, result);
            } else if (name.equals(NAME_WEATHER)) {
                readWeather(parser, result);
            } else if (name.equals(NAME_LAST_UPDATE)) {
                readLastUpdate(parser, result);
            } else {
                skip(parser);
            }
        }
        return result;
    }

    /**
     * Read the precipitation information from {@code parser} and set them in {@code result}
     *
     * @param parser {@link org.xmlpull.v1.XmlPullParser}
     * @param result {@link fr.tvbarthel.apps.simplethermometer.openweathermap.OpenWeatherMapParserResult}
     * @throws XmlPullParserException
     * @throws IOException
     */
    private void readPrecipitation(XmlPullParser parser, OpenWeatherMapParserResult result) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, NAME_SPACE, NAME_PRECIPITATION);
        result.setPrecipitationMode(parser.getAttributeValue(NAME_SPACE, ATTRIBUTE_PRECIPITATION_MODE));
        parser.nextTag();
        parser.require(XmlPullParser.END_TAG, NAME_SPACE, NAME_PRECIPITATION);
    }


    /**
     * Read the cloud information from {@code parser} and set them in {@code result}
     *
     * @param parser {@link org.xmlpull.v1.XmlPullParser}
     * @param result {@link fr.tvbarthel.apps.simplethermometer.openweathermap.OpenWeatherMapParserResult}
     * @throws XmlPullParserException
     * @throws IOException
     */
    private void readCloud(XmlPullParser parser, OpenWeatherMapParserResult result) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, NAME_SPACE, NAME_CLOUD);
        result.setCloudValue(Float.valueOf(parser.getAttributeValue(NAME_SPACE, ATTRIBUTE_CLOUD_VALUE)));
        result.setCloudName(parser.getAttributeValue(NAME_SPACE, ATTRIBUTE_CLOUD_NAME));
        parser.nextTag();
        parser.require(XmlPullParser.END_TAG, NAME_SPACE, NAME_CLOUD);
    }

    /**
     * Read the pressure information from {@code parser} and set them in {@code result}
     *
     * @param parser {@link org.xmlpull.v1.XmlPullParser}
     * @param result {@link fr.tvbarthel.apps.simplethermometer.openweathermap.OpenWeatherMapParserResult}
     * @throws XmlPullParserException
     * @throws IOException
     */
    private void readPressure(XmlPullParser parser, OpenWeatherMapParserResult result) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, NAME_SPACE, NAME_PRESSURE);
        result.setPressureValue(Float.valueOf(parser.getAttributeValue(NAME_SPACE, ATTRIBUTE_PRESSURE_VALUE)));
        result.setPressureUnit(parser.getAttributeValue(NAME_SPACE, ATTRIBUTE_PRESSURE_UNIT));
        parser.nextTag();
        parser.require(XmlPullParser.END_TAG, NAME_SPACE, NAME_PRESSURE);
    }

    /**
     * Read the humidity information from {@code parser} and set them in {@code result}
     *
     * @param parser {@link org.xmlpull.v1.XmlPullParser}
     * @param result {@link fr.tvbarthel.apps.simplethermometer.openweathermap.OpenWeatherMapParserResult}
     * @throws XmlPullParserException
     * @throws IOException
     */
    private void readHumidity(XmlPullParser parser, OpenWeatherMapParserResult result) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, NAME_SPACE, NAME_HUMDITY);
        result.setHumidityValue(Float.valueOf(parser.getAttributeValue(NAME_SPACE, ATTRIBUTE_HUMIDITY_VALUE)));
        result.setHumidityUnit(parser.getAttributeValue(NAME_SPACE, ATTRIBUTE_HUMIDITY_UNIT));
        parser.nextTag();
        parser.require(XmlPullParser.END_TAG, NAME_SPACE, NAME_HUMDITY);
    }

    /**
     * Read the weather information from {@code parser} and set them in {@code result}
     *
     * @param parser {@link org.xmlpull.v1.XmlPullParser}
     * @param result {@link fr.tvbarthel.apps.simplethermometer.openweathermap.OpenWeatherMapParserResult}
     * @throws XmlPullParserException
     * @throws IOException
     */
    private void readWeather(XmlPullParser parser, OpenWeatherMapParserResult result) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, NAME_SPACE, NAME_WEATHER);
        result.setWeatherValue(parser.getAttributeValue(NAME_SPACE, ATTRIBUTE_WEATHER_VALUE));
        result.setWeatherNumber(Integer.valueOf(parser.getAttributeValue(NAME_SPACE, ATTRIBUTE_WEATHER_NUMBER)));
        result.setWeatherIcon(parser.getAttributeValue(NAME_SPACE, ATTRIBUTE_WEATHER_ICON));
        parser.nextTag();
        parser.require(XmlPullParser.END_TAG, NAME_SPACE, NAME_WEATHER);
    }

    /**
     * Read the last update information from {@code parser} and set them in {@code result}
     *
     * @param parser {@link org.xmlpull.v1.XmlPullParser}
     * @param result {@link fr.tvbarthel.apps.simplethermometer.openweathermap.OpenWeatherMapParserResult}
     * @throws XmlPullParserException
     * @throws IOException
     */
    private void readLastUpdate(XmlPullParser parser, OpenWeatherMapParserResult result) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, NAME_SPACE, NAME_LAST_UPDATE);
        result.setLastUpdate(parser.getAttributeValue(NAME_SPACE, ATTIBUTE_LAST_UPDATE_VALUE));
        parser.nextTag();
        parser.require(XmlPullParser.END_TAG, NAME_SPACE, NAME_LAST_UPDATE);
    }

    /**
     * Read the temperature information from {@code parser} and set them in {@code result}
     *
     * @param parser {@link org.xmlpull.v1.XmlPullParser}
     * @param result {@link fr.tvbarthel.apps.simplethermometer.openweathermap.OpenWeatherMapParserResult}
     * @throws XmlPullParserException
     * @throws IOException
     */
    private void readTemperature(XmlPullParser parser, OpenWeatherMapParserResult result) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, NAME_SPACE, NAME_TEMPERATURE);
        result.setTemperatureValue(Float.valueOf(parser.getAttributeValue(NAME_SPACE, ATTRIBUTE_TEMPERATURE_VALUE)));
        result.setTemperatureMax(Float.valueOf(parser.getAttributeValue(NAME_SPACE, ATTRIBUTE_TEMPERATURE_MAX)));
        result.setTemperatureMin(Float.valueOf(parser.getAttributeValue(NAME_SPACE, ATTRIBUTE_TEMPERATURE_MIN)));
        result.setTemperatureUnit(parser.getAttributeValue(NAME_SPACE, ATTRIBUTE_TEMPERATURE_UNIT));
        parser.nextTag();
        parser.require(XmlPullParser.END_TAG, NAME_SPACE, NAME_TEMPERATURE);
    }

    /**
     * Read the wind information from {@code parser} and set them in {@code result}
     *
     * @param parser {@link org.xmlpull.v1.XmlPullParser}
     * @param result {@link fr.tvbarthel.apps.simplethermometer.openweathermap.OpenWeatherMapParserResult}
     * @throws XmlPullParserException
     * @throws IOException
     */
    private void readWind(XmlPullParser parser, OpenWeatherMapParserResult result) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, NAME_SPACE, NAME_WIND);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            final String name = parser.getName();
            if (name.equals(NAME_WIND_SPEED)) {
                result.setWindSpeedValue(Float.valueOf(parser.getAttributeValue(NAME_SPACE, ATTRIBUTE_WIND_SPEED_VALUE)));
                result.setWindSpeedName(parser.getAttributeValue(NAME_SPACE, ATTRIBUTE_WIND_SPEED_NAME));
                parser.nextTag();
            } else if (name.equals(NAME_WIND_DIRECTION)) {
                result.setWindDirectionValue(Float.valueOf(parser.getAttributeValue(NAME_SPACE, ATTRIBUTE_WIND_DIRECTION_VALUE)));
                result.setWindDirectionCode(parser.getAttributeValue(NAME_SPACE, ATTRIBUTE_WIND_DIRECTION_CODE));
                result.setWindDirectionName(parser.getAttributeValue(NAME_SPACE, ATTRIBUTE_WIND_DIRECTION_NAME));
                parser.nextTag();
            } else {
                skip(parser);
            }
        }

    }

    /**
     * Read the city information from {@code parser} and set them in {@code result}
     *
     * @param parser {@link org.xmlpull.v1.XmlPullParser}
     * @param result {@link fr.tvbarthel.apps.simplethermometer.openweathermap.OpenWeatherMapParserResult}
     * @throws XmlPullParserException
     * @throws IOException
     */
    private void readCity(XmlPullParser parser, OpenWeatherMapParserResult result) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, NAME_SPACE, NAME_CITY);
        result.setCityId(Integer.valueOf(parser.getAttributeValue(NAME_SPACE, ATTRIBUTE_CITY_ID)));
        result.setCityName(parser.getAttributeValue(NAME_SPACE, ATTRIBUTE_CITY_NAME));
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            final String name = parser.getName();
            if (name.equals(NAME_COORDINATE)) {
                result.setLongitude(Float.valueOf(parser.getAttributeValue(NAME_SPACE, ATTRIBUTE_LONGITUDE)));
                result.setLatitude(Float.valueOf(parser.getAttributeValue(NAME_SPACE, ATTRIBUTE_LATITUDE)));
                parser.nextTag();
            } else if (name.equals(NAME_COUNTRY)) {
                parser.require(XmlPullParser.START_TAG, NAME_SPACE, NAME_COUNTRY);
                result.setCountry(readText(parser));
                parser.require(XmlPullParser.END_TAG, NAME_SPACE, NAME_COUNTRY);
            } else if (name.equals(NAME_SUN)) {
                result.setSunRise(parser.getAttributeValue(NAME_SPACE, ATTRIBUTE_SUN_RISE));
                result.setSunSet(parser.getAttributeValue(NAME_SPACE, ATTRIBUTE_SUN_SET));
                parser.nextTag();
            } else {
                skip(parser);
            }
        }
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
