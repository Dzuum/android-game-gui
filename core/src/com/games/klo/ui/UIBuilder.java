package com.games.klo.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.games.klo.content.Assets;
import com.games.klo.helpers.LanguageManager;

public class UIBuilder {

    /** Luo napin, jossa on vasemmalla ikoni ja oikealla teksti. **/
    public static ImageTextButton createImageTextButton(String textKey, String iconName, String buttonStyle) {
        String text = LanguageManager.getString(textKey);
        if (text == null)
            text = "KEY" + textKey;

        ImageTextButton button;
        if (buttonStyle == null)
            button = new ImageTextButton(text, Assets.getUiSkin());
        else
            button = new ImageTextButton(text, Assets.getUiSkin(), buttonStyle);

        if (iconName != null) {
            Image image = new Image(Assets.getUiSkin().getDrawable(iconName));

            button.getImageCell().setActor(image).size(image.getWidth(), image.getHeight())
                    .padLeft(10).padRight(10).left();
        }

        button.getLabelCell()
                .padRight(10).expand().fill().center();

        button.pack();

        return button;
    }

    public static ImageTextButton createImageTextButton(String textKey, String iconName) {
        String text = LanguageManager.getString(textKey);
        if (text == null)
            text = "KEY" + textKey;

        ImageTextButton button = new ImageTextButton(text, Assets.getUiSkin());

        if (iconName != null) {
            Image image = new Image(Assets.getUiSkin().getDrawable(iconName));

            button.getImageCell().setActor(image).size(image.getWidth(), image.getHeight())
                    .padLeft(10).padRight(10).left();
        }

        button.getLabelCell()
                .padRight(10).expand().fill().center();

        button.pack();

        return button;
    }

    /** Luo asetuksissa käytettävän napin, jota voi myös slidettää on/off. **/
    public static Stack createOptionsToggle(boolean enabled) {
        final Slider slider = new Slider(0, 1, 1, false, Assets.getUiSkin(), "optionSliderOff");
        final Label enabledLabel = new Label(LanguageManager.getString("off"), Assets.getUiSkin());

        Table labelTable = new Table();
        final Cell labelCell = labelTable.add(enabledLabel).padLeft(52)
                .expand().fillX().center().left();

        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (slider.getValue() == slider.getMinValue()) {
                    slider.setStyle(Assets.getUiSkin().get("optionSliderOff", Slider.SliderStyle.class));
                    enabledLabel.setText(LanguageManager.getString("off"));
                    labelCell.padLeft(52);
                } else {
                    slider.setStyle(Assets.getUiSkin().get("optionSliderOn", Slider.SliderStyle.class));
                    enabledLabel.setText(LanguageManager.getString("on"));
                    labelCell.padLeft(10);
                }
            }
        });

        if (enabled) {
            slider.setValue(slider.getMaxValue());
            enabledLabel.setText(LanguageManager.getString("on"));
            labelCell.padLeft(10);
        }

        Stack sliderStack = new Stack();
        sliderStack.addActor(slider);
        sliderStack.addActor(labelTable);
        for (EventListener listener : slider.getListeners())
            sliderStack.addListener(listener);

        return sliderStack;
    }

    /** Luo Tablen, jossa rahamäärä ja kultarahan ikoni. **/
    public static Table createSmallCostTable(int cost) {
        Table costTable = new Table();
        costTable.add(new Label(String.valueOf(cost), Assets.getUiSkin())).padRight(5);
        costTable.add(new Image(Assets.getUiSkin().getDrawable("Coin15"))).padTop(5);
        return costTable;
    }

    public static Dialog createGeneralQuitDialog() {
        return new Dialog(
                "quit", "InfoIcon",
                false, null,
                "no", "BackIcon",
                "yes", "ShutdownIcon");
    }

    public static Dialog createDiscardChangesDialog() {
        return new Dialog(
                "discardChanges", "InfoIcon",
                false, null,
                "no", "BackIcon", //HUOM! Suunnitelmas tää red
                "yes", "TrashIcon");
    }

    public static Dialog createChangesSavedDialog() {
        return new Dialog(
                "savedChanges", "DoneIcon",
                false, null,
                null, null,
                "ok", null);
    }

    public static Dialog createDiscardTroopsDialog() {
        return new Dialog(
                "discardTroop", "InfoIcon",
                false, null,
                "no", "BackIcon",
                "yes", "TrashIcon");
    }

    public static Dialog createTroopsSavedDialog() {
        return new Dialog(
                "savedTroop", "DoneIcon",
                false, null,
                null, null,
                "ok", null);
    }

    public static Dialog createDeleteUnitDialog() {
        return new Dialog(
                "deleteTroop", "WarningIcon",
                false, null,
                "no", "BackIcon",
                "yes", "TrashIcon");
    }

    public static Dialog createAttackSuccessDialog() {
        return new Dialog(
                "attackSuccess", "DoneIcon",
                false, null,
                null, null,
                "ok", null);
    }

    public static Dialog createAttackFailureDialog() {
        return new Dialog(
                "attackFailure", "FailIcon",
                false, null,
                null, null,
                "ok", null);
    }


    public static Dialog createDefendSuccessDialog() {
        return new Dialog(
                "defendSuccess", "DoneIcon",
                false, null,
                null, null,
                "ok", null);
    }

    public static Dialog createDefendFailureDialog() {
        return new Dialog(
                "defendFailure", "FailIcon",
                false, null,
                null, null,
                "ok", null);
    }

    public static Dialog createAttackDialog() {
        return new Dialog(
                "enemyLand", "AttackIcon",
                true, "invadeOrNo",
                "notNow", "BackIcon",
                "invade", "AttackIcon");
    }

    public static Dialog createDefendDialog() {
        return new Dialog(
                "incomingAttack", "DefendIcon",
                true, "defendOrNo",
                "notNow", "BackIcon",
                "defend", "DefendIcon");
    }

    public static Dialog createBattleQuitDialog() {
        return new Dialog(
                "quit", "InfoIcon",
                true, "battleWillPause",
                "no", "BackIcon",
                "yes", "ShutdownIcon");
    }

    public static Dialog createResetDialog() {
        return new Dialog(
                "wantToReset", "WarningIcon",
                true, "resetLose",
                "no", "BackIcon",
                "yes", "DoneIcon");
    }
}

