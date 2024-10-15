# HydraFP: Functional Programming Simplified for Java

HydraFP brings the power of functional programming to Java, making it easier to write clean, composable, and side-effect free code.

## Why HydraFP?

- **Pure Functional Approach**: Write safer, more predictable code
- **Powerful Effect System**: Manage side effects with ease
- **Rich Functional Toolset**: From monads to lenses, all in one library
- **Java-First Design**: Tailored for seamless integration with existing Java projects

## Key Features

- 🛡️ **Effect System**: Compose and manage side effects safely
- 🧩 **Algebraic Data Types**: `Option`, `Either`, `Try`, and more
- 🔄 **Immutable Collections**: Thread-safe, persistent data structures
- 🚀 **Performance Optimized**: Tail call optimization, memoization, lazy evaluation
- 🔍 **Pattern Matching**: Expressive and type-safe pattern matching for Java


## STOP HERE
This still under creating and only a placeholder, please don't expect anything for now

## Quick Start

Add HydraFP to your project:

```xml
<dependency>
    <groupId>io.hydrafp</groupId>
    <artifactId>hydrafp-core</artifactId>
    <version>1.0.0</version>
</dependency>
```

Simple example using the Effect system:

```java
import io.hydrafp.core.effect.*;

Effect<String> effect = Effects.defer(() -> "Hello, HydraFP!");
String result = effect.unsafeRunSync(new SimpleRuntime());
System.out.println(result); // Outputs: Hello, HydraFP!
```

## Learn More

- [Documentation](https://hydrafp.io/docs)
- [API Reference](https://hydrafp.io/api)
- [Tutorials](https://hydrafp.io/tutorials)

## Contributing

We welcome contributions! See our [Contributing Guide](CONTRIBUTING.md) for details.

## License

HydraFP is open-source software licensed under the [MIT license](LICENSE).

---

Start writing cleaner, more maintainable Java code today with HydraFP!