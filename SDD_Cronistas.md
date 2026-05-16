# SDD — School of Chroniclers: Strophe Selection

## METADATA
```
version: 1.0
language: Java 21
build: Maven
entry_point: org.example.cronistas.Main
test_framework: JUnit 5 (via spring-boot-starter-test)
coverage_target: >80% lines
```

---

## NARRATIVE
Cronistas transcribes winds into a chronological diary. System extracts the strophe (continuous subsequence) with maximum score.

---

## ENTITIES

### Viento
| Field      | Type    | Constraint              |
|------------|---------|-------------------------|
| id         | int     | unique in diario        |
| marca      | char    | A-Z uppercase           |
| corriente  | Corriente (enum) | NORTE/ESTE/SUR/OESTE |
| intensidad | int     | 1–9                     |

### Diario
- Ordered list of Viento (chronological)
- Order: [V1, V2, V3, ...]

### Estrofa
- Continuous, consecutive sublist of Diario
- No skipping, no reversing
- Valid lengths: 3, 4, or 5 vientos

---

## VALIDATION RULES (ALL must pass)

### Rule 1 — Length
```
3 <= estrofa.size() <= 5
```

### Rule 2 — Strictly Increasing Marcas
```
for i in 1..n: vientos[i].marca > vientos[i-1].marca
```
- Valid: A→C→F→K
- Invalid: A→C→C→K (equal), A→F→C→K (decreasing)

### Rule 3 — Corriente Rotation (clockwise)
```
cycle: NORTE → ESTE → SUR → OESTE → NORTE
for i in 1..n: vientos[i].corriente == next(vientos[i-1].corriente)
```
- Valid: N→E→S, S→O→N, O→N→E
- Any starting point allowed

### Rule 4 — Intensity Limit
```
sum(vientos[i].intensidad) <= I_MAX
```
- I_MAX is a system parameter

---

## SCORING SYSTEM

```
puntaje = base + bonus_A + bonus_B
base    = sum(intensidades)
```

### Bonus A — Continuous Calligraphy (+5)
```
condition: for i in 1..n: vientos[i].marca == vientos[i-1].marca + 1
```
- With bonus: D→E→F→G
- Without: D→E→G→H (gap at F)

### Bonus B — Final Storm (+3)
```
condition: vientos[last].intensidad > vientos[i].intensidad for ALL i < last
```
- With bonus: [2,4,3,7] — 7 beats all
- Without: [2,9,3,7] — 7 does not beat 9

> Bonuses accumulate: max possible = base + 5 + 3

---

## ALGORITHM

### Search Strategy
```
for i in 0..N-1:
  for len in 3..5:
    if i+len <= N:
      candidata = vientos[i .. i+len-1]
      if valid(candidata, I_MAX):
        score = calcScore(candidata)
        if score > maxScore: best = candidata
```

### Complexity
- Total candidates: O(3N) = O(N)  [len fixed 3/4/5]
- Validation per candidate: O(1)  [max 5 elements]
- **Total: O(N)**

---

## OUTPUT FORMAT
```
Estrofa seleccionada:
(ID:1, Marca:A, Corriente:NORTE, Intensidad:5)
...
Puntaje: 23
Bonus aplicados: Caligrafía continua (+5), Tormenta final (+3)
```
If no valid estrofa: `Sin estrofa válida. Puntaje: 0`

---

## EDGE CASES (mandatory)

| Case | Expected behavior |
|------|-------------------|
| Diario < 3 vientos | Return empty, score 0 |
| No rotation match in any 3-window | Return empty, score 0 |
| min sum of 3 > I_MAX | Return empty, score 0 |
| Repeated marca in candidates | Fails Rule 2, skip |
| Multiple estrofas same max score | Return any one |

---

## CLASS STRUCTURE

```
org.example.cronistas/
├── model/
│   ├── Corriente.java       # enum NORTE/ESTE/SUR/OESTE + siguiente()
│   ├── Viento.java          # id, marca, corriente, intensidad
│   ├── Diario.java          # ordered list of Viento
│   └── Estrofa.java         # vientos + puntajeBase + bonusCaligrafia + bonusTormenta
├── validator/
│   └── ValidadorEstrofa.java    # 4 rules + esValida()
├── calculator/
│   └── CalculadorBonusEstrofa.java  # calcularBase/BonusCaligrafia/BonusTormenta
├── searcher/
│   └── BuscadorEstrofaMaxima.java  # O(N) search
└── Main.java                # hardcoded test case, console output
```

---

## TEST COVERAGE REQUIREMENTS

| Area | Tests |
|------|-------|
| Rule 1 (length) | valid 3,4,5; invalid 2,6 |
| Rule 2 (marcas) | strictly increasing; equal; decreasing |
| Rule 3 (rotation) | correct sequence; wrong step; any start |
| Rule 4 (intensity) | at limit; over limit |
| Bonus A | consecutive letters; gap present |
| Bonus B | last > all; last not > one |
| Search | best found; no valid → empty; < 3 vientos |
| Edge cases | all mandatory edge cases above |

---

## TEST CASE (hardcoded in Main)

```
Viento(1, 'A', NORTE, 5)
Viento(2, 'B', ESTE,  3)
Viento(3, 'C', SUR,   7)
Viento(4, 'D', ESTE,  2)
Viento(5, 'E', SUR,   4)
Viento(6, 'F', OESTE, 6)
I_MAX = 20

Expected best: [V1,V2,V3] → puntaje 23 (base 15 + caligrafía +5 + tormenta +3)
```
