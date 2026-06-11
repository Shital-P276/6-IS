# Experiment 12 – VAPT Tools: Nmap, Nikto, WPScan
**Title:** Demonstration of VAPT Tools in Kali Linux

---

## Mental Model
- **VAPT** = Vulnerability Assessment and Penetration Testing
- **Nmap** = scans a network/machine to find open ports and services
- **Nikto** = scans a web server for known vulnerabilities
- **WPScan** = scans WordPress sites for vulnerabilities
- Think of it as: Nmap = find the doors, Nikto = check if doors are weak, WPScan = WordPress-specific check

---

## Flow
```
Start Apache (local web server) → Nmap scan (find open ports)
→ Nikto scan (find web vulnerabilities) → WPScan (WordPress scan)
```

---

## PART 1 – Setup Local Web Server

### Step 1 – Start Apache
```bash
sudo systemctl start apache2
sudo systemctl enable apache2
sudo systemctl status apache2
```
Expected: `Active: active (running)`

### Step 2 – Verify in Browser
Open browser → `http://localhost` or `http://127.0.0.1`
You should see the Apache default page.

---

## PART 2 – Nmap Network Scanning

### Step 3 – Install Nmap (if not installed)
```bash
sudo apt install nmap -y
```

### Step 4 – Basic Scan
```bash
nmap 127.0.0.1
```
Expected output:
```
PORT   STATE SERVICE
80/tcp open  http
22/tcp open  ssh
```

### Step 5 – Service Version Scan
```bash
nmap -sV 127.0.0.1
```
> Shows what software is running on each port

### Step 6 – Aggressive Scan
```bash
nmap -A 127.0.0.1
```
> Shows OS detection, version, scripts, traceroute

### Step 7 – Scan Specific Port
```bash
nmap -p 80 127.0.0.1
nmap -p 1-1000 127.0.0.1
```

---

## PART 3 – Nikto Web Vulnerability Scan

### Step 8 – Install Nikto (if not installed)
```bash
sudo apt install nikto -y
```

### Step 9 – Scan Local Web Server
```bash
nikto -h http://127.0.0.1
```
Expected output shows:
```
+ Server: Apache/x.x.x (Ubuntu)
+ The anti-clickjacking X-Frame-Options header is not present
+ No CGI Directories found
+ Allowed HTTP Methods: GET, POST, OPTIONS, HEAD
+ /icons/README: Apache default file found
```

### Step 10 – Save Output to File
```bash
nikto -h http://127.0.0.1 -o nikto_report.txt
cat nikto_report.txt
```

---

## PART 4 – WPScan WordPress Vulnerability Scan

### Step 11 – Install WPScan (if not installed)
```bash
sudo apt install wpscan -y
```

### Step 12 – Basic WordPress Scan
```bash
wpscan --url https://demo.wp-api.org
```
> Use this demo site — it's a public WordPress test site

### Step 13 – Enumerate Users
```bash
wpscan --url https://demo.wp-api.org --enumerate u
```
Expected: Lists WordPress usernames found

### Step 14 – Enumerate Plugins
```bash
wpscan --url https://demo.wp-api.org --enumerate p
```
Expected: Lists installed plugins and known vulnerabilities

### Step 15 – Enumerate Themes
```bash
wpscan --url https://demo.wp-api.org --enumerate t
```

---

## Quick Command Reference

```bash
# Nmap
nmap 127.0.0.1                    # basic scan
nmap -sV 127.0.0.1                # version detection
nmap -A 127.0.0.1                 # aggressive scan
nmap -p 80 127.0.0.1              # specific port
nmap -p 1-65535 127.0.0.1         # all ports

# Nikto
nikto -h http://127.0.0.1         # scan web server
nikto -h http://127.0.0.1 -o out.txt  # save output

# WPScan
wpscan --url <site>               # basic scan
wpscan --url <site> --enumerate u # find users
wpscan --url <site> --enumerate p # find plugins
wpscan --url <site> --enumerate t # find themes
```

---

## Key Terms (For Viva)

| Term | Meaning |
|------|---------|
| VAPT | Vulnerability Assessment and Penetration Testing |
| Nmap | Network Mapper — finds open ports and services |
| Nikto | Web server vulnerability scanner |
| WPScan | WordPress-specific vulnerability scanner |
| Open Port | A door that is accepting connections |
| Vulnerability | A weakness that can be exploited |
| Enumeration | Gathering info about users, plugins, services |
| Apache | Popular web server software |
| CVE | Common Vulnerabilities and Exposures — vulnerability ID system |

---

## VAPT Phases (For Viva)

```
1. Information Gathering   → who/what is the target
2. Network Scanning        → Nmap — find open ports
3. Vulnerability Scanning  → Nikto/WPScan — find weaknesses
4. Security Analysis       → understand impact of findings
```

---

## Observation Table (Write in Record)

| Tool | Command | Result |
|------|---------|--------|
| Apache | `systemctl start apache2` | Web server running |
| Nmap basic | `nmap 127.0.0.1` | Port 80 open (http) |
| Nmap version | `nmap -sV 127.0.0.1` | Apache version shown |
| Nikto | `nikto -h http://127.0.0.1` | Vulnerabilities listed |
| WPScan basic | `wpscan --url demo.wp-api.org` | WordPress info shown |
| WPScan users | `--enumerate u` | Usernames found |
| WPScan plugins | `--enumerate p` | Plugin list shown |
