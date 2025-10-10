package co.edu.uniquindio.stayNow.controllers;

import co.edu.uniquindio.stayNow.dto.MetricsResponseDTO;
import co.edu.uniquindio.stayNow.services.interfaces.MetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;


@RestController
@RequestMapping("/accommodation")
@RequiredArgsConstructor
public class MetricsController {

    private final MetricsService metricsService;

    @GetMapping("/{id}/metrics")
    public ResponseEntity<MetricsResponseDTO> getAccommodationMetrics(
            @PathVariable("id") Long accommodationId,
            @RequestParam(value = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(value = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) throws Exception {

        MetricsResponseDTO metrics = metricsService.getAccommodationMetrics(accommodationId, from, to);
        return ResponseEntity.ok(metrics);
    }
}
