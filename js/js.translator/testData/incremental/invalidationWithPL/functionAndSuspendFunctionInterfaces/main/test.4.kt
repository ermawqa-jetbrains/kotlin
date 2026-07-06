import sample.*
import kotlin.coroutines.*
import kotlin.reflect.*

// Compiled at step 4 where both `target` (arity 2) and `target3` (arity 3) exist; also runs, unchanged,
// at step 5 where `target3` is removed and its reference falls back to a partial-linkage stub.
fun test(stepId: Int, isWasm: Boolean): String {
    val fails = StringBuilder()
    fun check(cond: Boolean, msg: String) { if (!cond) fails.append("$msg; ") }
    when (stepId) {
        4 -> {
            val ref = ::target
            check((ref as Function3<Long, Int, Continuation<Int>, Any?>)(40L, 2, emptyCont) == 42, "s4: as Function3 invoke")

            // Arity-3 suspend: invocable as Function4 with a trailing Continuation.
            val ref3 = ::target3
            check(ref3 is SuspendFunction3<*, *, *, *>, "s4: ref3 is SuspendFunction3")
            check(ref3 is KSuspendFunction3<*, *, *, *>, "s4: ref3 is KSuspendFunction3")
            check(ref3 is Function4<*, *, *, *, *>, "s4: ref3 is Function4")
            check(ref3 is KFunction4<*, *, *, *, *>, "s4: ref3 is KFunction4")
            check((ref3 as Function4<Int, Int, Int, Continuation<Int>, Any?>)(1, 2, 3, emptyCont) == 6, "s4: ref3 as Function4 invoke")
            check((ref3 as KFunction4<Int, Int, Int, Continuation<Int>, Any?>)(1, 2, 3, emptyCont) == 6, "s4: ref3 as KFunction4 invoke")
            check(builder { (ref3 as SuspendFunction3<Int, Int, Int, Int>)(1, 2, 3) } == 6, "s4: ref3 as SuspendFunction3 invoke")
            check(builder { (ref3 as KSuspendFunction3<Int, Int, Int, Int>)(1, 2, 3) } == 6, "s4: ref3 as KSuspendFunction3 invoke")
        }
        5 -> {
            // `target3` removed. Obtaining and casting the reference must still succeed (partial
            // linkage keeps a throwing stub for the missing declaration); only *invoking* it must fail.
            // The reference/cast are outside the try on purpose, so if they threw the test would fail.
            val ref3 = ::target3
            val f = ref3 as Function4<Int, Int, Int, Continuation<Int>, Any?>
            val f1 = ref3 as SuspendFunction3<Int, Int, Int, Int>
            return try {
                f(1, 2, 3, emptyCont)
                builder { f1(1, 2, 3) }
                "s5: expected the invocation of removed target3 to fail, but it returned normally"
            } catch (e: Throwable) {
                "OK"
            }
        }
        else -> return "test.4.kt reached unexpected step $stepId"
    }
    return if (fails.isEmpty()) "OK" else fails.toString()
}
