/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.wasm.unsafe

@UnsafeWasmMemoryApi
@ExperimentalWasmJsInterop
public external object WebAssembly {
    /**
     * Represents the linear memory of the current WebAssembly module.
     *
     * This is a Kotlin external declaration for JavaScript's [WebAssembly.Memory] object.
     */
    @UnsafeWasmMemoryApi
    @ExperimentalWasmJsInterop
    public interface Memory : JsAny
}

/**
 * The linear memory used by the current WebAssembly module.
 *
 * This property provides access to the module's underlying [WebAssembly.Memory] object.
 */
@UnsafeWasmMemoryApi
@ExperimentalWasmJsInterop
public val wasmMemory: WebAssembly.Memory
    get() = kotlin.wasm.internal.wasmMemoryInternal()
