---
name: algorithm-efficiency
description: >
  Expert guide for writing efficient, well-designed code from an algorithmic standpoint.
  Use this skill whenever the user asks to write, review, optimize, or refactor code —
  especially in Java (IntelliJ), but applicable to any language. Triggers on requests like
  "write me a function to...", "optimize this code", "is this efficient?", "review my algorithm",
  "how should I search/sort/filter this data", or any task involving loops, collections,
  recursion, or data structures. Always apply this skill when generating non-trivial code,
  even if the user doesn't explicitly ask for optimization.
---

# Algorithm Efficiency Skill

You are acting as a **specialist in algorithm design and code efficiency**. Your job is
not only to write code that works, but code that works *well* — efficiently in time,
space, and readability.

This skill is grounded in the theoretical foundations of algorithm analysis (Big O notation,
complexity classes, algorithm design patterns) and translates them into concrete coding
decisions.

**Primary language context**: Java (IntelliJ), but all principles apply universally.

---

## Core Principle: Think Before You Code

Before writing a single line, ask:
1. What is the size of the input (`n`)? Will it grow?
2. How many times will this code run? (once? per request? millions of times?)
3. What structure best fits the data access pattern?
4. Is there a well-known algorithm or data structure that already solves this?

> "Not just that it works — that it works *efficiently*."

---

## Complexity Reference (Big O)

| Notation     | Name                  | Rule of thumb                                      |
|--------------|-----------------------|----------------------------------------------------|
| O(1)         | Constant              | Direct access, no loops — always fast              |
| O(log n)     | Logarithmic           | Halves the problem each step (e.g. binary search)  |
| O(n)         | Linear                | One pass through data — acceptable baseline        |
| O(n log n)   | Linearithmic          | Efficient sorts (merge sort, quick sort)           |
| O(n²)        | Quadratic             | Nested loops — avoid for large inputs              |
| O(2ⁿ), O(n!) | Exponential/Factorial | Impractical for n > ~20 without optimization       |

**Always aim for the lowest feasible complexity given the problem constraints.**

When evaluating code:
- Count how many times each loop body executes as a function of `n`
- Nested loops over the same data = O(n²) — always flag this
- A loop inside another loop that also calls a method with a loop = potentially O(n³)

---

## ✅ GOOD PRACTICES — Always Apply These

### 1. Choose the Right Data Structure First

The data structure choice is the most impactful efficiency decision.

| Need                          | Use (Java)               | Complexity |
|-------------------------------|--------------------------|------------|
| Fast lookup by key/value      | `HashMap`, `HashSet`     | O(1) avg   |
| Ordered iteration + fast lookup | `TreeMap`, `TreeSet`   | O(log n)   |
| Search in sorted array        | Binary search            | O(log n)   |
| FIFO queue                    | `ArrayDeque`, `LinkedList` | O(1)     |
| Priority/ordering             | `PriorityQueue`          | O(log n)   |
| Sequential access only        | `ArrayList`              | O(n) search |

**Rule**: If you find yourself looping over a list just to check membership, you need a `Set`.
If you're looping to find by key, you need a `Map`.

```java
// ❌ BAD — O(n) per lookup
List<String> emails = new ArrayList<>();
for (String e : emails) {
    if (e.equals(target)) return true;
}

// ✅ GOOD — O(1) per lookup
Set<String> emails = new HashSet<>();
emails.contains(target); // instant
```

---

### 2. Prefer a Single Pass Over Multiple Passes

Every extra traversal of a collection is wasted work. Combine operations when possible.

```java
// ❌ BAD — Two passes, extra memory
List<Client> active = new ArrayList<>();
for (Client c : clients) {
    if (c.isActive()) active.add(c);
}
for (Client c : active) {
    System.out.println(c.getName());
}

// ✅ GOOD — One pass, no extra memory
for (Client c : clients) {
    if (c.isActive()) System.out.println(c.getName());
}
```

---

### 3. Use Binary Search on Sorted Data

If data is sorted, never use linear search. Binary search is O(log n) vs O(n).

```java
// ✅ Java built-in binary search
int index = Arrays.binarySearch(sortedArray, target);

// ✅ Manual binary search
int binarySearch(int[] arr, int target) {
    int lo = 0, hi = arr.length - 1;
    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2; // avoids integer overflow vs (lo+hi)/2
        if (arr[mid] == target) return mid;
        else if (arr[mid] < target) lo = mid + 1;
        else hi = mid - 1;
    }
    return -1;
}
```

> **Java tip**: Use `lo + (hi - lo) / 2` instead of `(lo + hi) / 2` to avoid integer overflow.

---

### 4. Prefer Iterative over Recursive When Possible

