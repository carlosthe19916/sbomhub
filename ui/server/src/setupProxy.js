import { createProxyMiddleware } from "http-proxy-middleware";

export default function (app) {
  app.use(
    "/api",
    createProxyMiddleware({
      target: process.env.SPOG_API_URL || "http://localhost:8083",
      changeOrigin: true,
      pathRewrite: {
        "^/api": "/api",
      },
      logLevel: process.env.DEBUG ? "debug" : "info",
    })
  );
}
