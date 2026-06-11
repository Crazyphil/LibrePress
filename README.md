# LibrePress

A Java library for interacting with the NewspaperDirect platform — register devices, receive pushed newspaper issues, activate them, and read their
DRM-protected PDF files.

LibrePress handles the entire workflow: authenticating with a NewspaperDirect service (e.g. PressReader), registering a device, polling for newspaper issues
pushed to that device, activating the download, and opening the resulting PDF.

## Requirements

- Java 11 or later, or Android 11 R or later
- A NewspaperDirect/[PressReader](https://www.pressreader.com/) account

## Installation

The library is published to GitHub Packages. Add the repository and dependency to your Gradle build:

```groovy
repositories {
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/crazyphil/LibrePress")
        credentials {
            username = project.findProperty("gpr.user") ?: System.getenv("USERNAME")
            password = project.findProperty("gpr.key") ?: System.getenv("TOKEN")
        }
    }
}

dependencies {
    implementation 'it.kapfer:librepress:1.0.0'
}
```

For Maven, add the repository to your `pom.xml` and declare the dependency:

```xml
<repository>
	<id>github</id>
	<url>https://maven.pkg.github.com/crazyphil/LibrePress</url>
</repository>

<dependency>
	<groupId>it.kapfer</groupId>
	<artifactId>librepress</artifactId>
	<version>1.0.0</version>
</dependency>
```

You need a [GitHub personal access token](https://github.com/settings/tokens) with `read:packages` scope to download the package.

## Quick Start

The `LibrePress` class is the main entry point. It manages devices, retrieves pushed newspapers, and opens them for reading:

```java
LibrePress librePress = new LibrePress();

// Register a device
Credentials credentials = new Credentials("user@example.com", "password".toCharArray());
DeviceRegistration device = librePress.registerDevice(credentials, "My Reader").join();

// Store device persistently so you can reconstruct DeviceRegistration
// without re-registering.

// Retrieve and activate newspapers pushed to the device
Collection<NewspaperIssue> issues = librePress.retrievePushedNewspapers(device).join();

// Store issues persistently so you can re-retrieve the newspapers
// after program restarts.

// Open an issue directly from the server with live streaming
try (NewspaperReader reader = librePress.openNewspaper(issues.iterator().next()).join()) {
    // ... work with the PDF via Apache PDFBox ...
    PDFRenderer pdfRenderer = new PDFRenderer(reader.getDocument());
    // ...
}

// Or save to a file first
NewspaperReader reader = librePress.openNewspaper(issue, new File("newspaper.pdf")).join();

// Unregister when no longer needed
librePress.unregisterDevice("user@example.com", device).join();
```

All API methods return `CompletableFuture` and are non-blocking.

## Core Concepts

- **Credentials** - Holds username and password. Call `clear()` after use to wipe the password from memory.
- **DeviceRegistration** - Represents a registered device identified by an activation token and client ID. Persist this between sessions to avoid
  re-registering.
- **NewspaperIssue** - An activated newspaper with download URLs, an expiration time, and the encryption key.
- **NewspaperReader** - Opens the DRM-protected PDF via PDFBox. Call `getDocument()` to access the `PDDocument`.
- **EncryptionKeyProvider** - Lower-level class for decrypting activation certificates when you already have the encoded certificate string.

## Advanced Usage

For more control, use the individual services directly instead of the `LibrePress` facade:

- `DeviceRegistrationService` - Register/unregister devices, list available providers
- `MessageService` - Poll for pushed newspapers, delete messages
- `ActivationService` - Activate a specific newspaper and open it

## Development

### Build

```bash
./gradlew build
```

### Run Tests

```bash
./gradlew test
```

Integration tests require credential secrets (see [the README file](src/test/resources/it/kapfer/librepress/integration/README.md) for the required environment
variables).

### Publish

Releases are published to GitHub Packages automatically when a GitHub release is created. The CI workflow builds and publishes using `./gradlew publish`.

### Project Structure

```
src/main/java/it/kapfer/librepress/
├── LibrePress.java            # Main entry point
├── drm/                       # Certificate decryption and encryption key extraction
├── pdf/                       # PDF opening with NDPD custom security handler
└── server/                    # API communication, device management, messaging
```
