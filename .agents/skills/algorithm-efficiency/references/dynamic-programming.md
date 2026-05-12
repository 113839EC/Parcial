# Dynamic Programming — Deep Reference

## When to Apply DP

Apply dynamic programming when a problem has BOTH:
1. **Overlapping subproblems** — the same sub-calculation appears multiple times
2. **Optimal substructure** — the optimal solution is built from optimal sub-solutions

**Classic DP problems**: Fibonacci, coin change, longest common subsequence,
0/1 knapsack, edit distance, minimum path sum.

---

## Pattern 1: Top-Down (Memoization)

Start from the top (original problem), recurse down, cache results.

```java
// Fibonacci with memoization
Map<Integer, Long> memo = new HashMap<>();

long fib(int n) {
    if (n <= 1) return n;
    if (memo.containsKey(n)) return memo.get(n); // cache hit
    long result = fib(n - 1) + fib(n - 2);
    memo.put(n, result);                          // cache store
    return result;
}
// Time: O(n), Space: O(n)
```

**Use when**: the recursive structure is natural and not all subproblems need solving.

---

## Pattern 2: Bottom-Up (Tabulation)

Start from base cases, build up to the answer iteratively.

```java
// Fibonacci bottom-up — O(n) time, O(1) space
long fib(int n) {
    if (n == 0) return 0;
    long a = 0, b = 1;
    for (int i = 2; i <= n; i++) {
        long temp = a + b;
        a = b;
        b = temp;
    }
    return b;
}
```

**Use when**: all subproblems must be solved, and you want to avoid recursion overhead.
Bottom-up is generally preferred in Java because it:
- Avoids stack overflow
- Has lower constant factors (no method call overhead)
- Is easier for the JIT compiler to optimize

---

## Recognizing DP Opportunities

Ask these questions about the recursive solution:
- Are the same arguments passed to recursive calls multiple times? → YES → Add memoization
- Can I define `f(n)` purely in terms of `f(n-1)`, `f(n-2)`, etc.? → YES → Classic DP
- Is the search space a DAG (directed acyclic graph) of states? → YES → DP applies

---

## Space Optimization

Often DP tables can be compressed when you only need the last k rows/values.

```java
// Full table — O(n) space
int[] dp = new int[n + 1];
dp[0] = 0; dp[1] = 1;
for (int i = 2; i <= n; i++) dp[i] = dp[i-1] + dp[i-2];

// Space-optimized — O(1) space (only need last 2 values)
int a = 0, b = 1;
for (int i = 2; i <= n; i++) {
    int c = a + b;
    a = b;
    b = c;
}
```

---

## Complexity Comparison: Fibonacci

| Approach           | Time   | Space  | Notes                            |
|--------------------|--------|--------|----------------------------------|
| Naive recursion    | O(2ⁿ)  | O(n)   | Unusable for n > 40              |
| Top-down (memo)    | O(n)   | O(n)   | Readable, safe for moderate n    |
| Bottom-up (iter)   | O(n)   | O(1)   | Best for large n                 |
| Matrix exponentiation | O(log n) | O(1) | For extremely large n          |
