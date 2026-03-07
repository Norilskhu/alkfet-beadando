# alkfet-beadando

## CI/CD - Docker image build & push

A projekt GitHub Actions CI workflow-t használ, amely minden `main` branch-re történő push esetén automatikusan:
1. Buildeli mind a 4 service Docker image-ét (alkfet-api-svc, alkfet-db-svc, alkfet-mcp-svc, alkfet-fe)
2. Feltölti az image-eket a GitHub Container Registry-be (`ghcr.io`)

### Image-ek elérése
```
ghcr.io/<github-user>/alkfet-beadando/alkfet-api-svc:latest
ghcr.io/<github-user>/alkfet-beadando/alkfet-db-svc:latest
ghcr.io/<github-user>/alkfet-beadando/alkfet-mcp-svc:latest
ghcr.io/<github-user>/alkfet-beadando/alkfet-fe:latest
```

### Lokális Docker build
```bash
docker build -t alkfet-api-svc ./alkfet-api-svc
docker build -t alkfet-db-svc ./alkfet-db-svc
docker build -t alkfet-mcp-svc ./alkfet-mcp-svc
docker build -t alkfet-fe ./alkfet-fe
```

## MCP beállítása
- Spring AI dedikált végponttal rendelkezik, amely SSE-t használ. Lokális configban a meghatározott url: http://localhost:8085/sse
- Claude letöltése
- Claude Desktop config.json-be a következő sorok hozzáadása:

```json
{
  "mcpServers": {
    "spring-ai-server": {
      "command": "npx",
      "args": [
        "mcp-remote",
        "http://localhost:8085/sse"
      ]
    }
  }
}
```
