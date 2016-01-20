package com.games.klo.interfaces;

public interface IObservable {
    void registerObserver(IObserver observer);
    void unregisterObserver(IObserver observer);
    void notifyObservers();
}
