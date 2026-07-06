package sample

// `target3` removed: the arity-3 functional interfaces are no longer referenced here, and a stale
// `::target3` reference in a not-recompiled consumer becomes a partial-linkage stub.
suspend fun target(x: Long, y: Int): Int = (x + y).toInt()