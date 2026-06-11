# Experiment 9 – Firewall Configuration using UFW/GUFW
**Title:** Firewall Configuration and Traffic Control Using UFW in Linux

---

## What This Experiment Is About
You will configure a host-based firewall on Linux using UFW (Uncomplicated Firewall) and demonstrate:
- How to allow and block specific ports
- How blocking a port stops a client from connecting
- How allowing it again restores communication

The scenario is the Online Exam System:
- Ubuntu machine = Exam Server (running on port 5000)
- Keycloak = Authentication server (port 8080)
- Firewall protects both from unauthorized access

---

## Setup Options
**Option A – Two machines (ideal):** Ubuntu as server, Kali as client
**Option B – One machine (Killercoda/lab):** Simulate both on same machine using localhost

---

## PART 1 – Install and Enable UFW

### Step 1 – Install UFW
```bash
sudo apt update
sudo apt install ufw -y
```

### Step 2 – Install GUFW (GUI version — optional)
```bash
sudo apt install gufw -y
gufw    # opens graphical interface
```

### Step 3 – Enable the Firewall
```bash
sudo ufw enable
```
Expected:
```
Firewall is active and enabled on system startup
```

### Step 4 – Check Current Status
```bash
sudo ufw status verbose
```
Expected output:
```
Status: active
Logging: on (low)
Default: deny (incoming), allow (outgoing), disabled (routed)
```
> By default UFW **blocks all incoming** and **allows all outgoing**

---

## PART 2 – Configure Rules

### Step 5 – Set Default Policies (Explicitly)
```bash
sudo ufw default deny incoming
sudo ufw default allow outgoing
```

### Step 6 – Allow SSH First (Important — don't lock yourself out)
```bash
sudo ufw allow 22
```

### Step 7 – Allow Required Ports for Exam System
```bash
# Keycloak authentication server
sudo ufw allow 8080

# Exam server socket
sudo ufw allow 5000

# Web server (Apache)
sudo ufw allow 80
```

### Step 8 – View All Active Rules
```bash
sudo ufw status numbered
```
Expected:
```
     To                         Action      From
--   --                         ------      ----
[ 1] 22                         ALLOW IN    Anywhere
[ 2] 8080                       ALLOW IN    Anywhere
[ 3] 5000                       ALLOW IN    Anywhere
[ 4] 80                         ALLOW IN    Anywhere
```

---

## PART 3 – Demonstrate Allow vs Block

### Step 9 – Start a Simple Server on Port 5000
```bash
# Terminal 1 — start server
python3 -m http.server 5000
```

### Step 10 – Test Connection (Port is Allowed)
```bash
# Terminal 2 — connect as client
curl http://localhost:5000
```
Expected: HTML content received ✔

If testing from a second machine (Kali client):
```bash
curl http://<server-ip>:5000
```

### Step 11 – Block Port 5000
```bash
sudo ufw deny 5000
sudo ufw status numbered
```
> You'll see port 5000 now shows DENY

### Step 12 – Test Again (Should Fail Now)
```bash
curl http://localhost:5000
```
Expected:
```
curl: (7) Failed to connect to localhost port 5000: Connection refused
```
✔ Firewall successfully blocking traffic

### Step 13 – Allow Port Again
```bash
sudo ufw allow 5000
```

### Step 14 – Test Again (Should Work)
```bash
curl http://localhost:5000
```
Expected: HTML content received again ✔

---

## PART 4 – Additional Useful Rules

```bash
# Allow specific IP only
sudo ufw allow from 192.168.1.100

# Block a specific IP
sudo ufw deny from 192.168.1.200

# Allow specific IP on specific port only
sudo ufw allow from 192.168.1.100 to any port 5000

# Delete a rule by number
sudo ufw status numbered   # get the number first
sudo ufw delete 3          # delete rule number 3

# Disable firewall
sudo ufw disable

# Reset all rules (fresh start)
sudo ufw reset
```

---

## Quick Command Reference
```bash
sudo ufw enable                    # turn on firewall
sudo ufw disable                   # turn off firewall
sudo ufw status verbose            # full status
sudo ufw status numbered           # rules with numbers
sudo ufw default deny incoming     # block all by default
sudo ufw default allow outgoing    # allow outbound
sudo ufw allow 5000                # open port
sudo ufw deny 5000                 # block port
sudo ufw delete 3                  # delete rule #3
sudo ufw allow from <ip>           # allow specific IP
sudo ufw reset                     # wipe all rules
```

---

## Key Terms (For Viva)
| Term | Meaning |
|------|---------|
| UFW | Uncomplicated Firewall — frontend for Linux iptables |
| GUFW | Graphical interface for UFW |
| iptables | Low-level Linux firewall that UFW manages underneath |
| Default deny | Block all incoming traffic unless explicitly allowed |
| Port filtering | Allow/block traffic based on port number |
| Host-based firewall | Runs on the machine itself (vs network firewall on router) |
| Inbound | Traffic coming INTO your machine |
| Outbound | Traffic going OUT of your machine |
| Stateful inspection | Firewall tracks connection state not just ports |

## Observation Table (Write in Record)
| Action | Command | Result |
|--------|---------|--------|
| Enable firewall | `ufw enable` | Firewall active |
| Default deny incoming | `ufw default deny incoming` | All inbound blocked |
| Allow port 5000 | `ufw allow 5000` | Client connects ✔ |
| Deny port 5000 | `ufw deny 5000` | Connection refused ✔ |
| Allow port 5000 again | `ufw allow 5000` | Communication restored ✔ |
