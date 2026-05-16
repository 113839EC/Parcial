# JUSTIFICATION.md — Big O Complexity of the Search Algorithm

## Chosen Algorithm: Linear Search with Fixed-Size Sliding Window

### Description

The diary of N winds is traversed with two nested loops:
- Outer loop: starting index `i` from 0 to N-1  → O(N) iterations
- Inner loop: length `len` from 3 to 5         → maximum 3 iterations (constant)

For each candidate, validation and score calculation are performed on a sublist of at most 5 elements.

### Component Analysis

| Component | Cost |
|------------|-------|
| Total candidates evaluated | `3 * N` = O(N) |
| Validation per candidate (4 rules over ≤5 elements) | O(1) |
| Score calculation (base + bonus) over ≤5 elements | O(1) |
| Comparison and update of best | O(1) |

### Total Complexity

```
T(N) = N * 3 * O(1) = O(N)
```

The algorithm is **O(N)** in time, where N = number of winds in the diary.

Additional space is **O(1)**: only the best strophe found is stored (at most 5 references).

### Why There Is No Faster Alternative

The set of valid candidates cannot be reduced without reading each wind at least once. Reading all winds is Ω(N), therefore O(N) is optimal.

### Comparison with Naive Brute Force

| Approach | Complexity |
|---------|-------------|
| Generate all possible subsets | O(2^N) |
| Fixed-length sliding window 3-5 | **O(N)** ← chosen |

The key is that the restriction to **continuous** subsequences of bounded length (3–5) reduces the search space from exponential to linear.
