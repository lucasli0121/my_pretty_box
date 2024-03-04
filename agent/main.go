/******************************************************************************
 * Author: liguoqiang
 * Date: 2023-10-06 16:41:18
 * LastEditors: liguoqiang
 * LastEditTime: 2023-12-18 21:37:51
 * Description:
********************************************************************************/
/*
 * @Author: liguoqiang
 * @Date: 2021-03-07 09:31:25
 * @LastEditors: liguoqiang
 * @LastEditTime: 2023-09-24 16:26:55
 * @Description: 实现后台管理的主程序
 */
package main

import (
	"fmt"
	"runtime"

	"agent/api"
	"agent/cfg"
	"agent/gopool"
	mylog "agent/log"
)

func main() {
	runtime.GOMAXPROCS(runtime.NumCPU())
	err := cfg.InitConfig("./cfg/cfg.yml")
	if err != nil {
		fmt.Println("initialize config failed, ", err)
		return
	}

	mylog.Init()
	defer mylog.Close()
	taskPool, _ := gopool.InitPool(512)
	defer taskPool.Close()
	// if !mdb.Open(taskPool) {
	// 	fmt.Println("connect database failed exit!")
	// 	return
	// }
	// defer mdb.Close()
	//init mqtt object
	// if !mq.InitMqtt() {
	// 	fmt.Println("init mqtt failed exit!")
	// 	return
	// }
	// defer mq.CloseMqtt()
	//启动web服务
	api.StartWeb()
}
