package ru.practicum.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.dto.RequestHitDto;
import ru.practicum.dto.StatDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
public class StatClient {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final RestTemplate restTemplate;

    @Value("${stats-server.url:http://localhost:9090}")
    private String serverUrl;

    public StatClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void createHit(RequestHitDto requestHitDto) {
        String url = serverUrl + "/hit";

        HttpEntity<RequestHitDto> request = new HttpEntity<>(requestHitDto, defaultHeaders());

        try {
            ResponseEntity<Void> response = restTemplate.postForEntity(url, request, Void.class);
            if (response.getStatusCode() == HttpStatus.CREATED) {
                log.info("Хит успешно создан: app={}, uri={}, ip={}",
                        requestHitDto.getApp(), requestHitDto.getUri(), requestHitDto.getIp());
            }
        } catch (Exception e) {
            log.error("Ошибка при создании хита: {}", e.getMessage());
        }
    }

    public List<StatDto> getStats(LocalDateTime start, LocalDateTime end,
                                  List<String> uris, boolean unique) {

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(serverUrl + "/stats")
                .queryParam("start", start.format(FORMATTER))
                .queryParam("end", end.format(FORMATTER));

        if (uris != null && !uris.isEmpty()) {
            for (String uri : uris) {
                builder.queryParam("uris", uri);
            }
        }

        String url = builder.build().encode().toUriString();

        HttpEntity<Void> requestEntity = new HttpEntity<>(defaultHeaders());

        try {
            ResponseEntity<List<StatDto>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    new ParameterizedTypeReference<>() {}
            );

            log.info("Получена статистика, количество записей: {}",
                    response.getBody() != null ? response.getBody().size() : 0);

            return response.getBody();

        } catch (Exception e) {
            log.error("Ошибка при получении статистики: {}", e.getMessage());
            return List.of();
        }
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));
        return httpHeaders;
    }
}
