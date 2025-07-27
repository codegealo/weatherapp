package com.sample.wetherapp.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sample.wetherapp.dto.WeatherResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service("primary")
public class WeatherStackClient implements ExternalWeatherClient{

    @Value("${weatherstack.api}")
    private String api;

    private WeatherResponseDto apiCall(String city) throws JsonProcessingException {
        RestClient restClient = RestClient.create();

        String responseBody = restClient.get()
                .uri(api.concat(city))
                .retrieve()
                .body(String.class);

        return parseResponse(responseBody);
    }

    private WeatherResponseDto parseResponse(String responseBody) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(responseBody);
        double temp = rootNode.get("current").get("temperature").asDouble();
        double wind = rootNode.get("current").get("wind_speed").asDouble();
        return new WeatherResponseDto((int)wind,(int)temp);
    }

    @Override
    public WeatherResponseDto getWeather(String city) throws Exception {
        return apiCall(city);
    }

}
