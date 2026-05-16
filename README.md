# Parcial — Backend Genérico para Juego de Mesa 2D

Backend REST con Spring Boot para un juego de mesa 2D multijugador por turnos. Sirve como base reutilizable para cualquier parcial de juego de mesa: tablero configurable, movimiento por coordenadas, sistema de puntaje, items, salida, y BFS incluido.

---

## Stack tecnológico

| Herramienta | Versión | Para qué sirve |
|---|---|---|
| Java | 21 | Lenguaje principal |
| Spring Boot | 3.3.4 | Framework base (web, JPA, validación) |
| H2 Database | Runtime | Base de datos en memoria (sin instalar nada) |
| Spring Data JPA | — | Repositorios, transacciones, ORM |
| Lombok | — | Elimina boilerplate (getters, builders, constructores) |
| Jackson | — | Serializa el tablero `int[][]` a JSON en la BD |
| SpringDoc OpenAPI | 2.6.0 | Swagger UI automático |
| Spring Validation | — | Validación de requests con anotaciones (`@NotBlank`, `@Min`) |
| JUnit 5 + Mockito + AssertJ | — | Tests unitarios del servicio |

---

## Estructura del proyecto

```
src/main/java/org/example/
├── controller/
│   └── GameController.java        # 5 endpoints REST
├── service/
│   ├── GameService.java           # Lógica de negocio del juego
│   └── BoardService.java          # Operaciones sobre el tablero
├── model/
│   ├── entity/
│   │   ├── Game.java              # Entidad JPA: tablero, estado, jugadores
│   │   └── Player.java            # Entidad JPA: nombre, posición, score
│   ├── dto/
│   │   ├── CreateGameRequest.java # Body para crear partida
│   │   ├── MoveRequest.java       # Body para mover jugador
│   │   └── GameResponse.java      # Respuesta unificada con board + players
│   └── enums/
│       ├── GameStatus.java        # WAITING | IN_PROGRESS | FINISHED
│       └── CellType.java          # EMPTY(0) WALL(1) PLAYER(2) ITEM(3) EXIT(4)
├── repository/
│   ├── GameRepository.java
│   └── PlayerRepository.java
└── exception/
    ├── GameException.java         # Excepción de dominio
    └── GlobalExceptionHandler.java # Manejo global de errores HTTP
```

---

## API REST

### Crear partida
```http
POST /api/games
Content-Type: application/json

{
  "width": 5,
  "height": 5,
  "playerName": "Emanuel"
}
```
Crea tablero `width x height`. Jugador aparece en `(0,0)`, EXIT en `(height-1, width-1)`. Estado inicial: `WAITING`.

### Unirse a partida
```http
POST /api/games/{id}/join?playerName=Juan
```
Segundo jugador se une. Estado pasa a `IN_PROGRESS`. Acepta cualquier cantidad de jugadores mientras esté en `WAITING`.

### Mover jugador
```http
POST /api/games/{id}/move
Content-Type: application/json

{
  "playerId": 1,
  "x": 1,
  "y": 0
}
```
Mueve al jugador a la celda `(x, y)`. Validaciones: solo el jugador del turno actual puede mover, la celda no puede ser `WALL` ni estar fuera de límites.

### Ver estado de partida
```http
GET /api/games/{id}
```

### Listar partidas abiertas
```http
GET /api/games/open
```
Devuelve todas las partidas en estado `WAITING`.

---

## Lógica del juego

### Ciclo de vida
```
WAITING → (segundo jugador se une) → IN_PROGRESS → (alguien llega al EXIT) → FINISHED
```

### Turnos
Rotación circular entre todos los jugadores. Al terminar un turno, `currentPlayerId` pasa al siguiente en la lista (`(idx + 1) % players.size()`).

### Puntaje
| Evento | Puntos |
|---|---|
| Recoger ITEM (celda 3) | +10 |
| Llegar al EXIT (celda 4) | +100 + fin de partida |

### Tablero
- Representado como `int[][]` (board[fila][columna])
- Almacenado en BD como JSON string (columna `boardJson`, longitud 10000)
- Serializado/deserializado por `BoardService` con Jackson

---

## Algoritmos en BoardService

### BFS — Camino mínimo
```java
boardService.shortestPath(board, new int[]{startX, startY}, new int[]{endX, endY})
// Devuelve List<int[]> con el camino, vacío si no hay ruta
```
BFS estándar con 4 direcciones. Ignora celdas `WALL`. Listo para usar si el parcial pide pathfinding.

