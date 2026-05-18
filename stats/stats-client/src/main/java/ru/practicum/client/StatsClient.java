package ru.practicum.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import java.util.List;
import java.util.ArrayList;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class StatsClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final ParameterizedTypeReference<List<ViewStatsDto>> STATS_TYPE_REFERENCE =
            new ParameterizedTypeReference<List<ViewStatsDto>>() {
            };

    public StatsClient(@Value("${java-explore-with-me.url:http://localhost:9090}") String serverUrl,
                       RestTemplateBuilder builder) {
        this.baseUrl = serverUrl;
        this.restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .build();
    }

    public void saveHit(String app, String uri, String ip, LocalDateTime timestamp) {
        EndpointHitDto hitDto = EndpointHitDto.builder()
                .app(app)
                .uri(uri)
                .ip(ip)
                .timestamp(timestamp.format(FORMATTER))
                .build();

        restTemplate.postForLocation("/hit", hitDto);
    }

    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        String encodedStart = URLEncoder.encode(start.format(FORMATTER), StandardCharsets.UTF_8);
        String encodedEnd = URLEncoder.encode(end.format(FORMATTER), StandardCharsets.UTF_8);

        StringBuilder urlBuilder = new StringBuilder(baseUrl);
        urlBuilder.append("/stats")
                .append("?start=").append(encodedStart)
                .append("&end=").append(encodedEnd)
                .append("&unique=").append(unique);

        if (uris != null && !uris.isEmpty()) {
            for (String uri : uris) {
                String encodedUri = URLEncoder.encode(uri, StandardCharsets.UTF_8);
                urlBuilder.append("&uris=").append(encodedUri);
            }
        }

        String finalUrl = urlBuilder.toString();
        log.info("Sending GET request to stats-server: {}", finalUrl);

        URI targetUri = URI.create(finalUrl);

        try {
            ResponseEntity<List<ViewStatsDto>> response = restTemplate.exchange(
                    targetUri,
                    HttpMethod.GET,
                    null,
                    STATS_TYPE_REFERENCE
            );

            return response.getBody();
        } catch (RestClientException e) {
            log.error("Failed to execute getStats due to REST error: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
}