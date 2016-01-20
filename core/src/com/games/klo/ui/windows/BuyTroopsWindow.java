package com.games.klo.ui.windows;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.games.klo.content.Assets;
import com.games.klo.content.Troops;
import com.games.klo.ui.UIBuilder;

public class BuyTroopsWindow extends SuperWindow {

    private int money;

    public BuyTroopsWindow(int money) {
        super("troopsOverview", "TroopsIcon", true);

        this.money = money;

        Table troopsTable = new Table();
        troopsTable.defaults().padLeft(20).padTop(20); //LeftTop
        troopsTable.padRight(20).padBottom(20); //RightBottom

        for (int j = 0; j < 2; j++) {
            for (int i = 0; i < Troops.MAX_COUNT / 2; i++) {
                final int index = i + j * Troops.MAX_COUNT / 2;

                Table slotTable = new Table();

                ImageButton slotButton = new ImageButton(Assets.getUiSkin(), "slot");

                Table costTable;
                Table statsTable = new Table();

                if (Troops.units[index] != null) {
                    //Katsotaan onko ostettavissa
                    if (Troops.units[index].getCost() > money) {
                        slotButton.setTouchable(Touchable.disabled);
                        slotButton.setDisabled(true);
                    }

                    slotButton.getImageCell().setActor(new Image(
                            Assets.getUiSkin().getDrawable(Troops.units[index].getIconName())));

                    costTable = UIBuilder.createSmallCostTable(Troops.units[index].getCost());

                    statsTable.columnDefaults(0).padLeft(4).padRight(4);
                    statsTable.columnDefaults(1).padRight(4);
                    statsTable.add(new Image(Assets.getUiSkin().getDrawable("PowerSmall")));
                    statsTable.add(new Label(String.valueOf(Troops.units[index].getPower()),
                            Assets.getUiSkin()));
                    statsTable.row();
                    statsTable.add(new Image(Assets.getUiSkin().getDrawable("SpeedSmall")));
                    statsTable.add(new Label(String.valueOf(Troops.units[index].getSpeed()),
                            Assets.getUiSkin()));
                    statsTable.row();
                    statsTable.add(new Image(Assets.getUiSkin().getDrawable("RangeSmall")));
                    statsTable.add(new Label(String.valueOf(Troops.units[index].getRange()),
                            Assets.getUiSkin()));
                    statsTable.row();
                } else {
                    costTable = new Table();
                }

                costTable.setBackground(Assets.getUiSkin().getDrawable("SlotAddUp"));
                statsTable.setBackground(Assets.getUiSkin().getDrawable("SlotAddUp"));

                slotTable.add(costTable).expand().fill().left().padBottom(-2);
                slotTable.row();
                slotTable.add(slotButton).expand().fill();
                slotTable.add(statsTable).fill().padLeft(-2);

                troopsTable.add(slotTable);
            }

            troopsTable.row();
        }

        addTable(troopsTable);
    }
}
