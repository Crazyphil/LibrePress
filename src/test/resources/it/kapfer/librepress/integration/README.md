# `integration` test resources

This directory contains real-world credentials and test files for tests in the `it.kapfer.librepress.integration` package. These files are **not** checked in to
Git on purpose, as they contain secrets that should not be shared with the world and copyrighted material. Users who want to run the tests with their own files
can put those in this directory and adapt the `@ValueSource` annotations in the appropriate test classes.

## File naming

The credentials file with real-world secrets for integration tests is called `credentials.secrets`.

Each real activation test case is identified by a filename prefix, for example `brain-games`. The same prefix is used for the activation response file and its
metadata file:

```text
<prefix>.activation
<prefix>.properties
```

## `credentials.secrets` file

The `credentials.secrets` file is a Java properties file with credentials and device registration data to use the NewspaperDirect API. As an alternative to this
file, the individual properties can also be provided as environment variables, making the secrets CI-friendly.

Required properties:

| Property          | Environment variable           | Required value                                                                       |
|-------------------|--------------------------------|--------------------------------------------------------------------------------------|
| `username`        | `CREDENTIALS_USERNAME`         | Username of the account to authenticate to                                           |
| `password`        | `CREDENTIALS_PASSWORD`         | Password of the account to authenticate to                                           |
| `activationToken` | `CREDENTIALS_ACTIVATION_TOKEN` | Activation token returned by the server on device registration                       |
| `clientId`        | `CREDENTIALS_CLIENT_ID`        | Client ID used by the client to register the device                                  |
| `licenseUrl`      | `CREDENTIALS_LICENSE_URL`      | Activation URL for a newspaper issue that was already activated for the client above |

## `.activation` files

The `.activation` files contain captured XML activation responses from the NewspaperDirect content activation service. The integration test serves this file as
the HTTP response body and verifies that `ActivationService` maps it to the expected `NewspaperIssue` values.

## `.properties` files

Each `.activation` file must have a matching Java properties file with the same prefix. The test loads this file to create the device registration and to verify
the parsed activation result.

Required properties:

| Property            | Required value                                                                                           |
|---------------------|----------------------------------------------------------------------------------------------------------|
| `clientNumber`      | Integer client number used to create the `DeviceRegistration` for the activation request.                |
| `title`             | Expected newspaper title returned by the activation response.                                            |
| `issue`             | Expected issue identifier returned by the activation response.                                           |
| `urlExpirationTime` | Expected URL expiration timestamp in ISO-8601 local date-time format, for example `2026-07-02T23:59:59`. |
| `downloadUrl`       | Expected first download URL returned by the activation response.                                         |
| `encryptionKey`     | Base64-encoded encryption key expected in the parsed `NewspaperIssue`.                                   |

Example:

```properties
clientNumber=-1765963002
title=Brain Games
issue=sfdy2019021000000000001001
urlExpirationTime=2026-07-02T23:59:59
downloadUrl=http://cdn.ndcds.net/cds/files?...
encryptionKey=kSa2OBeotiMF9wR7bDo69g==
```

The test assumes both files exist. If either the `.activation` file or the `.properties` file is missing, the corresponding parameterized test invocation is
skipped.
