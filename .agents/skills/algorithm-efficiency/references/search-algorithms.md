# Search Algorithms — Deep Reference

## Linear Search

**When to use**: Unsorted data, small collections (< ~100 elements), one-off searches.

**Complexity**:
- Best case: O(1) — element at index 0
- Average case: O(n/2) = O(n)
- Worst case: O(n) — element not present or at last position

```java
// Generic linear search
int linearSearch(int[] arr, int target) {
    for (int i = 0; i < arr.length; i++) {
        if (arr[i] == target) return i;
    }
    return -1;
}
```

**When NOT to use**:
- Inside another loop (creates O(n²))
- On sorted data (use binary search)
- When called repeatedly on the same large dataset (build an index instead)

---

## Binary Search

**Requirement**: Data MUST be sorted. If not sorted, sort first (cost O(n log n)),
then binary search (O(log n)) — still better than repeated O(n) searches.

**Complexity**:
- Best case: O(1) — target is the middle element
- Average case: O(log n)
- Worst case: O(log n)

**Core idea**: Each comparison eliminates half the remaining search space.
- n = 1,000,000 → max 20 comparisons
- n = 1,000,000,000 → max 30 comparisons

```java
// Manual implementation (Java)
int binarySearch(int[] arr, int target) {
    int lo = 0, hi = arr.length - 1;
    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2; // safe from overflow
        if (arr[mid] == target) return mid;
        else if (arr[mid] < target) lo = mid + 1;
        else hi = mid - 1;
    }
    return -1;
}

// Java built-in (use this in practice)
int index = Arrays.binarySearch(sortedArray, target);
// Returns index if found, negative value if not found
```

**Java note**: `Arrays.binarySearch()` returns `-(insertion point) - 1` if not found.
Check with `index >= 0`.

---

## Hash-Based Search (O(1))

When you need to check membership or look up by key repeatedly, use hash structures.

```java
// Membership check — O(1)
Set<String> allowedEmails = new HashSet<>(Arrays.asList("a@x.com", "b@x.com"));
boolean allowed = allowedEmails.contains(inputEmail);

// Key-value lookup — O(1)
Map<String, User> userById = new HashMap<>();
// populate once...
User u = userById.get(someId); // O(1) regardless of map size
```

**Trade-off**: O(n) space, but O(1) per query. Worth it when queries > 1.

---

## Comparison Summary

| Scenario                          | Best Choice         | Complexity |
|-----------------------------------|---------------------|------------|
| Unsorted, search once             | Linear search       | O(n)       |
| Sorted array, search              | Binary search       | O(log n)   |
| Search by key, many times         | HashMap             | O(1) avg   |
| Membership check, many times      | HashSet             | O(1) avg   |
| Range queries (between A and B)   | TreeMap/TreeSet     | O(log n)   |
