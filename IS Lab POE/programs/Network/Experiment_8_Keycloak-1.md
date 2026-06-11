# Experiment 8 – User Authentication using Keycloak (Docker)
**Title:** Implement User Authentication using Keycloak for Online Examination System

---

## What This Experiment Is About
You are setting up a real authentication server (Keycloak) using Docker and:
- Creating an Online Exam security zone (Realm)
- Registering the exam app as a client
- Creating roles: Student, Faculty, Admin
- Creating users and assigning roles
- Testing login and viewing the JWT token that proves who you are

---

## Entities
| Entity | Role |
|--------|------|
| Docker | Runs Keycloak as a container |
| Keycloak | Authentication server — handles all logins |
| Realm | Security zone → `OnlineExamRealm` |
| Client | The exam app → `exam-app` |
| JWT Token | Proof of login — contains username + role |

---

## PART 1 – Install Docker

### Step 1 – Update and Install
```bash
sudo apt update
sudo apt install docker.io -y
```

### Step 2 – Start Docker Service
```bash
sudo systemctl enable docker
sudo systemctl start docker
```

### Step 3 – Verify Docker Works
```bash
docker --version
sudo docker ps
```
Expected:
```
Docker version xx.x.x
CONTAINER ID   IMAGE   COMMAND   ...  (empty list is fine)
```

### Step 4 – Optional: Run Docker Without sudo
```bash
sudo usermod -aG docker $USER
newgrp docker
```

---

## PART 2 – Deploy Keycloak

### Step 5 – Pull Keycloak Image
```bash
sudo docker pull quay.io/keycloak/keycloak:latest
```
> With network access this downloads quickly — ~500MB

### Step 6 – Run Keycloak Container
```bash
sudo docker run -d -p 8080:8080 \
  -e KEYCLOAK_ADMIN=admin \
  -e KEYCLOAK_ADMIN_PASSWORD=admin123 \
  quay.io/keycloak/keycloak:latest start-dev
```
What each part means:
- `-d` = run in background
- `-p 8080:8080` = expose port 8080 to your browser
- `-e KEYCLOAK_ADMIN=admin` = admin username
- `-e KEYCLOAK_ADMIN_PASSWORD=admin123` = admin password
- `start-dev` = development mode (no HTTPS needed)

### Step 7 – Verify Container is Running
```bash
sudo docker ps
```
Expected:
```
CONTAINER ID   IMAGE              STATUS
xxxxxxxxxxxx   keycloak:latest    Up X minutes
```

### Step 8 – Wait ~30-60 Seconds Then Check
```bash
curl http://localhost:8080
```
> If you see HTML → Keycloak is ready
> If connection refused → wait 30 more seconds and try again

---

## PART 3 – Configure Keycloak (Admin Console)

Open Firefox → go to:
```
http://localhost:8080
```
Login with:
- Username: `admin`
- Password: `admin123`

---

### Step 9 – Create Realm
> A Realm is a security zone — like a separate login system for our exam app

1. Click the dropdown at top-left (currently shows **"Keycloak"**)
2. Click **"Create Realm"**
3. Realm name: `OnlineExamRealm`
4. Make sure **Enabled = ON**
5. Click **Create**

You are now inside **OnlineExamRealm** — all further steps are inside this realm.

---

### Step 10 – Create Client (Exam App)
> A Client is the application that will use Keycloak for login

1. Left menu → **Clients** → **Create Client**
2. Fill in:
   - Client ID: `exam-app`
   - Client Type: `OpenID Connect`
3. Click **Next**
4. Root URL: `http://localhost:3000`
5. Click **Save**

---

### Step 11 – Create Roles
> Roles define what each type of user can do

1. Left menu → **Realm Roles**
2. Click **Create Role**
3. Role name: `Student` → **Save**
4. Repeat → Create role: `Faculty` → **Save**
5. Repeat → Create role: `Admin` → **Save**

---

