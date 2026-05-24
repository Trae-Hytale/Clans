package me.trae.clans.effect;

import io.github.trae.di.annotations.type.component.Service;
import me.trae.clans.ClansPlugin;
import me.trae.core.framework.shared.effect.AbstractEffectManager;

import java.util.List;

@Service
public class EffectManager extends AbstractEffectManager<ClansPlugin, Effect> {

    public EffectManager(final List<Effect> effectList) {
        super(effectList);
    }
}