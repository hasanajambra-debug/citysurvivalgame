package entities;

import items.weapon;
import items.item;
import items.food;

public class player extends entity {
    public int hp;
    public weapon weapon;
    public int itemsCollected;
    public int steps;
    public int enemiesDefeated;
    public int foodsCollected;
    public int weaponsCollected;

    public player(int x, int y) {
        super(x, y);
        hp = 3;
        weapon = null;
        itemsCollected = 0;
        steps = 0;
        enemiesDefeated = 0;
        foodsCollected = 0;
        weaponsCollected = 0;
    }

    public void move(char dir, int size) {
        if (dir == 'W' && pos.x > 0) pos.x--;
        else if (dir == 'S' && pos.x < size - 1) pos.x++;
        else if (dir == 'A' && pos.y > 0) pos.y--;
        else if (dir == 'D' && pos.y < size - 1) pos.y++;
        steps++;
    }

    public void collect(item item) {
        itemsCollected++;

        if (item instanceof food) {
            hp++;
            foodsCollected++;
            if (foodsCollected % 5 == 0) {
                hp++;
            }
        } else if (item instanceof weapon) {
            weaponsCollected++;
            weapon w = (weapon) item;
            if (weapon == null || w.level > weapon.level) {
                weapon = w;
            }
            if (weaponsCollected % 3 == 0) {
                hp++;
            }
        }
    }
}