### Step 12 – Create Users and Assign Roles

#### Create Student User
1. Left menu → **Users** → **Add User**
2. Username: `student1`
3. Email: `student1@kit.edu`
4. Click **Create**
5. Go to **Credentials** tab
   - Click **Set Password**
   - Password: `student123`
   - **Temporary: OFF** (important — otherwise user must change password on first login)
   - Click **Save**
6. Go to **Role Mapping** tab
   - Click **Assign Role**
   - Select `Student` → **Assign**

#### Create Faculty User
Repeat above with:
- Username: `faculty1`, Password: `faculty123`, Role: `Faculty`

#### Create Admin User
Repeat above with:
- Username: `admin1`, Password: `admin123`, Role: `Admin`

---

## PART 4 – Test Authentication

### Step 13 – Test Login via Browser
Open new tab → go to:
```
http://localhost:8080/realms/OnlineExamRealm/account
```
- Login with `student1` / `student123`
- You should see the Keycloak account page with student1's profile

Expected: ✔ Login successful

---

### Step 14 – Get and Decode JWT Token

#### Get the Token via Terminal
```bash
curl -s -X POST \
  "http://localhost:8080/realms/OnlineExamRealm/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=exam-app" \
  -d "username=student1" \
  -d "password=student123" \
  -d "grant_type=password" | python3 -m json.tool
```
> This directly gives you the JWT token in terminal — no browser dev tools needed!

You'll see:
```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5...",
  "token_type": "Bearer",
  "expires_in": 300
}
```

#### Decode the Token
Copy the `access_token` value → open browser → go to:
```
https://jwt.io
```
Paste token in left box → right side shows decoded:
```json
{
  "preferred_username": "student1",
  "realm_access": {
    "roles": ["Student"]
  },
  "exp": 1234567890,
  "iss": "http://localhost:8080/realms/OnlineExamRealm"
}
```

---

## PART 5 – Cleanup

```bash
# Get container ID
sudo docker ps

# Stop Keycloak
sudo docker stop <container_id>

# Remove container
sudo docker rm <container_id>
```

---

## Quick Command Reference
```bash
# Install and start Docker
sudo apt install docker.io -y
sudo systemctl start docker

# Run Keycloak
sudo docker run -d -p 8080:8080 \
  -e KEYCLOAK_ADMIN=admin \
  -e KEYCLOAK_ADMIN_PASSWORD=admin123 \
  quay.io/keycloak/keycloak:latest start-dev

# Check it's running
sudo docker ps
curl http://localhost:8080

# Get JWT token via terminal
curl -s -X POST \
  "http://localhost:8080/realms/OnlineExamRealm/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=exam-app" \
  -d "username=student1" \
  -d "password=student123" \
  -d "grant_type=password" | python3 -m json.tool

# Stop and remove
sudo docker stop <id>
sudo docker rm <id>
```

---

## Key Terms (For Viva)
| Term | Meaning |
|------|---------|
| Docker | Runs apps in isolated containers without full VM |
| Keycloak | Open-source Identity and Access Management (IAM) server |
| Realm | Security domain — contains all users, roles, clients |
| Client | Application registered to use Keycloak for login |
| JWT | JSON Web Token — encrypted proof of login with role info |
| SSO | Single Sign-On — login once, access multiple apps |
| OAuth 2.0 | Authorization framework Keycloak is built on |
| OpenID Connect | Identity layer on top of OAuth 2.0 for authentication |
| Role-Based Access | Permissions based on role (Student/Faculty/Admin) |
| IAM | Identity and Access Management |

## Observation Table
| Action | Expected Result |
|--------|----------------|
| `docker ps` | Keycloak container running |
| Open `localhost:8080` | Admin console loads |
| Create realm | OnlineExamRealm created |
| Create roles | Student, Faculty, Admin visible |
| Login as student1 | Login successful |
| JWT token decoded | Shows username + role: Student |
| Login wrong password | Authentication failed |
