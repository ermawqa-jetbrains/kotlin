import sample.*
import kotlin.coroutines.*
import kotlin.reflect.*

// Step 3: same arity as step 2, but the first parameter is now Long. The functional-interface arity
// (SuspendFunction2/Function3) is unchanged, only the type arguments differ.
fun test(stepId: Int, isWasm: Boolean): String {
    val fails = StringBuilder()
    fun check(cond: Boolean, msg: String) { if (!cond) fails.append("$msg; ") }
    when (stepId) {
        3 -> {
            val ref = ::target
            check(ref is SuspendFunction2<*, *, *>, "s3: ref is SuspendFunction2")
            check(ref is KSuspendFunction2<*, *, *>, "s3: ref is KSuspendFunction2")
            check(ref is Function3<*, *, *, *>, "s3: ref is Function3")
            check(ref is KFunction3<*, *, *, *>, "s3: ref is KFunction3")
            check((ref as Function3<Long, Int, Continuation<Int>, Any?>)(40L, 2, emptyCont) == 42, "s3: ref as Function3 invoke")
            check((ref as KFunction3<Long, Int, Continuation<Int>, Any?>)(40L, 2, emptyCont) == 42, "s3: ref as KFunction3 invoke")
            check(builder { (ref as SuspendFunction2<Long, Int, Int>)(40L, 2) } == 42, "s3: ref as SuspendFunction2 invoke")
            check(builder { (ref as KSuspendFunction2<Long, Int, Int>)(40L, 2) } == 42, "s3: ref as KSuspendFunction2 invoke")
        }
        else -> return "test.3.kt reached unexpected step $stepId"
    }
    return if (fails.isEmpty()) "OK" else fails.toString()
}
