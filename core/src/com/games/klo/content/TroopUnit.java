package com.games.klo.content;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TroopUnit {

    public static final int MIN_STAT = 1;
    public static final int MAX_STAT = 10;

    private static int iconCount;

    public String Name;
    public String icon;

    private int cost;
    private int power;
    private int speed;
    private int range;

    public String getIconName() { return icon; }

    public int getCost() { return cost; }
    public int getPower() { return power; }
    public int getSpeed() { return speed; }
    public int getRange() { return range; }

    static {
        //Katsotaan monta eri ikonia on luotu yksiköille.
        //Jokainen ikonikuva on määritellyn kaavan mukaisesti nimetty.
        iconCount = 0;
        while (Assets.getUiSkin().has("TroopIcon" + (iconCount + 1), TextureRegion.class))
            iconCount++;
    }

    public TroopUnit() {
        Name = "Troop Unit " + (Troops.getUnitCount() + 1);
        icon = "TroopIcon1";

        power = 2;
        speed = 2;
        range = 2;

        updateCost();
    }

    /** Muodostin, joka kopioi toisen yksikön ominaisuudet. **/
    public TroopUnit(TroopUnit copy) {
        copyValuesFrom(copy);
    }

    public void copyValuesFrom(TroopUnit copy) {
        Name = copy.Name;
        icon = copy.icon;

        power = copy.power;
        speed = copy.speed;
        range = copy.range;

        updateCost();
    }

    public void setIcon(String iconName) {
        if (Assets.getUiSkin().has(iconName, TextureRegion.class))
            icon = iconName;
    }

    public void prevIcon() {
        int current = Integer.valueOf(icon.substring(icon.length() - 1));
        current--;
        if (current < 1)
            current = iconCount;

        icon = "TroopIcon" + current;
    }

    public void nextIcon() {
        int current = Integer.valueOf(icon.substring(icon.length() - 1));
        current++;
        if (current > iconCount)
            current = 1;

        icon = "TroopIcon" + current;
    }

    public void setPower(int power) {
        if (power >= MIN_STAT && power <= MAX_STAT)
            this.power = power;

        updateCost();
    }

    public void lessPower() {
        if (power > MIN_STAT) {
            power--;
            updateCost();
        }
    }

    public void morePower() {
        if (power < MAX_STAT) {
            power++;
            updateCost();
        }
    }

    public void setSpeed(int speed) {
        if (speed >= MIN_STAT && speed <= MAX_STAT)
            this.speed = speed;

        updateCost();
    }

    public void lessSpeed() {
        if (speed > MIN_STAT) {
            speed--;
            updateCost();
        }
    }

    public void moreSpeed() {
        if (speed < MAX_STAT) {
            speed++;
            updateCost();
        }
    }

    public void setRange(int range) {
        if (range >= MIN_STAT && range <= MAX_STAT)
            this.range = range;

        updateCost();
    }

    public void lessRange() {
        if (range > MIN_STAT) {
            range--;
            updateCost();
        }
    }

    public void moreRange() {
        if (range < MAX_STAT) {
            range++;
            updateCost();
        }
    }

    /** Yksikön hinta lasketaan automaattisesti. **/
    private void updateCost() {
        cost = (power + speed + range) * 10;
    }
}
