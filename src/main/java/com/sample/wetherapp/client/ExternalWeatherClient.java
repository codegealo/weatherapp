package com.sample.wetherapp.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sample.wetherapp.dto.WeatherResponseDto;

public interface ExternalWeatherClient {
    WeatherResponseDto getWeather(String city) throws Exception;
}
