# Experiment 7 – SSL/TLS (TLS 1.3) with Wireshark
**Title:** Demonstration of SSL/TLS Protocol Using Wireshark in Kali Linux

---

## Mental Model
- **HTTP** = postcards (anyone can read)
- **HTTPS** = sealed envelope (encrypted)
- **TLS Handshake** = the process of sealing the envelope before sending
- **Wireshark** = network spy tool — we use it to *watch* the handshake happen

---

## What You'll Observe (Expected Output Summary)
1. TCP 3-way handshake (SYN, SYN-ACK, ACK)
2. TLS Client Hello → browser says "I support these ciphers"
3. TLS Server Hello → server picks a cipher
4. Certificate exchange → server proves its identity
5. Encrypted Application Data → actual webpage, but unreadable
6. HTTP comparison → plain text, fully readable

---

## PART 1 – Setup Wireshark

### On TryHackMe (browser Kali)
- Machine already has Wireshark installed
- Open terminal and type:
```bash
wireshark
```
- If GUI doesn't open try:
```bash
sudo wireshark
```

### On your own Kali/Ubuntu
```bash
sudo apt install wireshark -y
sudo wireshark
```

---

## PART 2 – Capture TLS Traffic (Step by Step)

### Step 1 – Start Wireshark
1. Open Wireshark
2. Select network interface → **eth0** or **wlan0**
3. Click the **blue shark fin** (Start Capture)

### Step 2 – Apply Filter
In the filter bar at top, type:
```
tls
```
or
```
tcp.port == 443
```

### Step 3 – Visit a Secure Website
Open Firefox/browser and go to:
```
https://www.google.com
```
Wait for page to fully load.

### Step 4 – Stop Capture
Click the **red square** (Stop Capture) in Wireshark.

---

## PART 3 – Analyze Packets (What to Look For)

### 1. TCP Handshake
Change filter to:
```
tcp
```
Look for:
| Packet | Info |
|--------|------|
| SYN | Client → Server (I want to connect) |
| SYN-ACK | Server → Client (OK, connect) |
| ACK | Client → Server (Connected!) |

### 2. Client Hello
```
tls.handshake.type == 1
```
Click the packet → expand **Transport Layer Security → Client Hello**

Look for:
- Supported TLS versions
- List of cipher suites
- Extensions (SNI = website name)

### 3. Server Hello
```
tls.handshake.type == 2
```
Look for:
- **TLS 1.3** selected
- Chosen cipher suite (e.g. `TLS_AES_128_GCM_SHA256`)

### 4. Certificate
```
tls.handshake.certificate
```
Expand certificate → Look for:
- Issuer (who signed it — e.g. Google Trust Services)
- Subject (who it belongs to — e.g. *.google.com)
- Public Key
- Signature Algorithm

### 5. Encrypted Application Data
```
tls.record.content_type == 23
```
You'll see packets labeled **Application Data** — payload is gibberish (encrypted).

---

## PART 4 – HTTP vs HTTPS Comparison

Visit a plain HTTP site:
```
http://neverssl.com
```

Apply filter:
```
http
```

You'll see:
- Plain GET request
- Full headers visible
- Webpage content readable in Wireshark

### Comparison Table
| Feature | HTTP | HTTPS |
|---------|------|-------|
| Port | 80 | 443 |
| Data in Wireshark | Fully readable | Encrypted gibberish |
| Security | None | TLS encrypted |
| Certificate | No | Yes |

---

## TLS 1.3 vs TLS 1.2 (For Viva)

| Feature | TLS 1.2 | TLS 1.3 |
|---------|---------|---------|
| Handshake rounds | Multiple (2-RTT) | Single (1-RTT) |
| Weak ciphers | Allowed | Removed |
| RSA key exchange | Allowed | Not allowed |
| Speed | Slower | Faster |
| Forward Secrecy | Optional | Always on |

---

## TLS Handshake Flow (For Viva)

```
Client                          Server
  |                               |
  |-------- Client Hello -------->|   (supported ciphers, TLS versions)
  |<------- Server Hello ---------|   (chosen cipher, TLS 1.3 selected)
  |<------- Certificate ----------|   (server proves identity)
  |<------- Key Exchange ---------|
  |-------- Finished ------------>|
  |                               |
  |===== Encrypted Data ==========|   (all communication now encrypted)
```

---

## Key Terms (For Viva)

| Term | Meaning |
|------|---------|
| TLS 1.3 | Latest secure version of Transport Layer Security |
| Client Hello | Browser lists what encryption it supports |
| Server Hello | Server picks the encryption method |
| Cipher Suite | Combination of algorithms used (e.g. AES_128_GCM_SHA256) |
| Certificate | Server's identity proof (X.509, signed by CA) |
| Forward Secrecy | Old sessions stay safe even if key is later stolen |
| SNI | Server Name Indication — tells server which site you want |
| Port 443 | Default port for HTTPS/TLS |

---

## Wireshark Filters Quick Reference

| What to see | Filter |
|-------------|--------|
| All TLS traffic | `tls` |
| HTTPS port | `tcp.port == 443` |
| Client Hello | `tls.handshake.type == 1` |
| Server Hello | `tls.handshake.type == 2` |
| Certificate | `tls.handshake.certificate` |
| Encrypted data | `tls.record.content_type == 23` |
| Plain HTTP | `http` |
| TCP handshake | `tcp` |
