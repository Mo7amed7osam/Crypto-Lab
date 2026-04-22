# Cryptography Lab (Java Swing)

A complete desktop app for encryption, decryption, and basic attack tooling for classic ciphers.

## Features

- Java Swing GUI with `JTabbedPane` tabs:
  - Encrypt
  - Decrypt
  - Attack
- Ciphers:
  - Caesar
  - Vigenere
  - One Time Pad (Encrypt only)
  - Rail Fence
  - Row Transposition
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
- AES-128 key expansion viewer
- AES-128 key expansion console program with input format, source, and output menus

## Project Structure

- `Main.java`
- `ui/`
- `ciphers/`
- `attacks/`
- `utils/`
- `files/`

## Compile

```bash
javac Main.java AesKeyExpansionProgram.java ui/*.java ciphers/*.java attacks/*.java utils/*.java
```

## Run

GUI app:

```bash
java Main
```

AES key expansion program:

```bash
java AesKeyExpansionProgram
```

## Usage Notes

- `Letters only (A-Z)` is ON by default and removes non-letters.
- OTP key length must equal text letter count (encryption only).
- Playfair merges `I/J`.
- Hill key matrix must be invertible modulo 26.

## Sample Data

Check files under `files/` for quick testing.
