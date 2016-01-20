package com.games.klo.ui.windows;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.games.klo.GameMain;
import com.games.klo.content.Assets;
import com.games.klo.helpers.Camera2D;
import com.games.klo.helpers.GamePreferences;
import com.games.klo.helpers.InputManager;
import com.games.klo.helpers.LanguageManager;
import com.games.klo.interfaces.LanguageObserver;
import com.games.klo.ui.Dialog;
import com.games.klo.ui.UIBuilder;

/** Hoitaa asetukset ja liikuttamisen nuolet. **/
public class UIOverlay implements Disposable, LanguageObserver {

    private static UIOverlay instance = null;

    private GameMain game;

    private Stage stage;
    private Camera2D worldCamera;

    private Button gearButton;
    private float gearTextureOffset;
    private boolean gearPressed = true;

    private ImageTextButton optionsButton;
    private ImageTextButton quitButton;

    private SuperWindow optionsWindow;
    private Table content;
    private Cell paneCell;

    private Label languageLabel;
    private Image flagImage;
    private Label arrowAnimLabel;
    private Label areasAnimLabel;
    private ImageTextButton resetButton;

    private ImageButton arrowButtonLeft;
    private Vector2 arrowLeftPosition;
    private ImageButton arrowButtonDown;
    private Vector2 arrowDownPosition;
    private ImageButton arrowButtonRight;
    private Vector2 arrowRightPosition;
    private ImageButton arrowButtonUp;
    private Vector2 arrowUpPosition;
    private float arrowsFromEdge;
    private float arrowOffsetCurr;
    private double arrowOffsetTimer;
    private float arrowSpeed; //% of viewportWidth in a second

    private UIOverlay() { }

    public void init(GameMain game, Camera2D worldCamera, int viewportWidth, int viewportHeight) {
        this.game = game;

        stage = new Stage(new StretchViewport(viewportWidth, viewportHeight));
        this.worldCamera = worldCamera;

        LanguageManager.getInstance().registerObserver(this);

        gearPressed = false;

        gearTextureOffset = 0.02f;
        initGearButton(viewportWidth, viewportHeight);

        arrowsFromEdge = 0.01f;
        arrowOffsetCurr = 2.0f;
        arrowOffsetTimer = 0;
        arrowSpeed = 0.75f * viewportWidth;
        initArrows(viewportWidth, viewportHeight);

        stage.addActor(gearButton);

        stage.addActor(arrowButtonLeft);
        stage.addActor(arrowButtonDown);
        stage.addActor(arrowButtonRight);
        stage.addActor(arrowButtonUp);

        InputManager.instance.prependInput(stage);
    }

    public static UIOverlay getInstance() {
        if (instance == null)
            instance = new UIOverlay();

        return instance;
    }

    public void resize(int viewportWidth, int viewportHeight) {
        stage.getViewport().update(viewportWidth, viewportHeight, false);
    }

    public void update(float elapsed) {
        updateArrows(elapsed);
        stage.act(elapsed);
    }

    public void draw() {
        if (gearPressed)
            gearButton.setDisabled(true);
        else
            gearButton.setDisabled(false);

        stage.draw();
    }

    @Override
    public void dispose() {
        LanguageManager.getInstance().unregisterObserver(this);
        stage.dispose();
    }

