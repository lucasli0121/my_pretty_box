/*
 * @Author: liguoqiang
 * @Date: 2022-06-02 17:04:32
 * @LastEditors: liguoqiang
 * @LastEditTime: 2023-09-26 14:04:04
 * @Description:
 */
package api

import (
	"agent/cfg"
	"agent/exception"
	mylog "agent/log"
	"agent/mdb/common"
	"context"
	"net/http"
	"os"
	"os/signal"
	"path/filepath"
	"strconv"
	"time"

	_ "agent/docs"

	"github.com/didip/tollbooth"
	"github.com/didip/tollbooth_gin"
	"github.com/gin-gonic/gin"
	swaggerFiles "github.com/swaggo/files"
	ginSwagger "github.com/swaggo/gin-swagger"
)

var postAction map[string]gin.HandlerFunc
var getAction map[string]gin.HandlerFunc
var svcHttp *http.Server = nil

// StartWeb function run a webservice at webPort
func StartWeb() {
	// 设置限流
	limt := tollbooth.NewLimiter(500, nil)
	limt.SetIPLookups([]string{"RemoteAddr", "X-Forwarded-For", "X-Real-IP"}).SetMethods([]string{"GET", "POST"})
	limt.SetMessage("{ \"code\": 201, \"message\": \"reached max request limit\"}")
	router := gin.Default()
	v1 := router.Group("/v1")
	router.Static("/public", cfg.This.StaticPath)
	initActions()
	for k, v := range getAction {
		v1.GET(k, tollbooth_gin.LimitHandler(limt), v)
	}
	for k, v := range postAction {
		v1.POST(k, tollbooth_gin.LimitHandler(limt), v)
	}
	router.MaxMultipartMemory = 8 << 40
	router.GET("/swagger/*any", ginSwagger.WrapHandler(swaggerFiles.Handler))
	// 单独启动http server，用于后面的关闭操作
	svcHttp = &http.Server{
		Addr:         cfg.This.Svr.Host,
		Handler:      router,
		ReadTimeout:  500 * time.Second,
		WriteTimeout: 500 * time.Second,
	}
	// 启动一个go例程用于启动服务
	go func() {
		err := svcHttp.ListenAndServe()
		if err != nil {
			mylog.Log.Errorln("start web server failed", cfg.This.Svr.Host)
		}
	}()
	quit := make(chan os.Signal, 1)
	signal.Notify(quit, os.Interrupt, os.Kill)
	<-quit

	ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
	defer cancel()
	svcHttp.Shutdown(ctx)
	<-ctx.Done()
	mylog.Log.Infoln("Shutdowning is done!!")
}

func initActions() {
	postAction = make(map[string]gin.HandlerFunc)
	postAction["/device/reportHeartData"] = reportHeartRateData
	postAction["/device/reportOtaInfo"] = reportOtaInfo
	postAction["/device/postHeart"] = postHeart
	postAction["/device/postEvent"] = postEvent
	postAction["/device/getServerTime"] = getServerTime
	postAction["/device/getED713Ota"] = getED713Ota
	postAction["/ed719/heartBeat"] = ed719HeartBeat
	postAction["/ed719/alarm"] = ed719Alarm
	postAction["/ed719/getPushSwitch"] = ed719GetPushSwitch
	postAction["/ed719/pushStatus"] = ed719PushStatus

	postAction["/upload/ota"] = uploadOtaFileFun

	getAction = make(map[string]gin.HandlerFunc)
	getAction["/device/getServerTime"] = getServerTime
}

func getPageDaoFromGin(c *gin.Context) *common.PageDao {
	pageNo := c.Query("pageNo")
	pageSize := c.Query("pageSize")
	var page *common.PageDao = nil
	if pageNo != "" {
		no, err1 := strconv.ParseInt(pageNo, 10, 64)
		size, err2 := strconv.ParseInt(pageSize, 10, 64)
		if err1 == nil && err2 == nil {
			page = common.NewPageDao(no, size)
		}
	}
	return page
}

func respInt(c *gin.Context, status int) {
	result, err := c.Writer.WriteString(strconv.Itoa(status))
	if err != nil {
		mylog.Log.Errorln("write string error:", err)
	} else {
		mylog.Log.Debugln("write response string result:", result)
	}
}
func respInt64(c *gin.Context, status int64) {
	result, err := c.Writer.WriteString(strconv.FormatInt(status, 10))
	if err != nil {
		mylog.Log.Errorln("write string error:", err)
	} else {
		mylog.Log.Debugln("write response string result:", result)
	}
}
func respFall(c *gin.Context, status int) {
	tm := time.Now().Unix()
	c.JSON(http.StatusOK, gin.H{"code": status, "now": tm})
}

// 返回response http 回应函数，返回为json，格式为
// 错误信息：{ code: 201, message: "" }，正常信息： {code: 200, data: {} }
func respJSON(c *gin.Context, status int, msg interface{}) {
	if status != http.StatusOK {
		c.JSON(status, gin.H{"code": status, "message": msg})
	} else {
		c.JSON(status, gin.H{"code": status, "message": "操作成功", "data": msg})
	}
}

// 返回带页号的response 回应，格式为{code: 200, pageNo: 1, pageSize 20, data: {} }
func respJSONWithPage(c *gin.Context, status int, page *common.PageDao, msg interface{}) {
	if status != http.StatusOK {
		c.JSON(status, gin.H{"code": status, "message": msg})
	} else {
		c.JSON(status, gin.H{"code": status, "pageNo": page.PageNo, "pageSize": page.PageSize, "totalPage": page.TotalPages, "data": msg})
	}
}

func uploadOtaFileFun(c *gin.Context) {
	uploadFileFunc(c, cfg.StaticOtaPath)
}

func uploadFileFunc(c *gin.Context, staticPath string) {
	exception.TryEx{
		Try: func() {
			file, err := c.FormFile("file")
			if err != nil {
				exception.Throw(http.StatusBadRequest, "upload file error")
			}
			filename := staticPath + filepath.Base(file.Filename)
			if err := c.SaveUploadedFile(file, filename); err != nil {
				exception.Throw(http.StatusBadRequest, "upload file error")
			}
			respJSON(c, http.StatusOK, cfg.This.Svr.OutUrl+filename)
		},
		Catch: func(e exception.Exception) {
			respJSON(c, e.Code, e.Msg)
		},
	}.Run()
}
