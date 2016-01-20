package com.games.klo.ui.windows;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.games.klo.content.Assets;
import com.games.klo.content.TroopUnit;
import com.games.klo.content.Troops;
import com.games.klo.enums.EditState;
import com.games.klo.helpers.GamePreferences;
import com.games.klo.helpers.LanguageManager;
import com.games.klo.ui.Dialog;
import com.games.klo.ui.UIBuilder;
import com.games.klo.ui.screens.BattleScreen;
import com.games.klo.ui.screens.MapScreen;

public class TroopEditWindow extends SuperWindow {

    private EditState editState;

    private Stage uiStage;

    private Table buttonTable;
    private ImageTextButton editButton;
    private ImageTextButton discardButton;
    private ImageTextButton saveButton;

    private Table iconTable;
    private Button troopIcon;
    private Button prevIconButton;
    private Button nextIconButton;
    private Table costTable;
    private Label costLabel;

    private Table statsTable;
    private Label powerLabel;
    private Label powerValueLabel;
    private Button lessPowerButton;
    private Button morePowerButton;
    private Label speedLabel;
    private Label speedValueLabel;
    private Button lessSpeedButton;
    private Button moreSpeedButton;
    private Label rangeLabel;
    private Label rangeValueLabel;
    private Button lessRangeButton;
    private Button moreRangeButton;

    private TroopUnit unit;
    private int unitIndex;
    private String originalName;
    private String originalIcon;
    private int originalPower;
    private int originalSpeed;
    private int originalRange;

    public TroopEditWindow(Stage uiStage, int unitIndex, MapScreen mapScreen) {
        this.uiStage = uiStage;
        this.unitIndex = unitIndex;

        //Katsotaan tultiinko tyhjästä paikasta vai ei
        if (Troops.units[unitIndex] == null) {
            unit = new TroopUnit();
            editState = EditState.Create;
        } else {
            unit = new TroopUnit(Troops.units[unitIndex]);
            editState = EditState.Overview;
        }

        super.init(unit.Name, "TroopsIcon", true, true);

        Skin uiSkin = Assets.getUiSkin();

        layoutIconTable();
        layoutCostTable();
        Table upperLeft = new Table();
        upperLeft.add(iconTable).center().expand();
        upperLeft.row();
        upperLeft.add(costTable).center().expand();

        layoutStatsTable();
        statsTable.padLeft(20);

        upperLeft.setBackground(uiSkin.getDrawable("Border"));
        statsTable.setBackground(uiSkin.getDrawable("Border"));

        Table upperHalf = new Table();
        upperHalf.add(upperLeft).expand().fill();
        upperHalf.add(statsTable).expand().fill();
        upperHalf.padTop(10).padLeft(20).padRight(20);

        buttonTable = new Table();

        if (editState == EditState.Create)
            updateToCreating(mapScreen);
        else
            updateToOverview();

        addTable(upperHalf);
        addTable(buttonTable);
    }

    @Override
    public void act(float delta) {
        if (editState == EditState.Create) {
            //Ei voi poistaa uutta joukkotyyppiä, koska se tulee olemaan ainoa.
            if (Troops.getUnitCount() > 0) {
                discardButton.setTouchable(Touchable.enabled);
                discardButton.setDisabled(false);
            } else {
                discardButton.setTouchable(Touchable.disabled);
                discardButton.setDisabled(true);
            }
        } else if (editState == EditState.Overview) {
            boolean canDelete = false;

            //Ei voi poistaa, jos ainoa yksikkö
            if (Troops.getUnitCount() <= 1) {
                canDelete = false;
            } else {
                //Ei voi poistaa, jos ei olemassa toista yksikköä, joka maksaa maksimissaan 200
                for (int i = 0; i < Troops.units.length; i++) {
                    if (Troops.units[i] == null)
                        continue;

                    //Onko olemassa jokin muu unit, joka maksaa enintään taistelun aloitusrahan?
                    if (unitIndex != i && Troops.units[i].getCost() <= BattleScreen.BATTLE_START_MONEY) {
                        canDelete = true;
                        break;
                    }
                }
            }

            if (canDelete) {
                discardButton.setTouchable(Touchable.enabled);
                discardButton.setDisabled(false);
            } else {
                discardButton.setTouchable(Touchable.disabled);
                discardButton.setDisabled(true);
            }
        }

        super.act(delta);
    }

    private void copyOriginals() {
        originalName = unit.Name;
        originalIcon = unit.getIconName();
        originalPower = unit.getPower();
        originalSpeed = unit.getSpeed();
        originalRange = unit.getRange();
    }

