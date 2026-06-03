# `pdf` test resources

This directory contains test files for tests in the `it.kapfer.librepress.pdf` package. These files are **not** checked in to Git on purpose, as they contain
copyrighted material. Users who want to run the tests with their own files can put those in this directory and adapt the `@ValueSource` annotations in the
appropriate test classes.

## File naming

Each test case is identified by a filename prefix, for example `newspaper`. The same prefix is used for the binary test file and its metadata file:

```text
<prefix>.pdn
<prefix>.properties
```

## `.pdn` files

The `.pdn` files are encrypted NewspaperDirect PDF data files used as input for `NewspaperReader`.

## `.properties` files

Each `.pdn` file must have a matching Java properties file with the same prefix. The test loads this file to get the decryption key and the expected values used
to verify the decrypted PDF.

Required properties:

| Property        | Required value                                                                                   |
|-----------------|--------------------------------------------------------------------------------------------------|
| `encryptionKey` | Base64-encoded decryption key for the matching `.pdn` file.                                      |
| `documentTitle` | Expected PDF document title after decryption, as returned by `PDDocumentInformation.getTitle()`. |
| `creationDate`  | Expected PDF creation timestamp in ISO-8601 instant format, for example `2026-06-01T12:34:56Z`.  |
| `page1Contents` | Expected byte count returned on the first page's content stream after decryption.                |

Example:

```properties
encryptionKey=dGVzdA==
documentTitle=Newspaper
creationDate=2026-06-01T12:34:56Z
page1Contents=16384
```

The test assumes both files exist. If either the `.pdn` file or the `.properties` file is missing, the corresponding parameterized test invocation is skipped.
