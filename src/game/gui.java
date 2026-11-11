package game;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class gui extends JFrame {
    private game game;
    private FieldPanel fieldPanel;
    private StatsPanel statsPanel;
    private InventoryPanel inventoryPanel;
    private JPanel mainPanel;
    private boolean gameRunning = true;
    private boolean isPaused = false;
    private final int CELL_SIZE = 50;
    private final String SAVE_FILE = "city_survival_save.txt";
    private final String RESULTS_FILE = "city_survival_results.txt";

    // Emoji pÃ«r elementet e lojÃ«s
    private static final String PLAYER_EMOJI = "ðŸ§™";
    private static final String ENEMY_EMOJI = "ðŸ‘¾";
    private static final String FOOD_EMOJI = "ðŸŽ";
    private static final String WEAPON1_EMOJI = "ðŸ”ª";
    private static final String WEAPON2_EMOJI = "âš”ï¸";
    private static final String WALL_EMOJI = "ðŸ§±";

    // Ngjyra pÃ«r emoji
    private static final Color PLAYER_COLOR = new Color(0, 200, 255);
    private static final Color ENEMY_COLOR = new Color(255, 50, 50);
    private static final Color FOOD_COLOR = new Color(255, 100, 100);
    private static final Color WEAPON1_COLOR = new Color(200, 200, 0);
    private static final Color WEAPON2_COLOR = new Color(0, 255, 150);
    private static final Color WALL_COLOR = new Color(150, 100, 200);

    // Tracking tÃ« inventarit
    private int weapon1Count = 0;
    private int weapon2Count = 0;
    private int foodCount = 0;

    // Tracking tÃ« lojÃ«s
    private Date gameStartTime;
    private Date gameEndTime;
    private boolean gameSaved = false;

    public gui() {
        setTitle("City Survival");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        game = new game(20);

        // RREGULLIM: Armiku nÃ« pozicion tÃ« rastÃ«sishÃ«m nÃ« fillim
        java.awt.Point randomEnemyPos = game.field.getRandomFreePosition();
        while (randomEnemyPos.x == game.field.player.pos.x &&
                randomEnemyPos.y == game.field.player.pos.y) {
            randomEnemyPos = game.field.getRandomFreePosition();
        }
        game.field.enemy.pos.x = randomEnemyPos.x;
        game.field.enemy.pos.y = randomEnemyPos.y;

        gameStartTime = new Date();

        initComponents();
        setupKeyBindings();

        getContentPane().setBackground(new Color(43, 0, 73));
    }

    private void initComponents() {
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(43, 0, 73));

        fieldPanel = new FieldPanel();
        JScrollPane scrollPane = new JScrollPane(fieldPanel);
        scrollPane.setPreferredSize(new Dimension(600, 600));
        scrollPane.getViewport().setBackground(new Color(30, 0, 50));

        statsPanel = new StatsPanel();
        inventoryPanel = new InventoryPanel();
        JPanel buttonPanel = createButtonPanel();

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(scrollPane, BorderLayout.CENTER);
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);
        leftPanel.setBackground(new Color(43, 0, 73));

        JPanel rightPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        rightPanel.add(statsPanel);
        rightPanel.add(inventoryPanel);
        rightPanel.setBackground(new Color(43, 0, 73));

        mainPanel.add(leftPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);

        add(mainPanel);
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(new Color(60, 0, 100));
        panel.setBorder(BorderFactory.createLineBorder(new Color(120, 0, 200), 2));

        JButton saveButton = new JButton("ðŸ’¾ Ruaj LojÃ«n");
        JButton loadButton = new JButton("ðŸ“„ Ringarko");
        JButton pauseButton = new JButton("â¸ï¸ PauzÃ«");
        JButton quitButton = new JButton("ðŸšª Dalje");

        styleButton(saveButton);
        styleButton(loadButton);
        styleButton(pauseButton);
        styleButton(quitButton);

        saveButton.addActionListener(e -> saveGame());
        loadButton.addActionListener(e -> loadGame());
        pauseButton.addActionListener(e -> togglePause());
        quitButton.addActionListener(e -> confirmQuit());

        panel.add(saveButton);
        panel.add(loadButton);
        panel.add(pauseButton);
        panel.add(quitButton);

        return panel;
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(120, 0, 200));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setPreferredSize(new Dimension(140, 40));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(150, 50, 220));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(120, 0, 200));
            }
        });
    }

    private void setupKeyBindings() {
        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getRootPane().getActionMap();

        String[] keys = {"W", "S", "A", "D", "UP", "DOWN", "LEFT", "RIGHT", "P", "Q"};

        for (String key : keys) {
            inputMap.put(KeyStroke.getKeyStroke(key), key);
        }

        actionMap.put("W", new MoveAction('W'));
        actionMap.put("S", new MoveAction('S'));
        actionMap.put("A", new MoveAction('A'));
        actionMap.put("D", new MoveAction('D'));
        actionMap.put("UP", new MoveAction('W'));
        actionMap.put("DOWN", new MoveAction('S'));
        actionMap.put("LEFT", new MoveAction('A'));
        actionMap.put("RIGHT", new MoveAction('D'));
        actionMap.put("P", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                togglePause();
            }
        });
        actionMap.put("Q", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmQuit();
            }
        });
    }

    private void movePlayer(char direction) {
        if (!gameRunning || isPaused) return;

        int oldX = game.field.player.pos.x;
        int oldY = game.field.player.pos.y;
        int newX = oldX;
        int newY = oldY;

        switch (direction) {
            case 'W': newX = oldX - 1; break;
            case 'S': newX = oldX + 1; break;
            case 'A': newY = oldY - 1; break;
            case 'D': newY = oldY + 1; break;
        }

        if (game.field.canMoveTo(newX, newY)) {
            game.field.player.move(direction, game.field.size);
            checkForItems();

            // RREGULLIM: Armiku lÃ«viz vetÃ«m PAS lojtarit
            game.field.enemy.moveTowardsPlayer(game.field.player, game.field);

            checkCollision();
            fieldPanel.repaint();
            statsPanel.updateStats();
            inventoryPanel.updateInventory();
        }
    }

    private void checkForItems() {
        Iterator<items.item> iterator = game.field.items.iterator();
        while (iterator.hasNext()) {
            items.item item = iterator.next();
            if (item.pos.x == game.field.player.pos.x &&
                    item.pos.y == game.field.player.pos.y) {

                game.field.player.collect(item);

                if (item instanceof items.food) {
                    foodCount++;
                } else if (item instanceof items.weapon) {
                    items.weapon weapon = (items.weapon) item;
                    if (weapon.level == 1) {
                        weapon1Count++;
                    } else if (weapon.level == 2) {
                        weapon2Count++;
                    }
                }

                iterator.remove();
                inventoryPanel.updateInventory();
                statsPanel.updateStats();
                break;
            }
        }
    }

    private void checkCollision() {
        if (game.field.player.pos.x == game.field.enemy.pos.x &&
                game.field.player.pos.y == game.field.enemy.pos.y) {

            fight();

            // Kontrollo nÃ«se loja pÃ«rfundon (vetÃ«m nÃ«se lojtari vdes)
            if (game.field.player.hp <= 0) {
                gameRunning = false;
                gameEndTime = new Date();
                showGameOverDialog(false); // Humbje
            }
        }
    }

    // LOGJIKA E PÃ‹RMIRÃ‹SUAR E LUFTIMIT
    private void fight() {
        int playerWeaponLevel = (game.field.player.weapon != null) ? game.field.player.weapon.level : 0;
        Random rand = new Random();
        int enemyWeaponLevel = rand.nextInt(2) + 1; // Armiku ka gjithmonÃ« armÃ« (1 ose 2)

        String message = "âš”ï¸ Dyluftim! Lojtari ðŸ§™ vs Armiku ðŸ‘¾\n";
        message += "Arma e lojtarit: " + (playerWeaponLevel == 0 ? "None" :
                (playerWeaponLevel == 1 ? "ðŸ”ª Level 1" : "âš”ï¸ Level 2")) + "\n";
        message += "Arma e armikut: " + (enemyWeaponLevel == 1 ? "ðŸ”ª Level 1" : "âš”ï¸ Level 2") + "\n\n";

        // PÃ«rcakto fituesin
        if (playerWeaponLevel > enemyWeaponLevel) {
            message += "ðŸ§™ Lojtari fitoi dyluftimin!\n";
            message += "Armiku u mposhte dhe u largua!\n";
            game.field.player.enemiesDefeated++;
        } else if (enemyWeaponLevel > playerWeaponLevel) {
            message += "ðŸ‘¾ Armiku fitoi dyluftimin!\n";
        } else {
            message += "âš–ï¸ Barazim nÃ« dyluftim!\n";
        }

        // RREGULL I RI: Lojtari humbet HP PAS Ã‡DO dyluftimi
        game.field.player.hp--;
        message += "ðŸ’” Lojtari humbet 1 HP nga dyluftimi!\n";
        message += "HP i mbetur: " + game.field.player.hp + " â¤ï¸";

        JOptionPane.showMessageDialog(this, message, "Luftim", JOptionPane.WARNING_MESSAGE);

        // Pas Ã§do dyluftimi, armiku lÃ«viz nÃ« pozicion tÃ« ri
        relocateEnemy();

        statsPanel.updateStats();
    }

    // MetodÃ« e re pÃ«r tÃ« rivendosur armikun nÃ« pozicion tÃ« ri
    private void relocateEnemy() {
        java.awt.Point newPos = game.field.getRandomFreePosition();

        // Sigurohu qÃ« armiku nuk vendoset mbi lojtarin
        while (newPos.x == game.field.player.pos.x && newPos.y == game.field.player.pos.y) {
            newPos = game.field.getRandomFreePosition();
        }

        game.field.enemy.pos.x = newPos.x;
        game.field.enemy.pos.y = newPos.y;

        fieldPanel.repaint();
    }

    private void togglePause() {
        isPaused = !isPaused;
        String status = isPaused ? "Loja Ã«shtÃ« nÃ« pauzÃ«" : "Loja vazhdoi!";
        JOptionPane.showMessageDialog(this, status, "PauzÃ«", JOptionPane.INFORMATION_MESSAGE);
    }

    private void confirmQuit() {
        int response = JOptionPane.showConfirmDialog(
                this,
                "Je i sigurt qÃ« do ta mbyllÃ«sh lojÃ«n?",
                "Konfirmim",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (response == JOptionPane.YES_OPTION) {
            saveGameProgress();
            saveGameResults(false);
            System.exit(0);
        }
    }

    private void saveGame() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(SAVE_FILE))) {
            writer.println("PlayerPosition:" + game.field.player.pos.x + "," + game.field.player.pos.y);
            writer.println("HP:" + game.field.player.hp);
            writer.println("WeaponLevel:" + (game.field.player.weapon != null ? game.field.player.weapon.level : "0"));
            writer.println("Weapon1Count:" + weapon1Count);
            writer.println("Weapon2Count:" + weapon2Count);
            writer.println("FoodCount:" + foodCount);
            writer.println("ItemsCollected:" + game.field.player.itemsCollected);
            writer.println("FoodsCollected:" + game.field.player.foodsCollected);
            writer.println("WeaponsCollected:" + game.field.player.weaponsCollected);
            writer.println("Steps:" + game.field.player.steps);
            writer.println("EnemiesDefeated:" + game.field.player.enemiesDefeated);
            writer.println("EnemyPosition:" + game.field.enemy.pos.x + "," + game.field.enemy.pos.y);
            writer.println("GameStartTime:" + gameStartTime.getTime());

            JOptionPane.showMessageDialog(this, "Loja u ruajt me sukses!", "Sukses", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Gabim nÃ« ruajtjen e lojÃ«s", "Gabim", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveGameResults(boolean won) {
        if (gameSaved) return;

        gameEndTime = new Date();
        long gameDuration = (gameEndTime.getTime() - gameStartTime.getTime()) / 1000;

        try (PrintWriter writer = new PrintWriter(new FileWriter(RESULTS_FILE, true))) {
            writer.println("â•”â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•—");
            writer.println("â•‘              REZULTATET E LOJÃ‹S                  â•‘");
            writer.println("â• â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•£");
            writer.println("â•‘ Data: " + new Date());
            writer.println("â•‘ KohÃ«zgjatja: " + gameDuration + " sekonda");
            writer.println("â•‘ Rezultati: " + (won ? "FITORE ðŸ†" : "HUMBJE ðŸ’€"));
            writer.println("â• â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•£");
            writer.println("â•‘ STATISTIKAT FINALE:");
            writer.println("â•‘ HP pÃ«rfundimtar: " + game.field.player.hp + " â¤ï¸");
            writer.println("â•‘ Arma e pajisur: " + (game.field.player.weapon == null ? "None" :
                    (game.field.player.weapon.level == 1 ? "ðŸ”ª Level 1" : "âš”ï¸ Level 2")));
            writer.println("â•‘ Ushqime tÃ« mbledhura: " + foodCount + " ðŸŽ");
            writer.println("â•‘ ArmÃ« Level 1 tÃ« mbledhura: " + weapon1Count + " ðŸ”ª");
            writer.println("â•‘ ArmÃ« Level 2 tÃ« mbledhura: " + weapon2Count + " âš”ï¸");
            writer.println("â•‘ Hapat e hedhur: " + game.field.player.steps + " ðŸ‘£");
            writer.println("â•‘ Armiq tÃ« mposhtur: " + game.field.player.enemiesDefeated + " ðŸ‘¾");
            writer.println("â•‘ Objekte totale: " + game.field.player.itemsCollected + " ðŸŽ");
            writer.println("â•‘ Ushqime totale: " + game.field.player.foodsCollected + " ðŸŽ");
            writer.println("â•‘ ArmÃ« totale: " + game.field.player.weaponsCollected + " ðŸ”ªâš”ï¸");
            writer.println("â•šâ•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•");
            writer.println();

            gameSaved = true;

        } catch (IOException e) {
            System.err.println("Gabim nÃ« ruajtjen e rezultateve: " + e.getMessage());
        }
    }

    private void saveGameProgress() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(SAVE_FILE))) {
            writer.println("PlayerPosition:" + game.field.player.pos.x + "," + game.field.player.pos.y);
            writer.println("HP:" + game.field.player.hp);
            writer.println("WeaponLevel:" + (game.field.player.weapon != null ? game.field.player.weapon.level : "0"));
            writer.println("Weapon1Count:" + weapon1Count);
            writer.println("Weapon2Count:" + weapon2Count);
            writer.println("FoodCount:" + foodCount);
            writer.println("ItemsCollected:" + game.field.player.itemsCollected);
            writer.println("FoodsCollected:" + game.field.player.foodsCollected);
            writer.println("WeaponsCollected:" + game.field.player.weaponsCollected);
            writer.println("Steps:" + game.field.player.steps);
            writer.println("EnemiesDefeated:" + game.field.player.enemiesDefeated);
            writer.println("EnemyPosition:" + game.field.enemy.pos.x + "," + game.field.enemy.pos.y);
            writer.println("GameStartTime:" + gameStartTime.getTime());

        } catch (IOException e) {
            System.err.println("Gabim nÃ« ruajtjen e progresit: " + e.getMessage());
        }
    }

    private void loadGame() {
        try (BufferedReader reader = new BufferedReader(new FileReader(SAVE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    switch (parts[0]) {
                        case "PlayerPosition":
                            String[] pos = parts[1].split(",");
                            game.field.player.pos.x = Integer.parseInt(pos[0]);
                            game.field.player.pos.y = Integer.parseInt(pos[1]);
                            break;
                        case "HP":
                            game.field.player.hp = Integer.parseInt(parts[1]);
                            break;
                        case "Weapon1Count":
                            weapon1Count = Integer.parseInt(parts[1]);
                            break;
                        case "Weapon2Count":
                            weapon2Count = Integer.parseInt(parts[1]);
                            break;
                        case "FoodCount":
                            foodCount = Integer.parseInt(parts[1]);
                            break;
                        case "ItemsCollected":
                            game.field.player.itemsCollected = Integer.parseInt(parts[1]);
                            break;
                        case "FoodsCollected":
                            game.field.player.foodsCollected = Integer.parseInt(parts[1]);
                            break;
                        case "WeaponsCollected":
                            game.field.player.weaponsCollected = Integer.parseInt(parts[1]);
                            break;
                        case "Steps":
                            game.field.player.steps = Integer.parseInt(parts[1]);
                            break;
                        case "EnemiesDefeated":
                            game.field.player.enemiesDefeated = Integer.parseInt(parts[1]);
                            break;
                        case "EnemyPosition":
                            String[] epos = parts[1].split(",");
                            game.field.enemy.pos.x = Integer.parseInt(epos[0]);
                            game.field.enemy.pos.y = Integer.parseInt(epos[1]);
                            break;
                        case "WeaponLevel":
                            int level = Integer.parseInt(parts[1]);
                            if (level > 0) {
                                game.field.player.weapon = new items.weapon(
                                        game.field.player.pos.x,
                                        game.field.player.pos.y,
                                        level
                                );
                            }
                            break;
                        case "GameStartTime":
                            long startTime = Long.parseLong(parts[1]);
                            gameStartTime = new Date(startTime);
                            break;
                    }
                }
            }

            fieldPanel.repaint();
            statsPanel.updateStats();
            inventoryPanel.updateInventory();
            gameRunning = true;

            JOptionPane.showMessageDialog(this, "Loja u ringarkua me sukses!", "Sukses", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Nuk u gjet lojÃ« e ruajtur", "Informacion", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gabim nÃ« leximin e file-it", "Gabim", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showGameOverDialog(boolean won) {
        String title = won ? "ðŸ† FITORE!" : "ðŸ’€ GAME OVER";
        String message = (won ? "ðŸŽ‰ URIME! Fitove lojÃ«n! ðŸŽ‰\n\n" : "ðŸ’€ GAME OVER ðŸ’€\n\n");

        gameEndTime = new Date();
        long gameDuration = (gameEndTime.getTime() - gameStartTime.getTime()) / 1000;

        message += "=== Statistikat Finale ===\n" +
                "HP pÃ«rfundimtar â¤ï¸: " + game.field.player.hp + "\n" +
                "Arma e pajisur: " + (game.field.player.weapon == null ? "None" :
                (game.field.player.weapon.level == 1 ? "ðŸ”ª" : "âš”ï¸")) + "\n" +
                "Ushqime tÃ« mbledhura ðŸŽ: " + foodCount + "\n" +
                "ArmÃ« Level 1 tÃ« mbledhura ðŸ”ª: " + weapon1Count + "\n" +
                "ArmÃ« Level 2 tÃ« mbledhura âš”ï¸: " + weapon2Count + "\n" +
                "Hapat e hedhur ðŸ‘£: " + game.field.player.steps + "\n" +
                "Armiq tÃ« mposhtur ðŸ‘¾: " + game.field.player.enemiesDefeated + "\n" +
                "KohÃ«zgjatja e lojÃ«s: " + gameDuration + " sekonda\n" +
                "========================";

        int messageType = won ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE;

        Object[] options = {"Ruaj Rezultatet", "Ristarto", "Dil"};
        int choice = JOptionPane.showOptionDialog(this,
                message,
                title,
                JOptionPane.DEFAULT_OPTION,
                messageType,
                null,
                options,
                options[0]);

        switch (choice) {
            case 0:
                saveGameResults(won);
                JOptionPane.showMessageDialog(this,
                        "Rezultatet u ruajtÃ«n me sukses nÃ« " + RESULTS_FILE,
                        "Sukses",
                        JOptionPane.INFORMATION_MESSAGE);
                restartGame();
                break;
            case 1:
                restartGame();
                break;
            case 2:
                saveGameResults(won);
                System.exit(0);
                break;
            default:
                saveGameResults(won);
                restartGame();
                break;
        }
    }

    private void restartGame() {
        game = new game(20);

        // RREGULLIM: Armiku nÃ« pozicion tÃ« rastÃ«sishÃ«m Ã§do herÃ«
        java.awt.Point randomEnemyPos = game.field.getRandomFreePosition();
        while (randomEnemyPos.x == game.field.player.pos.x &&
                randomEnemyPos.y == game.field.player.pos.y) {
            randomEnemyPos = game.field.getRandomFreePosition();
        }
        game.field.enemy.pos.x = randomEnemyPos.x;
        game.field.enemy.pos.y = randomEnemyPos.y;

        gameRunning = true;
        weapon1Count = 0;
        weapon2Count = 0;
        foodCount = 0;
        gameStartTime = new Date();
        gameSaved = false;
        fieldPanel.repaint();
        statsPanel.updateStats();
        inventoryPanel.updateInventory();
    }

    private void drawEmoji(Graphics2D g2d, String emoji, String colorName, int x, int y) {
        Font emojiFont = new Font("Segoe UI Emoji", Font.PLAIN, CELL_SIZE - 15);
        g2d.setFont(emojiFont);

        Color emojiColor = getColorByName(colorName);

        switch (emoji) {
            case PLAYER_EMOJI:
                drawPlayerEmoji(g2d, emoji, emojiColor, x, y);
                break;
            case ENEMY_EMOJI:
                drawEnemyEmoji(g2d, emoji, emojiColor, x, y);
                break;
            case FOOD_EMOJI:
                drawFoodEmoji(g2d, emoji, emojiColor, x, y);
                break;
            case WEAPON1_EMOJI:
                drawWeapon1Emoji(g2d, emoji, emojiColor, x, y);
                break;
            case WEAPON2_EMOJI:
                drawWeapon2Emoji(g2d, emoji, emojiColor, x, y);
                break;
            case WALL_EMOJI:
                drawWallEmoji(g2d, emoji, emojiColor, x, y);
                break;
            default:
                drawDefaultEmoji(g2d, emoji, emojiColor, x, y);
                break;
        }
    }

    private Color getColorByName(String colorName) {
        switch (colorName.toLowerCase()) {
            case "player": return PLAYER_COLOR;
            case "enemy": return ENEMY_COLOR;
            case "food": return FOOD_COLOR;
            case "weapon1": return WEAPON1_COLOR;
            case "weapon2": return WEAPON2_COLOR;
            case "wall": return WALL_COLOR;
            case "white": return Color.WHITE;
            default: return Color.WHITE;
        }
    }

    private void drawPlayerEmoji(Graphics2D g2d, String emoji, Color color, int x, int y) {
        FontMetrics fm = g2d.getFontMetrics();
        int emojiWidth = fm.stringWidth(emoji);
        int emojiHeight = fm.getHeight();

        g2d.setColor(new Color(0, 100, 255, 100));
        g2d.fillOval(x + (CELL_SIZE - emojiWidth)/2 - 5,
                y + (CELL_SIZE - emojiHeight)/2 - 5,
                emojiWidth + 10, emojiHeight + 10);

        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(3));
        Font boldFont = new Font("Segoe UI Emoji", Font.BOLD, CELL_SIZE - 10);
        g2d.setFont(boldFont);
        g2d.drawString(emoji, x + (CELL_SIZE - emojiWidth) / 2,
                y + (CELL_SIZE + emojiHeight) / 2 - fm.getDescent());

        g2d.setColor(color);
        g2d.drawString(emoji, x + (CELL_SIZE - emojiWidth) / 2 + 1,
                y + (CELL_SIZE + emojiHeight) / 2 - fm.getDescent() + 1);

        g2d.setColor(new Color(255, 255, 255, 150));
        g2d.drawString(emoji, x + (CELL_SIZE - emojiWidth) / 2 + 2,
                y + (CELL_SIZE + emojiHeight) / 2 - fm.getDescent() + 2);
    }

    private void drawEnemyEmoji(Graphics2D g2d, String emoji, Color color, int x, int y) {
        FontMetrics fm = g2d.getFontMetrics();
        int emojiWidth = fm.stringWidth(emoji);
        int emojiHeight = fm.getHeight();

        g2d.setColor(new Color(255, 0, 0, 50));
        for (int i = 0; i < 3; i++) {
            g2d.fillOval(x + (CELL_SIZE - emojiWidth)/2 - i*2,
                    y + (CELL_SIZE - emojiHeight)/2 - i*2,
                    emojiWidth + i*4, emojiHeight + i*4);
        }

        g2d.setColor(new Color(100, 0, 0));
        g2d.setStroke(new BasicStroke(2));
        Font boldFont = new Font("Segoe UI Emoji", Font.BOLD, CELL_SIZE - 12);
        g2d.setFont(boldFont);
        g2d.drawString(emoji, x + (CELL_SIZE - emojiWidth) / 2,
                y + (CELL_SIZE + emojiHeight) / 2 - fm.getDescent());

        g2d.setColor(color);
        g2d.drawString(emoji, x + (CELL_SIZE - emojiWidth) / 2,
                y + (CELL_SIZE + emojiHeight) / 2 - fm.getDescent());

        g2d.setColor(new Color(255, 150, 0, 100));
        g2d.drawString(emoji, x + (CELL_SIZE - emojiWidth) / 2 - 1,
                y + (CELL_SIZE + emojiHeight) / 2 - fm.getDescent() - 1);
    }

    private void drawFoodEmoji(Graphics2D g2d, String emoji, Color color, int x, int y) {
        FontMetrics fm = g2d.getFontMetrics();
        int emojiWidth = fm.stringWidth(emoji);
        int emojiHeight = fm.getHeight();

        g2d.setColor(new Color(255, 255, 0, 30));
        g2d.fillOval(x + (CELL_SIZE - emojiWidth)/2,
                y + (CELL_SIZE - emojiHeight)/2,
                emojiWidth, emojiHeight);

        g2d.setColor(new Color(150, 50, 50));
        g2d.setStroke(new BasicStroke(1));
        g2d.drawString(emoji, x + (CELL_SIZE - emojiWidth) / 2,
                y + (CELL_SIZE + emojiHeight) / 2 - fm.getDescent());

        g2d.setColor(color);
        g2d.drawString(emoji, x + (CELL_SIZE - emojiWidth) / 2,
                y + (CELL_SIZE + emojiHeight) / 2 - fm.getDescent());

        g2d.setColor(new Color(255, 255, 255, 100));
        g2d.drawString(emoji, x + (CELL_SIZE - emojiWidth) / 2 + 1,
                y + (CELL_SIZE + emojiHeight) / 2 - fm.getDescent() + 1);
    }

    private void drawWeapon1Emoji(Graphics2D g2d, String emoji, Color color, int x, int y) {
        FontMetrics fm = g2d.getFontMetrics();
        int emojiWidth = fm.stringWidth(emoji);
        int emojiHeight = fm.getHeight();

        g2d.setColor(new Color(200, 200, 200, 50));
        g2d.fillRect(x + (CELL_SIZE - emojiWidth)/2 - 2,
                y + (CELL_SIZE - emojiHeight)/2 - 2,
                emojiWidth + 4, emojiHeight + 4);

        g2d.setColor(new Color(150, 150, 150));
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawString(emoji, x + (CELL_SIZE - emojiWidth) / 2,
                y + (CELL_SIZE + emojiHeight) / 2 - fm.getDescent());

        g2d.setColor(color);
        g2d.drawString(emoji, x + (CELL_SIZE - emojiWidth) / 2,
                y + (CELL_SIZE + emojiHeight) / 2 - fm.getDescent());

        GradientPaint gradient = new GradientPaint(
                x, y, new Color(255, 255, 200, 150),
                x + emojiWidth, y + emojiHeight, new Color(255, 255, 0, 50)
        );
        g2d.setPaint(gradient);
        g2d.drawString(emoji, x + (CELL_SIZE - emojiWidth) / 2 + 1,
                y + (CELL_SIZE + emojiHeight) / 2 - fm.getDescent() + 1);
    }

    private void drawWeapon2Emoji(Graphics2D g2d, String emoji, Color color, int x, int y) {
        FontMetrics fm = g2d.getFontMetrics();
        int emojiWidth = fm.stringWidth(emoji);
        int emojiHeight = fm.getHeight();

        g2d.setColor(new Color(0, 255, 200, 100));
        for (int i = 0; i < 2; i++) {
            g2d.fillOval(x + (CELL_SIZE - emojiWidth)/2 - i*3,
                    y + (CELL_SIZE - emojiHeight)/2 - i*3,
                    emojiWidth + i*6, emojiHeight + i*6);
        }

        g2d.setColor(new Color(0, 100, 50));
        g2d.setStroke(new BasicStroke(2));
        Font boldFont = new Font("Segoe UI Emoji", Font.BOLD, CELL_SIZE - 10);
        g2d.setFont(boldFont);
        g2d.drawString(emoji, x + (CELL_SIZE - emojiWidth) / 2,
                y + (CELL_SIZE + emojiHeight) / 2 - fm.getDescent());

        g2d.setColor(color);
        g2d.drawString(emoji, x + (CELL_SIZE - emojiWidth) / 2,
                y + (CELL_SIZE + emojiHeight) / 2 - fm.getDescent());

        g2d.setColor(new Color(255, 255, 255, 150));
        g2d.drawString(emoji, x + (CELL_SIZE - emojiWidth) / 2 + 2,
                y + (CELL_SIZE + emojiHeight) / 2 - fm.getDescent() + 2);
    }

    private void drawWallEmoji(Graphics2D g2d, String emoji, Color color, int x, int y) {
        FontMetrics fm = g2d.getFontMetrics();
        int emojiWidth = fm.stringWidth(emoji);
        int emojiHeight = fm.getHeight();

        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.fillRect(x + (CELL_SIZE - emojiWidth)/2 + 2,
                y + (CELL_SIZE - emojiHeight)/2 + 2,
                emojiWidth, emojiHeight);

        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawString(emoji, x + (CELL_SIZE - emojiWidth) / 2 + 2,
                y + (CELL_SIZE + emojiHeight) / 2 - fm.getDescent() + 2);

        g2d.setColor(color);
        g2d.drawString(emoji, x + (CELL_SIZE - emojiWidth) / 2,
                y + (CELL_SIZE + emojiHeight) / 2 - fm.getDescent());

        g2d.setColor(new Color(200, 200, 200, 50));
        for (int i = 0; i < 3; i++) {
            g2d.drawLine(x + (CELL_SIZE - emojiWidth)/2 + i*3,
                    y + (CELL_SIZE - emojiHeight)/2,
                    x + (CELL_SIZE - emojiWidth)/2 + i*3,
                    y + (CELL_SIZE - emojiHeight)/2 + emojiHeight);
        }
    }

    private void drawDefaultEmoji(Graphics2D g2d, String emoji, Color color, int x, int y) {
        FontMetrics fm = g2d.getFontMetrics();
        int emojiWidth = fm.stringWidth(emoji);
        int emojiHeight = fm.getHeight();

        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawString(emoji, x + (CELL_SIZE - emojiWidth) / 2,
                y + (CELL_SIZE + emojiHeight) / 2 - fm.getDescent());

        g2d.setColor(color);
        g2d.drawString(emoji, x + (CELL_SIZE - emojiWidth) / 2,
                y + (CELL_SIZE + emojiHeight) / 2 - fm.getDescent());
    }

    class FieldPanel extends JPanel {
        public FieldPanel() {
            int size = game.field.size;
            setPreferredSize(new Dimension(size * CELL_SIZE, size * CELL_SIZE));
            setBackground(new Color(20, 0, 40));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            for (int i = 0; i < game.field.size; i++) {
                for (int j = 0; j < game.field.size; j++) {
                    int x = j * CELL_SIZE;
                    int y = i * CELL_SIZE;

                    if (game.field.mapGrid[i][j] == '#') {
                        g2d.setColor(new Color(60, 20, 100));
                        g2d.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                        g2d.setColor(new Color(40, 0, 70));
                        g2d.drawRect(x, y, CELL_SIZE, CELL_SIZE);
                        drawEmoji(g2d, WALL_EMOJI, "wall", x, y);
                    } else {
                        g2d.setColor(new Color(50, 10, 90));
                        g2d.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                        g2d.setColor(new Color(70, 30, 110));
                        g2d.drawRect(x, y, CELL_SIZE, CELL_SIZE);
                    }
                }
            }

            for (items.item item : game.field.items) {
                if (item != null && item.pos != null) {
                    int x = item.pos.y * CELL_SIZE;
                    int y = item.pos.x * CELL_SIZE;

                    if (item instanceof items.food) {
                        drawEmoji(g2d, FOOD_EMOJI, "food", x, y);
                    } else if (item instanceof items.weapon) {
                        items.weapon w = (items.weapon) item;
                        if (w.level == 1) {
                            drawEmoji(g2d, WEAPON1_EMOJI, "weapon1", x, y);
                        } else {
                            drawEmoji(g2d, WEAPON2_EMOJI, "weapon2", x, y);
                        }
                    }
                }
            }

            int enemyX = game.field.enemy.pos.y * CELL_SIZE;
            int enemyY = game.field.enemy.pos.x * CELL_SIZE;
            drawEmoji(g2d, ENEMY_EMOJI, "enemy", enemyX, enemyY);

            int playerX = game.field.player.pos.y * CELL_SIZE;
            int playerY = game.field.player.pos.x * CELL_SIZE;
            drawEmoji(g2d, PLAYER_EMOJI, "player", playerX, playerY);
        }
    }

    class StatsPanel extends JPanel {
        private JLabel hpLabel;
        private JLabel weaponLabel;
        private JLabel itemsLabel;
        private JLabel foodLabel;
        private JLabel weaponsLabel;
        private JLabel stepsLabel;
        private JLabel enemiesLabel;

        public StatsPanel() {
            setLayout(new GridLayout(8, 1, 5, 5));
            setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(new Color(120, 0, 200), 2),
                    "Statistikat",
                    TitledBorder.CENTER,
                    TitledBorder.TOP,
                    new Font("Arial", Font.BOLD, 16),
                    Color.WHITE
            ));
            setBackground(new Color(60, 0, 100));

            hpLabel = createStatLabel();
            weaponLabel = createStatLabel();
            itemsLabel = createStatLabel();
            foodLabel = createStatLabel();
            weaponsLabel = createStatLabel();
            stepsLabel = createStatLabel();
            enemiesLabel = createStatLabel();

            add(createStatRow("HP:", hpLabel));
            add(createStatRow("Arma e pajisur:", weaponLabel));
            add(createStatRow("Objekte totale:", itemsLabel));
            add(createStatRow("Ushqime:", foodLabel));
            add(createStatRow("ArmÃ« gjithsej:", weaponsLabel));
            add(createStatRow("Hapat:", stepsLabel));
            add(createStatRow("Armiq:", enemiesLabel));

            updateStats();
        }

        private JLabel createStatLabel() {
            JLabel label = new JLabel("0");
            label.setFont(new Font("Arial", Font.BOLD, 14));
            label.setForeground(new Color(220, 180, 255));
            label.setHorizontalAlignment(SwingConstants.RIGHT);
            return label;
        }

        private JPanel createStatRow(String text, JLabel valueLabel) {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setOpaque(false);

            JLabel textLabel = new JLabel(text);
            textLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            textLabel.setForeground(Color.WHITE);

            panel.add(textLabel, BorderLayout.WEST);
            panel.add(valueLabel, BorderLayout.EAST);

            return panel;
        }

        public void updateStats() {
            hpLabel.setText(String.valueOf(game.field.player.hp));
            weaponLabel.setText(game.field.player.weapon == null ? "None" :
                    (game.field.player.weapon.level == 1 ? "ðŸ”ª" : "âš”ï¸"));
            itemsLabel.setText(String.valueOf(game.field.player.itemsCollected));
            foodLabel.setText(String.valueOf(game.field.player.foodsCollected));
            weaponsLabel.setText(String.valueOf(game.field.player.weaponsCollected));
            stepsLabel.setText(String.valueOf(game.field.player.steps));
            enemiesLabel.setText(String.valueOf(game.field.player.enemiesDefeated));
        }
    }

    class InventoryPanel extends JPanel {
        private JLabel foodCountLabel;
        private JLabel weapon1CountLabel;
        private JLabel weapon2CountLabel;
        private JLabel equippedWeaponLabel;

        public InventoryPanel() {
            setLayout(new GridLayout(5, 1, 5, 5));
            setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(new Color(120, 0, 200), 2),
                    " Inventari",
                    TitledBorder.CENTER,
                    TitledBorder.TOP,
                    new Font("Arial", Font.BOLD, 16),
                    Color.WHITE
            ));
            setBackground(new Color(60, 0, 100));

            foodCountLabel = createInventoryLabel();
            weapon1CountLabel = createInventoryLabel();
            weapon2CountLabel = createInventoryLabel();
            equippedWeaponLabel = createInventoryLabel();
            equippedWeaponLabel.setForeground(new Color(255, 215, 0));

            add(createInventoryRow(FOOD_EMOJI + " Ushqime:", foodCountLabel));
            add(createInventoryRow(WEAPON1_EMOJI + " ArmÃ« Level 1:", weapon1CountLabel));
            add(createInventoryRow(WEAPON2_EMOJI + " ArmÃ« Level 2:", weapon2CountLabel));

            JPanel equippedPanel = new JPanel(new BorderLayout());
            equippedPanel.setOpaque(false);
            JLabel equippedText = new JLabel("Arma e pajisur: ");
            equippedText.setFont(new Font("Arial", Font.PLAIN, 14));
            equippedText.setForeground(Color.WHITE);
            equippedPanel.add(equippedText, BorderLayout.WEST);
            equippedPanel.add(equippedWeaponLabel, BorderLayout.EAST);
            add(equippedPanel);

            updateInventory();
        }

        private JLabel createInventoryLabel() {
            JLabel label = new JLabel("0");
            label.setFont(new Font("Arial", Font.BOLD, 14));
            label.setForeground(new Color(220, 180, 255));
            label.setHorizontalAlignment(SwingConstants.RIGHT);
            return label;
        }

        private JPanel createInventoryRow(String text, JLabel valueLabel) {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setOpaque(false);

            JLabel textLabel = new JLabel(text);
            textLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            textLabel.setForeground(Color.WHITE);

            panel.add(textLabel, BorderLayout.WEST);
            panel.add(valueLabel, BorderLayout.EAST);

            return panel;
        }

        public void updateInventory() {
            foodCountLabel.setText(String.valueOf(foodCount));
            weapon1CountLabel.setText(String.valueOf(weapon1Count));
            weapon2CountLabel.setText(String.valueOf(weapon2Count));

            if (game.field.player.weapon != null) {
                equippedWeaponLabel.setText(
                        game.field.player.weapon.level == 1 ? WEAPON1_EMOJI + " Level 1" :
                                WEAPON2_EMOJI + " Level 2"
                );
            } else {
                equippedWeaponLabel.setText("None");
            }
        }
    }

    class MoveAction extends AbstractAction {
        private char direction;

        public MoveAction(char direction) {
            this.direction = direction;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            movePlayer(direction);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            gui gui = new gui();
            gui.setVisible(true);
        });
    }
}
