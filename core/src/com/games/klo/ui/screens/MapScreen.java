package com.games.klo.ui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.games.klo.content.Areas;
import com.games.klo.content.Assets;
import com.games.klo.ui.Dialog;
import com.games.klo.GameMain;
import com.games.klo.helpers.GamePreferences;
import com.games.klo.helpers.InputManager;
import com.games.klo.ui.UIBuilder;
import com.games.klo.ui.windows.TroopsOverviewWindow;

public class MapScreen implements Screen {

    private GameMain game;
    private Stage worldStage;
    private Stage uiStage;

    private Button citadelButton;
    private Image citadelGlow;

    private Button[] areaButtons;
    private Image[] areaGlows;

    float glowFactorMin = 0.4f;
    float glowFactorMax = 1.0f;
    float glowFactorCurr = 1.0f;
    float glowFactor = -1.0f;
    float glowSpeed = 0.8f;

    Dialog tutorialDialog;
    int currentTutorialIndex;

    public MapScreen(GameMain game) {
        this.game = game;
    }

    public void show() {
        worldStage = new Stage(new StretchViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        uiStage = new Stage(new StretchViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        worldStage.getViewport().setCamera(game.getCamera());

        InputManager.instance.appendInput(uiStage);
        InputManager.instance.appendInput(worldStage);

        Image background = new Image(new Texture(Gdx.files.internal("Backgrounds\\Map.png")));
        worldStage.addActor(background);

        game.getCamera().setMaxViewArea(background.getWidth(), background.getHeight());
        setCameraStartPosition();

        initCitadel();
        initAreas();

        if (!GamePreferences.instance.isTutorialCompleted())
            startTutorial();
        else
            afterTutorial();
    }

    public void hide() {
        InputManager.instance.removeInput(uiStage);
        InputManager.instance.removeInput(worldStage);

        dispose();
    }

    public void render(float delta) {
        if (!GamePreferences.instance.isTutorialCompleted()) {// && !tutorialStarted) {
            worldStage.act(delta);
            uiStage.act(delta);
        } else if (!game.isPaused()) {
            worldStage.act(delta);
            uiStage.act(delta);

            animateGlows();
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

    public void showAttackSuccess() {
        uiStage.addActor(UIBuilder.createAttackSuccessDialog());
    }

    public void showAttackFailure() {
        uiStage.addActor(UIBuilder.createAttackFailureDialog());
    }

    public void showDefendSuccess() {
        uiStage.addActor(UIBuilder.createDefendSuccessDialog());
    }

    public void showDefendFailure() {
        uiStage.addActor(UIBuilder.createDefendFailureDialog());
    }

    private void setCameraStartPosition() {
        float x = game.getCamera().getMaxViewArea().x * 0.5f;
        game.getCamera().setPosition(x, 0);
    }

    private void initCitadel() {
        citadelGlow = new Image(Assets.getUiSkin().getDrawable("CitadelGlow"));
        citadelButton = new Button(Assets.getUiSkin(), "citadel");

        citadelGlow.setPosition(
                Areas.getCitadelX() -
                        (citadelGlow.getWidth() - citadelButton.getWidth()) * 0.5f,
                Areas.getCitadelY() -
                        (citadelGlow.getHeight() - citadelButton.getHeight()) * 0.5f);
        citadelButton.setPosition(Areas.getCitadelX(), Areas.getCitadelY());

        worldStage.addActor(citadelGlow);
        worldStage.addActor(citadelButton);
    }

    private void initAreas() {
        areaButtons = new Button[Areas.COUNT];
        areaGlows = new Image[Areas.COUNT];

        for (int i = 0; i < Areas.COUNT; i++) {
            areaButtons[i] = new Button(Assets.getUiSkin(), "area");
            areaGlows[i] = new Image(Assets.getUiSkin().getDrawable("AreaGlow"));

            areaGlows[i].setPosition(
                    Areas.getAreaX(i) -
                            (areaGlows[i].getWidth() - areaButtons[i].getWidth()) * 0.5f,
                    Areas.getAreaY(i) -
                            (areaGlows[i].getHeight() - areaButtons[i].getHeight()) * 0.5f);
            areaButtons[i].setPosition(Areas.getAreaX(i), Areas.getAreaY(i));

            worldStage.addActor(areaGlows[i]);
            worldStage.addActor(areaButtons[i]);
        }

        updateGlowVisibility();
    }

    private void setCitadelTutorialListener() {
        final MapScreen instance = this;

        citadelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                TroopsOverviewWindow window = new TroopsOverviewWindow(uiStage, instance);
                window.setModalityOff();
                uiStage.addActor(window);

                removeListeners();
                setGlowsOff();

                showNextTutorial();
            }
        });
    }

    private void setCitadelListener() {
        citadelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                uiStage.addActor(new TroopsOverviewWindow(uiStage));
            }
        });
    }

    private void removeListeners() {
        citadelButton.clearListeners();
        for (Button button : areaButtons)
            button.clearListeners();
    }

    private void setAreaListeners() {
        for (int i = 0; i < Areas.COUNT; i++) {
            final int currentArea = i;
            areaButtons[i].addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (!Areas.isCaptured(currentArea) && Areas.canAttack(currentArea)) {
                        Dialog dlg = UIBuilder.createAttackDialog();

                        //Hyökkäämisen listeneri
                        dlg.addPositiveListener(new ClickListener() {
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                game.changeToAttack(currentArea);
                            }
                        });

                        uiStage.addActor(dlg);
                    } else if (Areas.isCaptured(currentArea) && Areas.canEnemyAttack(currentArea)) {
                        Dialog dlg = UIBuilder.createDefendDialog();

                        //Puolustamisen listeneri
                        dlg.addPositiveListener(new ClickListener() {
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                game.changeToDefend(currentArea);
                            }
                        });

                        uiStage.addActor(dlg);
                    }
                }
            });
        }
    }

    //Päivittää hohdon ja niiden näkymisen
    private void animateGlows() {
        if (GamePreferences.instance.animateAreas) {
            glowFactorCurr += Gdx.graphics.getDeltaTime() * glowFactor * glowSpeed;
            if (glowFactorCurr <= glowFactorMin) {
                glowFactorCurr = glowFactorMin;
                glowFactor = 1.0f;
            } else if (glowFactorCurr >= glowFactorMax) {
                glowFactorCurr = glowFactorMax;
                glowFactor = -1.0f;
            }
        } else {
            glowFactorCurr = 1.0f;
        }

        citadelGlow.setColor(1, 1, 1, glowFactorCurr);
        for (int i = 0; i < areaGlows.length; i++) {
            areaGlows[i].setVisible(false);

            if (Areas.canEnemyAttack(i)) {
                areaGlows[i].setVisible(true);
                areaGlows[i].setColor(1, 0.0f, 0.0f, glowFactorCurr);
            } else if (Areas.canAttack(i)) {
                areaGlows[i].setVisible(true);
                areaGlows[i].setColor(1, 1, 1, glowFactorCurr);
            }
        }
    }

    private void updateGlowVisibility() {
        for (int i = 0; i < areaGlows.length; i++) {
            areaGlows[i].setVisible(false);

            if (Areas.canAttack(i))
                areaGlows[i].setVisible(true);
        }
    }

    /** Käytetään tutoriaalin kanssa. **/
    private void setGlowsOff() {
        for (Image image : areaGlows)
            image.setVisible(false);

        citadelGlow.setVisible(false);
    }

    /** Käytetään tutoriaalin kanssa. **/
    private void showCitadelGlow() {
        citadelGlow.setVisible(true);
    }

    public void startTutorial() {
        removeListeners();
        setGlowsOff();

        currentTutorialIndex = 0;
        showNextTutorial();
    }

    private void afterTutorial() {
        setCitadelListener();
        setAreaListeners();
        showCitadelGlow();
    }

    public void showNextTutorial() {
        currentTutorialIndex++;

        if (tutorialDialog != null)
            tutorialDialog.remove();

        if (currentTutorialIndex == 1 || currentTutorialIndex == 3
                || currentTutorialIndex == 5 || currentTutorialIndex == 6
                || currentTutorialIndex == 7 || currentTutorialIndex >= 9) {
            tutorialDialog = new Dialog(
                    null, null,
                    false, "tutorial" + currentTutorialIndex,
                    null, null,
                    "continue", null,
                    true);
        } else {
            tutorialDialog = new Dialog(
                    null, null,
                    false, "tutorial" + currentTutorialIndex,
                    null, null,
                    null, null,
                    true);
            tutorialDialog.setModalityOff();
        }

        if (currentTutorialIndex == 1)
            tutorialDialog.setCenter();
        else if (currentTutorialIndex == 2 || currentTutorialIndex == 8)
            tutorialDialog.setTop();
        else
            tutorialDialog.setBottom();

        //Seuraavaa ei lisätä, jos nappeja ei ole
        tutorialDialog.addPositiveListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (currentTutorialIndex == 1) {
                    setCitadelTutorialListener();
                    showCitadelGlow();
                }

                if (currentTutorialIndex < 10)
                    showNextTutorial();
                else {
                    GamePreferences.instance.setTutorialCompleted();
                    afterTutorial();
                }
            }
        });

        uiStage.addActor(tutorialDialog);
    }
}
