package com.games.klo.ui.windows;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.games.klo.content.Assets;
import com.games.klo.interfaces.IObservable;
import com.games.klo.interfaces.IObserver;
import com.games.klo.helpers.LanguageManager;
import com.games.klo.interfaces.LanguageObserver;

import java.util.ArrayList;
import java.util.List;

public class SuperWindow extends Group implements IObservable, LanguageObserver {

    //NOT GOODNESS
    private static final int TITLE_PAD_HORIZONTAL = 20;

    private List<IObserver> observerList;

    private Window window;

    private Image titleIcon;
    private Button closeButton;
    private Label titleLabel;

    private String titleKey;

    public SuperWindow() { }

    public SuperWindow(String titleKey, String titleIconName,
                            boolean closeable) {
        init(titleKey, titleIconName, closeable, true);
    }

    public SuperWindow(String titleKey, String titleIconName,
                       boolean closeable, boolean titleSeparated) {
        init(titleKey, titleIconName, closeable, titleSeparated);
    }

    public void init(String titleKey, String titleIconName,
            boolean closeable, boolean titleSeparated) {
        this.titleKey = titleKey;

        LanguageManager.getInstance().registerObserver(this);

        observerList = new ArrayList<IObserver>();

        if (titleSeparated)
            window = new Window("", Assets.getUiSkin(), "default");
        else
            window = new Window("", Assets.getUiSkin(), "noTitle");

        window.setModal(true); //Asettaa ikkunan olemaan sen Stagessa ainoa, joka saa input eventit.
        window.setMovable(false);

        setUpTitle(titleIconName);

        if (closeable)
            setUpCloseButton();

        setWindowSize();
        setCenter();

        this.addActor(window);
    }

    private void setUpTitle(String titleIconName) {
        window.getTitleTable().clearChildren();

        if (titleIconName != null) {
            titleIcon = new Image(Assets.getUiSkin().getDrawable(titleIconName));
            window.getTitleTable().add(titleIcon).left()
                    .padLeft(TITLE_PAD_HORIZONTAL)
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

    private void setUpCloseButton() {
        closeButton = new Button(Assets.getUiSkin().get("windowClose", Button.ButtonStyle.class));

        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                close();
            }
        });

        this.addActor(closeButton);
    }

    public void addTable(Table table) {
        window.add(table).expand().fill();
        window.row();

        setWindowSize();
        setCenter();
    }

    public void addCloseListener(ClickListener listener) {
        if (closeButton != null)
            closeButton.addListener(listener);
    }

    protected void close() {
        LanguageManager.getInstance().unregisterObserver(this);
        this.remove();
    }

    protected void showCloseButton() {
        closeButton.setVisible(true);
    }

    protected void hideCloseButton() {
        closeButton.setVisible(false);
    }

    /** Vähän purkkaa, mutta oli pakko Stage2d:n outouksien takia näin tehdä. **/
    public void setWindowSize() {
        window.pack();

        float titleIconWidth = titleIcon == null ? 0 : titleIcon.getWidth();
        float titleWidth = window.getTitleLabel().getWidth() + titleIconWidth + TITLE_PAD_HORIZONTAL * 2;

        if (titleWidth > window.getWidth())
            window.setWidth(titleWidth);

        setCenter();
        setCloseButtonPosition();
    }

    private void setCenter() {
        window.setPosition(
                (Gdx.graphics.getWidth() - window.getWidth()) * 0.5f,
                (Gdx.graphics.getHeight() - window.getHeight()) * 0.5f);

        setCloseButtonPosition();
    }

    private void setCloseButtonPosition() {
        if (closeButton == null)
            return;

        closeButton.setPosition(
                window.getRight() - closeButton.getWidth(),
                window.getTop() - closeButton.getHeight());
        closeButton.toFront();
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (closeButton != null)
            closeButton.toFront();
    }

    @Override
    public void registerObserver(IObserver observer) {
        observerList.add(observer);
    }

    @Override
    public void unregisterObserver(IObserver observer) {
        observerList.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (IObserver observer : observerList)
            observer.update(this);
    }

    /**
     * Should be called last in overriding methods because of setWindowSize().
     */
    public void updateLanguage() {
        if (titleKey != null)
            titleLabel.setText(LanguageManager.getString(titleKey));

        setWindowSize();
    }

    public void setModalityOff() {
        window.setModal(false);
    }
}
