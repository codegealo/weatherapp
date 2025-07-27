package com.sample.wetherapp.service;

import com.sample.wetherapp.dto.WeatherResponseDto;

public interface WeatherService {
    WeatherResponseDto getWeatherByCity(String city);
}