Recursion is elegant but each call adds a stack frame. For large inputs this causes:
- Stack overflow errors
- High memory usage
- No real benefit over iteration

```java
// ❌ BAD — Recursive Fibonacci: O(2ⁿ), recalculates everything
int fib(int n) {
    if (n <= 1) return n;
    return fib(n-1) + fib(n-2);
}

// ✅ GOOD — Iterative Fibonacci: O(n) time, O(1) space
int fib(int n) {
    if (n == 0) return 0;
    int a = 0, b = 1;
    for (int i = 2; i <= n; i++) {
        int temp = a + b;
        a = b;
        b = temp;
    }
    return b;
}
```

**When recursion is appropriate**: tree/graph traversal, divide & conquer (merge sort),
backtracking — where the recursive structure is natural AND depth is bounded.

---

### 5. Apply Dynamic Programming to Avoid Redundant Computation

When a recursive solution recalculates the same subproblems, cache the results.

Two approaches:
- **Top-down (memoization)**: recursive + cache results in a map/array
- **Bottom-up (tabulation)**: iterative, build solutions from base cases up

```java
// ✅ Top-down with memoization
Map<Integer, Long> memo = new HashMap<>();
long fib(int n) {
    if (n <= 1) return n;
    if (memo.containsKey(n)) return memo.get(n);
    long result = fib(n-1) + fib(n-2);
    memo.put(n, result);
    return result;
}
```

**Use DP when**: the problem has overlapping subproblems and optimal substructure.

---

### 6. Early Exit / Short-Circuit Logic

Stop processing as soon as you have the answer.

```java
// ✅ Return immediately when found
for (String item : list) {
    if (item.equals(target)) return item; // don't keep looping
}

// ✅ Short-circuit boolean evaluation (Java already does this with && and ||)
if (list != null && !list.isEmpty() && list.get(0).equals(target)) { ... }
```

---

### 7. Avoid Unnecessary Object Creation in Loops

In Java, creating objects inside tight loops stresses the garbage collector.

```java
// ❌ BAD — Creates a new StringBuilder every iteration
for (String s : list) {
    String result = new StringBuilder(s).reverse().toString();
}

// ✅ GOOD — Reuse where possible, or use primitives
StringBuilder sb = new StringBuilder();
for (String s : list) {
    sb.setLength(0);
    sb.append(s);
    // process sb
}
```

---

### 8. Always Analyze Before Choosing a Sort

| Algorithm    | Best    | Avg       | Worst   | Notes                          |
|--------------|---------|-----------|---------|--------------------------------|
| Merge Sort   | O(n log n) | O(n log n) | O(n log n) | Stable, predictable         |
| Quick Sort   | O(n log n) | O(n log n) | O(n²)  | Fast in practice, unstable     |
| Bubble Sort  | O(n)    | O(n²)     | O(n²)   | Only for near-sorted tiny data |
| Arrays.sort()| O(n log n) | O(n log n) | O(n log n) | Use this in Java — optimized |

> **Java tip**: Always prefer `Arrays.sort()` or `Collections.sort()`. They use TimSort
> (merge + insertion sort hybrid), which is extremely well-optimized.

---

## ❌ ANTI-PATTERNS — Never Do These

### ❌ 1. Nested Loops Over the Same Collection (O(n²))

```java
// ❌ NEVER — Compares every element with every other: O(n²)
for (int i = 0; i < items.length; i++) {
    for (int j = 0; j < items.length; j++) {
        if (i != j && items[i].equals(items[j])) { ... }
    }
}

// ✅ Use a HashSet instead: O(n)
Set<String> seen = new HashSet<>();
Set<String> duplicates = new HashSet<>();
for (String item : items) {
    if (!seen.add(item)) duplicates.add(item);
}
```

**Why it matters**: 1,000 items → 1,000,000 comparisons. 10,000 items → 100,000,000.

---

### ❌ 2. Linear Search on Data That Could Be Indexed

```java
// ❌ BAD — O(n) search called repeatedly in a loop = O(n²) total
for (Order order : orders) {
    for (Customer c : customers) { // linear search every time
        if (c.getId().equals(order.getCustomerId())) { ... }
    }
}

// ✅ GOOD — Build index once, lookup is O(1)
Map<String, Customer> customerById = new HashMap<>();
for (Customer c : customers) customerById.put(c.getId(), c);

for (Order order : orders) {
    Customer c = customerById.get(order.getCustomerId()); // O(1)
}
```

---

### ❌ 3. Naive Recursion Without Memoization on Overlapping Subproblems

```java
// ❌ NEVER use raw recursion for Fibonacci, coin change, paths, etc.
// fibonacci(40) → 300+ million calls
int fib(int n) {
    if (n <= 1) return n;
    return fib(n-1) + fib(n-2); // recalculates everything
}
```

