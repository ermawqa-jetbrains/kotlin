import WithInternal = JS_TESTS.foo.WithInternal;

function assert(condition: boolean, message: string) {
    if (!condition) {
        throw `FAIL: ${message}`;
    }
}

function box(): string {
    assert(WithInternal.publicVal === "y", "publicVal");

    if (false) {
        // @ts-expect-error internal companion block property must not be exported
        WithInternal.secret;
    }

    return "OK";
}
