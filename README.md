# GUIDE.md — használati útmutató

## Áttekintés

Ez egy tanúsítványkezelő alkalmazás, amely lehetővé teszi gyökér (root CA) és felhasználói tanúsítványok létrehozását, tárolását, listázását, letöltését és törlését.

### Architektúra

```
┌─────────────┐     ┌───────────────┐     ┌──────────────┐     ┌─────────┐
│  alkfet-fe  │────▶│ alkfet-api-svc│────▶│alkfet-db-svc │────▶│ MongoDB │
│  (Angular)  │     │  (Spring Boot)│     │ (Spring Boot)│     │  (7.0)  │
│  port: 80   │     │  port: 8080   │     │  port: 8081  │     │  27017  │
└─────────────┘     └───────────────┘     └──────────────┘     └─────────┘
                                                │
                                          ┌──────────────┐
                                          │alkfet-mcp-svc│
                                          │ (Spring AI)  │
                                          │  port: 8085  │
                                          └──────────────┘
                                                │
                                          Claude Code / Claude Desktop
                                          (MCP kliens, SSE-n keresztül)
```

| Komponens | Technológia | Port | Leírás |
|-----------|-------------|------|--------|
| **alkfet-fe** | Angular 21, Nginx | 80 | Frontend webalkalmazás |
| **alkfet-api-svc** | Spring Boot 3.5 | 8080 | REST API gateway |
| **alkfet-db-svc** | Spring Boot 3.5, MongoDB | 8081 | Adatbázis service + MCP server |
| **alkfet-mcp-svc** | Spring Boot 3.5, Spring AI | 8085 | MCP server (SSE) — AI tool-ok |
| **MongoDB** | MongoDB 7.0 | 27017 | Tanúsítványok tárolása |

---

## 1. Rendszer indítása (Kubernetes + ArgoCD)

