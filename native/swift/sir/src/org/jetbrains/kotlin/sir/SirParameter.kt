/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.sir

class SirParameter(
    val argumentName: String? = null, // external function parameter (argument) name
    val parameterName: String? = null, // internal function parameter name
    val type: SirType,
    val origin: Origin? = null,
    val isVariadic: Boolean = false,
    /**
     * When set together with [isVariadic], the Swift-facing signature renders the parameter as an array
     * (`[T]`) instead of a variadic (`T...`), while it is still bridged as a Kotlin `vararg` (spread on
     * the Kotlin side). Required for parameters of reverse-bridged methods (interface/protocol methods
     * and `open` overridable methods): the reverse-bridge thunk passes an `Array` to the Swift
     * declaration, and Swift has no array-to-variadic splat.
     */
    val renderVariadicAsArray: Boolean = false,
) {
    init {
        require(argumentName?.isEmpty() != true) { "argumentName must not be empty; use null to suppress the argument label" }
    }

    interface Origin

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SirParameter) return false

        if (argumentName != other.argumentName) return false
        if (parameterName != other.parameterName) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = argumentName?.hashCode() ?: 0
        result = 31 * result + (parameterName?.hashCode() ?: 0)
        result = 31 * result + type.hashCode()
        return result
    }
}
