package sample

// Keep `target` and add a higher-arity suspend function: additionally loads
// SuspendFunction3 (<: Function4).
suspend fun target(x: Long, y: Int): Int = (x + y).toInt()

suspend fun target3(a: Int, b: Int, c: Int): Int = a + b + c
