# Java-Specific Optimizations

## Collections: Picking the Right One

| Use case                             | Class                  | Key operation complexity |
|--------------------------------------|------------------------|--------------------------|
| Fast lookup by value                 | `HashSet<T>`           | contains: O(1)           |
| Fast lookup by key                   | `HashMap<K,V>`         | get/put: O(1)            |
| Sorted keys + range queries          | `TreeMap<K,V>`         | get/put: O(log n)        |
| Ordered unique values                | `TreeSet<T>`           | add/contains: O(log n)   |
| Queue / deque                        | `ArrayDeque<T>`        | add/poll: O(1)           |
| Priority queue (min-heap by default) | `PriorityQueue<T>`     | poll: O(log n)           |
| Dynamic array (most uses)            | `ArrayList<T>`         | get: O(1), add: O(1) amortized |
| Frequent insert/delete at middle     | `LinkedList<T>`        | insert: O(1) if iterator |

**Rule**: Never use `LinkedList` for random access — it's O(n). Use `ArrayList`.

---

## Streams: Lazy but Watch Out for Intermediate Collections

Java Streams are lazy — they don't evaluate until a terminal operation.
Avoid collecting to intermediate lists unless needed.

```java
// ❌ BAD — forces evaluation twice (two terminal ops via collect)
List<String> filtered = list.stream()
    .filter(s -> s.startsWith("A"))
    .collect(Collectors.toList());
long count = filtered.stream().count();

// ✅ GOOD — single pipeline
long count = list.stream()
    .filter(s -> s.startsWith("A"))
    .count();
```

**Parallel streams**: Only use for CPU-bound tasks on large datasets.
For I/O-bound work or small lists, parallel streams add overhead without benefit.

```java
// Only worthwhile for large data + expensive per-element computation
list.parallelStream()
    .map(this::expensiveTransform)
    .collect(Collectors.toList());
```

---

## String Handling

```java
// ❌ BAD — O(n²) due to immutable String concatenation
String result = "";
for (int i = 0; i < 10000; i++) result += i;

// ✅ GOOD — O(n)
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 10000; i++) sb.append(i);
String result = sb.toString();

// For joining with delimiter:
String joined = String.join(", ", list); // clean, O(n)
// Or: Collectors.joining(", ") in streams
```

---

## Sorting in Java

```java
// Primitives — uses dual-pivot QuickSort: O(n log n)
Arrays.sort(intArray);

// Objects — uses TimSort (stable): O(n log n)
Arrays.sort(objectArray);
Collections.sort(list);

// Custom order
list.sort(Comparator.comparing(Person::getAge));
list.sort(Comparator.comparing(Person::getAge).reversed());

// Multiple fields
list.sort(Comparator.comparing(Person::getLastName)
                    .thenComparing(Person::getFirstName));
```

**Never implement your own sort** unless there's a very specific domain reason.
Java's built-in sorts are production-tuned with decades of optimization.

---

## Integer Overflow — Common Bug in Binary Search

```java
// ❌ Can overflow when lo and hi are both large positive ints
int mid = (lo + hi) / 2;

// ✅ Overflow-safe
int mid = lo + (hi - lo) / 2;
```

---

## HashMap Pre-sizing

When you know approximate size, pre-size to avoid rehashing:

```java
// Default load factor 0.75, initial capacity 16
// If you'll store ~100 elements:
Map<String, Integer> map = new HashMap<>(128); // (100 / 0.75) + buffer
```

---

## Boxing/Unboxing in Collections

Java collections use boxed types (`Integer`, `Long`). For tight numerical loops,
prefer primitive arrays `int[]` over `List<Integer>`.

```java
// ❌ Slower — boxing/unboxing overhead
List<Integer> numbers = new ArrayList<>();
int sum = 0;
for (Integer n : numbers) sum += n; // unboxes each element

// ✅ Faster for large numerical work
int[] numbers = new int[1000];
int sum = 0;
for (int n : numbers) sum += n; // no boxing

// Or use primitive streams
int sum = IntStream.of(numbers).sum();
```
