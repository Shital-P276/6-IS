# Experiment 10 – Malware Detection using ClamAV
**Title:** Malware Detection and Security Analysis Using ClamAV and VirusTotal

---

## Mental Model
- **ClamAV** = antivirus software for Linux
- **Signature database** = list of known malware fingerprints (needs to be updated)
- **EICAR file** = a fake harmless "malware" used for testing antivirus
- **VirusTotal** = website that scans your file using 70+ antivirus engines at once
- **freshclam** = the command that updates ClamAV's virus database

---

## Flow
```
Install ClamAV → Update Signatures → Create Test Malware (EICAR)
→ Scan → Detected → Verify on VirusTotal → Remove
```

---

## PART 1 – Install and Configure ClamAV

### Step 1 – Update System
```bash
sudo apt update
```

### Step 2 – Install ClamAV
```bash
sudo apt install clamav clamav-daemon -y
```

### Step 3 – Verify Installation
```bash
clamscan --version
```
Expected:
```
ClamAV 1.x.x
```

### Step 4 – Update Virus Signatures
```bash
# Stop services first (required before update)
sudo systemctl stop clamav-freshclam
sudo systemctl stop clamav-daemon

# Download latest signatures
sudo freshclam
```
Expected:
```
Database updated (version: xxxxxxx)
```

### Step 5 – Start ClamAV Service
```bash
sudo systemctl start clamav-daemon
sudo systemctl status clamav-daemon
```
Expected: `Active: active (running)`

---

## PART 2 – Create Test Files

### Step 6 – Create Lab Directory
```bash
mkdir malware_lab
cd malware_lab
```

### Step 7 – Create a Normal File
```bash
echo "This is a normal file" > normal.txt
```

### Step 8 – Create EICAR Test Malware File
```bash
echo 'X5O!P%@AP[4\PZX54(P^)7CC)7}$EICAR-STANDARD-ANTIVIRUS-TESTFILE!$H+H*' > eicar.txt
echo 'X5O!P%@AP[4\PZX54(P^)7CC)7}$EICAR-STANDARD-ANTIVIRUS-TESTFILE!$H+H*' > eicar.com
```
> EICAR is a globally recognized harmless test file — all antivirus software detects it

---

## PART 3 – Scan for Malware

### Step 9 – Scan the Directory
```bash
cd ..
clamscan malware_lab
```
Expected output:
```
malware_lab/normal.txt: OK
malware_lab/eicar.txt: Eicar-Test-Signature FOUND
malware_lab/eicar.com: Eicar-Test-Signature FOUND
```

### Step 10 – Recursive Scan
```bash
clamscan -r malware_lab
```
> Scans all files inside all subdirectories

### Step 11 – Scan with Summary
```bash
clamscan -r --bell malware_lab
```
Expected scan summary:
```
----------- SCAN SUMMARY -----------
Known viruses:    8700000+
Scanned files:    3
Infected files:   2
```

### Step 12 – Remove Infected Files
```bash
clamscan --remove malware_lab
```
Expected:
```
malware_lab/eicar.txt: Eicar-Test-Signature FOUND
malware_lab/eicar.txt: Removed.
```

---

## PART 4 – VirusTotal Verification (Browser)

1. Open browser → `https://www.virustotal.com`
2. Click **"Choose File"** → upload `eicar.txt`
3. See results — 60+ engines flag it as malware

> This shows how threat intelligence platforms work in real SOC environments

---

## Quick Command Reference

```bash
clamscan <file>              # scan single file
clamscan <directory>         # scan directory
clamscan -r <directory>      # recursive scan
clamscan --remove <dir>      # scan and delete infected
clamscan -i <directory>      # show only infected files
sudo freshclam               # update virus database
clamscan --version           # check version
```

---

## Key Terms (For Viva)

| Term | Meaning |
|------|---------|
| ClamAV | Open-source antivirus for Linux |
| EICAR | Harmless standard test file to verify antivirus works |
| Signature-based | Compares file hash/pattern to known malware database |
| Heuristic | Detects suspicious behavior even without known signature |
| VirusTotal | Online platform scanning files with 70+ AV engines |
| freshclam | ClamAV's database updater tool |
| SOC | Security Operations Center — team that monitors threats |
| Threat Intelligence | Global database of known malware info |

---

## Observation Table (Write in Record)

| Activity | Result |
|----------|--------|
| ClamAV installed | Successful |
| freshclam update | Signatures downloaded |
| normal.txt scan | Clean — OK |
| eicar.txt scan | Eicar-Test-Signature FOUND |
| VirusTotal upload | Detected by 60+ engines |
| --remove flag | Infected file deleted |
