# Project Rules

## Language

- **All code and documentation must be in English** — variable names, comments, docs, commit messages

## Code Style

- **Never use `var`** — always declare with explicit type (`int`, `String`, `List<T>`, etc.)
- **Naming conventions:**
  - Variables, parameters, fields: `camelCase`
  - Static constants: `ALL_CAPS_WITH_UNDERSCORES`
  - Methods/functions: `PascalCase` (upper camelCase, first letter capitalized)
- **Enforce access modifiers** — fields must be `private` by default; only expose via getters/setters or make `public` when strictly necessary
