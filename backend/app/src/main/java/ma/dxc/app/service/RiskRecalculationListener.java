package ma.dxc.app.service;

import ma.dxc.app.config.AsyncConfig;
import ma.dxc.app.event.IngestionCompletedEvent;
import ma.dxc.app.event.RiskConfigSubmittedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Fires RiskCalculationService.recomputeAll() whenever new data lands
 * (IngestionCompletedEvent) or the user submits a weight form
 * (RiskConfigSubmittedEvent) - recomputeAll() itself no-ops until Role and Env
 * weights both exist, so an early ingestion-only event is a safe no-op.
 *
 * Both events are published from inside a @Transactional method (RiskConfigService /
 * RiskWeightConfigService). A plain @Async @EventListener would race the publishing
 * transaction's commit - the listener's own new transaction can start querying the
 * DB on another thread before the just-saved rows are actually committed, silently
 * no-op'ing the recompute. @TransactionalEventListener(AFTER_COMMIT) waits for the
 * commit first; fallbackExecution=true keeps it working for events published with no
 * active transaction (IngestionCompletedEvent is fired from inside a plain
 * CompletableFuture.supplyAsync in JobService, off any transaction).
 */
@Component
public class RiskRecalculationListener {

    private final Logger logger = LoggerFactory.getLogger(RiskRecalculationListener.class);
    private final RiskCalculationService riskCalculationService;
    private final AtomicBoolean running = new AtomicBoolean(false);

    public RiskRecalculationListener(RiskCalculationService riskCalculationService) {
        this.riskCalculationService = riskCalculationService;
    }

    @Async(AsyncConfig.CALC_EXECUTOR)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true, classes = IngestionCompletedEvent.class)
    public void onIngestionCompleted(IngestionCompletedEvent event) {
        recompute();
    }

    @Async(AsyncConfig.CALC_EXECUTOR)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true, classes = RiskConfigSubmittedEvent.class)
    public void onRiskConfigSubmitted(RiskConfigSubmittedEvent event) {
        recompute();
    }

    private void recompute() {
        if (!running.compareAndSet(false, true)) {
            logger.info("Risk recomputation already running, skipping this trigger");
            return;
        }
        try {
            riskCalculationService.recomputeAll();
        } finally {
            running.set(false);
        }
    }
}
