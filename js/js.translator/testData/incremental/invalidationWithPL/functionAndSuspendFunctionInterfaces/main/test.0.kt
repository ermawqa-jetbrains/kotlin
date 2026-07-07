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

// Expected result of each `is` check.
private val EXPECTED_CHECKS: Array<Array<Boolean>> = arrayOf(
    arrayOf(false, false, true, true, true, true, false, false, false, false),
    arrayOf(true, true, false, false, false, false, false, false, false, false),
    arrayOf(false, false, false, false, false, false, true, true, true, true),
    arrayOf(false, false, false, false, false, false, true, true, true, true),
)

private fun isChecks(ref: Any?, expected: Array<Boolean>, check: (Boolean, Boolean, String) -> Unit) {

    check((ref is Function1<*, *>), expected[0], "ref is Function1")
    check((ref is KFunction1<*, *>), expected[1], "ref is KFunction1")
    check((ref is SuspendFunction1<*, *>), expected[2], "ref is SuspendFunction1")
    check((ref is KSuspendFunction1<*, *>), expected[3], "ref is KSuspendFunction1")

    check((ref is Function2<*, *, *>), expected[4], "ref is Function2")
    check((ref is KFunction2<*, *, *>), expected[5], "ref is KFunction2")
    check((ref is SuspendFunction2<*, *, *>), expected[6], "ref is SuspendFunction2")
    check((ref is KSuspendFunction2<*, *, *>), expected[7], "ref is KSuspendFunction2")

    check((ref is Function3<*, *, *, *>), expected[8], "ref is Function3")
    check((ref is KFunction3<*, *, *, *>), expected[9], "ref is KFunction3")
}

// Expected result of each invocation check that is relevant at the given step.
private val EXPECTED_CALLS: Array<Array<Int>> = arrayOf(
    arrayOf(42, 42), // 0: suspend fun target(Int)        -> Function2 / SuspendFunction1
    arrayOf(42),     // 1: fun target(Int)                -> Function1 (suspend supertype unloaded)
    arrayOf(42, 42), // 2: suspend fun target(Int, Int)   -> Function3 / SuspendFunction2
    arrayOf(42, 42), // 3: suspend fun target(Long, Int)  -> Function3 / SuspendFunction2 (different type args)
)

private fun callChecks(stepId: Int, check: (Int, Int, String) -> Unit) {

    // Invocation checks: only the shapes that match `target` at this step are cast and invoked.
    val actual: Array<Int> = when (stepId) {
        0 -> arrayOf(asFunction2(41), asSuspendFunction1(41))
        1 -> arrayOf(asFunction1(41))
        2 -> arrayOf(asFunction3(40, 2), asSuspendFunction2(40, 2))
        3 -> arrayOf(asFunction3L(40L, 2), asSuspendFunction2L(40L, 2))
        else -> return
    }
    val expected = EXPECTED_CALLS[stepId]
    for (i in expected.indices) {
        check(actual[i], expected[i], "call #$i")
    }
}

fun test(stepId: Int, isWasm: Boolean): String {
    if (stepId !in 0..3) return "test.kt (steps 0..3) reached unexpected step $stepId"

    val fails = StringBuilder()
    fun <T> check(result: T, expected: T, msg: String) {
        if (result != expected) fails.append("$msg - expected ${expected}, got ${result}; ")
    }

    // `is` checks: which functional-interface supertypes `target` currently has.
    val ref = ::target
    isChecks(ref, EXPECTED_CHECKS[stepId], ::check)

    callChecks(stepId, ::check)

    return if (fails.isEmpty()) "OK" else "step $stepId: $fails"
}
