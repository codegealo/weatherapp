package com.sample.wetherapp.service;

import com.sample.wetherapp.client.ExternalWeatherClient;
import com.sample.wetherapp.dto.WeatherResponseDto;
import com.sample.wetherapp.service.v1.WeatherServiceV1;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WeatherServiceV1Test {

    @Mock
//    @Qualifier("primary")
    private ExternalWeatherClient externalWeatherClientPrimary;

    @Mock
//    @Qualifier("backup")
    private ExternalWeatherClient externalWeatherClientBackup;

    @InjectMocks
    private WeatherServiceV1 weatherServiceV1;

    @Test
    void testGetWeatherByCity() throws Exception {
        WeatherResponseDto response = new WeatherResponseDto(1,1);

        when(externalWeatherClientPrimary.getWeather(anyString())).thenReturn(response);
        weatherServiceV1.init();
        Assertions.assertEquals(response,weatherServiceV1.getWeatherByCity(""));

        when(externalWeatherClientPrimary.getWeather(anyString())).thenThrow(new RuntimeException());
        when(externalWeatherClientBackup.getWeather(anyString())).thenReturn(response);
        Assertions.assertEquals(response,weatherServiceV1.getWeatherByCity(""));

        ReflectionTestUtils.setField(weatherServiceV1, "cacheDuration", 3);
        Map<String, WeatherServiceV1.WeatherCache> cacheMap = new HashMap<>();
        cacheMap.put("test", new WeatherServiceV1.WeatherCache(Instant.now(), response));
        ReflectionTestUtils.setField(weatherServiceV1, "cacheMap", cacheMap);
        when(externalWeatherClientBackup.getWeather(anyString())).thenThrow(new RuntimeException());
        Assertions.assertEquals(response,weatherServiceV1.getWeatherByCity("test"));

        cacheMap.put("test", new WeatherServiceV1.WeatherCache(Instant.now()
                .minus(3, ChronoUnit.SECONDS), response));
        ReflectionTestUtils.setField(weatherServiceV1, "cacheMap", cacheMap);
        Assertions.assertEquals(response,weatherServiceV1.getWeatherByCity("test"));
    }

    //TODO more comprehensive testing

}
