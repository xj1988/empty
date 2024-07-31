package main
 
import (
	"fmt"
	"net/http"
)

// 定义一个处理函数来响应 HTTP 请求
func helloWorldHandler(w http.ResponseWriter, r *http.Request) {
	fmt.Fprintf(w, "Hello, World!")
}

func main() {
	// 注册处理函数
	http.HandleFunc("/", helloWorldHandler)

	// 启动 HTTP 服务器，监听 8000 端口
	fmt.Println("Starting server on :8000...")
	if err := http.ListenAndServe(":8000", nil); err != nil {
		fmt.Printf("Could not start server: %v\n", err)
	}
}
