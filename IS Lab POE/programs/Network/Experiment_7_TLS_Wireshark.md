# Experiment 7 – SSL/TLS (TLS 1.3) with Wireshark
**Title:** Demonstration of SSL/TLS Protocol Using Wireshark in Kali Linux

---

## What This Experiment Is About
You will capture real HTTPS network traffic using Wireshark and observe:
- How a TLS handshake happens step by step
- What packets are exchanged between browser and server
- That HTTPS data is encrypted (unreadable) unlike HTTP

---

## Tools Needed
- Kali Linux (lab machine or TryHackMe AttackBox)
- Wireshark (pre-installed on Kali)
- Firefox browser
- Internet connection ← you have this tomorrow!

---

## PART 1 – Start Wireshark

### Open Wireshark
```bash
sudo wireshark
```
> Use `sudo` to avoid permission errors on capturing

### Select Network Interface
- Look for the interface with a **wavy activity line** next to it
- Usually `eth0` on lab machines, `ens5` on TryHackMe, `wlan0` on WiFi
- **Double click it** to start capturing immediately

---

## PART 2 – Capture TLS Traffic

### Step 1 – Apply Capture Filter (Optional, before capturing)
In the **"Enter a capture filter"** box at top, type:
```
port 443
```
This captures only HTTPS traffic from the start — less noise

### Step 2 – Start Capture
Click the **blue shark fin** button (top left) if not already capturing

### Step 3 – Open Firefox and Visit
```
https://www.google.com
```
Wait for the full page to load

### Step 4 – Stop Capture
Click the **red square** button in Wireshark

Now you have packets to analyze.

---

## PART 3 – Analyze Packets (Apply These Filters One by One)

### Filter 1 – See All TLS Traffic
```
tls
```
You'll see a list of TLS packets. This is the entire HTTPS session.

---

### Filter 2 – TCP Handshake (happens BEFORE TLS)
```
tcp
```
Look for the first 3 packets:
| Packet | Flags | Meaning |
|--------|-------|---------|
| 1 | SYN | Client → Server: "I want to connect" |
| 2 | SYN-ACK | Server → Client: "OK, connect" |
| 3 | ACK | Client → Server: "Connected" |

> TCP handshake must complete before TLS can start

---

### Filter 3 – Client Hello
```
tls.handshake.type == 1
```
Click the packet → expand **Transport Layer Security → Handshake Protocol → Client Hello**

What to look for and point out:
- **Version:** TLS 1.3 (or says "TLS 1.2" but extensions will show 1.3 support)
- **Cipher Suites:** list of encryption methods the browser supports
- **Extensions → server_name:** shows which website you're connecting to (SNI)

---

### Filter 4 – Server Hello
```
tls.handshake.type == 2
```
Click the packet → expand **Server Hello**

What to look for:
- **Version:** TLS 1.3 selected
- **Cipher Suite:** e.g. `TLS_AES_128_GCM_SHA256` — this is what was agreed upon

---

### Filter 5 – Certificate
```
tls.handshake.certificate
```
Click the packet → expand **Certificate → RDNSequence**

What to look for:
- **Issuer:** who signed this certificate (e.g. Google Trust Services)
- **Subject:** who this certificate belongs to (e.g. *.google.com)
- **Validity:** Not Before / Not After dates
- **Public Key:** the server's public key

> This is the same X.509 certificate format from Experiment 6!

---

### Filter 6 – Encrypted Application Data
```
tls.record.content_type == 23
```
Click any of these packets → look at the data section

What you'll see:
- Packets labeled **"Application Data"**
- The payload is **completely unreadable** — just random bytes
- This is the actual webpage content, fully encrypted

---

## PART 4 – HTTP vs HTTPS Comparison

### Visit a Plain HTTP Site
In Firefox, go to:
```
http://neverssl.com
```
> This site intentionally has no HTTPS — perfect for comparison

### Apply Filter
```
http
```

What you'll see now:
- **GET /** request — fully readable
- **HTTP/1.1 200 OK** response
- Actual HTML content of the webpage visible in Wireshark
- Headers like `Host:`, `User-Agent:` all visible in plain text

### Side by Side Comparison
| Feature | HTTP (neverssl.com) | HTTPS (google.com) |
|---------|--------------------|--------------------|
| Port | 80 | 443 |
| Data visible | Yes — full content | No — encrypted |
| Certificate | None | X.509 certificate |
| Handshake | TCP only | TCP + TLS |
| Security | None | Encrypted + Authenticated |

---

## PART 5 – tshark (Terminal Alternative if Wireshark GUI Fails)

```bash
sudo apt install tshark -y

# Capture TLS packets on eth0
sudo tshark -i eth0 -Y "tls" -c 50

# Show only Client Hello
sudo tshark -i eth0 -Y "tls.handshake.type == 1" -c 10

# Show cipher suite info
sudo tshark -i eth0 -Y "tls.handshake.type == 2" -V -c 5
```

---

## TLS 1.3 Handshake Flow (For Viva)
```
Client                              Server
  |                                   |
  |--------- Client Hello ----------->|  (supported ciphers, TLS versions)
  |<-------- Server Hello ------------|  (chosen cipher: TLS_AES_128_GCM_SHA256)
  |<-------- Certificate -------------|  (server proves its identity)
  |<-------- Key Exchange ------------|
  |--------- Finished --------------->|
  |                                   |
  |======= Encrypted Data ============|  (everything from here is encrypted)
```

## Key Terms (For Viva)
| Term | Meaning |
|------|---------|
| TLS 1.3 | Latest version of Transport Layer Security protocol |
| Client Hello | Browser lists supported ciphers and TLS versions |
| Server Hello | Server picks the cipher and confirms TLS version |
| Cipher Suite | Set of algorithms agreed upon (e.g. AES_128_GCM_SHA256) |
| Certificate | Server's X.509 identity proof signed by a CA |
| Forward Secrecy | Old sessions safe even if private key is later stolen |
| SNI | Server Name Indication — tells server which site you want |
| Port 443 | Default port for HTTPS/TLS traffic |
| Application Data | Encrypted payload — the actual webpage content |

## Wireshark Filter Quick Reference
| What to see | Filter |
|-------------|--------|
| All TLS | `tls` |
| HTTPS port | `tcp.port == 443` |
| Client Hello | `tls.handshake.type == 1` |
| Server Hello | `tls.handshake.type == 2` |
| Certificate | `tls.handshake.certificate` |
| Encrypted data | `tls.record.content_type == 23` |
| Plain HTTP | `http` |
| TCP handshake | `tcp` |
