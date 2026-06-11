# Experiment 8 – User Authentication using Kerberos
**Title:** Implement User Authentication using Kerberos in Linux

---

## Mental Model
- **Kerberos** = a trusted middleman that gives you a "ticket" to access services
- **KDC** = Key Distribution Center — the main Kerberos server (gives out tickets)
- **TGT** = Ticket Granting Ticket — proof that you logged in (like a wristband at an event)
- **Service Ticket** = specific access pass for one service (like a ride ticket)
- **Realm** = Kerberos domain name — always written in CAPS (e.g. EXAM.LOCAL)
- **Principal** = a user or service in Kerberos (e.g. student@EXAM.LOCAL)

---

## How Kerberos Works (For Viva)
```
1. User sends username to KDC
2. KDC sends back encrypted TGT (only user's password can decrypt it)
3. User decrypts TGT → proves they know the password
4. User sends TGT to KDC to request access to a service
5. KDC sends Service Ticket
6. User presents Service Ticket to the service → Access granted
```
> Password is NEVER sent over the network — that's the key security advantage

---

## Setup Overview
- **KDC Server** = Ubuntu/Kali machine running Kerberos server
- **Client** = another terminal or same machine testing authentication
- **Realm** = `EXAM.LOCAL`

---

## PART 1 – Install Kerberos Server (KDC)

### Step 1 – Install Packages
```bash
sudo apt update
sudo apt install krb5-kdc krb5-admin-server -y
```
> When prompted for realm, type: `EXAM.LOCAL`
> For KDC server hostname: `localhost`
> For admin server: `localhost`

### Step 2 – Create the Realm Database
```bash
sudo krb5_newrealm
```
> Set a master password when prompted (e.g. `master123`) — remember this

Expected:
```
Initializing database '/var/lib/krb5kdc/principal' for realm 'EXAM.LOCAL'
```

### Step 3 – Start Kerberos Services
```bash
sudo systemctl start krb5-kdc
sudo systemctl start krb5-admin-server
sudo systemctl enable krb5-kdc
sudo systemctl enable krb5-admin-server
```

### Step 4 – Verify Services Running
```bash
sudo systemctl status krb5-kdc
sudo systemctl status krb5-admin-server
```
Expected: `Active: active (running)`

---

## PART 2 – Create Users (Principals)

### Step 5 – Open Kerberos Admin Console
```bash
sudo kadmin.local
```
> You are now inside the kadmin prompt

### Step 6 – Create Principals (Users)
```
kadmin.local:  addprinc student1
```
> Set password: `student123`

```
kadmin.local:  addprinc faculty1
```
> Set password: `faculty123`

```
kadmin.local:  addprinc admin/admin
```
> Set password: `admin123`

### Step 7 – List All Principals
```
kadmin.local:  listprincs
```
Expected:
```
K/M@EXAM.LOCAL
admin/admin@EXAM.LOCAL
faculty1@EXAM.LOCAL
kadmin/admin@EXAM.LOCAL
student1@EXAM.LOCAL
```

### Step 8 – Exit Admin Console
```
kadmin.local:  exit
```

---

## PART 3 – Install Kerberos Client and Test

### Step 9 – Install Client Tools
```bash
sudo apt install krb5-user -y
```
> Same realm settings: `EXAM.LOCAL`, `localhost`, `localhost`

### Step 10 – Request a TGT (Login)
```bash
kinit student1
```
> Enter password: `student123`

### Step 11 – Verify Ticket Received
```bash
klist
```
Expected output:
```
Credentials cache: FILE:/tmp/krb5cc_0
        Principal: student1@EXAM.LOCAL

  Issued                Expires               Principal
Jun  3 12:00:00 2025  Jun  4 12:00:00 2025  krbtgt/EXAM.LOCAL@EXAM.LOCAL
```
> This is the TGT — proof of successful authentication ✔

### Step 12 – Destroy Ticket (Logout)
```bash
kdestroy
klist
```
Expected after kdestroy:
```
klist: No credentials cache found
```

---

## PART 4 – Test Wrong Password (Negative Test)
```bash
kinit wronguser
```
Expected:
```
kinit: Client not found in Kerberos database while getting initial credentials
```

```bash
kinit student1
# Enter wrong password
```
Expected:
```
kinit: Preauthentication failed while getting initial credentials
```

---

## Quick Command Reference

```bash
# Server side
sudo krb5_newrealm                    # create realm database
sudo kadmin.local                     # open admin console
sudo systemctl start krb5-kdc        # start KDC
sudo systemctl start krb5-admin-server

# Inside kadmin.local
addprinc student1                     # create user
listprincs                            # list all users
delprinc student1                     # delete user
cpw student1                          # change password
exit                                  # quit

# Client side
kinit student1                        # login / get TGT
klist                                 # show tickets
kdestroy                              # logout / destroy ticket
```

---

## Key Terms (For Viva)

| Term | Meaning |
|------|---------|
| Kerberos | Network authentication protocol using tickets |
| KDC | Key Distribution Center — issues all tickets |
| TGT | Ticket Granting Ticket — proves you logged in |
| Service Ticket | Ticket for accessing a specific service |
| Realm | Kerberos administrative domain (CAPS by convention) |
| Principal | A user or service identity in Kerberos |
| kinit | Command to login and get a TGT |
| klist | Command to view current tickets |
| kdestroy | Command to delete tickets (logout) |
| kadmin.local | Admin tool to manage Kerberos users |
| Mutual Authentication | Both client AND server verify each other |

---

## Kerberos vs Other Auth Tools (For Viva)

| Tool | Used For |
|------|---------|
| **Kerberos** | Ticket-based auth, used in Active Directory / Linux |
| NTLM | Windows challenge-response (older, less secure) |
| LDAP | Directory service — stores user data, not auth itself |
| RADIUS | Network access auth — WiFi, VPN logins |
| Keycloak | Modern OAuth2/OpenID IAM — web apps |

---

## Observation Table (Write in Record)

| Action | Command | Result |
|--------|---------|--------|
| Create realm | `krb5_newrealm` | Database initialized |
| Add user | `addprinc student1` | Principal created |
| List users | `listprincs` | All principals shown |
| Login | `kinit student1` | TGT issued |
| View ticket | `klist` | TGT details shown |
| Logout | `kdestroy` | Ticket destroyed |
| Wrong password | `kinit student1` (wrong pw) | Preauthentication failed |