    private void layoutIconTable() {
        troopIcon = new Button(Assets.getUiSkin().getDrawable("SlotUp"));
        troopIcon.add(new Image(Assets.getUiSkin().getDrawable(unit.getIconName())));
        troopIcon.setDisabled(true);

        int spacePad = 5;

        prevIconButton = new Button(Assets.getUiSkin(), "arrowLeft60");
        nextIconButton = new Button(Assets.getUiSkin(), "arrowRight60");

        iconTable = new Table();
        iconTable.add(prevIconButton);
        iconTable.add(troopIcon).padLeft(spacePad).padRight(spacePad);
        iconTable.add(nextIconButton);

        prevIconButton.setVisible(false);
        nextIconButton.setVisible(false);
    }

    private void layoutCostTable() {
        costLabel = new Label(LanguageManager.getString("cost")
                + ": " + unit.getCost(), Assets.getUiSkin());
        Image costCoin = new Image(Assets.getUiSkin().getDrawable("Coin30"));

        costTable = new Table();
        costTable.add(costLabel).padRight(5);
        costTable.add(costCoin).padTop(5);
    }

    private void layoutStatsTable() {
        Skin uiSkin = Assets.getUiSkin();

        int arrowPadTop = 8;
        int spacePad = 8;

        statsTable = new Table();
        statsTable.columnDefaults(0).center().padRight(10).padTop(10); //Icon
        statsTable.columnDefaults(1).left().expandX().padRight(20); //Label
        statsTable.columnDefaults(2).padTop(arrowPadTop).right(); //Left
        statsTable.columnDefaults(3).center().width(20).padLeft(spacePad).padRight(spacePad); //Value
        statsTable.columnDefaults(4).padTop(arrowPadTop).left(); //Right

        //POWER
        Image powerImage = new Image(uiSkin.getDrawable("Power40"));
        powerLabel = new Label(LanguageManager.getString("power"), uiSkin);
        powerValueLabel = new Label("", uiSkin);
        lessPowerButton = new Button(uiSkin, "arrowLeft30");
        morePowerButton = new Button(uiSkin, "arrowRight30");

        statsTable.add(powerImage).width(powerImage.getWidth()).height(powerImage.getHeight());
        statsTable.add(powerLabel);
        statsTable.add(lessPowerButton);
        statsTable.add(powerValueLabel);
        statsTable.add(morePowerButton);
        statsTable.row();

        //SPEED
        Image speedImage = new Image(uiSkin.getDrawable("Speed40"));
        speedLabel = new Label(LanguageManager.getString("speed"), uiSkin);
        speedValueLabel = new Label("", uiSkin);
        lessSpeedButton = new Button(uiSkin, "arrowLeft30");
        moreSpeedButton = new Button(uiSkin, "arrowRight30");

        statsTable.add(speedImage).width(speedImage.getWidth()).height(speedImage.getHeight());
        statsTable.add(speedLabel);
        statsTable.add(lessSpeedButton);
        statsTable.add(speedValueLabel);
        statsTable.add(moreSpeedButton);
        statsTable.row();

        //RANGE
        Image rangeImage = new Image(uiSkin.getDrawable("Range40"));
        rangeLabel = new Label(LanguageManager.getString("range"), uiSkin);
        rangeValueLabel = new Label("", uiSkin);
        lessRangeButton = new Button(uiSkin, "arrowLeft30");
        moreRangeButton = new Button(uiSkin, "arrowRight30");

        statsTable.add(rangeImage).width(rangeImage.getWidth()).height(rangeImage.getHeight());
        statsTable.add(rangeLabel);
        statsTable.add(lessRangeButton);
        statsTable.add(rangeValueLabel);
        statsTable.add(moreRangeButton);

        updateValues();
        setEditListeners();
    }

