package com.sample.wetherapp.service.v1;

import com.sample.wetherapp.client.ExternalWeatherClient;
import com.sample.wetherapp.dto.WeatherResponseDto;
import com.sample.wetherapp.service.WeatherService;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.swing.plaf.basic.BasicDesktopIconUI;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.Temporal;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Service
public class WeathgerServiceV1 implements WeatherService {

    @Autowired
    @Qualifier("primary")
    private ExternalWeatherClient externalWeatherClientPrimary;

    @Autowired
    @Qualifier("backup")
    private ExternalWeatherClient externalWeatherClientBackup;

    @Value("${cacheDuration}")
    private long cacheDuration;

    private Map<String,WeatherCache> cacheMap;

    @PostConstruct
    public void init(){
        cacheMap = new HashMap<>();
    }

    @Override
    public WeatherResponseDto getWeatherByCity(String city) {
        city = city.toLowerCase();
        try {
            if (isCacheValid(city)) {
                return cacheMap.get(city).weatherResponseDto;
            }
            return getWeather(city);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private  boolean isCacheValid(String city){
        if(cacheMap.get(city) != null){
            Duration duration = Duration.between(cacheMap.get(city).lastUpdate,Instant.now());
            return (duration.getSeconds()<cacheDuration);
        }
        return false;
    }

    private WeatherResponseDto getWeather(String city) throws Exception {

        WeatherResponseDto weatherResponseDto;
        try {
            weatherResponseDto = externalWeatherClientPrimary.getWeather(city);
            cacheMap.put(city, new WeatherCache(Instant.now(),weatherResponseDto));
        } catch (Exception e) {
            weatherResponseDto = externalWeatherClientBackup.getWeather(city);
            cacheMap.put(city, new WeatherCache(Instant.now(),weatherResponseDto));
        }
        return weatherResponseDto;
    }


    @AllArgsConstructor
    class WeatherCache{
        Instant lastUpdate;
        WeatherResponseDto weatherResponseDto;
    }


}
