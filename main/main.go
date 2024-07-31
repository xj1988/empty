package empty
 
import (
    "net/http"
    "github.com/gin-gonic/gin"
)
 
// 初始化路由
func setupRouter() *gin.Engine {
	// 创建一个Gin引擎实例，并使用默认中间件
    r := gin.Default() 
 
    // 定义一个GET路由，当访问/时返回"Hello, World!"
    r.GET("/", func(c *gin.Context) {
        c.String(http.StatusOK, "Hello, World!")
    })
 
	// 返回初始化好的Gin引擎
    return r 
}
 
func main() {
	// 调用初始化路由的函数
    router := setupRouter() 
	// 启动服务，默认在0.0.0.0:8080监听
    router.Run() 
}
