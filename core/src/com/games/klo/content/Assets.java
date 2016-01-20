package com.games.klo.content;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Assets {

    public static final Assets instance = new Assets();

    private Skin uiSkin;

    private Assets() {
        load();
    }

    /** Oikotie apufunktio. **/
    public static Skin getUiSkin() { return instance.uiSkin; }

    public void load() {
        uiSkin = new Skin(Gdx.files.internal("Resources\\gui.json"));
    }

    public void dispose() {
        uiSkin.dispose();
    }
}
