package com.games.klo.content;

import com.badlogic.gdx.Preferences;

public class Troops {

    public static final int MAX_COUNT = 6;

    public static TroopUnit[] units = new TroopUnit[MAX_COUNT];

    /** Laskee kuinka monta yksikkötyyppiä on luotu. **/
    public static int getUnitCount() {
        int count = 0;
        for (TroopUnit unit : units)
            if (unit != null)
                count++;

        return count;
    }

    /** Ladataan tiedostosta aiemmin luodut yksiköt. **/
    public static void loadFrom(Preferences preferences) {
        for (int i = 0; i < units.length; i++) {
            if (preferences.contains("Unit" + i)) {
                units[i] = new TroopUnit();
                units[i].Name = preferences.getString("Unit" + i + "Name");
                units[i].setIcon(preferences.getString("Unit" + i + "Icon"));
                units[i].setPower(preferences.getInteger("Unit" + i + "Power"));
                units[i].setSpeed(preferences.getInteger("Unit" + i + "Speed"));
                units[i].setRange(preferences.getInteger("Unit" + i + "Range"));
            }
        }
    }

    /** Tallennetaan tiedostoon luodut yksiköt. **/
    public static void saveTo(Preferences preferences) {
        for (int i = 0; i < units.length; i++) {
            if (units[i] != null) {
                preferences.putString("Unit" + i, "Exists");
                preferences.putString("Unit" + i + "Name", units[i].Name);
                preferences.putString("Unit" + i + "Icon", units[i].getIconName());
                preferences.putInteger("Unit" + i + "Power", units[i].getPower());
                preferences.putInteger("Unit" + i + "Speed", units[i].getSpeed());
                preferences.putInteger("Unit" + i + "Range", units[i].getRange());
            }
        }
    }

    public static void removeUnitFrom(Preferences preferences, int index) {
        if (preferences.contains("Unit" + index)) {
            preferences.remove("Unit" + index);
            preferences.remove("Unit" + index + "Name");
            preferences.remove("Unit" + index + "Icon");
            preferences.remove("Unit" + index + "Power");
            preferences.remove("Unit" + index + "Speed");
            preferences.remove("Unit" + index + "Range");
        }
    }

    public static void reset() {
        for (int i = 0; i < Troops.units.length; i++)
            Troops.units[i] = null;
    }
}
