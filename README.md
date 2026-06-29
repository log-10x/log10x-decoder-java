# Log10x Decoder for Java

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Maven Central](https://img.shields.io/maven-central/v/com.log10x/log10x-decoder-core.svg)](https://central.sonatype.com/artifact/com.log10x/log10x-decoder-core)

Java library and CLI tool for decoding [Log10x-encoded](https://doc.log10x.com/run/transform/#encoding) log events back to their original form. [Log10x](https://www.log10x.com/?utm_source=github&utm_medium=readme&utm_campaign=log10x-decoder-java&utm_content=hero) losslessly compacts log events by extracting recurring structure into templates and storing only the variable values — this library reverses that process.

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
    <version>1.0.0</version>
</dependency>
```

### Gradle

```groovy
implementation 'com.log10x:log10x-decoder-core:1.0.0'
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

## Publishing to Maven Central

Releases are published via GitHub Actions, triggered by pushing git tags.

Each module has its own tag suffix and workflow:

| Module | Tag format | Workflow |
|--------|-----------|----------|
| `log10x-decoder-core` | `v{version}-core` | `publish-core.yml` |
| `log10x-decoder-cli` | `v{version}-cli` | `publish-cli.yml` |

The version is extracted from the tag automatically (e.g., tag `v1.0.0-core` publishes version `1.0.0`).

**Core must be published before CLI**, since the CLI pulls core from Maven Central as a dependency. Wait for core to appear on [Maven Central](https://central.sonatype.com/artifact/com.log10x/log10x-decoder-core) before tagging CLI.

### Release steps

```bash
# 1. Tag and push core
git tag v1.0.0-core
git push origin v1.0.0-core

# 2. Wait for core to appear on Maven Central (~10-30 min)

# 3. Tag and push CLI
git tag v1.0.0-cli
git push origin v1.0.0-cli
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
- [Log10x Pricing](https://www.log10x.com/pricing?utm_source=github&utm_medium=readme&utm_campaign=log10x-decoder-java&utm_content=footer)
- [Documentation](https://doc.log10x.com)
- [Contact Sales](mailto:sales@log10x.com)

## Contributing

Contributions are welcome! Please read our contributing guidelines and submit pull requests to the repository.

## Support

For issues and feature requests:
- Open an issue on [GitHub](https://github.com/log-10x/log10x-decoder-java/issues)
- Contact the Log10x team at [support@log10x.com](mailto:support@log10x.com)
