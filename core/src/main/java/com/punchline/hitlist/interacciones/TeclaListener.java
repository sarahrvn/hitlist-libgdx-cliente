package com.punchline.hitlist.interacciones;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;

public class TeclaListener implements InputProcessor {
    // Jugador 1 (WASD)
    private boolean w = false, a = false, s = false, d = false;
    private boolean wJustPressed = false, sJustPressed = false, aJustPressed = false, dJustPressed = false, eJustPressed = false, vJustPressed = false;;

    // Jugador 2 (Flechitas)
    private boolean up = false, down = false, left = false, right = false;
    private boolean upJustPressed = false, downJustPressed = false, leftJustPressed = false, rightJustPressed = false, iJustPressed = false, oJustPressed = false;

    // Generales
    private boolean escape = false, escapeJustPressed = false;
    private boolean enter = false, enterJustPressed = false;

    @Override
    public boolean keyDown(int keycode) {
        // P1
        if(keycode == Keys.W) { w = true; wJustPressed = true; }
        if(keycode == Keys.S) { s = true; sJustPressed = true; }
        if(keycode == Keys.A) { a = true; aJustPressed = true; }
        if(keycode == Keys.D) { d = true; dJustPressed = true; }
        if(keycode == Keys.E) { eJustPressed = true; }
        if(keycode == Keys.V) { vJustPressed = true; }

        // P2
        if(keycode == Keys.UP) { up = true; upJustPressed = true; }
        if(keycode == Keys.DOWN) { down = true; downJustPressed = true; }
        if(keycode == Keys.LEFT) { left = true; leftJustPressed = true; }
        if(keycode == Keys.RIGHT) { right = true; rightJustPressed = true; }
        if(keycode == Keys.I) { iJustPressed = true; }
        if(keycode == Keys.O) { oJustPressed = true; }


        // General
        if(keycode == Keys.ESCAPE) { escape = true; escapeJustPressed = true; }
        if(keycode==Keys.ENTER) { this.enter = true; this.enterJustPressed = true; }

        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if(keycode == Keys.W) w = false;
        if(keycode == Keys.S) s = false;
        if(keycode == Keys.A) a = false;
        if(keycode == Keys.D) d = false;
        if(keycode == Keys.UP) up = false;
        if(keycode == Keys.DOWN) down = false;
        if(keycode == Keys.LEFT) left = false;
        if(keycode == Keys.RIGHT) right = false;
        return true;
    }

    // ---- Getters para Jugador 1 ----
    public boolean isP1ArribaJustPressed() {
        if (wJustPressed) {
            wJustPressed = false;
            return true;
        }
        return false;
    }
    public boolean isP1Abajo() { return s; }
    public boolean isP1AbajoJustPressed() {
        if (sJustPressed) {
            sJustPressed = false;
            return true;
        }
        return false;
    }
    public boolean isP1Izquierda() { return a; }
    public boolean isP1IzquierdaJustPressed() {
        if (aJustPressed) {
            aJustPressed = false;
            return true;
        }
        return false;
    }
    public boolean isP1Derecha() { return d; }
    public boolean isP1DerechaJustPressed() {
        if (dJustPressed) {
            dJustPressed = false;
            return true;
        }
        return false;
    }
    public boolean isP1AgarrarJustPressed() {
        if (eJustPressed) {
            eJustPressed = false;
            return true;
        }
        return false;
    }
    public boolean isP1AtacarJustPressed() {
        if (vJustPressed) {
            vJustPressed = false;
            return true;
        }
        return false;
    }

    // ---- Getters para Jugador 2 ----
    public boolean isP2ArribaJustPressed() {
        if (upJustPressed) {
            upJustPressed = false;
            return true;
        }
        return false;
    }
    public boolean isP2Abajo() { return down; }
    public boolean isP2AbajoJustPressed() {
        if (downJustPressed) {
            downJustPressed = false;
            return true;
        }
        return false;
    }
    public boolean isP2Izquierda() { return left; }
    public boolean isP2IzquierdaJustPressed() {
        if (leftJustPressed) {
            leftJustPressed = false;
            return true;
        }
        return false;
    }
    public boolean isP2Derecha() { return right; }
    public boolean isP2DerechaJustPressed() {
        if (rightJustPressed) {
            rightJustPressed = false;
            return true;
        }
        return false;
    }
    public boolean isP2AgarrarJustPressed() {
        if (iJustPressed) {
            iJustPressed = false;
            return true;
        }
        return false;
    }
    public boolean isP2AtacarJustPressed() {
        if (oJustPressed) {
            oJustPressed = false;
            return true;
        }
        return false;
    }

    // ---- JUST PRESSED GENERALES ----
    public boolean isEscapeJustPressed() {
        if (this.escapeJustPressed) {
            this.escapeJustPressed = false;
            return true;
        }
        return false;
    }

    public boolean isEnterJustPressed() {
        if (this.enterJustPressed) {
            this.enterJustPressed = false;
            return true;
        }
        return false;
    }

    public boolean isArribaJustPressed() {
        if (this.wJustPressed || this.upJustPressed) {
            this.wJustPressed = false;
            this.upJustPressed = false;
            return true;
        }
        return false;
    }

    public boolean isAbajoJustPressed() {
        if (this.sJustPressed || this.downJustPressed) {
            this.sJustPressed = false;
            this.downJustPressed = false;
            return true;
        }
        return false;
    }

    public boolean isIzquierdaJustPressed() {
        if (this.aJustPressed || this.leftJustPressed) {
            this.aJustPressed = false;
            this.leftJustPressed = false;
            return true;
        }
        return false;
    }

    public boolean isDerechaJustPressed() {
        if (this.dJustPressed || this.rightJustPressed) {
            this.dJustPressed = false;
            this.rightJustPressed = false;
            return true;
        }
        return false;
    }

    // Métodos obligatorios vacíos
    @Override public boolean keyTyped(char character) { return false; }
    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }
}
