import sample.*
import kotlin.coroutines.*
import kotlin.reflect.*

// Step 2: `target` is now an arity-2 suspend function -> SuspendFunction2 (<: Function3) get loaded.
fun test(stepId: Int, isWasm: Boolean): String {
    val fails = StringBuilder()
    fun check(cond: Boolean, msg: String) { if (!cond) fails.append("$msg; ") }
    when (stepId) {
        2 -> {
            val ref = ::target
            check(ref is SuspendFunction2<*, *, *>, "s2: ref is SuspendFunction2")
            check(ref is KSuspendFunction2<*, *, *>, "s2: ref is KSuspendFunction2")
            check(ref is Function3<*, *, *, *>, "s2: ref is Function3")
            check(ref is KFunction3<*, *, *, *>, "s2: ref is KFunction3")
            check((ref as Function3<Int, Int, Continuation<Int>, Any?>)(40, 2, emptyCont) == 42, "s2: ref as Function3 invoke")
            check((ref as KFunction3<Int, Int, Continuation<Int>, Any?>)(40, 2, emptyCont) == 42, "s2: ref as KFunction3 invoke")
            check(builder { (ref as SuspendFunction2<Int, Int, Int>)(40, 2) } == 42, "s2: ref as SuspendFunction2 invoke")
            check(builder { (ref as KSuspendFunction2<Int, Int, Int>)(40, 2) } == 42, "s2: ref as KSuspendFunction2 invoke")
        }
        else -> return "test.2.kt reached unexpected step $stepId"
    }
    return if (fails.isEmpty()) "OK" else fails.toString()
}
