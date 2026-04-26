package ru.practicum.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.EndpointHitDto;

import java.util.Map;

@Service
public class StatsClient {
    private final RestTemplate rest;

    public StatsClient(@Value("${java-explore-with-me.url}") String serverUrl, RestTemplateBuilder builder) {
        this.rest = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }

    public void saveHit(EndpointHitDto hitDto) {
        rest.postForLocation("/hit", hitDto);
    }

    public ResponseEntity<Object> getStats(String start, String end, String[] uris, boolean unique) {
        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end,
                "uris", uris,
                "unique", unique
        );
        return rest.getForEntity("/stats?start={start}&end={end}&uris={uris}&unique={unique}", Object.class, parameters);
    }
}