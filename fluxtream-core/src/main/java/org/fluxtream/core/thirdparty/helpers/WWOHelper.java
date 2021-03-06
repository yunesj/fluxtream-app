package org.fluxtream.core.thirdparty.helpers;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.fluxtream.core.Configuration;
import org.fluxtream.core.aspects.FlxLogger;
import org.fluxtream.core.domain.metadata.WeatherInfo;
import org.fluxtream.core.utils.UnexpectedHttpResponseCodeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.fluxtream.core.utils.HttpUtils.fetch;

@Component
public class WWOHelper {

    FlxLogger logger = FlxLogger.getLogger(WWOHelper.class);

	@Autowired
	private Configuration env;


    public List<WeatherInfo> getWeatherInfo(double latitude, double longitude, String fdate) throws UnexpectedHttpResponseCodeException, IOException {
        List<WeatherInfo> weather = new ArrayList<WeatherInfo>();

        // if no key is provided, just return an empty List
        // TODO: handle this more gracefully on the frontend
        if (env.get("wwo.key")==null)
            return weather;

		String wwoUrl = "http://api.worldweatheronline.com/premium/v1/past-weather.ashx?" +
"key=" + env.get("wwo.key") + "&format=json&q=" + latitude + "," + longitude + "&date=" + fdate;
        long then = System.currentTimeMillis();
		String wwoJson = fetch(wwoUrl);
        long now = System.currentTimeMillis();
        if (now-then>3000)
            logger.warn("WorldWeatherOnline just got very slow (" + (now-then)+ " ms)");

		JSONObject wwoInfo = JSONObject.fromObject(wwoJson);
		if (wwoInfo!=null) {
			JSONObject data = wwoInfo.getJSONObject("data");
			if (data==null) return weather;
			JSONArray weatherDataArray = data.getJSONArray("weather");
			if (weatherDataArray==null) return weather;
			JSONObject weatherData = weatherDataArray.getJSONObject(0);
			if (weatherData==null) return weather;
			JSONArray hourly = weatherData.getJSONArray("hourly");
			if (hourly!=null) {
				@SuppressWarnings("rawtypes") Iterator iterator = hourly.iterator();
				while (iterator.hasNext()) {
					JSONObject hourlyRecord = (JSONObject) iterator.next();
					WeatherInfo weatherInfo = new WeatherInfo();
					weatherInfo.cloudcover = Integer.valueOf(hourlyRecord.getString("cloudcover"));
					weatherInfo.humidity = Integer.valueOf(hourlyRecord.getString("humidity"));
					weatherInfo.precipMM = Float.valueOf(hourlyRecord.getString("precipMM"));
					weatherInfo.pressure = Integer.valueOf(hourlyRecord.getString("pressure"));
					weatherInfo.tempC = Integer.valueOf(hourlyRecord.getString("tempC"));
					weatherInfo.tempF = Integer.valueOf(hourlyRecord.getString("tempF"));
					weatherInfo.minuteOfDay = Integer.valueOf(hourlyRecord.getString("time"));

					weatherInfo.visibility = Integer.valueOf(hourlyRecord.getString("visibility"));
					weatherInfo.weatherCode = Integer.valueOf(hourlyRecord.getString("weatherCode"));
					JSONArray weatherDesc = hourlyRecord.getJSONArray("weatherDesc");
					JSONArray weatherIconUrl = hourlyRecord.getJSONArray("weatherIconUrl");
					weatherInfo.weatherDesc = weatherDesc.getJSONObject(0).getString("value");
                    weatherInfo.weatherIconUrl = null;
                    weatherInfo.weatherIconUrlDay = null;
                    weatherInfo.weatherIconUrlNight = null;
                    weatherInfo.weatherIconUrl = weatherIconUrl.getJSONObject(0).getString("value");
					weatherInfo.winddirDegree = Integer.valueOf(hourlyRecord.getString("winddirDegree"));
					weatherInfo.windspeedMiles = Integer.valueOf(hourlyRecord.getString("windspeedMiles"));
                    weatherInfo.windspeedKmph = Integer.valueOf(hourlyRecord.getString("windspeedKmph"));
                    weatherInfo.winddir16Point = hourlyRecord.getString("winddir16Point");

					weather.add(weatherInfo);
				}
			}
		}
		return weather;
	}
}
