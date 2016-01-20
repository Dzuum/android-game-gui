package com.games.klo.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;

public class InputManager {

    public static InputManager instance;
    /** Sallii useamman eri inputin lähteen käytön samanaikaisesti. **/
    private InputMultiplexer inputMultiplexer;

    static {
        instance = new InputManager();
    }

    private InputManager() {
        inputMultiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    public void clear() {
        inputMultiplexer.clear();
    }

    /** Laittaa InputProcessorin alkuun.
     * <br>Tämä tarkoittaa sitä, että kyseinen InputProcessor saa input eventit ensin. **/
    public void prependInput(InputProcessor inputProcessor) {
        inputMultiplexer.addProcessor(0, inputProcessor);
    }

    /** Laittaa loppuun kyseisen InputProcessorin.
     * <br>Tämä tarkoittaa sitä, että kyseinen InputProcessor saa input eventin
     * käsiteltäväksi viimeisenä, ellei aiempi käsitellyt sitä. **/
    public void appendInput(InputProcessor inputProcessor) {
        inputMultiplexer.addProcessor(inputProcessor);
    }

    public void removeInput(InputProcessor inputProcessor) {
        inputMultiplexer.removeProcessor(inputProcessor);
    }
}
