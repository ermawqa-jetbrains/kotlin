import sample.*
import kotlin.coroutines.*
import kotlin.reflect.*

// Step 0: arity-1 suspend `target`. Invocable as a plain Function2 with a trailing Continuation, and
// the reflective reference is a KSuspendFunction1 (<: KFunction2). `emptyCont`/`builder` live in the
// never-modified helpers.kt.
fun test(stepId: Int, isWasm: Boolean): String {
    val fails = StringBuilder()
    fun check(cond: Boolean, msg: String) { if (!cond) fails.append("$msg; ") }
    when (stepId) {
        0 -> {
            val ref = ::target
            check(ref is SuspendFunction1<*, *>, "s0: ref is SuspendFunction1")
            check(ref is KSuspendFunction1<*, *>, "s0: ref is KSuspendFunction1")
            check(ref is Function2<*, *, *>, "s0: ref is Function2")
            check(ref is KFunction2<*, *, *>, "s0: ref is KFunction2")
            check((ref as Function2<Int, Continuation<Int>, Any?>)(41, emptyCont) == 42, "s0: ref as Function2 invoke")
            check((ref as KFunction2<Int, Continuation<Int>, Any?>)(41, emptyCont) == 42, "s0: ref as KFunction2 invoke")
            check(builder { (ref as SuspendFunction1<Int, Int>)(41) } == 42, "s0: ref as SuspendFunction1 invoke")
            check(builder { (ref as KSuspendFunction1<Int, Int>)(41) } == 42, "s0: ref as KSuspendFunction1 invoke")
        }
        else -> return "test.0.kt reached unexpected step $stepId"
    }
    return if (fails.isEmpty()) "OK" else fails.toString()
}
