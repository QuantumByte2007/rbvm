package ma.dxc.app.controller;

import ma.dxc.app.config.AsyncConfig;
import ma.dxc.app.dto.DashboardMetricsDto;
import ma.dxc.app.event.RiskRecalculationCompletedEvent;
import ma.dxc.app.service.DashboardMetricsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

// SSE is inherently a REST/streaming concern - no MVC twin for the stream itself,
// only for the dashboard page shell (see DashboardController).
@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardSseController {

    private static final long NO_TIMEOUT = 0L;

    private final Logger logger = LoggerFactory.getLogger(DashboardSseController.class);
    private final DashboardMetricsService metricsService;
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public DashboardSseController(DashboardMetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @GetMapping("/metrics")
    public ResponseEntity<DashboardMetricsDto> getMetrics() {
        return ResponseEntity.ok(metricsService.computeMetrics());
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream() {
        SseEmitter emitter = new SseEmitter(NO_TIMEOUT);
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(e -> emitters.remove(emitter));
        return emitter;
    }

    // RiskCalculationService publishes this from inside its own @Transactional
    // recomputeAll() - AFTER_COMMIT avoids reading the DB before those Host updates
    // are actually committed (same race as RiskRecalculationListener).
    @Async(AsyncConfig.CALC_EXECUTOR)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true, classes = RiskRecalculationCompletedEvent.class)
    public void onRecalculationCompleted(RiskRecalculationCompletedEvent event) {
        if (emitters.isEmpty()) {
            return;
        }
        DashboardMetricsDto metrics = metricsService.computeMetrics();
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("metrics").data(metrics, MediaType.APPLICATION_JSON));
            } catch (IOException e) {
                logger.debug("Dropping stale dashboard SSE connection", e);
                emitters.remove(emitter);
            }
        }
    }
}
