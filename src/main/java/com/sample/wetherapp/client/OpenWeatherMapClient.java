package com.sample.wetherapp.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sample.wetherapp.dto.WeatherResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service("backup")
public class OpenWeatherMapClient implements ExternalWeatherClient{

    @Value("${openweathermap.api}")
    private String api;

    //TODO parameterize request
    private WeatherResponseDto apiCall() throws JsonProcessingException {
        RestClient restClient = RestClient.create();

        String responseBody = restClient.get()
                .uri(api)
                .retrieve()
                .body(String.class);

        return parseResponse(responseBody);
    }

    private WeatherResponseDto parseResponse(String responseBody) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(responseBody);
        double temp = rootNode.get("main").get("temp").asDouble() - 273.15;
        double wind = rootNode.get("wind").get("speed").asDouble();
        return new WeatherResponseDto((int)wind,(int)temp);
    }

    @Override
    public WeatherResponseDto getWeather(String city) {
        try {
            if(city.equals("melbourne")){
                return apiCall();
            }
            throw new RuntimeException(new Exception(city +":not handled"));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