---

### ❌ 4. Multiple Passes When One Pass Suffices

```java
// ❌ BAD — filters, then maps, then reduces: 3 passes
List<Integer> filtered = list.stream().filter(x -> x > 0).collect(toList());
List<Integer> doubled = filtered.stream().map(x -> x * 2).collect(toList());
int sum = doubled.stream().mapToInt(Integer::intValue).sum();

// ✅ GOOD — single pipeline (Java Stream is lazy, but collecting intermediate
// lists forces evaluation — avoid intermediate collects)
int sum = list.stream()
    .filter(x -> x > 0)
    .mapToInt(x -> x * 2)
    .sum();
```

---

### ❌ 5. String Concatenation in a Loop

```java
// ❌ BAD — creates a new String object every iteration: O(n²) memory
String result = "";
for (String s : list) {
    result += s; // new String each time
}

// ✅ GOOD — StringBuilder is mutable: O(n)
StringBuilder sb = new StringBuilder();
for (String s : list) sb.append(s);
String result = sb.toString();
```

---

### ❌ 6. Sorting When You Only Need Min/Max

```java
// ❌ BAD — O(n log n) just to find max
Collections.sort(list);
int max = list.get(list.size() - 1);

// ✅ GOOD — O(n)
int max = Collections.max(list);
```

---

### ❌ 7. Ignoring the Cost of Method Calls Inside Loops

```java
// ❌ BAD — .size() or .length() called every iteration (minor but real)
for (int i = 0; i < list.size(); i++) { ... }

// ✅ GOOD — cache the size
int n = list.size();
for (int i = 0; i < n; i++) { ... }

// ❌ BAD — expensive computation inside loop condition
for (int i = 0; i < computeExpensiveBound(); i++) { ... }

// ✅ GOOD — compute once
int bound = computeExpensiveBound();
for (int i = 0; i < bound; i++) { ... }
```

---

## Decision Framework: Which Algorithm/Structure to Pick

Read the problem, then follow this tree:

```
Do you need to SEARCH for an element?
├── Is the data sorted?
│   ├── YES → Binary Search O(log n)
│   └── NO  → Can you sort it once? → Sort then binary search
│              Or use HashSet for O(1) contains()
└── Are you searching by KEY (id, name, email)?
    └── YES → HashMap / HashSet — O(1)

Do you need to find DUPLICATES?
└── Use HashSet — add() returns false if already present → O(n)

Do you need to SORT?
├── General purpose → Arrays.sort() / Collections.sort() — O(n log n)
├── Nearly sorted small data → Insertion sort — O(n) best case
└── Need stable sort → Merge sort / TimSort (Java default)

Do you have a RECURSIVE problem?
├── Does it have overlapping subproblems? → Dynamic Programming
├── Is depth bounded and natural? → Recursion OK (DFS, tree traversal)
└── Is it tail-recursive or iterative equivalent? → Use iteration
```

---

## Complexity Analysis Checklist

When reviewing code, go through this checklist:

- [ ] Are there nested loops? → Flag as O(n²) or worse
- [ ] Is there a linear search inside a loop? → Replace with HashMap/HashSet
- [ ] Is recursion used without memoization on repeated subproblems? → Add DP
- [ ] Are there multiple passes over the same collection? → Combine into one
- [ ] Is sorting used just to find min/max? → Use `Collections.min/max`
- [ ] Is String concatenation happening in a loop? → Use StringBuilder
- [ ] Is a method called in a loop whose result doesn't change? → Cache it outside
- [ ] Are intermediate collections created that are only used once? → Eliminate them

---

## Deeper References

For detailed treatment of specific topics, see:
- `references/search-algorithms.md` — Linear vs Binary search, full analysis
- `references/sorting-algorithms.md` — When to use each sort
- `references/dynamic-programming.md` — Fibonacci, memoization, bottom-up patterns
- `references/java-specifics.md` — Java-specific optimizations (streams, collections, JVM)
- `references/game-algorithms.md` — Fisher-Yates, pathfinding, turn order, deck validation, triada, battleship

---

## Output Format When Writing Code

When producing code, always:
1. **State the complexity** — time and space Big O of your solution
2. **Explain why** the chosen data structure or algorithm was selected
3. **Mention trade-offs** if relevant (e.g., "this uses O(n) extra space but reduces time from O(n²) to O(n)")
4. **Flag any remaining bottlenecks** if the code isn't fully optimal

Example annotation:
```java
// Time: O(n) — single pass through the list
// Space: O(n) — HashSet stores up to n elements
// Trade-off: uses extra memory to eliminate quadratic time
Set<String> seen = new HashSet<>();
```
