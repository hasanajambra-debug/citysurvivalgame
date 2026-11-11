package entities;

import game.position;

public abstract class entity{
    public position pos; // pozicioni i Ã§do entiteti

    public entity(int x, int y) {
        pos = new position(x, y);
    }

    public position getPos() {
        return pos;
    }

    public void setPos(int x, int y) {
        pos.x = x;
        pos.y = y;
    }
}


