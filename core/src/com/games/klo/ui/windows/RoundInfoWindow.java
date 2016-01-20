package com.games.klo.ui.windows;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.games.klo.content.Assets;
import com.games.klo.helpers.LanguageManager;
import com.games.klo.ui.UIBuilder;

public class RoundInfoWindow extends SuperWindow {

    private Label healthLabel;
    private Label rewardLabel;

    public RoundInfoWindow() {
        super("roundInfoSingle", "InfoIcon", true);

        Table upperHalf = new Table();

        Button enemyIcon = new Button(Assets.getUiSkin().getDrawable("SlotUp"));
        enemyIcon.add(new Image(Assets.getUiSkin().getDrawable("MonsterIcon1")));
        enemyIcon.setDisabled(true);

        Label countLabel = new Label("x20", Assets.getUiSkin());

        upperHalf.add(enemyIcon).center();
        upperHalf.row();
        upperHalf.add(countLabel).center();

        Table lowerHalf = new Table();
        lowerHalf.columnDefaults(0).center().expand().fill().padRight(20);
        lowerHalf.columnDefaults(1).center().expand().fill();

        healthLabel = new Label(LanguageManager.getString("health"),
                Assets.getUiSkin());
        Label healthAmountLabel = new Label("5", Assets.getUiSkin());
        rewardLabel = new Label(LanguageManager.getString("reward"),
                Assets.getUiSkin());
        Table costTable = UIBuilder.createSmallCostTable(10);

        lowerHalf.add(healthLabel);
        lowerHalf.add(healthAmountLabel);
        lowerHalf.row();
        lowerHalf.add(rewardLabel);
        lowerHalf.add(costTable);

        Table table = new Table();
        table.add(upperHalf).expand().fill().pad(10);
        table.row();
        table.add(lowerHalf).expand().fill().padBottom(10).padLeft(60).padRight(60 - 15).center();

        addTable(table);
    }

    @Override
    public void updateLanguage() {
        if (healthLabel != null)
            healthLabel.setText(LanguageManager.getString("health"));
        if (rewardLabel != null)
            rewardLabel.setText(LanguageManager.getString("reward"));

        super.updateLanguage();
    }
}
