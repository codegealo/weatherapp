package com.sample.wetherapp.client;

import com.sample.wetherapp.dto.WeatherResponseDto;

public interface ExternalWeatherClient {
    WeatherResponseDto getWeather(String city) throws Exception;
}
