package me.trae.clans.clan.events.energy.interfaces;

public interface IEnergyEvent {

    long getAmount();

    default boolean hasAmount() {
        return this.getAmount() > 0L;
    }
}