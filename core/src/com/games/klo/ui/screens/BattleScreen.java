package com.games.klo.ui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.games.klo.content.Areas;
import com.games.klo.GameMain;
import com.games.klo.content.Assets;
import com.games.klo.helpers.InputManager;
import com.games.klo.helpers.LanguageManager;
import com.games.klo.interfaces.LanguageObserver;
import com.games.klo.ui.UIBuilder;
import com.games.klo.ui.windows.BuyTroopsWindow;
import com.games.klo.ui.windows.RoundInfoWindow;

public class BattleScreen implements Screen, LanguageObserver {

    public final static int BATTLE_START_MONEY = 200;

    private GameMain game;
    private Stage worldStage;
    private Stage uiStage;

    private ImageTextButton startButton;
    private ImageTextButton loseButton;
    private ImageTextButton infoButton;
    private ImageTextButton buyButton;

    private Label goldLabel;
    private int goldAmount;
    private Label livesLabel;
    private int livesAmount;

    int money;

    public BattleScreen(GameMain game) {
        this.game = game;
    }

    public void show() {
        money = BATTLE_START_MONEY;
        goldAmount = 200;
        livesAmount = 20;

        LanguageManager.getInstance().registerObserver(this);

        worldStage = new Stage(new StretchViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        uiStage = new Stage(new StretchViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        worldStage.getViewport().setCamera(game.getCamera());

        InputManager.instance.appendInput(uiStage);
        InputManager.instance.appendInput(worldStage);

        Image background = new Image(new Texture(Gdx.files.internal("Backgrounds\\Battle1.png")));
        worldStage.addActor(background);

        game.getCamera().setMaxViewArea(background.getWidth(), background.getHeight());
        setCameraStartPosition();

        startButton = UIBuilder.createImageTextButton("startRound", "StartIcon");
        loseButton = UIBuilder.createImageTextButton("loseBattle", "StartIcon");
        infoButton = UIBuilder.createImageTextButton("roundInfo", "InfoIcon");
        buyButton = UIBuilder.createImageTextButton("buyTroops", "TroopsIcon");

        Image goldImage = new Image(Assets.getUiSkin().getDrawable("Coin30"));
        goldLabel = new Label(LanguageManager.getString("goldLabel") + " " + goldAmount, Assets.getUiSkin());
        Image livesImage = new Image(Assets.getUiSkin().getDrawable("Heart30"));
        livesLabel = new Label(LanguageManager.getString("livesLabel") + " " + livesAmount, Assets.getUiSkin());

        float spacing = 10;
        startButton.setPosition(Gdx.graphics.getWidth() - startButton.getWidth() - spacing, spacing);
        loseButton.setPosition(Gdx.graphics.getWidth() - loseButton.getWidth() - spacing,
                startButton.getTop() + spacing);
        infoButton.setPosition(startButton.getX() - infoButton.getWidth() - spacing, spacing);
        buyButton.setPosition(spacing, spacing);

        goldImage.setPosition(spacing,
                Gdx.graphics.getHeight() - goldImage.getHeight() - spacing);
        goldLabel.setPosition(spacing * 2 + goldImage.getWidth(),
                Gdx.graphics.getHeight() - goldImage.getHeight() - spacing);
        livesImage.setPosition(spacing,
                goldImage.getY() - livesImage.getHeight() - spacing);
        livesLabel.setPosition(spacing * 2 + livesImage.getWidth(),
                goldImage.getY() - livesImage.getHeight() - spacing);

        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.battleWon();
                Areas.setCaptured(game.getCurrentArea());
            }
        });
        loseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.battleLost();
                Areas.setLost(game.getCurrentArea());
            }
        });
        infoButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                uiStage.addActor(new RoundInfoWindow());
            }
        });
        buyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                uiStage.addActor(new BuyTroopsWindow(money));
            }
        });

        uiStage.addActor(startButton);
        uiStage.addActor(loseButton);
        uiStage.addActor(infoButton);
        uiStage.addActor(buyButton);

        uiStage.addActor(goldImage);
        uiStage.addActor(goldLabel);
        uiStage.addActor(livesImage);
        uiStage.addActor(livesLabel);
    }

    public void hide() {
        LanguageManager.getInstance().unregisterObserver(this);

        InputManager.instance.removeInput(uiStage);
        InputManager.instance.removeInput(worldStage);

        dispose();
    }

    public void render(float delta) {
        if (!game.isPaused()) {
            worldStage.act(delta);
            uiStage.act(delta);
        }

        worldStage.draw();
        uiStage.draw();
    }

    public void resize(int width, int height) {
        worldStage.getViewport().update(width, height);
        uiStage.getViewport().update(width, height);
    }

    public void dispose() {
        worldStage.dispose();
        uiStage.dispose();
    }

    public void pause() { }
    public void resume() { }

    private void setCameraStartPosition() {
        game.getCamera().setPosition(0, 0);
    }

    @Override
    public void updateLanguage() {
        float spacing = 10;

        if (startButton != null) {
            startButton.setText(LanguageManager.getString("startRound"));
            startButton.pack();
            startButton.setPosition(Gdx.graphics.getWidth() - startButton.getWidth() - spacing, spacing);
        }

        if (loseButton != null) {
            loseButton.setText(LanguageManager.getString("loseBattle"));
            loseButton.pack();
            loseButton.setPosition(Gdx.graphics.getWidth() - loseButton.getWidth() - spacing,
                    startButton.getTop() + spacing);
        }

        if (infoButton != null) {
            infoButton.setText(LanguageManager.getString("roundInfo"));
            infoButton.pack();
            infoButton.setPosition(startButton.getX() - infoButton.getWidth() - spacing, spacing);
        }

        if (buyButton != null) {
            buyButton.setText(LanguageManager.getString("buyTroops"));
            buyButton.pack();
            buyButton.setPosition(spacing, spacing);
        }

        if (goldLabel != null)
            goldLabel.setText(LanguageManager.getString("goldLabel") + " " + goldAmount);

        if (livesLabel != null)
            livesLabel.setText(LanguageManager.getString("livesLabel") + " " + livesAmount);
    }
}
