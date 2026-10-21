# order-server — an MCP server in Java (Spring Boot + Spring AI)

A repo for  **"Connect Claude to Your Spring Boot App — MCP Server in Java (2026)"**.
It exposes three tools to any MCP client (e.g. Claude Desktop) over **stdio**, with **zero** REST controllers.

## Stack (verified June 2026)
- Java 21
- Spring Boot **3.5.15**
- Spring AI **1.1.7** (managed by `spring-ai-bom`)
- Dependency: `spring-ai-starter-mcp-server` (stdio)

## The three tools
`OrderTools` is a plain `@Component`. Each method is annotated with `@McpTool`; Spring AI scans it,
generates a JSON schema from the signature, and registers it at startup.

| Tool | Description |
|------|-------------|
| `getOrderStatus` | Get the live status of an order by its id |
| `searchOrders`   | Find orders by customer |
| `cancelOrder`    | Cancel an order by id |

> The MCP annotations live in `org.springframework.ai.mcp.annotation` (`@McpTool`, `@McpToolParam`).
> These are **not** the same as the older tool-calling `@Tool`/`@ToolParam` in `org.springframework.ai.tool.annotation`.

## Build
```bash
./mvnw clean package
# -> target/order-server-1.0.0.jar
```

## Connect to Claude Desktop
Edit `claude_desktop_config.json` (macOS: `~/Library/Application Support/Claude/claude_desktop_config.json`),
add the server, then fully restart Claude Desktop:
```json
{
  "mcpServers": {
    "order-server": {
      "command": "java",
      "args": ["-jar", "/absolute/path/to/order-server/target/order-server-1.0.0.jar"]
    }
  }
}
```
Ask Claude: *"What is the status of order 1052?"* — it will call `getOrderStatus("1042")` and answer with live data
(`Order 1042 (Mechanical Keyboard) is SHIPPED`).

Ask Claude: *"Who are our top VIP customers with at least 3 orders, and what items have they purchased so far?"* — it will call internal methods and answer with live data
(`Order 1042 (Mechanical Keyboard) is SHIPPED`).

### Why these properties (stdio gotcha)
With stdio, **stdout is the protocol channel**, so the Spring banner and console logs would corrupt the JSON-RPC
stream. `application.properties` disables the banner, disables the web server, and empties the console log pattern.

## Going to production (Streamable-HTTP)
Two changes — and **your tool code does not change**:
1. swap the starter `spring-ai-starter-mcp-server` → `spring-ai-starter-mcp-server-webmvc`
2. set `spring.ai.mcp.server.protocol=STREAMABLE` (and remove `web-application-type=none`)

Spring Boot then **auto-configures** an HTTP endpoint at **`/mcp`**. You still deploy the jar like any Spring Boot
service (jar → Docker → cloud) — there is no automatic deploy.

> ⚠️ This starter server has **no authentication**. Anyone who can reach it can call any tool, including
> `cancelOrder`. Securing it (Spring Security + OAuth2 + JWT) is the next video.
