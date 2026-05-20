package me.trae.clans.effect;

import io.github.trae.di.annotations.method.Scheduler;
import io.github.trae.di.annotations.type.component.Service;
import me.trae.clans.ClansPlugin;
import me.trae.core.framework.shared.effect.AbstractEffectManager;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class EffectManager extends AbstractEffectManager<ClansPlugin, Effect> {

    public EffectManager(final List<Effect> effectList) {
        super(effectList);
    }

    @Scheduler(period = AbstractEffectManager.EXPIRATION_SCHEDULER_PERIOD, unit = TimeUnit.MILLISECONDS)
    public void onScheduler() {
        this.processExpirations();
    }
}