    private void initGearButton(int viewportWidth, int viewportHeight) {
        gearButton = new Button(Assets.getUiSkin(), "optionsGear");

        gearButton.setPosition(
                (1 - gearTextureOffset) * viewportWidth - gearButton.getWidth(),
                (1 - gearTextureOffset) * viewportHeight - gearButton.getHeight());

        gearButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                createWindowOptionsOrQuit();
                gearPressed = true;
            }
        });
    }

    private void createWindowOptionsOrQuit() {
        final SuperWindow window = new SuperWindow(null, null, true, false);

        window.addCloseListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gearPressed = false;
            }
        });

        Table content = new Table();
        content.defaults().expand().fill().padLeft(20).padRight(20);

        optionsButton = UIBuilder.createImageTextButton("options", "OptionsGear");
        quitButton = UIBuilder.createImageTextButton("quitButton", "ShutdownIcon");

        optionsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                window.remove();
                createOptionsWindow();
            }
        });

        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Dialog dlg;
                if (game.isBattle())
                    dlg = UIBuilder.createBattleQuitDialog();
                else
                    dlg = UIBuilder.createGeneralQuitDialog();

                dlg.addPositiveListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        Gdx.app.exit();
                    }
                });

                stage.addActor(dlg);
            }
        });

        content.add(optionsButton).padTop(40).padBottom(20);
        content.row();
        content.add(quitButton).padBottom(20);

        window.addTable(content);
        stage.addActor(window);
    }

    private void createOptionsWindow() {
        optionsWindow = new SuperWindow("options", "OptionsGear", true);

        optionsWindow.addCloseListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gearPressed = false;
            }
        });

        content = new Table();
        content.padLeft(10).padRight(0);

        content.columnDefaults(0).expand().right().padLeft(20).padTop(20).padBottom(10).padRight(20);
        content.columnDefaults(1).center().padBottom(10).padTop(10).padRight(20);

        languageLabel = new Label(LanguageManager.getString("language"), Assets.getUiSkin());
        updateFlag();
        Button prevLanguageButton = new Button(Assets.getUiSkin(), "arrowLeft30");
        Button nextLanguageButton = new Button(Assets.getUiSkin(), "arrowRight30");

        prevLanguageButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                LanguageManager.getInstance().prevLanguage();
                updateFlag();
            }
        });
        nextLanguageButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                LanguageManager.getInstance().nextLanguage();
                updateFlag();
            }
        });

        Table languageTable = new Table();
        languageTable.add(prevLanguageButton).padRight(10);
        languageTable.add(flagImage).width(flagImage.getWidth());
        languageTable.add(nextLanguageButton).padLeft(10);

        arrowAnimLabel = new Label(
                LanguageManager.getString("animArrow"), Assets.getUiSkin());
        areasAnimLabel = new Label(
                LanguageManager.getString("animAreas"), Assets.getUiSkin());

        final Stack arrowStack = UIBuilder.createOptionsToggle(GamePreferences.instance.animateArrows);
        arrowStack.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GamePreferences.instance.animateArrows = ((Slider) actor).getValue() == ((Slider) actor).getMaxValue();
            }
        });
        final Stack areasStack = UIBuilder.createOptionsToggle(GamePreferences.instance.animateAreas);
        areasStack.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GamePreferences.instance.animateAreas = ((Slider) actor).getValue() == ((Slider) actor).getMaxValue();
            }
        });

        resetButton = UIBuilder.createImageTextButton("resetGame", "WarningIcon");
        resetButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Dialog dlg = UIBuilder.createResetDialog();

                dlg.addPositiveListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        resetGame();
                    }
                });

                stage.addActor(dlg);
            }
        });
        Table resetTable = new Table();
        resetTable.add(resetButton).left();

        content.add(languageLabel).padBottom(21);
        content.add(languageTable);
        content.row().padBottom(0).padTop(0);
        content.add(arrowAnimLabel).padBottom(4);
        content.add(arrowStack).width(100);
        content.row();
        content.add(areasAnimLabel).padBottom(24);
        content.add(areasStack).width(100);
        content.row();
        content.add(resetTable).colspan(2).center();

        //Skrollaus mahdollisuus
        ScrollPane pane = new ScrollPane(content);
        pane.getStyle().vScrollKnob = Assets.getUiSkin().getDrawable("SlotAddUp");
        pane.getStyle().vScroll = Assets.getUiSkin().getDrawable("SliderBackgroundOn");
        pane.setScrollingDisabled(true, false);
        pane.setOverscroll(false, false);
        pane.setFadeScrollBars(false);
        pane.setFlickScroll(false);

        Table table = new Table();
        paneCell = table.add(pane).width(content.getPrefWidth())
                .height(200)
                .pad(10);

        table.setBackground(Assets.getUiSkin().getDrawable("Border"));

        optionsWindow.addTable(table);

        stage.addActor(optionsWindow);
    }

    private void initArrows(int viewportWidth, int viewportHeight) {
        ImageButtonStyle arrowStyle = new ImageButtonStyle();
        arrowStyle.imageUp = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("GUI\\ArrowUpUp.png"))));
        arrowStyle.imageDown = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("GUI\\ArrowUpDown.png"))));

        arrowButtonLeft = new ImageButton(arrowStyle);
        arrowButtonLeft.setTransform(true);
        arrowButtonLeft.setRotation(90);
        arrowLeftPosition = new Vector2(
                arrowButtonLeft.getHeight() + viewportWidth * arrowsFromEdge,
                viewportHeight * 0.5f - arrowButtonLeft.getWidth() * 0.5f);
        arrowButtonLeft.setPosition(arrowLeftPosition.x, arrowLeftPosition.y);

        arrowButtonDown = new ImageButton(arrowStyle);
        arrowButtonDown.setTransform(true);
        arrowButtonDown.setRotation(180);
        arrowDownPosition = new Vector2(
                viewportWidth * 0.5f + arrowButtonDown.getWidth() * 0.5f,
                arrowButtonDown.getHeight() + viewportHeight * arrowsFromEdge);
        arrowButtonDown.setPosition(arrowDownPosition.x, arrowDownPosition.y);

        arrowButtonRight = new ImageButton(arrowStyle);
        arrowButtonRight.setTransform(true);
        arrowButtonRight.setRotation(270);
        arrowRightPosition = new Vector2(
                viewportWidth - arrowButtonRight.getHeight() - viewportWidth * arrowsFromEdge,
                viewportHeight * 0.5f + arrowButtonRight.getWidth() * 0.5f);
        arrowButtonRight.setPosition(arrowRightPosition.x, arrowRightPosition.y);

        arrowButtonUp = new ImageButton(arrowStyle);
        arrowButtonUp.setTransform(true);
        arrowButtonUp.setRotation(0);
        arrowUpPosition = new Vector2(
                viewportWidth * 0.5f - arrowButtonUp.getWidth() * 0.5f,
                viewportHeight - arrowButtonUp.getHeight() - viewportHeight * arrowsFromEdge);
        arrowButtonUp.setPosition(arrowUpPosition.x, arrowUpPosition.y);
    }

    private void updateArrows(float elapsed) {
        arrowOffsetTimer += elapsed * 3;
        arrowOffsetCurr = (float)Math.sin(arrowOffsetTimer) / 100.0f;

        float currOffsetX = arrowOffsetCurr * worldCamera.viewportWidth;
        float currOffsetY = arrowOffsetCurr * worldCamera.viewportHeight;

        arrowButtonLeft.setPosition(arrowLeftPosition.x, arrowLeftPosition.y);
        arrowButtonDown.setPosition(arrowDownPosition.x, arrowDownPosition.y);
        arrowButtonRight.setPosition(arrowRightPosition.x, arrowRightPosition.y);
        arrowButtonUp.setPosition(arrowUpPosition.x, arrowUpPosition.y);

        if (arrowButtonLeft.isPressed() && GamePreferences.instance.isTutorialCompleted())
            worldCamera.moveBy(-arrowSpeed * elapsed, 0);
        else if (GamePreferences.instance.animateArrows)
            arrowButtonLeft.setPosition(arrowLeftPosition.x + currOffsetX, arrowLeftPosition.y);

        if (arrowButtonDown.isPressed() && GamePreferences.instance.isTutorialCompleted())
            worldCamera.moveBy(0, -arrowSpeed * elapsed);
        else if (GamePreferences.instance.animateArrows)
            arrowButtonDown.setPosition(arrowDownPosition.x, arrowDownPosition.y + currOffsetY);

        if (arrowButtonRight.isPressed() && GamePreferences.instance.isTutorialCompleted())
            worldCamera.moveBy(arrowSpeed * elapsed, 0);
        else if (GamePreferences.instance.animateArrows)
            arrowButtonRight.setPosition(arrowRightPosition.x - currOffsetX, arrowRightPosition.y);

        if (arrowButtonUp.isPressed() && GamePreferences.instance.isTutorialCompleted())
            worldCamera.moveBy(0, arrowSpeed * elapsed);
        else if (GamePreferences.instance.animateArrows)
            arrowButtonUp.setPosition(arrowUpPosition.x, arrowUpPosition.y - currOffsetY);

        if (arrowButtonLeft.isVisible() && !worldCamera.canMoveLeft())
            arrowButtonLeft.setVisible(false);
        else if (!arrowButtonLeft.isVisible() && worldCamera.canMoveLeft())
            arrowButtonLeft.setVisible(true);

        if (arrowButtonDown.isVisible() && !worldCamera.canMoveDown())
            arrowButtonDown.setVisible(false);
        else if (!arrowButtonDown.isVisible() && worldCamera.canMoveDown())
            arrowButtonDown.setVisible(true);

        if (arrowButtonRight.isVisible() && !worldCamera.canMoveRight())
            arrowButtonRight.setVisible(false);
        else if (!arrowButtonRight.isVisible() && worldCamera.canMoveRight())
            arrowButtonRight.setVisible(true);

        if (arrowButtonUp.isVisible() && !worldCamera.canMoveUp())
            arrowButtonUp.setVisible(false);
        else if (!arrowButtonUp.isVisible() && worldCamera.canMoveUp())
            arrowButtonUp.setVisible(true);
    }

    private void updateFlag() {
        if (flagImage == null)
            flagImage = new Image(Assets.getUiSkin(),
                    LanguageManager.getInstance().getFlagImageName());
        else
            flagImage.setDrawable(Assets.getUiSkin(),
                    LanguageManager.getInstance().getFlagImageName());
    }

    public void updateLanguage() {
        if (optionsButton != null)
            optionsButton.setText(LanguageManager.getString("options"));
        if (quitButton != null)
            quitButton.setText(LanguageManager.getString("quitButton"));
        if (languageLabel != null)
            languageLabel.setText(LanguageManager.getString("language"));
        if (arrowAnimLabel != null)
            arrowAnimLabel.setText(LanguageManager.getString("animArrow"));
        if (areasAnimLabel != null)
            areasAnimLabel.setText(LanguageManager.getString("animAreas"));
        if (resetButton != null)
            resetButton.setText(LanguageManager.getString("resetGame"));

        paneCell.width(content.getPrefWidth());
        optionsWindow.setWindowSize();
    }

    private void resetGame() {
        GamePreferences.instance.resetGame();
        game.reset();
    }
}
