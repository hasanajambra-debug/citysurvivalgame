package field;

import entities.player;
import entities.enemy;
import items.food;
import items.item;
import items.weapon;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Iterator;
import java.io.*;

public class field {
    public int size;
    public player player;
    public enemy enemy;
    public List<item> items = new ArrayList<>();
    public char[][] mapGrid;
    public List<java.awt.Point> walls = new ArrayList<>();
    Random rand = new Random();

    public field(int size) {
        this.size = size;
        loadMapFromFile("map.txt");
        init();
    }

    private void loadMapFromFile(String filename) {
        try {
            List<String> lines = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;

            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            reader.close();

            int rows = lines.size();
            int cols = lines.get(0).length();
            mapGrid = new char[rows][cols];

            for (int i = 0; i < rows; i++) {
                line = lines.get(i);
                for (int j = 0; j < Math.min(cols, line.length()); j++) {
                    mapGrid[i][j] = line.charAt(j);
                    if (mapGrid[i][j] == '#') {
                        walls.add(new java.awt.Point(i, j));
                    }
                }
            }
            this.size = rows;

        } catch (IOException e) {
            createDefaultMap();
        }
    }

    private void createDefaultMap() {
        mapGrid = new char[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                mapGrid[i][j] = '.';
            }
        }
    }

    void init() {
        // Lexo pozicionet nga map.txt
        for (int i = 0; i < mapGrid.length; i++) {
            for (int j = 0; j < mapGrid[i].length; j++) {
                char cell = mapGrid[i][j];

                if (cell == 'P') {
                    player = new player(i, j);
                } else if (cell == 'E') {
                    enemy = new enemy(i, j);
                } else if (cell == 'F') {
                    items.add(new food(i, j));
                } else if (cell == '1') {
                    items.add(new weapon(i, j, 1));
                } else if (cell == '2') {
                    items.add(new weapon(i, j, 2));
                }
            }
        }

        // NÃ«se nuk ka pozicione nÃ« map, krijo random nÃ« vende tÃ« lira
        if (player == null) {
            java.awt.Point freePos = getRandomFreePosition();
            player = new player(freePos.x, freePos.y);
        }
        if (enemy == null) {
            java.awt.Point freePos = getRandomFreePosition();
            enemy = new enemy(freePos.x, freePos.y);
        }
    }

    public java.awt.Point getRandomFreePosition() {
        int x, y;
        do {
            x = rand.nextInt(size);
            y = rand.nextInt(size);
        } while (mapGrid[x][y] == '#');
        return new java.awt.Point(x, y);
    }

    public boolean isWall(int x, int y) {
        if (x < 0 || x >= size || y < 0 || y >= size) return true;
        return mapGrid[x][y] == '#';
    }

    public boolean canMoveTo(int x, int y) {
        if (x < 0 || x >= size || y < 0 || y >= size) return false;
        return mapGrid[x][y] != '#';
    }

    public void print() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                String c = "â¬œ";
                if (player.pos.x == i && player.pos.y == j) c = "ðŸ§";
                else if (enemy.pos.x == i && enemy.pos.y == j) c = "ðŸ‘¾";
                else if (mapGrid[i][j] == '#') c = "ðŸ§±";
                else {
                    // Shfaq objektet nga lista e items
                    for (item it : items) {
                        if (it.pos.x == i && it.pos.y == j) {
                            if (it instanceof food) c = "ðŸŽ";
                            else if (it instanceof weapon) {
                                weapon w = (weapon) it;
                                c = (w.level == 1 ? "ðŸ”ª" : "âš”ï¸");
                            }
                            break;
                        }
                    }
                }
                System.out.print(c + " ");
            }
            System.out.println();
        }
    }

    public item checkItemAtPlayer() {
        Iterator<item> it = items.iterator();
        while (it.hasNext()) {
            item item = it.next();
            if (item.pos.x == player.pos.x && item.pos.y == player.pos.y) {
                it.remove();
                return item;
            }
        }
        return null;
    }
}

