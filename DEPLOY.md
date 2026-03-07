# Telepítési útmutató — alkfet-beadando

## Előfeltételek

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) — Kubernetes engedélyezve
- [kubectl](https://kubernetes.io/docs/tasks/tools/)
- [Helm](https://helm.sh/docs/intro/install/) (opcionális, MongoDB Helm charthoz)
- [ArgoCD CLI](https://argo-cd.readthedocs.io/en/stable/cli_installation/) (opcionális)

---

## 1. Helyi Kubernetes klaszter elindítása

### Docker Desktop (macOS)
1. Docker Desktop → Settings → Kubernetes → **Enable Kubernetes** → Apply & Restart
2. Ellenőrzés:
```bash
kubectl cluster-info
kubectl get nodes
```

---

## 2. ArgoCD manuális telepítése

```bash
# ArgoCD namespace létrehozása és telepítése
kubectl create namespace argocd

# --server-side flag szükséges, mert a CRD-k túl nagyok a kliens oldali apply limitjéhez (262144 bytes)
kubectl apply -n argocd --server-side -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml

# Várjuk meg, hogy minden pod elindul
kubectl wait --for=condition=Ready pod --all -n argocd --timeout=300s

# ArgoCD UI elérése port-forwardinggal
kubectl port-forward svc/argocd-server -n argocd 8443:443
```

Az ArgoCD UI elérhető: **https://localhost:8443**

### Admin jelszó lekérése:
```bash
kubectl -n argocd get secret argocd-initial-admin-secret \
  -o jsonpath="{.data.password}" | base64 -d && echo
```

Bejelentkezés: `admin` / (fenti jelszó)

---

## 3. Alkalmazás telepítése ArgoCD-vel

### Az ArgoCD Application regisztrálása:
```bash
kubectl apply -f k8s/argocd/application.yaml
```

Ez utasítja az ArgoCD-t, hogy:
- Figyeli a `https://github.com/Norilskhu/alkfet-beadando.git` repót
- A `main` branch `k8s/` mappáját szinkronizálja
- Az `alkfet` namespace-be telepíti automatikusan az összes komponenst:
  - **MongoDB** (`k8s/mongodb.yaml`)
  - **alkfet-db-svc** (`k8s/alkfet-db-svc.yaml`) — `k8s` Spring profillal, külső MongoDB-re csatlakozik
  - **alkfet-api-svc** (`k8s/alkfet-api-svc.yaml`)
  - **alkfet-mcp-svc** (`k8s/alkfet-mcp-svc.yaml`)
  - **alkfet-fe** (`k8s/alkfet-fe.yaml`) — Nginx-szel kiszolgált Angular frontend

> **Megjegyzés:** A GitHub Container Registry (ghcr.io) package-ek **publikusak**, nem kell image pull secret.

### Szinkronizáció ellenőrzése:
```bash
# ArgoCD CLI-vel (ha telepítve van)
argocd login localhost:8443 --insecure --username admin
argocd app get alkfet-beadando
argocd app sync alkfet-beadando

# kubectl-lel
kubectl get all -n alkfet
```

---

## 4. Alkalmazás elérése

### Port-forwarding:
```bash
# Frontend
kubectl port-forward svc/alkfet-fe -n alkfet 4200:80

# API service
kubectl port-forward svc/alkfet-api-svc -n alkfet 8080:8080

# MCP service
kubectl port-forward svc/alkfet-mcp-svc -n alkfet 8085:8085

# DB service
kubectl port-forward svc/alkfet-db-svc -n alkfet 8081:8081
```

### Ingress (ha nginx ingress controller telepítve van):
```bash
# Nginx Ingress Controller telepítése
helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
helm install ingress-nginx ingress-nginx/ingress-nginx --namespace ingress-nginx --create-namespace

# /etc/hosts fájlba hozzáadni:
echo "127.0.0.1 alkfet.local" | sudo tee -a /etc/hosts

# Majd elérhető:
# http://alkfet.local       → Frontend
# http://alkfet.local/api   → API
```

---

## 5. CD workflow — hogyan működik

```
git push → GitHub Actions (CI)
              ↓
         Docker image build
              ↓
         ghcr.io push (:latest tag)
              ↓
         ArgoCD (CD) — figyeli a k8s/ mappát
              ↓
         Kubernetes deployment frissítése automatikusan
```

Az ArgoCD az `automated` syncPolicy miatt automatikusan:
- **prune: true** — eltávolítja a repo-ból törölt erőforrásokat
- **selfHeal: true** — visszaállítja a manuálisan módosított erőforrásokat

---

## 6. Image frissítés kényszerítése

Ha az ArgoCD nem veszi észre az új `:latest` image-et (mert a tag nem változott), kényszerítsd a rolloutot:

```bash
kubectl rollout restart deployment/alkfet-api-svc -n alkfet
kubectl rollout restart deployment/alkfet-db-svc -n alkfet
kubectl rollout restart deployment/alkfet-mcp-svc -n alkfet
kubectl rollout restart deployment/alkfet-fe -n alkfet
```

> **Tipp:** Éles környezetben `latest` helyett commit SHA tag-et használj (pl. `:abc1234`), és az ArgoCD Image Updater eszközzel automatizáld a frissítéseket.

---

## Architektúra megjegyzések

- **alkfet-db-svc**: Lokálisan embedded MongoDB-t (Flapdoodle) használ. Kubernetes-ben a `k8s` Spring profil aktiválódik (`SPRING_PROFILES_ACTIVE=k8s`), ami kikapcsolja az embedded MongoDB-t és a klaszteren belüli `mongodb` Service-hez csatlakozik.
- **Health check-ek**: A Kubernetes liveness/readiness probe-ok `tcpSocket`-et használnak (nincs Spring Actuator dependency).
- **MongoDB**: A `k8s/mongodb.yaml`-ban definiált `mongo:7.0` image `emptyDir` volume-mal fut (nem perzisztens — pod újraindítás esetén az adatok elvesznek). Éles környezetben PersistentVolumeClaim vagy Helm chart (`bitnami/mongodb`) ajánlott.

---

## Fájlstruktúra

```
k8s/
├── namespace.yaml          # alkfet namespace
├── mongodb.yaml            # MongoDB Deployment + Service
├── alkfet-db-svc.yaml      # DB service Deployment + Service (k8s profil)
├── alkfet-mcp-svc.yaml     # MCP service Deployment + Service
├── alkfet-api-svc.yaml     # API service Deployment + Service
├── alkfet-fe.yaml          # Frontend Deployment + Service + Ingress
└── argocd/
    └── application.yaml    # ArgoCD Application definíció
```
