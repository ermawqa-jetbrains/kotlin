import sample.*
import kotlin.coroutines.*
import kotlin.reflect.*

// Step 1: `target` lost its `suspend` modifier. On JS/Wasm the `suspend` flag is not part of the
// IdSignature, so the reference still links (no partial-linkage stub); but the SuspendFunction1 /
// Function2 supertype is no longer synthesized, so `target` is now a plain (Int) -> Int and is NOT a
// SuspendFunction1. This exercises unloading the suspend functional-interface supertype.
fun test(stepId: Int, isWasm: Boolean): String {
    val fails = StringBuilder()
    fun check(cond: Boolean, msg: String) { if (!cond) fails.append("$msg; ") }
    when (stepId) {
        1 -> {
            val ref = ::target
            check(ref is Function1<*, *>, "s1: ref is Function1")
            check(ref is KFunction1<*, *>, "s1: ref is KFunction1")
            check(ref !is SuspendFunction1<*, *>, "s1: ref !is SuspendFunction1")
            check(ref !is SuspendFunction0<*>, "s1: ref !is SuspendFunction0")
            check(ref !is Function2<*, *, *>, "s1: ref !is Function2")
            check(ref !is KFunction2<*, *, *>, "s1: ref !is KFunction2")
            check((ref as Function1<Int, Int>)(41) == 42, "s1: ref as Function1 invoke")
            check((ref as KFunction1<Int, Int>)(41) == 42, "s1: ref as KFunction1 invoke")
        }
        else -> return "test.1.kt reached unexpected step $stepId"
    }
    return if (fails.isEmpty()) "OK" else fails.toString()
}
