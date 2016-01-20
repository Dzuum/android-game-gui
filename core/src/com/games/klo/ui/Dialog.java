package com.games.klo.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.games.klo.content.Assets;
import com.games.klo.helpers.LanguageManager;
import com.games.klo.interfaces.LanguageObserver;

public class Dialog extends Group implements LanguageObserver {

    private static final int CONTENT_PADDING_HORIZONTAL = 15;
    private static final int CONTENT_PADDING_VERTICAL = 10;
    private static final int BUTTON_PADDING = 10;

    private Window window;

    private Image titleIcon;
    private Label titleLabel;
    private Label contentLabel;
    private ImageTextButton buttonNegative;
    private ImageTextButton buttonPositive;

    private String titleKey;
    private String contentKey;
    private String negativeTextKey;
    private String positiveTextKey;

    private boolean isTutorial;

    public Dialog(String titleKey, String titleIconName,
                  boolean backgroundTitled, String contentKey,
                  String negativeTextKey, String negativeIconName,
                  String positiveTextKey, String positiveIconName,
                  boolean isTutorial) {
        init(titleKey, titleIconName, backgroundTitled, contentKey,
                negativeTextKey, negativeIconName, positiveTextKey, positiveIconName,
                isTutorial);
    }

    public Dialog(String titleKey, String titleIconName,
                  boolean backgroundTitled, String contentKey,
                  String negativeTextKey, String negativeIconName,
                  String positiveTextKey, String positiveIconName) {
        init(titleKey, titleIconName, backgroundTitled, contentKey,
                negativeTextKey, negativeIconName, positiveTextKey, positiveIconName,
                false);
    }

    private void init(String titleKey, String titleIconName,
                      boolean backgroundTitled, String contentKey,
                      String negativeTextKey, String negativeIconName,
                      String positiveTextKey, String positiveIconName,
                      boolean isTutorial) {
        this.titleKey = titleKey;
        this.contentKey = contentKey;
        this.negativeTextKey = negativeTextKey;
        this.positiveTextKey = positiveTextKey;
        this.isTutorial = isTutorial;

        LanguageManager.getInstance().registerObserver(this);

        Table table = new Table();

        if (isTutorial)
            window = new Window("", Assets.getUiSkin(), "brown");

        if (backgroundTitled) {
            if (window == null)
                window = new Window("", Assets.getUiSkin(), "default");

            setUpTitle(titleIconName);
        } else {
            if (window == null)
                window = new Window("", Assets.getUiSkin(), "noTitle");

            Table titleTable = createTitle(titleIconName);
            table.add(titleTable).pad(10).expand().fill();
            table.row();
        }

        window.setModal(true); //Asettaa dialogin olemaan sen Stagessa ainoa, joka saa input eventit.
        window.setMovable(false);

        if (contentKey != null) {
            contentLabel = new Label(LanguageManager.getString(contentKey), Assets.getUiSkin());
            if (isTutorial)
                contentLabel.setWrap(false);
            else
                contentLabel.setWrap(true);
            table.add(contentLabel)
                    .padLeft(CONTENT_PADDING_HORIZONTAL).padRight(CONTENT_PADDING_HORIZONTAL)
                    .padTop(CONTENT_PADDING_VERTICAL).padBottom(CONTENT_PADDING_VERTICAL)
                    .expand().fill();
            table.row();
        }

        Table buttonTable = setUpButtonTable(negativeIconName, positiveIconName);
        table.add(buttonTable).expand().fill();

        window.add(table).expand().fill();

        window.pack();
        setCenter();

        this.addActor(window);
    }

    private void setUpTitle(String titleIconName) {
        window.getTitleTable().clearChildren();

        if (titleIconName != null) {
            titleIcon = new Image(Assets.getUiSkin().getDrawable(titleIconName));
            window.getTitleTable().add(titleIcon).left()
                    .padLeft(20)
                    .width(titleIcon.getWidth()).height(titleIcon.getHeight());
        }

        if (titleKey != null) {
            titleLabel = new Label(LanguageManager.getString(titleKey), Assets.getUiSkin());
            titleLabel.setWrap(true);

            window.getTitleTable().add(titleLabel).left()
                    .padLeft(20).padBottom(6)
                    .expand().fill();
        }
    }

    private Table createTitle(String titleIconName) {
        Table titleTable = new Table();

        if (titleKey != null) {
            titleLabel = new Label(LanguageManager.getString(titleKey), Assets.getUiSkin());
            titleLabel.setWrap(true);

            if (titleIconName != null) {
                titleIcon = new Image(Assets.getUiSkin().getDrawable(titleIconName));
                titleTable.add(titleIcon).left().padLeft(20).width(titleIcon.getWidth()).height(titleIcon.getHeight());
            }

            titleTable.add(titleLabel).padLeft(20).left().width(200);
        }

        return titleTable;
    }

    private Table setUpButtonTable(String negativeIconName, String positiveIconName) {
        Table buttonTable = new Table();

        if (negativeTextKey != null) {
            if (isTutorial)
                buttonNegative = UIBuilder.createImageTextButton(negativeTextKey, negativeIconName, "brown");
            else
                buttonNegative = UIBuilder.createImageTextButton(negativeTextKey, negativeIconName);

            buttonNegative.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    close();
                }
            });

            buttonTable.add(buttonNegative).pad(BUTTON_PADDING);
        }

        if (positiveTextKey != null) {
            if (isTutorial)
                buttonPositive = UIBuilder.createImageTextButton(positiveTextKey, positiveIconName, "brown");
            else
                buttonPositive = UIBuilder.createImageTextButton(positiveTextKey, positiveIconName);

            buttonPositive.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    close();
                }
            });

            buttonTable.add(buttonPositive).pad(BUTTON_PADDING);
        }

        return buttonTable;
    }

    public void setCenter() {
        window.setPosition(
                (Gdx.graphics.getWidth() - window.getWidth()) * 0.5f,
                (Gdx.graphics.getHeight() - window.getHeight()) * 0.5f);
    }

    public void setTop() {
        window.setPosition(
                (Gdx.graphics.getWidth() - window.getWidth()) * 0.5f,
                (Gdx.graphics.getHeight() - window.getHeight() - 20.0f));
    }

    public void setBottom() {
        window.setPosition(
                (Gdx.graphics.getWidth() - window.getWidth()) * 0.5f, 20.0f);
    }

    public void addNegativeListener(ClickListener listener) {
        if (buttonNegative != null)
            buttonNegative.addListener(listener);
    }

    public void addPositiveListener(ClickListener listener) {
        if (buttonPositive != null)
            buttonPositive.addListener(listener);
    }

    private void close() {
        LanguageManager.getInstance().unregisterObserver(this);
        this.remove();
    }

    public void updateLanguage() {
        if (titleLabel != null)
            titleLabel.setText(LanguageManager.getString(titleKey));
        if (contentLabel != null)
            contentLabel.setText(LanguageManager.getString(contentKey));
        if (buttonNegative != null)
            buttonNegative.getLabel().setText(LanguageManager.getString(negativeTextKey));
        if (buttonPositive != null)
            buttonPositive.getLabel().setText(LanguageManager.getString(positiveTextKey));
    }

    public void setModalityOff() {
        window.setModal(false);
    }
}
