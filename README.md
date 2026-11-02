# Java `var` Examples

This repository is a complementary resource to my LinkedIn article [Why Your Java `var` Code Fails Code Review](https://www.linkedin.com/pulse/why-your-java-var-code-fails-review-stoyan-topchiyski-xwome). It contains runnable Java 21 examples of where `var` helps, where it hurts, and where it infers a surprising type.

## Prerequisites

- JDK 21 or later
- Maven 3.6.3 or later

## What's Inside

| File | Shows |
|---|---|
| `main/.../var/Var.java` | `var` is not a reserved keyword, so it is still a legal name |
| `main/.../mechanics/BytecodeParityDemo.java` | `var` compiles to the same bytecode as explicit types |
| `test/.../good/BoilerplateEliminationTest.java` | Removing redundant types |
| `test/.../good/PipelineDecompositionTest.java` | Splitting long stream chains into named steps |
| `test/.../bad/ReadabilityAndScopeTest.java` | Unclear names and wide variable scope |
| `test/.../ugly/AnonymousClassSpyTest.java` | Anonymous class types vs. local records |
| `test/.../ugly/IntersectionTypeTrapTest.java` | Mixed `List.of()` arguments infer an intersection type |
| `test/.../ugly/DiamondOperatorTrapTest.java` | `var` with `<>` infers `Object` |
| `test/.../ugly/PrimitiveOverloadTrapTest.java` | `int` literals and the `List.remove(int)` overload |

Each test puts the problem (`BAD`, `TRAP`) next to the fix (`GOOD`, `SENIOR PATTERN`). `TestMocks` provides simple stand-ins for Selenium and RestAssured so everything runs without a browser or server; don't treat them as a reference for the real APIs.

## Running

Run all tests:
```bash
mvn clean test
```

Run one example:
```bash
mvn test -Dtest=PrimitiveOverloadTrapTest
```

Compare the bytecode of the explicit and `var` versions:
```bash
mvn compile
javap -v -c target/classes/com/acme/mechanics/BytecodeParityDemo.class
```

## Contact

Stoyan Topchiyski – [LinkedIn](https://www.linkedin.com/in/stoyan-topchiyski/)

## License

[MIT](LICENSE)
