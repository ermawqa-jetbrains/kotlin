import ExportedWithCompanionBlock = JS_TESTS.foo.ExportedWithCompanionBlock;

function assert(condition: boolean, message: string) {
    if (!condition) {
        throw `FAIL: ${message}`;
    }
}

function box(): string {
    assert(ExportedWithCompanionBlock.readOnly === "O", "readOnly");
    assert(ExportedWithCompanionBlock.mutable === "", "mutable initial");
    assert(ExportedWithCompanionBlock.append() === "OK", "append default");
    assert(ExportedWithCompanionBlock.mutable === "K", "mutable after append");
    ExportedWithCompanionBlock.mutable = "Q";
    assert(ExportedWithCompanionBlock.mutable === "Q", "mutable write");
    assert(ExportedWithCompanionBlock.append("L") === "OL", "append argument");
    assert(ExportedWithCompanionBlock.mutable === "L", "mutable after argument");

    return "OK";
}
