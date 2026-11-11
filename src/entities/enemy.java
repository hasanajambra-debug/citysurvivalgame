package entities;

import java.util.Random;
import field.field;

public class enemy extends entity {
    public enemy(int x, int y) {
        super(x, y);
    }

    public void moveTowardsPlayer(player player, field field) {
        int playerX = player.pos.x;
        int playerY = player.pos.y;
        int enemyX = this.pos.x;
        int enemyY = this.pos.y;

        // Llogarit drejtimin
        int dx = Integer.compare(playerX, enemyX);
        int dy = Integer.compare(playerY, enemyY);

        // Zgjidh drejtimin bazuar në distancën më të madhe
        int newX = enemyX;
        int newY = enemyY;

        if (Math.abs(playerX - enemyX) > Math.abs(playerY - enemyY)) {
            // Lëviz horizontalisht
            newX = enemyX + dx;
            // Kontrollo nëse pozicioni i ri është valid
            if (field.canMoveTo(newX, newY)) {
                this.pos.x = newX;
            } else {
                // Provo drejtimin tjetër (vertikalisht)
                newX = enemyX;
                newY = enemyY + dy;
                if (field.canMoveTo(newX, newY)) {
                    this.pos.y = newY;
                }
            }
        } else {
            // Lëviz vertikalisht
            newY = enemyY + dy;
            // Kontrollo nëse pozicioni i ri është valid
            if (field.canMoveTo(newX, newY)) {
                this.pos.y = newY;
            } else {
                // Provo drejtimin tjetër (horizontalisht)
                newY = enemyY;
                newX = enemyX + dx;
                if (field.canMoveTo(newX, newY)) {
                    this.pos.x = newX;
                }
            }
        }
    }
}