### Előfeltételek

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) — **Kubernetes engedélyezve** (Settings → Kubernetes → Enable Kubernetes)
- [kubectl](https://kubernetes.io/docs/tasks/tools/)

### 1.1. Kubernetes klaszter ellenőrzése

```bash
kubectl cluster-info
kubectl get nodes
```

Ha a Kubernetes nem fut, indítsd el a Docker Desktop-ban.

### 1.2. ArgoCD telepítése és alkalmazás indítása

```bash
# ArgoCD telepítése
kubectl create namespace argocd
kubectl apply -n argocd --server-side -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml
kubectl wait --for=condition=Ready pod --all -n argocd --timeout=300s

# Alkalmazás regisztrálása — ez telepíti automatikusan az összes komponenst
kubectl apply -f k8s/argocd/application.yaml
```

### 1.3. ArgoCD UI megnyitása

Külön terminálban:
```bash
kubectl port-forward svc/argocd-server -n argocd 8443:443
```

Admin jelszó lekérése:
```bash
kubectl -n argocd get secret argocd-initial-admin-secret -o jsonpath="{.data.password}" | base64 -d && echo
```

Megnyitás böngészőben: **https://localhost:8443**
- Felhasználó: `admin`
- Jelszó: a fenti parancs kimenete

Az ArgoCD automatikusan telepíti az összes komponenst: MongoDB, alkfet-db-svc, alkfet-api-svc, alkfet-mcp-svc, alkfet-fe.

### 1.4. Podok ellenőrzése

```bash
kubectl get pods -n alkfet
```

Várd meg, amíg minden pod `Running` állapotba kerül.

---

## 2. Port-forwarding — alkalmazás elérése lokálisan

A Kubernetes klaszterben futó service-ek alapértelmezetten nem érhetők el közvetlenül. A `kubectl port-forward` parancsokkal teheted elérhetővé a lokális gépeden.

### 2.1. Frontend elérése

```bash
kubectl port-forward svc/alkfet-fe -n alkfet 4200:80
```

Megnyitás böngészőben: **http://localhost:4200**

### 2.2. Összes service elérése (külön-külön terminálban)

| Parancs | Lokális URL | Leírás |
|---------|-------------|--------|
| `kubectl port-forward svc/alkfet-fe -n alkfet 4200:80` | http://localhost:4200 | Frontend |
| `kubectl port-forward svc/alkfet-api-svc -n alkfet 8080:8080` | http://localhost:8080 | API |
| `kubectl port-forward svc/alkfet-mcp-svc -n alkfet 8085:8085` | http://localhost:8085 | MCP server |
| `kubectl port-forward svc/alkfet-db-svc -n alkfet 8081:8081` | http://localhost:8081 | DB service |
| `kubectl port-forward svc/argocd-server -n argocd 8443:443` | https://localhost:8443 | ArgoCD UI |

> **Fontos:** Minden `port-forward` parancs egy terminált foglal el, és amíg fut, addig él a kapcsolat. Ha `Ctrl+C`-vel leállítod, a hozzáférés megszűnik.

---

## 3. Frontend használata

A frontend a **http://localhost:4200** címen érhető el (port-forward szükséges).

### 3.1. Gyökér tanúsítványok kezelése

Navigáció: **Gyökér tanúsítványok** menüpont (vagy http://localhost:4200/root-certificates)

#### Új gyökér tanúsítvány létrehozása (self-signed)

1. Kattints az **„+ Új létrehozása"** gombra
2. Töltsd ki a mezőket:
    - **Common Name (CN)** * — pl. `My Root CA`
    - **Szervezet (O)** * — pl. `My Organization`
    - **Szervezeti egység (OU)** — pl. `IT Department`
    - **Ország (C)** * — 2 karakteres országkód, pl. `HU`
    - **Állam / Megye (ST)** — pl. `Budapest`
    - **Helység (L)** — pl. `Budapest`
    - **Érvényesség (napokban)** * — pl. `365`
3. Kattints a **„Létrehozás"** gombra

A `*`-gal jelölt mezők kötelezőek.

#### Tanúsítvány listázása

A főoldalon táblázatban jelennek meg a gyökér tanúsítványok:
- **Common Name** — a tanúsítvány neve
- **Szervezet** — a kiállító szervezet
- **Sorozatszám** — egyedi azonosító
- **Érvényes től / ig** — érvényességi időszak
- **Státusz** — `Érvényes` vagy `Lejárt`

Az oldal alján lapozó (pagination) található.

#### PEM letöltése

Kattints a **⬇ PEM** gombra a tanúsítvány sorában. A böngésző letölti a `.pem` fájlt.

#### Tanúsítvány törlése

Kattints a **🗑** gombra a tanúsítvány sorában.

---

### 3.2. Felhasználói tanúsítványok kezelése

Navigáció: **Felhasználói tanúsítványok** menüpont (vagy http://localhost:4200/user-certificates)

#### CSR aláírása és tárolása

1. Kattints a **„+ CSR aláírása"** gombra
2. Töltsd ki:
    - **Gyökér tanúsítvány** * — válaszd ki a legördülő listából, melyik root CA-val írod alá
    - **CSR fájl feltöltése** — `.csr` vagy `.pem` fájl feltöltése (opcionális, ha kézzel is beillesztheted)
    - **CSR tartalom (PEM)** * — a CSR PEM formátumban (fájl feltöltésekor automatikusan kitöltődik)
    - **Érvényesség (napokban)** * — pl. `90`
3. Kattints az **„✔ Aláírás és tárolás"** gombra

#### Tanúsítvány listázása

Táblázatban jelennek meg a felhasználói tanúsítványok:
- **Common Name** — a tanúsítvány neve
- **Gyökér tanúsítvány** — melyik root CA írta alá
- **Sorozatszám** — egyedi azonosító
- **Érvényes ig** — lejárati dátum
- **Státusz** — `ACTIVE`, `REVOKED` vagy `EXPIRED`

#### PEM letöltése

Kattints a **⬇ PEM** gombra.

#### Tanúsítvány törlése

Kattints a **🗑** gombra.

---

## 4. REST API

Az API service a **http://localhost:8080** címen érhető el (port-forward szükséges).

### 4.1. Gyökér tanúsítványok

| Művelet | Metódus | URL |
|---------|---------|-----|
| Listázás | `GET` | `/api/v1/root-certificates?page=0&size=10` |
| Lekérdezés ID alapján | `GET` | `/api/v1/root-certificates/{id}` |
| Létrehozás | `POST` | `/api/v1/root-certificates` |
| Törlés | `DELETE` | `/api/v1/root-certificates/{id}` |

### 4.2. Felhasználói tanúsítványok

| Művelet | Metódus | URL |
|---------|---------|-----|
| Listázás | `GET` | `/api/v1/user-certificates?page=0&size=10` |
| Szűrés root cert alapján | `GET` | `/api/v1/user-certificates?rootCertificateId={id}` |
| Lekérdezés ID alapján | `GET` | `/api/v1/user-certificates/{id}` |
| CSR aláírása | `POST` | `/api/v1/user-certificates` |
| Törlés | `DELETE` | `/api/v1/user-certificates/{id}` |

---

## 5. MCP szerver használata Claude Code-dal

Az **alkfet-mcp-svc** egy Spring AI MCP szerver, amely SSE (Server-Sent Events) protokollon keresztül érhető el. Ez lehetővé teszi, hogy AI kliensek (pl. Claude Code, Claude Desktop) természetes nyelven kérdezzenek az adatbázisban tárolt tanúsítványokról.

### 5.1. MCP szerver elérhetősége

Az MCP szerver SSE végpontja:
```
http://localhost:8085/sse
```

> **Fontos:** Ehhez futnia kell a port-forwardnak:
> ```bash
> kubectl port-forward svc/alkfet-mcp-svc -n alkfet 8085:8085
> ```

### 5.2. Elérhető MCP tool-ok

| Tool | Leírás |
|------|--------|
| `listRootCertificates` | Gyökér tanúsítványok listázása (lapozható) |
| `getRootCertificateById` | Egy gyökér tanúsítvány lekérdezése ID alapján |
| `countRootCertificates` | Gyökér tanúsítványok számának lekérdezése |
| `listUserCertificates` | Felhasználói tanúsítványok listázása (lapozható) |
| `listUserCertificatesByRootCert` | Felhasználói tanúsítványok listázása egy adott root cert alapján |
| `getUserCertificateById` | Egy felhasználói tanúsítvány lekérdezése ID alapján |
| `countUserCertificates` | Felhasználói tanúsítványok számának lekérdezése |

### 5.3. Claude Code konfigurálása

A Claude Code a projekted gyökerében lévő `.mcp.json` fájlból olvassa az MCP szerverek konfigurációját.

```json
{
  "mcpServers": {
    "alkfet-mcp-server": {
      "command": "npx",
      "args": [
        "mcp-remote",
        "http://localhost:8085/sse"
      ]
    }
  }
}
```

> **Előfeltétel:** Node.js telepítve kell legyen (az `npx` miatt), és az MCP service port-forwardjának futnia kell.

#### Használat Claude Code-ban

Miután a `.mcp.json` konfigurálva van és az MCP szerver elérhető (`port-forward` fut), a Claude Code automatikusan felismeri az MCP szervert. Természetes nyelven tehetsz fel kérdéseket:

**Példa kérdések:**
```
"Hány gyökér tanúsítvány van az adatbázisban?"
"Listázd az összes felhasználói tanúsítványt!"
```

A Claude Code az MCP tool-okon keresztül lekérdezi az adatbázist, és természetes nyelven válaszol.

### 5.4. Claude Desktop konfigurálása

A Claude Desktop a `claude_desktop_config.json` fájlból olvassa a konfigurációt.

Tartalom:
```json
{
  "mcpServers": {
    "alkfet-mcp-server": {
      "command": "npx",
      "args": [
        "mcp-remote",
        "http://localhost:8085/sse"
      ]
    }
  }
}
```

A Claude Desktop indítása után az MCP szerver tool-jai automatikusan elérhetők lesznek.

---

## 6. Lokális fejlesztői futtatás (Kubernetes nélkül)

Ha nem Kubernetes-ben, hanem közvetlenül a gépen szeretnéd futtatni az alkalmazást fejlesztéshez:

### 6.1. Backend service-ek indítása

Három külön terminálban:

```bash
# 1. DB service (embedded MongoDB-vel — nem kell külön MongoDB)
cd alkfet-db-svc
./mvnw spring-boot:run

# 2. MCP service
cd alkfet-mcp-svc
./mvnw spring-boot:run

# 3. API service
cd alkfet-api-svc
./mvnw spring-boot:run
```

### 6.2. Frontend indítása

```bash
cd alkfet-fe
npm install
npm start
```

A frontend elérhető: **http://localhost:4200**

> **Megjegyzés:** Lokális fejlesztésnél az Angular `proxy.conf.json` konfiguráció automatikusan továbbítja az `/api` kéréseket a `localhost:8080`-ra (API service-hez). Az `alkfet-db-svc` embedded MongoDB-t (Flapdoodle) használ, nem kell külön MongoDB-t telepíteni.

---
