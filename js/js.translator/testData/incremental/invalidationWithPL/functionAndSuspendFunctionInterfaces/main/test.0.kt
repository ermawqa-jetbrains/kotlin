import sample.*
import kotlin.coroutines.*
import kotlin.reflect.*

// Signatures of ::target are changing - we wrap its invocations into functions.
private fun asFunction2(a: Int): Int =
    (::target as Function2<Int, Continuation<Int>, Any?>)(a, emptyCont) as Int

private fun asSuspendFunction1(a: Int): Int =
    builder { (::target as SuspendFunction1<Int, Int>)(a) }

private fun asFunction1(a: Int): Int =
    (::target as Function1<Int, Int>)(a)

private fun asFunction3(a: Int, b: Int): Int =
    (::target as Function3<Int, Int, Continuation<Int>, Any?>)(a, b, emptyCont) as Int

private fun asSuspendFunction2(a: Int, b: Int): Int =
    builder { (::target as SuspendFunction2<Int, Int, Int>)(a, b) }

private fun asFunction3L(a: Long, b: Int): Int =
    (::target as Function3<Long, Int, Continuation<Int>, Any?>)(a, b, emptyCont) as Int

private fun asSuspendFunction2L(a: Long, b: Int): Int =
    builder { (::target as SuspendFunction2<Long, Int, Int>)(a, b) }

private val EXPECTED: Array<Array<Any>> = arrayOf(
    arrayOf(false, false, true, true, true, true, false, false, false, false, -1, 42, 42, -1, -1, -1, -1),
    arrayOf(true, true, false, false, false, false, false, false, false, false, 42, -1, -1, -1, -1, -1, -1),
    arrayOf(false, false, false, false, false, false, true, true, true, true, -1, -1, -1, 42, 42, -1, -1),
    arrayOf(false, false, false, false, false, false, true, true, true, true, -1, -1, -1, -1, -1, 42, 42),
)

private fun checks(ref: Any?, expected: Array<Any>, check: (Any, Any, String) -> Unit) {

    val checks: List<Pair<() -> Any, String>> = listOf(
        ({ ref is Function1<*, *> }) to "ref is Function1",
        ({ ref is KFunction1<*, *> }) to "ref is KFunction1",
        ({ ref is SuspendFunction1<*, *> }) to "ref is SuspendFunction1",
        ({ ref is KSuspendFunction1<*, *> }) to "ref is KSuspendFunction1",
        ({ ref is Function2<*, *, *> }) to "ref is Function2",
        ({ ref is KFunction2<*, *, *> }) to "ref is KFunction2",
        ({ ref is SuspendFunction2<*, *, *> }) to "ref is SuspendFunction2",
        ({ ref is KSuspendFunction2<*, *, *> }) to "ref is KSuspendFunction2",
        ({ ref is Function3<*, *, *, *> }) to "ref is Function3",
        ({ ref is KFunction3<*, *, *, *> }) to "ref is KFunction3",
        ({ asFunction1(41) }) to "ref as Function1 call",
        ({ asFunction2(41) }) to "ref as Function2 call",
        ({ asSuspendFunction1(41) }) to "ref as SuspendFunction1 call",
        ({ asFunction3(40, 2) }) to "ref as Function3 call",
        ({ asSuspendFunction2(40, 2) }) to "ref as SuspendFunction2 call",
        ({ asFunction3L(40L, 2) }) to "ref as Function3L call",
        ({ asSuspendFunction2L(40L, 2) }) to "ref as SuspendFunction2L call",
    )
    for (i in checks.indices) {
        if (expected[i] != -1) check(checks[i].first(), expected[i], checks[i].second)
    }
}

fun test(stepId: Int, isWasm: Boolean): String {
    if (stepId !in 0..3) return "test.kt (steps 0..3) reached unexpected step $stepId"

    val fails = StringBuilder()
    fun check(result: Any, expected: Any, msg: String) {
        if (result != expected) fails.append("$msg - expected ${expected}, got ${result}; ")
    }

    val ref = ::target
    checks(ref, EXPECTED[stepId], ::check)

    return if (fails.isEmpty()) "OK" else "step $stepId: $fails"
}
