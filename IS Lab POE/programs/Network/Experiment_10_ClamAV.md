# Experiment 10 – Malware Detection using ClamAV and VirusTotal
**Title:** Malware Detection and Security Analysis Using ClamAV and Online Threat Intelligence

---

## What This Experiment Is About
You will:
- Install ClamAV antivirus on Linux
- Update its virus signature database from the internet
- Create a harmless test malware file (EICAR)
- Scan and detect it using ClamAV
- Verify it on VirusTotal (online threat intelligence)
- Remove the infected file

With network access, the `freshclam` update will actually download real signatures — making this a proper working demo.

---

## PART 1 – Install ClamAV

### Step 1 – Install
```bash
sudo apt update
sudo apt install clamav clamav-daemon -y
```

### Step 2 – Verify Installation
```bash
clamscan --version
```
Expected:
```
ClamAV 1.x.x/xxxxxxx/...
```

---

## PART 2 – Update Virus Signatures (Needs Internet)

### Step 3 – Stop Services Before Updating
```bash
sudo systemctl stop clamav-freshclam
sudo systemctl stop clamav-daemon
```
> These services lock the database — must stop them before freshclam can update

### Step 4 – Update Signatures
```bash
sudo freshclam
```
With network access you'll see:
```
ClamAV update process started at ...
Downloading daily.cvd [100%]
Database updated (version: xxxxxxx, sigs: 8700000+, ...)
```
> This downloads the latest malware signatures from ClamAV servers — needs internet ✔

### Step 5 – Start ClamAV Service
```bash
sudo systemctl start clamav-daemon
sudo systemctl status clamav-daemon
```
Expected: `Active: active (running)`

---

## PART 3 – Create Test Files

### Step 6 – Create Lab Directory
```bash
mkdir ~/malware_lab
cd ~/malware_lab
```

### Step 7 – Create a Clean Normal File
```bash
echo "This is a normal exam question paper file" > normal.txt
```

### Step 8 – Create EICAR Test Malware File
```bash
echo 'X5O!P%@AP[4\PZX54(P^)7CC)7}$EICAR-STANDARD-ANTIVIRUS-TESTFILE!$H+H*' > eicar.txt
echo 'X5O!P%@AP[4\PZX54(P^)7CC)7}$EICAR-STANDARD-ANTIVIRUS-TESTFILE!$H+H*' > eicar.com
```

**What is EICAR?**
- A globally standardized test file used to verify antivirus is working
- Completely harmless — cannot do anything malicious
- Every antivirus in the world recognizes and flags it
- Safe to create and use for testing

### Step 9 – Verify Files Created
```bash
ls -la ~/malware_lab
cat normal.txt
cat eicar.txt
```

---

## PART 4 – Scan for Malware

### Step 10 – Scan the Directory
```bash
cd ~
clamscan malware_lab
```
Expected output:
```
malware_lab/normal.txt: OK
malware_lab/eicar.txt: Eicar-Test-Signature FOUND
malware_lab/eicar.com: Eicar-Test-Signature FOUND

----------- SCAN SUMMARY -----------
Known viruses:    8700000+
Engine version:   1.x.x
Scanned files:    3
Infected files:   2
```

### Step 11 – Recursive Scan (Scans All Subdirectories)
```bash
clamscan -r malware_lab
```

### Step 12 – Show Only Infected Files
```bash
clamscan -r -i malware_lab
```
> `-i` = show infected files only — cleaner output

### Step 13 – Scan and Remove Infected Files
```bash
clamscan --remove malware_lab
```
Expected:
```
malware_lab/eicar.txt: Eicar-Test-Signature FOUND
malware_lab/eicar.txt: Removed.
malware_lab/eicar.com: Eicar-Test-Signature FOUND
malware_lab/eicar.com: Removed.
```

### Step 14 – Verify Removal
```bash
ls malware_lab
```
Expected: only `normal.txt` remains

---

## PART 5 – VirusTotal Verification (Needs Internet + Browser)

1. Recreate the EICAR file first:
```bash
echo 'X5O!P%@AP[4\PZX54(P^)7CC)7}$EICAR-STANDARD-ANTIVIRUS-TESTFILE!$H+H*' > ~/malware_lab/eicar.txt
```

2. Open Firefox → go to:
```
https://www.virustotal.com
```

3. Click **"Choose File"** → navigate to `~/malware_lab/eicar.txt` → Upload

4. Wait for analysis (~10 seconds with internet)

5. You'll see something like:
```
64/72 security vendors flagged this file as malicious
Detection: Eicar-Test-Signature / EICAR_Test_File / ...
```

> This is exactly how SOC analysts verify suspicious files using global threat intelligence

---

## Quick Command Reference
```bash
# Install
sudo apt install clamav clamav-daemon -y

# Update signatures (stop services first)
sudo systemctl stop clamav-freshclam
sudo systemctl stop clamav-daemon
sudo freshclam

# Start service
sudo systemctl start clamav-daemon

# Scan commands
clamscan <directory>           # basic scan
clamscan -r <directory>        # recursive scan
clamscan -r -i <directory>     # show only infected
clamscan --remove <directory>  # scan and delete infected
clamscan --version             # check version
```

---

## Key Terms (For Viva)
| Term | Meaning |
|------|---------|
| ClamAV | Open-source antivirus engine for Linux |
| freshclam | ClamAV's tool to download latest virus signatures |
| EICAR | Standard harmless test file — recognized by all AV software |
| Signature-based | Compares file patterns to known malware database |
| Heuristic detection | Detects suspicious behavior even without known signature |
| VirusTotal | Online platform — scans files with 70+ AV engines |
| SOC | Security Operations Center — team monitoring and responding to threats |
| Threat Intelligence | Global database of known malware information |

## Observation Table
| Activity | Result |
|----------|--------|
| ClamAV installed | Version shown ✔ |
| freshclam update | 8,700,000+ signatures downloaded ✔ |
| normal.txt scan | OK — Clean ✔ |
| eicar.txt scan | Eicar-Test-Signature FOUND ✔ |
| --remove flag | Infected files deleted ✔ |
| VirusTotal upload | 64/72 engines detect as malware ✔ |
