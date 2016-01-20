package com.games.klo.interfaces;

public interface LanguageObservable {
    void registerObserver(LanguageObserver observer);
    void unregisterObserver(LanguageObserver observer);
    void notifyLanguageChanged();
}
