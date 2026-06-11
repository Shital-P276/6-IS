# Experiment 6 – Digital Signature & PKI (OpenSSL)
**Title:** Digital Signature Creation and Digitally Signed Certificate

---

## What This Experiment Is About
You are simulating a university that:
- Acts as its own Certificate Authority (CA)
- Issues a certificate to an Exam Server
- Uses that certificate to digitally sign a question paper (PDF or text file)
- Verifies the signature to prove the document is genuine and untampered

---

## Entities in This Experiment
| Entity | Role | Files |
|--------|------|-------|
| University CA | Issues certificates (trust anchor) | `university_ca.key`, `university_ca.crt` |
| Exam Server | Signs the question paper | `exam_server.key`, `exam_server.csr`, `exam_server.crt` |
| Exam Client | Verifies the signature | Uses `university_ca.crt` to verify |
| Attacker | Tampers the document | Shows signature breaks |

---

## PART 1 – Setup (Run Once)

### Install OpenSSL
```bash
sudo apt update
sudo apt install openssl -y
openssl version
```

### Create a Working Directory
```bash
mkdir pki_demo
cd pki_demo
```

---

## PART 2 – Create University Root CA

### Step 1 – Generate CA Private Key
```bash
openssl genrsa -out university_ca.key 4096
```
- This creates a 4096-bit RSA private key
- This key is the university's secret — used to sign certificates
- **Never share this file**

### Step 2 – Create Self-Signed CA Certificate
```bash
openssl req -x509 -new -nodes -key university_ca.key -sha256 -days 3650 -out university_ca.crt
```
When prompted, fill in details for the **University/CA**:
```
Country Name: IN
State: Maharashtra
Locality: Kolhapur
Organization Name: KIT University CA        ← remember this, must be DIFFERENT from server
Organizational Unit: Certificate Authority
Common Name: KIT Root CA                    ← remember this, must be DIFFERENT from server
Email: ca@kit.edu
```
- `-x509` means self-signed (CA signs its own certificate)
- `-days 3650` = valid for 10 years
- This creates `university_ca.crt` — the public certificate everyone will trust

### Verify CA Certificate Was Created
```bash
openssl x509 -in university_ca.crt -text -noout
```
Look for: `Issuer` and `Subject` should both say KIT Root CA (self-signed)

---

## PART 3 – Issue Certificate to Exam Server

### Step 3 – Generate Exam Server Private Key
```bash
openssl genrsa -out exam_server.key 2048
```
- 2048-bit is fine for the server (CA uses 4096 for extra security)

### Step 4 – Create Certificate Signing Request (CSR)
```bash
openssl req -new -key exam_server.key -out exam_server.csr
```
When prompted, fill in details for the **Exam Server** — USE DIFFERENT VALUES than CA:
```
Country Name: IN
State: Maharashtra
Locality: Kolhapur
Organization Name: KIT Exam Department      ← DIFFERENT from CA
Organizational Unit: Exam Server
Common Name: ExamServer                     ← DIFFERENT from CA
Email: examserver@kit.edu
```
> ⚠️ If you use the same Organization + Common Name as the CA, verification will fail with "self-signed certificate" error

### Step 5 – CA Signs the Server Certificate
```bash
openssl x509 -req -in exam_server.csr \
  -CA university_ca.crt \
  -CAkey university_ca.key \
  -CAcreateserial \
  -out exam_server.crt \
  -days 365 -sha256
```
- The CA reads the CSR, signs it with its private key, and outputs `exam_server.crt`
- `-days 365` = server cert valid for 1 year (shorter than CA)

### Step 6 – Verify the Trust Chain
```bash
openssl verify -CAfile university_ca.crt exam_server.crt
```
Expected:
```
exam_server.crt: OK
```
This confirms: CA signed the server certificate → trust chain established ✔

---

## PART 4 – Sign a Document

### METHOD A – Terminal (Always Works)

#### Create the Question Paper
```bash
echo "Question Paper - Information Security - Final Exam 2025" > question_paper.txt
cat question_paper.txt
```

#### Sign It
```bash
openssl dgst -sha256 -sign exam_server.key -out signature.bin question_paper.txt
```
- Calculates SHA256 hash of the file
- Encrypts the hash using exam_server's private key
- Saves the signature as `signature.bin`

#### Verify the Signature
```bash
openssl dgst -sha256 \
  -verify <(openssl x509 -in exam_server.crt -pubkey -noout) \
  -signature signature.bin \
  question_paper.txt
```
Expected:
```
Verified OK
```

#### Tampering Test
```bash
# Tamper the document
echo "EXTRA LINE ADDED BY ATTACKER" >> question_paper.txt

# Try to verify again
openssl dgst -sha256 \
  -verify <(openssl x509 -in exam_server.crt -pubkey -noout) \
  -signature signature.bin \
  question_paper.txt
```
Expected:
```
Verification Failure
```

---

### METHOD B – Adobe Acrobat (If Available on Lab PC)

#### Convert Certificate to .p12 Format (Adobe Compatible)
```bash
openssl pkcs12 -export \
  -out exam_server.p12 \
  -inkey exam_server.key \
  -in exam_server.crt
```
> Set a password when prompted — you'll need it in Adobe

#### Sign PDF in Adobe (Exam Server Side)
1. Open **Adobe Acrobat**
2. `Preferences → Signatures → Digital IDs → Import` → select `exam_server.p12` → enter password
3. Open `question_paper.pdf`
4. `Tools → Certificates → Digitally Sign`
5. Draw a box on document → select certificate → click **Sign**
6. Save as `question_paper_signed.pdf`

#### Verify in Adobe (Exam Client Side)
1. `Preferences → Trusted Certificates → Import` → select `university_ca.crt`
2. Check ✔ **Trust for signatures**
3. Open `question_paper_signed.pdf`

Expected: ✔ *"Signed and all signatures are valid"*

#### Tampering Test in Adobe
1. Open `question_paper_signed.pdf` in Notepad/text editor
2. Change any character → save
3. Reopen in Adobe

Expected: ✗ *"Signature is invalid – Document has been altered"*

---

## All Files Created in This Experiment
```
pki_demo/
├── university_ca.key       ← CA private key (secret)
├── university_ca.crt       ← CA public certificate
├── exam_server.key         ← Server private key (secret)
├── exam_server.csr         ← Server signing request (intermediate)
├── exam_server.crt         ← Server certificate (signed by CA)
├── exam_server.p12         ← Adobe-compatible bundle (key + cert)
├── question_paper.txt      ← The document
└── signature.bin           ← The digital signature
```

---

## How Digital Signature Works (For Viva)
```
SIGNING:
  Document → SHA256 Hash → Encrypt with Private Key → Signature

VERIFICATION:
  Signature → Decrypt with Public Key  → Hash A
  Document  → Recalculate SHA256       → Hash B
  Hash A == Hash B  →  ✔ Valid
  Hash A != Hash B  →  ✗ Tampered
```

## Key Terms (For Viva)
| Term | Meaning |
|------|---------|
| Root CA | Trusted authority that issues and signs certificates |
| CSR | Certificate Signing Request — server asks CA for a cert |
| X.509 | Standard format for digital certificates |
| PKCS#12 (.p12) | File format bundling private key + certificate (for Adobe) |
| SHA-256 | Hash algorithm — creates unique fingerprint of document |
| Digital Signature | Encrypted hash proving who signed and document is unchanged |
| Non-repudiation | Signer cannot deny having signed the document |
| Trust Chain | Client trusts CA → CA signed Server → Server signed document |
