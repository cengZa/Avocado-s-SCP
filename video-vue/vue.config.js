module.exports = {
  lintOnSave: false, // 关闭语法检测
  devServer: {
    // 代理服务器配置
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // 后端接口地址
        ws: true, // 是否启用websockets
        changeOrigin: true, // 是否修改请求头中的host为target的host
        pathRewrite: {
          '^/api': '' // 将/api开头的路径重写为空，即去掉/api
        }
      },
      '/dev': {
        target: 'http://localhost:5002', // 第二个后端接口地址
        changeOrigin: true, // 是否修改请求头中的host为target的host
         ws: true, // 是否启用websockets
        pathRewrite: {
          '^/dev': ''
        }
      }
    }
  }
};
