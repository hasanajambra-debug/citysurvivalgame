# City Survival 🧙👾

Lojë survival në Java (Swing GUI + version konsole) për detyrën e kursit.

## Si luhet
- Lëviz me **W/A/S/D** ose shigjetat
- **P** = pauzë, **Q** = dalje
- Mblidh 🍎 për HP, mblidh armë 🔪 (Level 1) dhe ⚔️ (Level 2)
- Armiku 👾 të ndjek; në takim bëhet dyluftim dhe humbet 1 HP

## Si ta nisësh
```bash
javac -encoding UTF-8 -d out src/*/*.java
java -cp out game.gamelauncher
```
(Ekzekuto nga rrënja e projektit që `map.txt` të gjendet.)

## Struktura
| Package | Përmbajtja |
|---|---|
| `entities` | entity, player, enemy |
| `items` | item, food, weapon |
| `field` | field (harta, mure, pozicione) |
| `game` | game, gui, gamelauncher, position |

