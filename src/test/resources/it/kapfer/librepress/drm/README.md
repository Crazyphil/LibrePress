# `drm` test resources

This directory contains test files for tests in the `it.kapfer.librepress.drm` package. These files are **not** checked in to Git on purpose, as they contain
copyrighted material. Users who want to run the tests with their own files can put those in this directory and adapt the `@ValueSource` annotations in the
appropriate test classes.

## File naming

Each DRM test case is a Java properties file with a `.certificate` extension:

```text
<prefix>.certificate
```

## `.certificate` files

The `.certificate` files contain certificate data and related device credentials used to construct an `EncryptionKeyProvider`. Although they use a custom file
extension, they are loaded as Java properties files.

Properties read by the test:

| Property             | Required value                                                                                                          |
|----------------------|-------------------------------------------------------------------------------------------------------------------------|
| `clientNumber`       | Integer client number passed to `EncryptionKeyProvider`.                                                                |
| `clientAddress`      | Client device address as a MAC-address-like string; may be empty or omitted if the certificate does not require one.    |
| `activationPassword` | Activation password passed to `EncryptionKeyProvider`; may be empty or omitted if the certificate does not require one. |
| `certificate`        | Base64-encoded certificate string used by `EncryptionKeyProvider` to derive an encryption key.                          |

Example:

```properties
clientNumber=123456789
clientAddress=00:00:00:00:00:00
#activationPassword=
certificate=dGVzdA==
```

The test assumes the `.certificate` file exists. If it is missing, the corresponding parameterized test invocation is skipped.
