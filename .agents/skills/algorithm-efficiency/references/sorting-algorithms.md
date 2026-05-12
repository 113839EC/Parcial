# Sorting Algorithms — Deep Reference

## When to Use Each Sort

| Algorithm      | Best        | Average     | Worst       | Space  | Stable | Use when |
|----------------|-------------|-------------|-------------|--------|--------|----------|
| TimSort        | O(n)        | O(n log n)  | O(n log n)  | O(n)   | Yes    | Default — Java's Arrays.sort() for objects |
| QuickSort      | O(n log n)  | O(n log n)  | O(n²)       | O(log n) | No   | Primitives — Java's Arrays.sort() for int[], long[] |
| MergeSort      | O(n log n)  | O(n log n)  | O(n log n)  | O(n)   | Yes    | Need guaranteed O(n log n), external sort |
| HeapSort       | O(n log n)  | O(n log n)  | O(n log n)  | O(1)   | No     | Need O(1) space + O(n log n) worst case |
| InsertionSort  | O(n)        | O(n²)       | O(n²)       | O(1)   | Yes    | Nearly sorted data, small arrays (< 16 elements) |
| BubbleSort     | O(n)        | O(n²)       | O(n²)       | O(1)   | Yes    | Never — only educational |
| CountingSort   | O(n + k)    | O(n + k)    | O(n + k)    | O(k)   | Yes    | Small integer range k, e.g., card values 1-13 |
| RadixSort      | O(nk)       | O(nk)       | O(nk)       | O(n+k) | Yes    | Large sets of integers/strings with fixed length |

**k** = range of values (CountingSort), digit count (RadixSort)

---

## Java Built-in — Always Prefer This

```java
// Primitives — dual-pivot QuickSort: O(n log n) avg, O(n²) worst (rare)
Arrays.sort(int[] arr);
Arrays.sort(int[] arr, int fromIndex, int toIndex);

// Objects — TimSort: O(n log n) guaranteed, stable
Arrays.sort(Object[] arr);
Collections.sort(List<T> list);

// Custom comparator
Arrays.sort(arr, Comparator.comparing(Card::getValue));
list.sort(Comparator.comparing(Player::getScore).reversed());

// Multiple fields
list.sort(Comparator.comparing(Player::getLevel)
                    .thenComparing(Player::getScore));
```

**Never implement your own sort** unless there's a domain-specific reason
(e.g., CountingSort for card values with known bounded range).

---

## CountingSort — O(n + k) for Bounded Integers

**When to use**: Sorting items with small known integer range.
Example: sorting cards by value (1–13), sorting tiles by type (1–6).

```java
// CountingSort — O(n + k), k = max value
int[] countingSort(int[] arr, int maxVal) {
    int[] count = new int[maxVal + 1];
    for (int x : arr) count[x]++;             // count occurrences
    for (int i = 1; i <= maxVal; i++) count[i] += count[i-1]; // prefix sum

    int[] output = new int[arr.length];
    for (int i = arr.length - 1; i >= 0; i--) {
        output[--count[arr[i]]] = arr[i];
    }
    return output;
}
// Time: O(n + k), Space: O(k)
// Faster than O(n log n) when k << n
```

**Game use case**: Sort a hand of 7 cards by value (1–13). k=13, n=7.
O(n + k) = O(20) vs O(n log n) = O(7 × 3) = O(21). Marginal here, but scales for larger hands.

---

## MergeSort — O(n log n) Guaranteed, Stable

**When to use**: Need guaranteed worst-case O(n log n) AND stable sort.

```java
// Merge sort — O(n log n) time, O(n) space
void mergeSort(int[] arr, int left, int right) {
    if (left >= right) return;
    int mid = left + (right - left) / 2;
    mergeSort(arr, left, mid);
    mergeSort(arr, mid + 1, right);
    merge(arr, left, mid, right);
}

void merge(int[] arr, int left, int mid, int right) {
    int[] temp = Arrays.copyOfRange(arr, left, right + 1);
    int i = 0, j = mid - left + 1, k = left;
    while (i <= mid - left && j <= right - left) {
        arr[k++] = temp[i] <= temp[j] ? temp[i++] : temp[j++];
    }
    while (i <= mid - left) arr[k++] = temp[i++];
    while (j <= right - left) arr[k++] = temp[j++];
}
```

**In practice**: Use `Arrays.sort()` — it uses TimSort which is MergeSort + InsertionSort hybrid, strictly better.

---

## InsertionSort — O(n) on Nearly Sorted Data

**When to use**: Small arrays (< 16 elements) or nearly sorted data.
Java's TimSort uses InsertionSort internally for small subarrays.

```java
// InsertionSort — O(n) best, O(n²) worst
void insertionSort(int[] arr) {
    for (int i = 1; i < arr.length; i++) {
        int key = arr[i];
        int j = i - 1;
        while (j >= 0 && arr[j] > key) {
            arr[j + 1] = arr[j];
            j--;
        }
        arr[j + 1] = key;
    }
}
```

**Game use case**: Re-sorting a turn order list after one player's speed changes.
Most players stay in place → nearly sorted → InsertionSort approaches O(n).

---

## Stability — Why It Matters

**Stable sort**: equal elements maintain their original relative order.

```
Before: [(Alice, 5), (Bob, 5), (Carol, 3)]
Stable sort by score DESC:   [(Alice, 5), (Bob, 5), (Carol, 3)]  ← Alice before Bob preserved
Unstable sort by score DESC: [(Bob, 5), (Alice, 5), (Carol, 3)]  ← order may flip
```

**Matters when**: sorting by multiple criteria sequentially (sort by level, then by score —
requires stable sort to preserve level order while sorting by score).

Java's `Collections.sort()` and `Arrays.sort(Object[])` are **stable**.
Java's `Arrays.sort(int[])` (QuickSort) is **not stable** — but primitives have no identity, so it doesn't matter.

---

## Decision Tree

```
Need to sort?
├── Use Java built-in first → Arrays.sort() / Collections.sort()
│
├── Integer values in small range (card values, dice, tile types)?
│   └── Consider CountingSort → O(n + k)
│
├── Already nearly sorted (re-sort after 1 change)?
│   └── InsertionSort → O(n) best case
│
├── Need guaranteed O(n log n) worst case + stable?
│   └── MergeSort (or just Arrays.sort(Object[]) — it's TimSort)
│
└── Need O(1) space + O(n log n) worst case?
    └── HeapSort (but Arrays.sort() is faster in practice)
```
