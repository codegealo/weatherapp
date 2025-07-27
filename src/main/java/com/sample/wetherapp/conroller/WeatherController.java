package com.sample.wetherapp.conroller;

import com.sample.wetherapp.dto.WeatherResponseDto;
import com.sample.wetherapp.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
//@RequestMapping("")
public class WeatherController {
    @Autowired
    WeatherService weatherService;


    @GetMapping("v1/weather")
    public WeatherResponseDto getWeather(@RequestParam String city){
        return weatherService.getWeatherByCity(city);
    }
}
