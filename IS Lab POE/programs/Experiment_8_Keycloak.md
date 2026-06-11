# Experiment 8 – User Authentication using Keycloak (Docker)
**Title:** Implement User Authentication using Keycloak for Online Examination System

---

## Mental Model
- **Docker** = lightweight container that runs Keycloak without installing it directly
- **Keycloak** = the authentication server (handles login, roles, tokens)
- **Realm** = a security zone → `OnlineExamRealm`
- **Client** = the app using Keycloak for login → `exam-app`
- **Roles** = Student / Faculty / Admin
- **JWT Token** = a ticket proving who you are and what role you have

---

## Flow
```
Install Docker → Pull Keycloak Image → Run Container
→ Open Admin Console → Create Realm → Create Client
→ Create Roles → Create Users → Test Login → View JWT Token
```

---

## PART 1 – Install Docker

### Step 1 – Update System
```bash
sudo apt update
sudo apt upgrade -y
```

### Step 2 – Install Docker
```bash
sudo apt install docker.io -y
sudo systemctl enable docker
sudo systemctl start docker
docker --version
```

### Step 3 – Allow Docker Without sudo (Optional)
```bash
sudo usermod -aG docker $USER
newgrp docker
```

---

## PART 2 – Run Keycloak

### Step 4 – Pull Keycloak Image
```bash
sudo docker pull quay.io/keycloak/keycloak:latest
```
> Takes a minute to download

### Step 5 – Run Keycloak Container
```bash
sudo docker run -d -p 8080:8080 \
  -e KEYCLOAK_ADMIN=admin \
  -e KEYCLOAK_ADMIN_PASSWORD=admin123 \
  quay.io/keycloak/keycloak:latest start-dev
```

### Step 6 – Verify Container Running
```bash
sudo docker ps
```
Expected:
```
CONTAINER ID   IMAGE              STATUS
xxxxxxxxxxxx   keycloak:latest    Up X minutes
```

### Step 7 – Wait for Keycloak to Start (~30 seconds) then Check
```bash
curl http://localhost:8080
```
> If you see HTML output → Keycloak is ready

---

## PART 3 – Configure Keycloak (Admin Console)

Open browser → `http://localhost:8080`
Login with:
- Username: `admin`
- Password: `admin123`

### Step 8 – Create Realm
1. Click dropdown top-left (shows "Keycloak")
2. Click **Create Realm**
3. Realm name: `OnlineExamRealm`
4. Click **Create**

### Step 9 – Create Client
1. Left menu → **Clients** → **Create Client**
2. Client ID: `exam-app`
3. Client Type: `OpenID Connect`
4. Root URL: `http://localhost:3000`
5. Click **Save**

### Step 10 – Create Roles
1. Left menu → **Realm Roles** → **Create Role**
2. Create these 3 roles:
   - `Student`
   - `Faculty`
   - `Admin`

### Step 11 – Create Users
**User 1:**
1. Left menu → **Users** → **Add User**
2. Username: `student1` → Click **Create**
3. **Credentials** tab → Set Password: `student123` → Toggle **Temporary OFF**
4. **Role Mapping** tab → Assign role: `Student`

**Repeat for:**
- Username: `faculty1` / Password: `faculty123` / Role: `Faculty`
- Username: `admin1` / Password: `admin123` / Role: `Admin`

---

## PART 4 – Test Authentication

### Step 12 – Test Login
Open browser:
```
http://localhost:8080/realms/OnlineExamRealm/account
```
Login with `student1` / `student123`

Expected: ✔ Login successful, account page loads

### Step 13 – View JWT Token
1. Press **F12** → Developer Tools
2. Go to **Application** → **Local Storage**
3. Copy the token value
4. Open `https://jwt.io` in new tab
5. Paste token in left box

You'll see decoded:
```json
{
  "preferred_username": "student1",
  "realm_access": {
    "roles": ["Student"]
  },
  "exp": 1234567890
}
```

---

## PART 5 – Stop Container (Cleanup)

```bash
sudo docker ps                    # get container ID
sudo docker stop <container_id>   # stop it
sudo docker rm <container_id>     # remove it
```

---

## Killercoda Note
> No browser on Killercoda — you can only do Parts 1 and 2 (Docker install + run)
> For Admin Console + JWT testing → use lab PC or TryHackMe AttackBox

---

## Quick Command Reference

```bash
sudo apt install docker.io -y
sudo systemctl start docker
sudo docker pull quay.io/keycloak/keycloak:latest
sudo docker run -d -p 8080:8080 \
  -e KEYCLOAK_ADMIN=admin \
  -e KEYCLOAK_ADMIN_PASSWORD=admin123 \
  quay.io/keycloak/keycloak:latest start-dev
sudo docker ps
sudo docker stop <id>
sudo docker rm <id>
```

---

## Key Terms (For Viva)

| Term | Meaning |
|------|---------|
| Docker | Runs apps in isolated containers |
| Keycloak | Open-source Identity and Access Management server |
| Realm | Security domain containing users, roles, clients |
| Client | App that uses Keycloak for login |
| JWT | JSON Web Token — proof of login with role info |
| SSO | Single Sign-On — login once, access many apps |
| OAuth 2.0 | Authorization protocol Keycloak uses |
| OpenID Connect | Identity layer on top of OAuth 2.0 |
| IAM | Identity and Access Management |
| Role-Based Access | Different permissions per role (Student/Faculty/Admin) |

---

## Observation Table (Write in Record)

| Action | Expected Result |
|--------|----------------|
| `docker ps` | Keycloak container running |
| Open `localhost:8080` | Keycloak admin console loads |
| Create realm | OnlineExamRealm created |
| Create roles | Student, Faculty, Admin created |
| Login as student1 | Login successful |
| JWT decoded at jwt.io | Shows username + role: Student |
| Wrong password login | Login failed |