### Fisher-Yates Shuffle
```java
boardService.shuffle(deck)
// Mezcla in-place un int[], distribución uniforme O(n)
```
Útil para repartir cartas, generar tableros aleatorios, etc.

---

## Tipos de celda

```java
EMPTY  = 0   // celda libre
WALL   = 1   // bloqueada, no se puede pisar
PLAYER = 2   // posición de un jugador
ITEM   = 3   // item recogible (+10 pts)
EXIT   = 4   // salida de la partida (+100 pts, termina juego)
```

---

## Base de datos

H2 en memoria, sin instalación. Al reiniciar la app los datos se pierden (`create-drop`).

- **Consola H2:** `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:gamedb`
  - User: `sa` / Password: *(vacío)*

---

## Documentación interactiva

Swagger UI disponible automáticamente al levantar la app:

```
http://localhost:8080/swagger-ui/index.html
```

Permite probar todos los endpoints sin Postman.

---

## Tests

5 tests unitarios en `GameServiceTest` con Mockito:

| Test | Qué verifica |
|---|---|
| `makeMove_validMove_updatesPlayerPosition` | Posición del jugador se actualiza |
| `makeMove_wrongTurn_throwsGameException` | No se puede mover si no es tu turno |
| `makeMove_invalidCell_throwsGameException` | No se puede mover a celda bloqueada |
| `makeMove_reachExit_setsFinishedAndAddsScore` | Llegar al EXIT termina el juego y da 100 pts |
| `getGame_nonExistentId_throwsGameException` | ID inexistente lanza excepción correcta |

Correr tests:
```bash
mvn test
```

---

## Levantar el proyecto

```bash
mvn spring-boot:run
```

Requisitos: Java 21, Maven 3.x. No requiere BD externa.

---

## Claude Code — Skills y Agentes instalados

Este proyecto tiene skills de Claude Code instalados en `.agents/skills/`. Se activan automáticamente al usar Claude Code en este directorio.

### Skills instalados

| Skill | Fuente | Comando | Para qué sirve |
|---|---|---|---|
| `caveman` | `juliusbrussee/caveman` | `/caveman` | Respuestas ultra-comprimidas ~75% menos tokens. Niveles: `lite`, `full`, `ultra` |
| `find-skills` | `vercel-labs/skills` | `/find-skills` | Busca e instala skills del ecosistema de agentes vía `npx skills` |
| `game-development` | `sickn33/antigravity-awesome-skills` | `/game-development` | Orquestador para desarrollo de juegos. Rutea a skills específicos según la plataforma |
| `java-springboot` | `github/awesome-copilot` | `/java-springboot` | Best practices para desarrollar aplicaciones Spring Boot en Java |
| `create-spring-boot-java-project` | `github/awesome-copilot` | `/create-spring-boot-java-project` | Genera skeleton completo de proyecto Spring Boot con Java |
| `unit-test-service-layer` | `giuseppe-trisciuoglio/developer-kit` | `/unit-test-service-layer` | Tests de capa de servicio con JUnit 5 + Mockito — aplica directo sobre `GameService` |
| `nodejs-backend-patterns` | `wshobson/agents` | `/nodejs-backend-patterns` | Patrones de backend Node.js (Express/Fastify): middleware, auth, error handling, REST, WebSockets |

### Cómo usar los skills

```bash
# Activar modo caveman (respuestas cortas, sin relleno)
/caveman

# Con nivel específico
/caveman lite     # frases completas pero sin relleno
/caveman ultra    # máxima compresión, abreviaciones

# Buscar un skill para una tarea
/find-skills      # y describir qué necesitás
```

### Gestionar skills con la CLI

```bash
# Buscar skills disponibles
npx skills find <query>

# Instalar un skill
npx skills add <owner/repo@skill>

# Ver skills instalados / actualizaciones
npx skills check
npx skills update
```

Los skills se guardan en `.agents/skills/` y el lock file en `skills-lock.json`. Ambos se versionen en git para que cualquiera que clone el repo tenga los mismos skills disponibles.

---

## Cómo extender para tu parcial

1. **Agregar tipo de celda**: agrega valor al enum `CellType`
2. **Nueva regla de movimiento**: modifica `GameService.makeMove()`
3. **Más algoritmos de tablero**: agrega métodos a `BoardService`
4. **Persistir datos entre reinicios**: cambiar H2 a PostgreSQL en `application.properties`
5. **Agregar cartas/mazo**: usar `boardService.shuffle()` sobre un array de IDs de cartas