# Project Rules

## Language

- **All code and documentation must be in English** — variable names, comments, docs, commit messages

## Code Style

- **Never use `var`** — always declare with explicit type (`int`, `String`, `List<T>`, etc.)
- **Naming conventions:**
  - Variables and method names must start with a letter, followed by letters, digits, or underscores
  - Variables, parameters, fields, methods: `camelCase` — must start with a lowercase letter
  - Classes: `PascalCase` — must start with an uppercase letter
  - Constants: `ALL_CAPS_WITH_UNDERSCORES`
- **Java is case-sensitive** — `myVar`, `MyVar`, and `MYVAR` are different identifiers
- **Every statement must end with `;`**
- **Control structures** (`if`, `for`, `while`, etc.) do not end with `;` — they use `{ }` blocks
- **Enforce access modifiers** — fields must be `private` by default; only expose via getters/setters or make `public` when strictly necessary
