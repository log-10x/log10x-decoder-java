# Log10x Decoder for Java

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Maven Central](https://img.shields.io/maven-central/v/com.log10x/log10x-decoder-core.svg)](https://central.sonatype.com/artifact/com.log10x/log10x-decoder-core)

Java library and CLI tool for decoding [Log10x-encoded](https://doc.log10x.com/run/transform/#encoding) log events back to their original form. Log10x losslessly compacts log events by extracting recurring structure into templates and storing only the variable values — this library reverses that process.

## Modules

| Module | Description |
|--------|-------------|
| `log10x-decoder-core` | Core decoding library — use this as a dependency in your Java applications |
| `log10x-decoder-cli` | Command-line tool for decoding encoded log files |

## Installation

### Maven

```xml
<dependency>
    <groupId>com.log10x</groupId>
    <artifactId>log10x-decoder-core</artifactId>
    <version>0.9.0</version>
</dependency>
```

### Gradle

```groovy
implementation 'com.log10x:log10x-decoder-core:0.9.0'
```

## Usage

### Library

```java
import com.log10x.decode.SingleEventDecoder;
import com.log10x.decode.lexer.TokenizeContext;

String decoded = SingleEventDecoder.decode(
    TokenizeContext.DEFAULT,
    templateHash,
    encodedEvent,
    templatePattern
);
```

### CLI

```bash
# Decode a file
log10x-decode -t templates.json -f encoded.log -o decoded.log

# Decode from stdin
cat encoded.log | log10x-decode -t templates.json
```

## Building from Source

### Prerequisites

- Java 8+ (JDK)
- Gradle 7+

### Build

```bash
./gradlew build
```

### Build fat JAR (CLI)

```bash
./gradlew :log10x-decoder-cli:shadowJar
```

### Build native image (requires GraalVM)

```bash
./gradlew :log10x-decoder-cli:nativeImage
```

## Also Available

- **JavaScript** — [log10x-decoder-js](https://github.com/log-10x/log10x-decoder-js) — Browser and Node.js, npm

## Documentation

- [Log10x Documentation](https://doc.log10x.com)
- [Encoding & Decoding](https://doc.log10x.com/run/transform/#encoding)

## License

This repository is licensed under the [Apache License 2.0](LICENSE).

### Important: Log10x Product License Required

This repository contains a decoder library for Log10x-encoded events. While the decoder itself is open source, **using the Log10x Edge Optimizer to encode events requires a commercial license**.

| Component | License |
|-----------|---------|
| This repository (decoder library & CLI) | Apache 2.0 (open source) |
| Log10x Edge Optimizer | Commercial license required |

**What this means:**
- You can freely use, modify, and distribute this decoder
- The Log10x Edge Optimizer that generates encoded events requires a paid subscription
- A valid Log10x license is required to run the Edge Optimizer

**Get Started:**
- [Log10x Pricing](https://log10x.com/pricing)
- [Documentation](https://doc.log10x.com)
- [Contact Sales](mailto:sales@log10x.com)

## Contributing

Contributions are welcome! Please read our contributing guidelines and submit pull requests to the repository.

## Support

For issues and feature requests:
- Open an issue on [GitHub](https://github.com/log-10x/log10x-decoder-java/issues)
- Contact the Log10x team at [support@log10x.com](mailto:support@log10x.com)
