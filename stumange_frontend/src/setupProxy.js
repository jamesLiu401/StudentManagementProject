const { createProxyMiddleware } = require('http-proxy-middleware');

module.exports = function(app) {
  app.use(
    '/api',
    createProxyMiddleware({
      target: 'http://localhost:8080',
      changeOrigin: true,
      secure: false,
      logLevel: 'debug',
      pathRewrite: {
        '^/api': '', // 移除 /api 前缀
      },
      // 添加CORS头
      onProxyRes: function (proxyRes, req, res) {
        // 添加CORS头
        proxyRes.headers['Access-Control-Allow-Origin'] = '*';
        proxyRes.headers['Access-Control-Allow-Methods'] = 'GET,PUT,POST,DELETE,OPTIONS';
        proxyRes.headers['Access-Control-Allow-Headers'] = 'Content-Type, Authorization, Content-Length, X-Requested-With';
        
        console.log('代理响应:', proxyRes.statusCode, req.url);
        
        // 记录403错误
        if (proxyRes.statusCode === 403) {
          console.error('收到403错误:', req.url, req.method);
        }
      },
      onProxyReq: function (proxyReq, req, res) {
        console.log('代理请求:', req.method, req.url);
        
        // 确保请求头正确设置
        proxyReq.setHeader('Content-Type', 'application/json');
        proxyReq.setHeader('Accept', 'application/json');
      },
      onError: function (err, req, res) {
        console.error('代理错误:', err.message);
        console.error('请求URL:', req.url);
        console.error('请求方法:', req.method);
        
        // 返回更详细的错误信息
        res.status(500).json({
          error: '代理服务器错误',
          message: err.message,
          url: req.url
        });
      }
    })
  );
};
