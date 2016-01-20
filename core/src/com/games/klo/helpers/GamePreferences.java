package com.games.klo.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.games.klo.content.Areas;
import com.games.klo.content.Troops;

public class GamePreferences {

    public static final GamePreferences instance = new GamePreferences();
    private Preferences preferences;

    public boolean animateArrows;
    public boolean animateAreas;

    private GamePreferences() {
        load();
    }

    public boolean isTutorialCompleted() {
        return preferences.contains("TutorialCompleted") || preferences.getBoolean("TutorialCompleted");
    }

    public void load() {
        //Tämä täällä, koska Androidilla voi tulla ongelmaa objektin jäädessä muistiin
        preferences = Gdx.app.getPreferences("Preferences");

        if (preferences.contains("AnimateArrows"))
            animateArrows = preferences.getBoolean("AnimateArrows");
        else
            animateArrows = true;

        if (preferences.contains("AnimateAreas"))
            animateAreas = preferences.getBoolean("AnimateAreas");
        else
            animateAreas = true;

        LanguageManager.getInstance().load(preferences);
        Troops.loadFrom(preferences);
        Areas.loadFrom(preferences);
    }

    public void save() {
        preferences.putBoolean("AnimateArrows", animateArrows);
        preferences.putBoolean("AnimateAreas", animateAreas);

        LanguageManager.getInstance().save(preferences);
        Troops.saveTo(preferences);
        Areas.saveTo(preferences);

        preferences.flush();
    }

    public void setTutorialCompleted() {
        preferences.putBoolean("TutorialCompleted", true);
    }

    public void deleteTroop(int index) {
        Troops.removeUnitFrom(preferences, index);
        save();
    }

    public void resetGame() {
        preferences.clear();
        preferences.flush();

        Troops.reset();
        Areas.reset();
    }
}
