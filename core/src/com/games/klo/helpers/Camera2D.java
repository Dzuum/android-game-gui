package com.games.klo.helpers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;

public class Camera2D extends OrthographicCamera implements GestureDetector.GestureListener {

    /** Null tarkoittaa ettei ole rajoitusta.
     * <br>Tämä arvo on yleensä näkymän taustakuvan koko, esim. karttanäkymän tausta. **/
    private Vector2 maxArea;
    private Vector2 minPosition;

    private boolean isFlinging;
    private Vector2 velocityMax;
    private Vector2 velocityCurr;
    private float flingTimerMax;
    private float flingTimerCurr;

    public Camera2D(float width, float height) {
        super(width, height);

        //minPosition on siksi, jotta kameran vasen alakulma on siinä, mistä piirtäminen alkaa
        minPosition = new Vector2(width * 0.5f, height * 0.5f);
        maxArea = null;

        isFlinging = false;
        velocityMax = Vector2.Zero;
        velocityCurr = velocityMax;
        flingTimerMax = 2.0f;
        flingTimerCurr = flingTimerMax;

        setPosition(minPosition.x, minPosition.y);
    }

    public void resize(float width, float height) {
        minPosition = new Vector2(width * 0.5f, height * 0.5f);

        viewportWidth = width;
        viewportHeight = height;
        clampView();
        update();
    }

    /** Pyyhkäisyn nopeus **/
    private void setFlingVelocity(float velocityX, float velocityY) {
        velocityMax = new Vector2(velocityX, velocityY);
        velocityCurr = velocityMax;
        isFlinging = true;
        flingTimerCurr = flingTimerMax;
    }

    private void setFlingOff() {
        isFlinging = false;
    }

    public void update(float elapsed) {
        if (isFlinging) { //Päivitetään pyyhkäisyn liike
            flingTimerCurr -= elapsed;
            velocityCurr.x = (flingTimerCurr / flingTimerMax) * velocityMax.x;
            velocityCurr.y = (flingTimerCurr / flingTimerMax) * velocityMax.y;

            if (flingTimerCurr <= 0)
                isFlinging = false;

            moveBy(velocityCurr.x * elapsed, velocityCurr.y * elapsed);
        }
    }

    public void moveBy(float deltaX, float deltaY) {
        position.add(deltaX, deltaY, 0);
        clampView();
        update();
    }

    public void setPosition(float newX, float newY) {
        position.set(newX, newY, 0);
        clampView();
        update();
        isFlinging = false;
    }

    public Vector2 getMaxViewArea() {
        return maxArea;
    }

    public void setMaxViewArea(float width, float height) {
        maxArea = new Vector2(width, height);
        clampView();
    }

    public boolean canMoveLeft() {
        return position.x > minPosition.x;
    }

    public boolean canMoveDown() {
        return position.y > minPosition.y;
    }

    public boolean canMoveRight() {
        return position.x < ((maxArea.x + minPosition.x) - viewportWidth);
    }

    public boolean canMoveUp() {
        return position.y < ((maxArea.y + minPosition.y) - viewportHeight);
    }

    private void clampView() {
        if (maxArea == null)
            return;

        if (maxArea.x < viewportWidth)
            position.x = maxArea.x * 0.5f;
        else {
            if (position.x < minPosition.x)
                position.x = minPosition.x;
            if (position.x > ((maxArea.x + minPosition.x) - viewportWidth))
                position.x = (maxArea.x + minPosition.x) - viewportWidth;
        }

        if (maxArea.y < viewportHeight)
            position.y = maxArea.y * 0.5f;
        else {
            if (position.y < minPosition.y)
                position.y = minPosition.y;
            if (position.y > ((maxArea.y + minPosition.y) - viewportHeight))
                position.y = (maxArea.y + minPosition.y) - viewportHeight;
        }
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        if (GamePreferences.instance.isTutorialCompleted())
            setFlingOff(); //Aloitetaan pyyhkäisy

        return true;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        if (GamePreferences.instance.isTutorialCompleted())
            moveBy(-deltaX, deltaY); //Liikutetaan sormella; ei pyyhkäisy

        return true;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        if (GamePreferences.instance.isTutorialCompleted())
            setFlingVelocity(-velocityX, velocityY); //Aloitetaan pyyhkäisy

        return true;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) { return false; }
    @Override
    public boolean longPress(float x, float y) { return false; }
    @Override
    public boolean panStop(float x, float y, int pointer, int button) { return false; }
    @Override
    public boolean zoom(float initialDistance, float distance) { return false; }
    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) { return false; }
}
