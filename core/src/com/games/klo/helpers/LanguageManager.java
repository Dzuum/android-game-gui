package com.games.klo.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.I18NBundle;
import com.games.klo.interfaces.LanguageObservable;
import com.games.klo.interfaces.LanguageObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;

public class LanguageManager implements LanguageObservable {

    private List<LanguageObserver> observerList;

    private static LanguageManager instance;

    /** Sisältää kaikkien eri kielien merkkijonot. **/
    private I18NBundle languageBundle;

    private int currentLocaleIndex;
    public final Locale[] supportedLocales = {
            new Locale("en", "GB"), new Locale("fi", "FI")
    };

    public Locale getCurrentLocale() { return supportedLocales[currentLocaleIndex]; }

    private LanguageManager() {
        observerList = new ArrayList<LanguageObserver>();
        notifyLanguageChanged();
    }

    public static LanguageManager getInstance() {
        if (instance == null)
            instance = new LanguageManager();

        return instance;
    }

    public static String getString(String key) {
        try {
            return getInstance().languageBundle.get(key);
        } catch (MissingResourceException mre) {    //Ei löydy kyseistä avainta,
            return key;                             //palautetaan itse avain
        }
    }

    /** Ladataan nykyinen kieli. **/
    public void load(Preferences preferences) {
        if (preferences.contains("Locale"))
            currentLocaleIndex = preferences.getInteger("Locale");
        else
            currentLocaleIndex = 0;

        languageBundle = I18NBundle.createBundle(Gdx.files.internal("Resources\\strings"), getCurrentLocale());
    }

    /** Tallennetaan nykyinen kieli. **/
    public void save(Preferences preferences) {
        preferences.putInteger("Locale", currentLocaleIndex);
    }

    public String getFlagImageName() {
        return "Flag"
                + getCurrentLocale().getLanguage().substring(0, 1).toUpperCase()
                + getCurrentLocale().getLanguage().substring(1).toLowerCase();
    }

    public void prevLanguage() {
        currentLocaleIndex--;
        if (currentLocaleIndex < 0)
            currentLocaleIndex = LanguageManager.getInstance().supportedLocales.length - 1;

        notifyLanguageChanged();
    }

    public void nextLanguage() {
        currentLocaleIndex++;
        if (currentLocaleIndex >= LanguageManager.getInstance().supportedLocales.length)
            currentLocaleIndex = 0;

        notifyLanguageChanged();
    }

    @Override
    public void registerObserver(LanguageObserver observer) {
        observerList.add(observer);
    }

    @Override
    public void unregisterObserver(LanguageObserver observer) {
        observerList.remove(observer);
    }

    @Override
    public void notifyLanguageChanged() {
        //Gdx.app.log("LANGDBG", "Notifying, locale: " + getCurrentLocale());
        languageBundle = I18NBundle.createBundle(Gdx.files.internal("Resources\\strings"), getCurrentLocale());
        //Gdx.app.log("LANGDBG", "Notifying, lang: " + languageBundle.getLocale());

        for (LanguageObserver observer : observerList)
            observer.updateLanguage();
    }
}
