package com.games.klo;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.input.GestureDetector;
import com.games.klo.content.Assets;
import com.games.klo.enums.GameState;
import com.games.klo.helpers.Camera2D;
import com.games.klo.helpers.GamePreferences;
import com.games.klo.helpers.InputManager;
import com.games.klo.ui.screens.BattleScreen;
import com.games.klo.ui.screens.MapScreen;
import com.games.klo.ui.windows.UIOverlay;

public class GameMain extends Game {

    public static final boolean DEBUG = false;

    private GameState gameState;

    private Camera2D camera;
    private GestureDetector cameraInput;

	private MapScreen mapScreen;
	private BattleScreen battleScreen;

	private boolean paused;

    private int currentArea;

    public Camera2D getCamera() { return camera; }
    public boolean isPaused() { return paused; }

    public int getCurrentArea() { return currentArea; }

    public boolean isBattle() { return gameState == GameState.Invasion || gameState == GameState.Defense; }

	@Override
	public void create () {
        if (DEBUG)
            Gdx.app.setLogLevel(Application.LOG_DEBUG);
        else
            Gdx.app.setLogLevel(Application.LOG_NONE);

        camera = new Camera2D(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cameraInput = new GestureDetector(camera);
        InputManager.instance.clear();
        InputManager.instance.prependInput(cameraInput);

        UIOverlay.getInstance().init(this, camera, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        mapScreen = new MapScreen(this);
        battleScreen = new BattleScreen(this);

		paused = false;
        changeToMapScreen();
        changeToMapState();
	}

    private void update() {
        camera.update(Gdx.graphics.getDeltaTime());
        UIOverlay.getInstance().update(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void render() {
        if (!paused)
            update();

        Gdx.gl.glClearColor(100 / 255.0f, 149 / 255.0f, 237 / 255.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Piirtää nykyisen screenin
        super.render();

        UIOverlay.getInstance().draw();
    }

    public void changeToAttack(int currentArea) {
        this.currentArea = currentArea;
        gameState = GameState.Invasion;

        setScreen(battleScreen);

        InputManager.instance.removeInput(cameraInput);
        InputManager.instance.appendInput(cameraInput);
    }

    public void changeToDefend(int currentArea) {
        this.currentArea = currentArea;
        gameState = GameState.Defense;

        setScreen(battleScreen);

        InputManager.instance.removeInput(cameraInput);
        InputManager.instance.appendInput(cameraInput);
    }

    public void battleWon() {
        changeToMapScreen();

        if (gameState == GameState.Invasion)
            mapScreen.showAttackSuccess();
        else if (gameState == GameState.Defense)
            mapScreen.showDefendSuccess();

        changeToMapState();
    }

    public void battleLost() {
        changeToMapScreen();

        if (gameState == GameState.Invasion)
            mapScreen.showAttackFailure();
        else if (gameState == GameState.Defense)
            mapScreen.showDefendFailure();

        changeToMapState();
    }

    private void changeToMapScreen() {
        setScreen(mapScreen);

        InputManager.instance.removeInput(cameraInput);
        InputManager.instance.appendInput(cameraInput);
    }

    private void changeToMapState() {
        gameState = GameState.Map;
    }

    private void changeToMap() {
        changeToMapScreen();
        changeToMapState();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height); //Kutsuu nykyisen screenin resizeä
        camera.resize(width, height);
        UIOverlay.getInstance().resize(width, height);
    }

	@Override
	public void dispose() {
        super.dispose(); //Kutsuu nykyisen screenin disposea
        Assets.instance.dispose();
        UIOverlay.getInstance().dispose();
	}

    @Override
    public void pause() {
        paused = true;
        GamePreferences.instance.save();
        super.pause(); //Kutsuu nykyisen screenin pausea
    }

    @Override
    public void resume() {
        GamePreferences.instance.load();
        Assets.instance.load();
        paused = false;
        super.resume(); //Kutsuu nykyisen screenin resumea
    }

    public void reset() {
        changeToMap();
        mapScreen.startTutorial();
    }
}
