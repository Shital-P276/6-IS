# Experiment 6 – Digital Signature & PKI
**Title:** Digital Signature Creation and Digitally Signed Certificate

---

## Mental Model
- 🏛️ **University CA** → the trusted authority (makes the official stamp)
- 📋 **Exam Server** → gets a certificate from CA, signs the question paper
- 🎓 **Exam Client** → installs CA cert, verifies the signed document
- 🔴 **Attacker** → tampers document → signature breaks

---

## PART 1 – Common Setup (Do This First, Always)
> Same for both methods. Run on Killercoda / any Linux terminal.

### Step 1 – Install OpenSSL
```bash
apt-get install openssl -y
```

### Step 2 – Create University Root CA
```bash
# Generate CA private key (4096-bit)
openssl genrsa -out university_ca.key 4096

# Create self-signed CA certificate (valid 10 years)
openssl req -x509 -new -nodes -key university_ca.key -sha256 -days 3650 -out university_ca.crt
```
> Fill in prompts: Country=IN, State=Maharashtra, Org=University CA, Common Name=University CA

### Step 3 – Issue Certificate to Exam Server
```bash
# Generate Exam Server private key
openssl genrsa -out exam_server.key 2048

# Create Certificate Signing Request
openssl req -new -key exam_server.key -out exam_server.csr

# CA signs the CSR and issues certificate
openssl x509 -req -in exam_server.csr \
  -CA university_ca.crt \
  -CAkey university_ca.key \
  -CAcreateserial \
  -out exam_server.crt \
  -days 365 -sha256
```

### Step 4 – Verify Certificate Chain (Trust Check)
```bash
openssl verify -CAfile university_ca.crt exam_server.crt
```
Expected output:
```
exam_server.crt: OK
```

### Files You Now Have
| File | Purpose |
|------|---------|
| `university_ca.key` | CA private key (never share) |
| `university_ca.crt` | CA public certificate (give to clients) |
| `exam_server.key` | Server private key (for signing) |
| `exam_server.crt` | Server certificate (signed by CA) |

---
---

## METHOD A – Using Adobe Acrobat (Lab PC)
> Use this when Adobe Acrobat is available on the lab computer

### Step 1 – Convert Certificate to Adobe Format (.p12)
```bash
openssl pkcs12 -export \
  -out exam_server.p12 \
  -inkey exam_server.key \
  -in exam_server.crt
```
> Set a password when prompted — you'll need it in Adobe

### Step 2 – Sign PDF (Exam Server Side)
1. Open **Adobe Acrobat**
2. Import certificate:
   `Preferences → Signatures → Digital IDs → Import` → select `exam_server.p12` → enter password
3. Open `question_paper.pdf`
4. Click `Tools → Certificates → Digitally Sign`
5. Draw a signature box on the document
6. Select the `exam_server` certificate → click **Sign**
7. Save as `question_paper_signed.pdf`

### Step 3 – Verify Signature (Exam Client Side)
1. Open **Adobe Acrobat**
2. Import CA certificate:
   `Preferences → Trusted Certificates → Import` → select `university_ca.crt`
3. Check ✔ **Trust for signatures**
4. Open `question_paper_signed.pdf`

**Expected output:**
```
✔ Signed and all signatures are valid
```

### Step 4 – Tampering Test
1. Open `question_paper_signed.pdf` in a text editor
2. Change any character → save
3. Open again in Adobe

**Expected output:**
```
✗ Signature is invalid – Document has been altered
```

---
---

## METHOD B – Terminal Only (Killercoda / No Adobe)
> Use this when practicing online or if Adobe is not available.
> Same cryptographic concept — just uses a text file instead of PDF.

### Step 1 – Create the Question Paper (text file)
```bash
echo "Question Paper - Final Exam 2025" > question_paper.txt
cat question_paper.txt
```

### Step 2 – Sign the Document (Exam Server)
```bash
openssl dgst -sha256 -sign exam_server.key -out signature.bin question_paper.txt
```
> This creates `signature.bin` — the digital signature file

### Step 3 – Verify the Signature (Exam Client)
```bash
openssl dgst -sha256 \
  -verify <(openssl x509 -in exam_server.crt -pubkey -noout) \
  -signature signature.bin \
  question_paper.txt
```

**Expected output:**
```
Verified OK
```

### Step 4 – Tampering Test
```bash
# Tamper the document
echo "TAMPERED CONTENT" >> question_paper.txt

# Try to verify again
openssl dgst -sha256 \
  -verify <(openssl x509 -in exam_server.crt -pubkey -noout) \
  -signature signature.bin \
  question_paper.txt
```

**Expected output:**
```
Verification Failure
```

### Step 5 – View Certificate Details (Bonus / Viva)
```bash
# View full certificate info
openssl x509 -in exam_server.crt -text -noout

# View CA certificate info
openssl x509 -in university_ca.crt -text -noout

# Check .p12 contents
openssl pkcs12 -info -in exam_server.p12
```

---

## How It Works (For Viva)

```
SIGNING:
  Document → SHA256 Hash → Encrypt with Private Key → Signature

VERIFICATION:
  Signature → Decrypt with Public Key → Get Hash A
  Document  → Recalculate SHA256          → Get Hash B
  Hash A == Hash B → ✔ Valid
  Hash A != Hash B → ✗ Tampered
```

## Key Terms (For Viva)

| Term | Meaning |
|------|---------|
| Root CA | Trusted authority that issues certificates |
| CSR | Certificate Signing Request — server asks CA for a cert |
| PKCS#12 (.p12) | Format that bundles private key + certificate for Adobe |
| SHA-256 | Hash algorithm used to fingerprint the document |
| Digital Signature | Encrypted hash of document using private key |
| Non-repudiation | Signer cannot deny having signed |
| Trust Chain | Client trusts CA → CA signed Server cert → Server signed document |
