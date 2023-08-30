import { createProxyMiddleware } from "http-proxy-middleware";

export default function (app) {
  app.use(
    "/api",
    createProxyMiddleware({
      target: process.env.SBOMHUB_API_URL || "http://0.0.0.0:9002",
      changeOrigin: true,
      pathRewrite: {
        "^/api": "",
      },
      logLevel: process.env.DEBUG ? "debug" : "info",
    })
  );
}
