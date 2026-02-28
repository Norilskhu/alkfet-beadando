# X.509 Certificate Store – Szerver indítás útmutató

## Architektúra

```
Frontend (Angular) – Port 4200
    ↓ (HTTP proxy)
    └─→ Backend API (alkfet-api-svc) – Port 8080
        ├─→ alkfet-db-svc – Port 8081
        └─→ alkfet-mcp-svc – Port 8082
```

## Backend szerverek indítása

### Option 1: Parancssorból (Maven)

**Terminal 1 – alkfet-api-svc (8080)**
```bash
cd alkfet-api-svc
./mvnw spring-boot:run
# vagy
mvn spring-boot:run
```

**Terminal 2 – alkfet-db-svc (8081)**
```bash
cd alkfet-db-svc
./mvnw spring-boot:run
# vagy
mvn spring-boot:run
```

**Terminal 3 – alkfet-mcp-svc (8082)**
```bash
cd alkfet-mcp-svc
./mvnw spring-boot:run
# vagy
mvn spring-boot:run
```

### Option 2: IDE-ből (IntelliJ IDEA)

1. Mindhárom `*Application.java` fájlt jobb klikk → **Run**
2. Vagy Run Configuration-ben Add new → Spring Boot, mindhárom modulhoz

## Frontend indítása

**Terminal 4 – alkfet-fe (4200)**
```bash
cd alkfet-fe
npm install  # első futtatásnál
npm start    # vagy: ng serve
```

Frontend automatikusan megnyílik: **http://localhost:4200**

## Logolás

Mindhárom backend modul **DEBUG** szinten logol:

- **zp.gde.hu*** (alkalmazás kódod) – DEBUG
- **org.springframework*** (Spring Framework) – DEBUG
- **org.springframework.web*** (Web/HTTP) – DEBUG
- **org.springframework.security*** (Security) – DEBUG
- **root** (egyéb) – INFO

Konfiguráció: `*-svc/src/main/resources/application.yaml`

### Log formátum
```
2026-02-28 15:46:06 - [modul] [szint] üzenet
```

## Proxy működése

- Frontend (localhost:4200) HTTP kéréseit, amelyek `/api/...`-vel kezdődnek,
  a `proxy.conf.json` szabályai alapján átirányítja az `alkfet-api-svc`-re (localhost:8080).

- Konfiguráció helye: `alkfet-fe/proxy.conf.json`
- A proxy **csak fejlesztéskor** aktív (`ng serve` módban).

## Port referencia

| Alkalmazás | Port | URL |
|---|---|---|
| Frontend (Angular) | 4200 | http://localhost:4200 |
| alkfet-api-svc | 8080 | http://localhost:8080 |
| alkfet-db-svc | 8081 | http://localhost:8081 |
| alkfet-mcp-svc | 8082 | http://localhost:8082 |

## Ellenőrzés

### Backend fut-e?
```bash
curl -i http://localhost:8080/api/v1/root-certificates?page=0&size=10
curl -i http://localhost:8081/api/v1/...
curl -i http://localhost:8082/api/v1/...
```

### Frontend proxy működik?
- Nyisd meg a browser Developer Tools-ot (F12)
- Network tab → bármely `/api/...` kérés válasza `http://localhost:8080`-ról érkezik ✓

## Production build (proxy nélkül)

Production-ban a frontend static fájlok, amit le kell servírozni egy backend-ből vagy 
webserverből (nginx, Apache, stb.). Az `/api` path-ok ebben az esetben a backend-hoz 
navigálnak természetesen.

```bash
cd alkfet-fe
npm run build
# Eredmény: dist/alkfet-fe/
```

## Lehetséges problémák

### CORS hiba
Ha CORS hiba lép fel, szükség lehet a backend-ben:
```yaml
# application.yaml (pl. alkfet-api-svc)
spring:
  web:
    cors:
      allowed-origins: "http://localhost:4200"
      allowed-methods: GET,POST,PUT,DELETE
      allowed-headers: "*"
      allow-credentials: true
```

### Port már foglalt
Ha `Address already in use` hiba:
```bash
# Lezárni a folyamatot
lsof -ti:8080 | xargs kill -9  # macOS/Linux
netstat -ano | findstr :8080 & taskkill /PID <PID> /F  # Windows
```

