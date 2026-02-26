# Cryptography Lab (Java Swing)

A complete desktop app for encryption, decryption, and basic attack tooling for classic ciphers.

## Features

- Java Swing GUI with `JTabbedPane` tabs:
  - Encrypt
  - Decrypt
  - Attack
- Ciphers:
  - Caesar
  - Playfair
  - Hill Cipher
- File support (UTF-8):
  - Load From File
  - Save Output To File
  - Encrypt File
  - Decrypt File
- Attack tools:
  - Caesar brute-force
  - Hill known-plaintext attack

## Project Structure

- `Main.java`
- `ui/`
- `ciphers/`
- `attacks/`
- `utils/`
- `files/`

## Compile

```bash
javac Main.java ui/*.java ciphers/*.java attacks/*.java utils/*.java
```

## Run

```bash
java Main
```

## Usage Notes

- `Letters only (A-Z)` is ON by default and removes non-letters.
- OTP key length must equal text letter count.
- Monoalphabetic map must contain 26 unique letters.
- Playfair merges `I/J`.
- Hill key matrix must be invertible modulo 26.

## Sample Data

Check files under `files/` for quick testing.
