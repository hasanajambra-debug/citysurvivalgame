package game;
import field.field;
import items.item;
import java.util.Random;
import java.util.Scanner;

public class game {
    public field field;
    Scanner sc = new Scanner(System.in);

    public game(int size) {
        field = new field(size);
    }

    public void start() {
        boolean over = false;
        while (!over) {
            field.print();
            showStats();
            System.out.print("LÃ«viz (W/A/S/D ose Q pÃ«r dalje): ");
            String input = sc.next();
            if (input.isEmpty()) continue;
            char move = Character.toUpperCase(input.charAt(0));

            if (move == 'Q') {
                System.out.print("Je i sigurt qÃ« do ta mbyllÃ«sh lojÃ«n? [Y/N]: ");
                String confIn = sc.next();
                char confirm = confIn.isEmpty() ? 'N' : Character.toUpperCase(confIn.charAt(0));
                if (confirm == 'Y') {
                    System.out.println("â„¹ï¸ Loja u ndÃ«rpre nga lojtari.");
                    System.out.println("â–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆ");
                    System.out.println("â„¹ï¸ L O J A U N D Ã‹ R P R E â„¹ï¸");
                    System.out.println("Lojtari vendosi ta mbyllÃ« lojÃ«n.");
                    System.out.println("â–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆ");
                    break;
                } else {
                    System.out.println("ðŸ‘‰ Loja vazhdon!");
                    continue;
                }
            }

            if (move == 'W' || move == 'A' || move == 'S' || move == 'D') {
                int newX = field.player.pos.x;
                int newY = field.player.pos.y;

                if (move == 'W') newX--;
                else if (move == 'S') newX++;
                else if (move == 'A') newY--;
                else if (move == 'D') newY++;

                if (field.canMoveTo(newX, newY)) {
                    field.player.move(move, field.size);
                } else {
                    System.out.println("â›” Nuk mund tÃ« lÃ«vizÃ«sh nÃ« mur!");
                    continue;
                }
            } else {
                System.out.println("â›” KomandÃ« e pavlefshme. PÃ«rdor W/A/S/D ose Q.");
                continue;
            }

            item item = field.checkItemAtPlayer();
            if (item != null) field.player.collect(item);

            field.enemy.moveTowardsPlayer(field.player, field);

            if (field.player.pos.x == field.enemy.pos.x && field.player.pos.y == field.enemy.pos.y) {
                fight();
            }

            if (field.player.hp <= 0) {
                System.out.println("ðŸ’€ GAME OVER ðŸ›‘ Lojtari humbi!");
                System.out.println("â–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆ");
                System.out.println("ðŸ’€ðŸ›‘ G A M E O V E R ðŸ›‘ðŸ’€");
                System.out.println("Lojtari humbi, loja pÃ«rfundoi!");
                System.out.println("â–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆ");
                over = true;
            }
        }
        showFinalStats();
    }

    void fight() {
        System.out.println("âš”ï¸ Dyluftim! Lojtari ðŸ§™ vs Armiku ðŸ‘¾");

        int enemyWeapon = new Random().nextInt(2) + 1; // Armiku ka gjithmonÃ« armÃ« (1 ose 2)
        System.out.println("Armiku ka armÃ« " + (enemyWeapon == 1 ? "ðŸ”ª Level 1" : "âš”ï¸ Level 2"));

        int playerWeaponLevel = (field.player.weapon == null ? 0 : field.player.weapon.level);

        // PÃ«rcakto fituesin
        if (playerWeaponLevel > enemyWeapon) {
            System.out.println("ðŸ§™ Lojtari fitoi dyluftimin!");
            System.out.println("ðŸ‘¾ Armiku u mposhte dhe u largua nÃ« pozicion tÃ« ri!");
            field.player.enemiesDefeated++;
        } else if (enemyWeapon > playerWeaponLevel) {
            System.out.println("ðŸ‘¾ Armiku fitoi dyluftimin!");
        } else {
            System.out.println("âš–ï¸ Barazim nÃ« dyluftim!");
        }

        // RREGULL I RI: Lojtari humbet HP PAS Ã‡DO dyluftimi
        field.player.hp--;
        System.out.println("ðŸ’” Lojtari humbet 1 HP nga dyluftimi!");
        System.out.println("HP i mbetur: " + field.player.hp + " â¤ï¸");

        if (field.player.hp <= 0) {
            System.out.println("ðŸ’€ GAME OVER ðŸ›‘ Lojtari humbi!");
            System.out.println("â–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆ");
            System.out.println("ðŸ’€ðŸ›‘ G A M E O V E R ðŸ›‘ðŸ’€");
            System.out.println("Lojtari humbi, loja pÃ«rfundoi!");
            System.out.println("â–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆ");
        } else {
            // Pas Ã§do dyluftimi, armiku lÃ«viz nÃ« pozicion tÃ« ri
            relocateEnemy();
        }
    }

    // MetodÃ« e re pÃ«r tÃ« rivendosur armikun
    void relocateEnemy() {
        java.awt.Point newPos = field.getRandomFreePosition();

        // Sigurohu qÃ« armiku nuk vendoset mbi lojtarin
        while (newPos.x == field.player.pos.x && newPos.y == field.player.pos.y) {
            newPos = field.getRandomFreePosition();
        }

        field.enemy.pos.x = newPos.x;
        field.enemy.pos.y = newPos.y;
    }

    void showStats() {
        System.out.println("=== Paneli Statistikor ===");
        System.out.println("HP â¤ï¸: " + field.player.hp);
        System.out.println("Arma: " + (field.player.weapon == null ? "None" : (field.player.weapon.level == 1 ? "ðŸ”ª" : "âš”ï¸")));
        System.out.println("Objekte tÃ« mbledhura ðŸŽ: " + field.player.itemsCollected);
        System.out.println("Ushqime tÃ« ngrÃ«na ðŸŽ: " + field.player.foodsCollected);
        System.out.println("ArmÃ« tÃ« mbledhura ðŸ”ªâš”ï¸: " + field.player.weaponsCollected);
        System.out.println("Hapat e hedhur ðŸ‘£: " + field.player.steps);
        System.out.println("Armiq tÃ« mposhtur ðŸ‘¾: " + field.player.enemiesDefeated);
        System.out.println("==========================");
    }

    void showFinalStats() {
        System.out.println("\n=== Statistikat Finale ===");
        System.out.println("HP pÃ«rfundimtar â¤ï¸: " + field.player.hp);
        System.out.println("Arma e pajisur: " + (field.player.weapon == null ? "None" : (field.player.weapon.level == 1 ? "ðŸ”ª" : "âš”ï¸")));
        System.out.println("Objekte tÃ« mbledhura ðŸŽ: " + field.player.itemsCollected);
        System.out.println("Ushqime tÃ« ngrÃ«na ðŸŽ: " + field.player.foodsCollected);
        System.out.println("ArmÃ« tÃ« mbledhura ðŸ”ªâš”ï¸: " + field.player.weaponsCollected);
        System.out.println("Hapat e hedhur ðŸ‘£: " + field.player.steps);
        System.out.println("Armiq tÃ« mposhtur ðŸ‘¾: " + field.player.enemiesDefeated);
        System.out.println("==========================");
    }
}