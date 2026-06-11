# Experiment 12 – VAPT Tools: Nmap, Nikto, WPScan
**Title:** Demonstration of VAPT Tools in Kali Linux

---

## What This Experiment Is About
VAPT = Vulnerability Assessment and Penetration Testing

You will use 3 tools to scan for vulnerabilities:
- **Nmap** → finds open ports and services on a machine
- **Nikto** → scans a web server for known vulnerabilities
- **WPScan** → scans a WordPress site for vulnerabilities, users, plugins

With network access, Nmap can scan real targets, Nikto scans your local Apache, and WPScan hits a live WordPress demo site.

---

## PART 1 – Setup Web Server (Target for Nmap + Nikto)

### Step 1 – Install and Start Apache
```bash
sudo apt update
sudo apt install apache2 -y
sudo systemctl start apache2
sudo systemctl enable apache2
```

### Step 2 – Verify Apache is Running
```bash
sudo systemctl status apache2
```
Expected: `Active: active (running)`

```bash
curl http://127.0.0.1
```
Expected: HTML output of Apache default page

Or open Firefox → `http://127.0.0.1` → see Apache default page

---

## PART 2 – Nmap Network Scanning

### Step 3 – Install Nmap
```bash
sudo apt install nmap -y
```

### Step 4 – Basic Port Scan (Local Machine)
```bash
nmap 127.0.0.1
```
Expected output:
```
PORT   STATE SERVICE
22/tcp open  ssh
80/tcp open  http
```
> Port 80 is open because Apache is running ✔

### Step 5 – Service Version Detection
```bash
nmap -sV 127.0.0.1
```
Expected:
```
PORT   STATE SERVICE VERSION
80/tcp open  http    Apache httpd 2.x.x (Ubuntu)
22/tcp open  ssh     OpenSSH 8.x
```
> `-sV` reveals exact software versions — useful to check for outdated/vulnerable versions

### Step 6 – Aggressive Scan (OS + Version + Scripts)
```bash
nmap -A 127.0.0.1
```
> Shows OS detection, traceroute, more service details

### Step 7 – Scan Specific Port Range
```bash
nmap -p 1-1000 127.0.0.1
```

### Step 8 – Scan a Real Public Target (With Network)
```bash
nmap scanme.nmap.org
```
> This is Nmap's official public test server — legal to scan, made for practice
> Shows real open ports on a live internet server

---

## PART 3 – Nikto Web Vulnerability Scanning

### Step 9 – Install Nikto
```bash
sudo apt install nikto -y
```

### Step 10 – Scan Local Apache Server
```bash
nikto -h http://127.0.0.1
```
Takes 1-2 minutes. Expected output:
```
+ Server: Apache/2.x.x (Ubuntu)
+ The anti-clickjacking X-Frame-Options header is not present
+ The X-Content-Type-Options header is not set
+ No CGI Directories found
+ Apache/2.x.x appears to be outdated
+ Allowed HTTP Methods: GET, POST, OPTIONS, HEAD
+ /icons/README: Apache default file found
+ /icons/: Directory indexing found
```

Each line starting with `+` is a finding — a potential vulnerability or misconfiguration.

### Step 11 – Save Nikto Output to File
```bash
nikto -h http://127.0.0.1 -o nikto_report.txt
cat nikto_report.txt
```

### Step 12 – Scan with More Detail
```bash
nikto -h http://127.0.0.1 -Tuning 1
```
> `-Tuning` lets you focus on specific vulnerability categories

---

## PART 4 – WPScan WordPress Vulnerability Scanning

### Step 13 – Install WPScan
```bash
sudo apt install wpscan -y
```
If not available via apt:
```bash
sudo apt install ruby ruby-dev -y
sudo gem install wpscan
```

### Step 14 – Basic WordPress Scan (Uses Internet)
```bash
wpscan --url https://demo.wp-api.org
```
Expected output:
```
[+] URL: https://demo.wp-api.org/
[+] WordPress version 6.x.x identified
[+] WordPress theme in use: twentytwentyone
[i] No plugins Found
```

### Step 15 – Enumerate Users
```bash
wpscan --url https://demo.wp-api.org --enumerate u
```
Expected:
```
[+] Enumerating Users
[i] User(s) Identified:
[+] admin
```

### Step 16 – Enumerate Plugins
```bash
wpscan --url https://demo.wp-api.org --enumerate p
```
Expected: Lists installed plugins and flags any with known CVEs

### Step 17 – Enumerate Themes
```bash
wpscan --url https://demo.wp-api.org --enumerate t
```

### Step 18 – Full Enumeration (All at Once)
```bash
wpscan --url https://demo.wp-api.org --enumerate u,p,t
```

---

## VAPT Phases (For Viva)
```
Phase 1: Information Gathering   → who/what is the target
Phase 2: Network Scanning        → Nmap — find open ports and services  
Phase 3: Vulnerability Scanning  → Nikto/WPScan — find weaknesses
Phase 4: Security Analysis       → understand what findings mean
```

---

## Quick Command Reference
```bash
# Nmap
nmap 127.0.0.1                      # basic scan
nmap -sV 127.0.0.1                  # version detection
nmap -A 127.0.0.1                   # aggressive scan
nmap -p 1-1000 127.0.0.1            # port range
nmap scanme.nmap.org                # public test target

# Nikto
nikto -h http://127.0.0.1           # scan web server
nikto -h http://127.0.0.1 -o out.txt  # save output

# WPScan
wpscan --url https://demo.wp-api.org              # basic
wpscan --url https://demo.wp-api.org --enumerate u  # users
wpscan --url https://demo.wp-api.org --enumerate p  # plugins
wpscan --url https://demo.wp-api.org --enumerate t  # themes
wpscan --url https://demo.wp-api.org --enumerate u,p,t  # all
```

---

## Key Terms (For Viva)
| Term | Meaning |
|------|---------|
| VAPT | Vulnerability Assessment and Penetration Testing |
| Nmap | Network Mapper — discovers open ports and services |
| Nikto | Web server vulnerability scanner |
| WPScan | WordPress-specific vulnerability scanner |
| Open Port | A service actively accepting connections |
| CVE | Common Vulnerabilities and Exposures — global vulnerability ID |
| Enumeration | Gathering information about users, plugins, services |
| Apache | Popular open-source web server |
| Fingerprinting | Identifying software type and version on a target |

## Observation Table (Write in Record)
| Tool | Command | Result |
|------|---------|--------|
| Apache | `systemctl start apache2` | Web server running on port 80 |
| Nmap basic | `nmap 127.0.0.1` | Port 80, 22 open |
| Nmap version | `nmap -sV 127.0.0.1` | Apache version identified |
| Nikto | `nikto -h http://127.0.0.1` | Multiple vulnerabilities found |
| WPScan basic | `wpscan --url demo.wp-api.org` | WordPress version found |
| WPScan users | `--enumerate u` | Admin user identified |
| WPScan plugins | `--enumerate p` | Plugin list with CVEs |
