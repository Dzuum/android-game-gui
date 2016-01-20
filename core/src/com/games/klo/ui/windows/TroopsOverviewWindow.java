package com.games.klo.ui.windows;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.games.klo.content.Assets;
import com.games.klo.content.Troops;
import com.games.klo.interfaces.IObservable;
import com.games.klo.interfaces.IObserver;
import com.games.klo.ui.screens.MapScreen;

public class TroopsOverviewWindow extends SuperWindow implements IObserver {

    private Stage uiStage;

    private ImageButton[] slotButtons;

    public TroopsOverviewWindow(Stage uiStage) {
        super("troopsOverview", "TroopsIcon", true);

        this.uiStage = uiStage;

        addTable(createTroopsTable(null));
    }

    public TroopsOverviewWindow(Stage uiStage, MapScreen mapScreen) {
        super("troopsOverview", "TroopsIcon", true);

        this.uiStage = uiStage;

        if (mapScreen != null) //Tällöin on tutoriaalissa käytettävänä
            this.hideCloseButton();

        addTable(createTroopsTable(mapScreen));
    }

    private Table createTroopsTable(final MapScreen mapScreen) {
        Table troopsTable = new Table();
        troopsTable.defaults().padLeft(20).padTop(20); //LeftTop
        troopsTable.padRight(20).padBottom(20); //RightBottom

        slotButtons = new ImageButton[Troops.MAX_COUNT];

        for (int j = 0; j < 2; j++) {
            for (int i = 0; i < Troops.MAX_COUNT / 2; i++) {
                final int index = i + j * Troops.MAX_COUNT / 2;

                final TroopsOverviewWindow instance = this;

                slotButtons[index] = new ImageButton(Assets.getUiSkin(), "slot");
                slotButtons[index].addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        //Aukaistaan yksikkötyypin muokkausikkuna
                        TroopEditWindow editWindow = new TroopEditWindow(uiStage, index, mapScreen);
                        editWindow.registerObserver(instance);
                        uiStage.addActor(editWindow);

                        //Jos oli tutoriaalin osana, niin edistetään sitä.
                        if (mapScreen != null) {
                            mapScreen.showNextTutorial();
                            instance.remove();
                        }
                    }
                });

                troopsTable.add(slotButtons[index]);
            }

            troopsTable.row();
        }

        updateIcons();

        return troopsTable;
    }

    @Override
    public void update(IObservable observed) {
        updateIcons();
        setWindowSize();
    }

    @Override
    public void updateLanguage() {
        super.updateLanguage();
    }

    private void updateIcons() {
        for (int i = 0; i < Troops.MAX_COUNT; i++) {
            slotButtons[i].clearChildren();

            if (Troops.units[i] != null) {
                slotButtons[i].add(new Image(
                        Assets.getUiSkin().getDrawable(Troops.units[i].getIconName())));
            }
        }
    }
}
