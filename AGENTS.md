# Coding Guidelines & Agent Rules

All AI agents and developers contributing to this codebase must adhere strictly to the following guidelines.

---

## 1. Code Formatting & Spacing

- **Empty Line After Code Blocks**:
  - Always insert an empty line immediately after any code block (e.g., after the closing brace `}` of an `if`, `for`, `while`, `try`, or local block) before any subsequent statements.
  - In chained constructs (e.g., `if-else` or `try-catch-finally`), keep clauses attached (`} else {`, `} catch (...) {`), placing the empty line after the final closing brace of the full construct.
  - Separate method declarations and class definitions with empty lines.

- **Mandatory Braces for Control Flow**:
  - All control flow statements (`if`, `else`, `for`, `while`, `do-while`) **must** always use braces `{ ... }`, even if the body contains only a single line.
  - Never use single-line braceless statements (e.g., avoid `if (condition) return;`).

---

## 2. Control Flow & Structure

- **Early Returns (Guard Clauses)**:
  - Prefer early returns and guard clauses over deeply nested conditional blocks.
  - Check failure or invalid conditions first and exit/return early to keep the main execution path clean and un-nested.

---

## 3. Type Inference & Variables

- **`var` Usage for Local Variables**:
  - Use `var` for local variable declarations whenever the type is understandable from context (e.g., constructor invocations, clear factory methods, or obvious RHS expressions):
    ```java
    var stack = new ItemStack(ModItems.COPPER_PIPE.get());
    var pos = context.getClickedPos();
    ```
  - Use explicit types when the expression's return type is ambiguous or when working with primitive conversions where clarity is essential.
  - Fields, method parameters, and return types must always be explicitly typed.

---

## 4. Annotations

- **Mandatory Annotations**:
  - Always apply annotations whenever necessary, recommended by compiler/IDE warnings, or required by the platform:
    - `@Override` on all overriding methods.
    - `@Nullable` / `@NonNull` / `@NotNull` for nullability contracts on method parameters and returns.
    - NeoForge event and subscriber annotations (`@SubscribeEvent`, `@EventBusSubscriber`) where appropriate.

---
