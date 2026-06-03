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

| Property          | Environment variable           | Required value                                                 |
|-------------------|--------------------------------|----------------------------------------------------------------|
| `username`        | `CREDENTIALS_USERNAME`         | Username of the account to authenticate to                     |
| `password`        | `CREDENTIALS_PASSWORD`         | Password of the account to authenticate to                     |
| `activationToken` | `CREDENTIALS_ACTIVATION_TOKEN` | Activation token returned by the server on device registration |
| `clientId`        | `CREDENTIALS_CLIENT_ID`        | Client ID used by the client to register the device            |
