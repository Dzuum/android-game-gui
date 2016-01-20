package com.games.klo.content;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.Vector2;

public class Areas {

    /** Hyökättävien alueiden lukumäärä **/
    public static final int COUNT = 14;

    private static Vector2 citadelPosition;

    private static Vector2[] positions = new Vector2[COUNT];
    private static boolean[] captured = new boolean[COUNT];

    public static float getCitadelX() { return citadelPosition.x; }
    public static float getCitadelY() { return citadelPosition.y; }

    public static float getAreaX(int index) { return positions[index].x; }
    public static float getAreaY(int index) { return positions[index].y; }

    static {
        citadelPosition = new Vector2(699.0f, 87.0f);

        positions[0] = new Vector2(594.617f, 255.357f);

        positions[1] = new Vector2(444.105f, 505.916f);
        positions[2] = new Vector2(971.40f, 430.114f);

        positions[3] = new Vector2(88.630f, 812.457f);
        positions[4] = new Vector2(680.059f, 743.886f);
        positions[5] = new Vector2(1439.20f, 761.029f);

        positions[6] = new Vector2(385.773f, 938.172f);
        positions[7] = new Vector2(1017.20f, 978.171f);

        positions[8] = new Vector2(140.059f, 1295.31f);
        positions[9] = new Vector2(828.630f, 1258.17f);

        positions[10] = new Vector2(534.345f, 1478.17f);

        positions[11] = new Vector2(297.202f, 1689.60f);
        positions[12] = new Vector2(951.48f, 1798.17f);
        positions[13] = new Vector2(1340.05f, 1701.02f);

        for (int i = 0; i < COUNT; i++)
            captured[i] = false;
    }

    public static void setCaptured(int index) {
        captured[index] = true;
    }

    public static void setLost(int index) {
        captured[index] = false;
    }

    public static boolean isCaptured(int index) {
        return captured[index];
    }

    /** Hardkoodattu tapa katsoa voiko alueeseen hyökätä.
     * <br><br>
     * Alueeseen voi hyökätä, jos sitä edeltävät (alhaalta) alueet on omistuksessa. **/
    public static boolean canAttack(int index) {
        if (captured[index])
            return false;

        switch (index) {
            case 0:
                return true;
            case 1:
                if (captured[0])
                    return true;
                break;
            case 2:
                if (captured[0])
                    return true;
                break;
            case 3:
                if (captured[1])
                    return true;
                break;
            case 4:
                if (captured[2])
                    return true;
                break;
            case 5:
                if (captured[2])
                    return true;
                break;
            case 6:
                if (captured[3] || captured[4])
                    return true;
                break;
            case 7:
                if (captured[4] || captured[5])
                    return true;
                break;
            case 8:
                if (captured[6])
                    return true;
                break;
            case 9:
                if (captured[7])
                    return true;
                break;
            case 10:
                if (captured[8] || captured[9])
                    return true;
                break;
            case 11:
                if (captured[10])
                    return true;
                break;
            case 12:
                if (captured[10] || captured[13])
                    return true;
                break;
            case 13:
                if (captured[9])
                    return true;
                break;
        }

        return false;
    }

    /** Hardkoodattu tapa katsoa voiko vihollinen hyökätä.
     * <br><br>
     * Hyökkääminen on mahdollista "ylimpiin" alueisiin, jotka pelaaja omista. **/
    public static boolean canEnemyAttack(int index) {
        if (!captured[index])
            return false;

        switch (index) {
            case 0:
                if (!captured[1] && !captured[2])
                    return true;
                break;
            case 1:
                if (!captured[3])
                    return true;
                break;
            case 2:
                if (!captured[4] && !captured[5])
                    return true;
                break;
            case 3:
                if (!captured[6])
                    return true;
                break;
            case 4:
                if (!captured[6] && !captured[7])
                    return true;
                break;
            case 5:
                if (!captured[7])
                    return true;
                break;
            case 6:
                if (!captured[8])
                    return true;
                break;
            case 7:
                if (!captured[9])
                    return true;
                break;
            case 8:
                if (!captured[10])
                    return true;
                break;
            case 9:
                if (!captured[10] && !captured[13])
                    return true;
                break;
            case 10:
                if (!captured[11] && !captured[12])
                    return true;
                break;
            case 11:
                return true;
            case 12:
                return true;
            case 13:
                return true;
        }

        return false;
    }

    /** Ladataan tiedostosta alueiden omistustiedot. **/
    public static void loadFrom(Preferences preferences) {
        for (int i = 0; i < captured.length; i++) {
            setLost(i);

            if (preferences.contains("Captured" + i) &&
                    preferences.getBoolean("Captured" + i))
                setCaptured(i);
        }
    }

    /** Tallennetaan alueiden omistustiedot. **/
    public static void saveTo(Preferences preferences) {
        for (int i = 0; i < captured.length; i++) {
            if (isCaptured(i))
                preferences.putBoolean("Captured" + i, true);
            else
                preferences.putBoolean("Captured" + i, false);
        }
    }

    /** Nollataan omistustiedot. **/
    public static void reset() {
        for (int i = 0; i < captured.length; i++)
            setLost(i);
    }
}
