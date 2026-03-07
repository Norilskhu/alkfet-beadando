# alkfet-beadando

MCP beállítása:
-Spring ai dedikált végponttal rendelkezik, amely SSE-t használ. Lokális configban a meghatározott url: http://localhost:8085/sse
-claude leetöltése
-claude desktop config.jsonbe a következő sorok hozzáadása:

{
"mcpServers": {
"spring-ai-server": {
"command": "npx",
"args": [
"mcp-remote",
"http://localhost:8085/sse"
]
}
},