    private void layoutOverviewButtonTable() {
        discardButton = new ImageTextButton(
                LanguageManager.getString("delete"),
                Assets.getUiSkin());
        discardButton.getImageCell().setActor(
                new Image(Assets.getUiSkin().getDrawable("TrashIcon")))
                .padLeft(10).padRight(10).padTop(0).padBottom(0);
        discardButton.getLabelCell()
                .padRight(10);

        editButton = new ImageTextButton(
                LanguageManager.getString("edit"),
                Assets.getUiSkin());
        editButton.getImageCell().setActor(
                new Image(Assets.getUiSkin().getDrawable("EditIcon")))
                .padLeft(10).padRight(10).padTop(0).padBottom(0);
        editButton.getLabelCell()
                .padRight(10);

        discardButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Dialog dlg = UIBuilder.createDeleteUnitDialog();

                dlg.addPositiveListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        deleteUnitEditing();
                        close();
                    }
                });

                uiStage.addActor(dlg);
            }
        });

        editButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                copyOriginals();
                updateToEditing();
            }
        });

        buttonTable.clearChildren();
        buttonTable.padTop(20).padBottom(20).padLeft(20).padRight(20);
        buttonTable.add(discardButton).center().expandX();
        buttonTable.add(editButton).center().expandX();
    }

    private void layoutCreatingButtonTable(final MapScreen mapScreen) {
        discardButton = new ImageTextButton(
                LanguageManager.getString("discardNewButton"),
                Assets.getUiSkin());
        discardButton.getImageCell().setActor(
                new Image(Assets.getUiSkin().getDrawable("TrashIcon")))
                .padLeft(10).padRight(10).padTop(0).padBottom(0);
        discardButton.getLabelCell()
                .padRight(10);

        saveButton = new ImageTextButton(
                LanguageManager.getString("saveNewButton"),
                Assets.getUiSkin());
        saveButton.getImageCell().setActor(
                new Image(Assets.getUiSkin().getDrawable("DoneIcon")))
                .padLeft(10).padRight(10).padTop(0).padBottom(0);
        saveButton.getLabelCell()
                .padRight(10);

        discardButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Dialog dlg = UIBuilder.createDiscardTroopsDialog();

                dlg.addPositiveListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        deleteUnitEditing();
                        close();
                    }
                });

                uiStage.addActor(dlg);
            }
        });

        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Dialog dlg = UIBuilder.createTroopsSavedDialog();

                dlg.addPositiveListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        saveChanges();

                        if (mapScreen == null)
                            updateToOverview();
                        else { //On osa tutoriaalia
                            close();
                            mapScreen.showNextTutorial();
                        }
                    }
                });

                uiStage.addActor(dlg);
            }
        });

        buttonTable.clearChildren();
        buttonTable.padTop(20).padBottom(20).padLeft(20).padRight(20);
        buttonTable.add(discardButton).center().expandX();
        buttonTable.add(saveButton).center().expandX();
    }

    private void layoutEditingButtonTable() {
        discardButton = new ImageTextButton(
                LanguageManager.getString("discardChangesButton"),
                Assets.getUiSkin());
        discardButton.getImageCell().setActor(
                new Image(Assets.getUiSkin().getDrawable("TrashIcon")))
                .padLeft(10).padRight(10).padTop(0).padBottom(0);
        discardButton.getLabelCell()
                .padRight(10);

        saveButton = new ImageTextButton(
                LanguageManager.getString("saveChangesButton"),
                Assets.getUiSkin());
        saveButton.getImageCell().setActor(
                new Image(Assets.getUiSkin().getDrawable("DoneIcon")))
                .padLeft(10).padRight(10).padTop(0).padBottom(0);
        saveButton.getLabelCell()
                .padRight(10);

        discardButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Dialog dlg = UIBuilder.createDiscardChangesDialog();

                dlg.addPositiveListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        revertChanges();
                        updateToOverview();
                    }
                });

                uiStage.addActor(dlg);
            }
        });

        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Dialog dlg = UIBuilder.createChangesSavedDialog();

                dlg.addPositiveListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        saveChanges();
                        updateToOverview();
                    }
                });

                uiStage.addActor(dlg);
            }
        });

        buttonTable.clearChildren();
        buttonTable.padTop(20).padBottom(20).padLeft(20).padRight(20);
        buttonTable.add(discardButton).center().expandX();
        buttonTable.add(saveButton).center().expandX();
    }

    private void setEditListeners() {
        prevIconButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                unit.prevIcon();
                updateIcon();
            }
        });

        nextIconButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                unit.nextIcon();
                updateIcon();
            }
        });

        lessPowerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                unit.lessPower();
                updateValues();
                checkViability();
            }
        });

        morePowerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                unit.morePower();
                updateValues();
                checkViability();
            }
        });

        lessSpeedButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                unit.lessSpeed();
                updateValues();
                checkViability();
            }
        });

        moreSpeedButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                unit.moreSpeed();
                updateValues();
                checkViability();
            }
        });

        lessRangeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                unit.lessRange();
                updateValues();
                checkViability();
            }
        });

        moreRangeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                unit.moreRange();
                updateValues();
                checkViability();
            }
        });
    }

    private void updateToOverview() {
        editState = EditState.Overview;

        showCloseButton();

        prevIconButton.setVisible(false);
        nextIconButton.setVisible(false);

        lessPowerButton.setVisible(false);
        morePowerButton.setVisible(false);

        lessSpeedButton.setVisible(false);
        moreSpeedButton.setVisible(false);

        lessRangeButton.setVisible(false);
        moreRangeButton.setVisible(false);

        updateIcon();
        updateValues();

        layoutOverviewButtonTable();
    }

    private void updateToCreating(MapScreen mapScreen) {
        editState = EditState.Create;

        hideCloseButton();

        prevIconButton.setVisible(true);
        nextIconButton.setVisible(true);

        lessPowerButton.setVisible(true);
        morePowerButton.setVisible(true);

        lessSpeedButton.setVisible(true);
        moreSpeedButton.setVisible(true);

        lessRangeButton.setVisible(true);
        moreRangeButton.setVisible(true);

        layoutCreatingButtonTable(mapScreen);
    }

    private void updateToEditing() {
        editState = EditState.Edit;

        hideCloseButton();

        prevIconButton.setVisible(true);
        nextIconButton.setVisible(true);

        lessPowerButton.setVisible(true);
        morePowerButton.setVisible(true);

        lessSpeedButton.setVisible(true);
        moreSpeedButton.setVisible(true);

        lessRangeButton.setVisible(true);
        moreRangeButton.setVisible(true);

        layoutEditingButtonTable();
    }

    private void deleteUnitEditing() {
        Troops.units[unitIndex] = null;
        GamePreferences.instance.deleteTroop(unitIndex);

        notifyObservers();
    }

    private void revertChanges() {
        unit.Name = originalName;
        unit.setIcon(originalIcon);
        unit.setPower(originalPower);
        unit.setSpeed(originalSpeed);
        unit.setRange(originalRange);

        notifyObservers();
    }

    private void saveChanges() {
        if (Troops.units[unitIndex] == null)
            Troops.units[unitIndex] = new TroopUnit(unit);
        else
            Troops.units[unitIndex].copyValuesFrom(unit);

        notifyObservers();
    }

    private void updateIcon() {
        troopIcon.clearChildren();
        troopIcon.add(new Image(Assets.getUiSkin().getDrawable(unit.getIconName())));
    }

    private void updateValues() {
        costLabel.setText(LanguageManager.getString("cost")
                + ": " + unit.getCost());

        if (unit.getPower() < 10)
            powerValueLabel.setText(" " + unit.getPower());
        else
            powerValueLabel.setText("" + unit.getPower());

        if (unit.getSpeed() < 10)
            speedValueLabel.setText(" " + unit.getSpeed());
        else
            speedValueLabel.setText("" + unit.getSpeed());

        if (unit.getRange() < 10)
            rangeValueLabel.setText(" " + unit.getRange());
        else
            rangeValueLabel.setText("" + unit.getRange());
    }

    private void checkViability() {
        //Katsotaan voidaanko tallentaa
        //Pitää siis olla yksi yksikkötyyppi, joka maksaa max 200
        boolean canSave = false;

        if (unit.getCost() <= BattleScreen.BATTLE_START_MONEY) {
            canSave = true;
        } else {
            for (int i = 0; i < Troops.units.length; i++) {
                if (Troops.units[i] == null)
                    continue;

                if (unitIndex != i && Troops.units[i].getCost() <= BattleScreen.BATTLE_START_MONEY) {
                    canSave = true;
                    break;
                }
            }
        }

        if (canSave) {
            saveButton.setTouchable(Touchable.enabled);
            saveButton.setDisabled(false);
        } else {
            saveButton.setTouchable(Touchable.disabled);
            saveButton.setDisabled(true);
        }
    }

    public void updateLanguage() {
        costLabel.setText(LanguageManager.getString("cost")
                + ": " + unit.getCost());
        powerLabel.setText(LanguageManager.getString("power"));
        speedLabel.setText(LanguageManager.getString("speed"));
        rangeLabel.setText(LanguageManager.getString("range"));

        if (editButton != null)
            editButton.setText(LanguageManager.getString("edit"));

        if (editState == EditState.Create) {
            if (discardButton != null)
                discardButton.setText(LanguageManager.getString("discardNewButton"));

            if (saveButton != null)
                saveButton.setText(LanguageManager.getString("saveNewButton"));
        } else {
            if (discardButton != null)
                discardButton.setText(LanguageManager.getString("discardChangesButton"));

            if (saveButton != null)
                saveButton.setText(LanguageManager.getString("saveChangesButton"));
        }

        super.updateLanguage();
    }
}
