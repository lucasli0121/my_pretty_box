/*
 * @Author: liguoqiang
 * @Date: 2023-02-09 17:20:34
 * @LastEditors: liguoqiang
 * @LastEditTime: 2023-09-26 12:08:41
 * @Description:
 */
package mdb

import (
	"agent/cfg"
	"agent/gopool"
	mylog "agent/log"
	"agent/mdb/common"
	"agent/mdb/mysql"
	"agent/mq"
	"encoding/json"
	"net/http"
	"strconv"
	"time"

	"github.com/gin-gonic/gin"
	"github.com/gin-gonic/gin/binding"
)

var taskPool *gopool.Pool = nil

/******************************************************
 * 定义 数据库初始化函数
 * 在open函数中实现数据库的打开操作
 *******************************************************/

func Open(pool *gopool.Pool) bool {
	taskPool = pool
	return mysql.Open()
}

func Close() {
	mysql.Close()
}

/**
 * @description: handle data reported by remote device
 * @return {*}
 */
func InsertHeartRate(c *gin.Context) int {
	type HeartRateBody struct {
		Mac     string `json:"mac"`
		PassKey string `json:"passkey"`
		PubTime uint64 `json:"pub_time"`
		RawData string `json:"rawData"`
	}
	var body HeartRateBody

	body.Mac = c.Request.PostFormValue("mac")
	body.PassKey = c.Request.PostFormValue("passkey")
	pubTime := c.Request.PostFormValue("pub_time")
	body.RawData = c.Request.PostFormValue("rawData")
	v, err := strconv.Atoi(pubTime)
	if err != nil {
		mylog.Log.Infoln("pub_time is invalid, pub_time:", pubTime)
		return -2
	}
	body.PubTime = uint64(v)
	mysql.OnlineDevice(body.Mac) // online device
	var tm time.Duration = time.Duration(body.PubTime) * time.Second
	var createTm = time.Unix(int64(tm.Seconds()), 0).Format(cfg.TmFmtStr)
	// filter := fmt.Sprintf("mac='%s' and create_time='%s'", body.Mac, createTm)
	// var gList []mysql.HeartRate
	// mysql.QueryHeartRateByCond(filter, nil, nil, &gList)
	// if len(gList) > 0 {
	// 	mylog.Log.Println("heart rate data already exist, mac:", body.Mac, ", create_time:", createTm)
	// 	return -1
	// }
	obj, ret := mysql.ParseRawData(body.Mac, createTm, body.RawData)
	mq.PublicData(common.MakeHeartRateTopic(body.Mac), obj)
	if obj != nil && taskPool != nil {
		err = taskPool.Put(&gopool.Task{
			Params: []interface{}{obj},
			Do: func(params ...interface{}) {
				obj := params[0].(*mysql.HeartRate)
				obj.Insert()
			},
		})
		if err != nil {
			mylog.Log.Errorln("put task failed, err:", err)
		}
	}
	return ret
}

/**
 * @description: handle OTA data
 * @return {*}
 */
func InsertOtaInfo(c *gin.Context) (int, interface{}) {
	return -2, nil
}

/**
 * @description: handle fall heart data reported by remote device
 * @return {*}
 */
func InsertFallHeart(c *gin.Context) int {
	type RawData struct {
		PersonState int `json:"people_state"`
		ActiveState int `json:"active_state"`
		FallState   int `json:"fall_state"`
	}
	type FallCheckBody struct {
		Mac     string `json:"mac"`
		PubTime uint64 `json:"pub_time"`
		Type    int    `json:"type"`
		PassKey string `json:"passkey"`
		Data    string `json:"rawData"`
	}

	var body FallCheckBody
	if err := c.ShouldBindBodyWith(&body, binding.JSON); err != nil {
		return -2
	}
	if body.Mac == "" {
		return -2
	}
	var rawData RawData
	err := json.Unmarshal([]byte(body.Data), &rawData)
	if err != nil {
		return -2
	}
	var tm time.Duration = time.Duration(body.PubTime) * time.Second
	var createTm = time.Unix(int64(tm.Seconds()), 0).Format(cfg.TmFmtStr)
	// var gList []mysql.FallCheck
	// mysql.QueryFallCheckByCond(fmt.Sprintf("mac='%s' and create_time='%s'", body.Mac, createTm), nil, nil, &gList)
	// if len(gList) > 0 {
	// 	return 1
	// }
	me := mysql.NewFallCheck()
	me.Mac = body.Mac
	me.DateTime = createTm
	me.Type = body.Type
	me.PersonState = rawData.PersonState
	me.ActiveState = rawData.ActiveState
	me.FallState = rawData.FallState
	mq.PublicData(common.MakeFallCheckTopic(me.Mac), me)
	if taskPool != nil {
		err = taskPool.Put(&gopool.Task{
			Params: []interface{}{me},
			Do: func(params ...interface{}) {
				me := params[0].(*mysql.FallCheck)
				mysql.OnlineDevice(me.Mac) // online device
				me.Insert()
			},
		})
		if err != nil {
			mylog.Log.Errorln("put task failed, err:", err)
		}
	}
	return 1
}

func InsertEvent(c *gin.Context) int {
	type FallAlarmBody struct {
		Mac     string `json:"mac"`
		PubTime uint64 `json:"pub_time"`
		Event   int    `json:"event"`
		Detail  string `json:"detail"`
		PassKey string `json:"passkey"`
	}
	var body FallAlarmBody
	if err := c.ShouldBindBodyWith(&body, binding.JSON); err != nil {
		return -2
	}
	if body.Mac == "" {
		return -2
	}
	var tm time.Duration = time.Duration(body.PubTime) * time.Second
	var createTm = time.Unix(int64(tm.Seconds()), 0).Format(cfg.TmFmtStr)
	// var gList []mysql.FallAlarm
	// mysql.QueryFallAlarmByCond(fmt.Sprintf("mac='%s' and create_time='%s'", body.Mac, createTm), nil, nil, &gList)
	// if len(gList) > 0 {
	// 	return 1
	// }
	me := mysql.NewUserDeviceAlarm()
	if me.QueryUserDeviceByMac(body.Mac) {
		me.Mac = body.Mac
		me.CreateTime = createTm
		me.AlarmEvent = body.Event
		// MQ publish alarm first
		mq.PublicData(common.MakeFallAlarmTopic(me.Mac), me)
	}
	if taskPool != nil {
		err := taskPool.Put(&gopool.Task{
			Params: []interface{}{me},
			Do: func(params ...interface{}) {
				me := params[0].(*mysql.UserDeviceAlarm)
				obj := mysql.NewFallAlarm()
				obj.Mac = me.Mac
				obj.DateTime = me.CreateTime
				obj.AlarmEvent = me.AlarmEvent
				obj.Insert()
			},
		})
		if err != nil {
			mylog.Log.Errorln("put task failed, err:", err)
		}
	}
	return 1
}

/******************************************************************************
 * function:
 * description:
 * param {*gin.Context} c
 * return {*}
********************************************************************************/

// swagger:model Ed713OtqReq
type Ed713OtqReq struct {
	Mac         string `json:"mac"`
	BaseVersion int    `json:"base_version"`
	CoreVersion int    `json:"core_version"`
}

func GetED713Ota(c *gin.Context) (int, interface{}) {
	var req *Ed713OtqReq = &Ed713OtqReq{}
	if err := c.ShouldBindBodyWith(req, binding.JSON); err != nil {
		mylog.Log.Errorln(err)
	}
	ed713Data := &mysql.Ed713OtaData{}
	if !mysql.QueryEd713OtaData(req.Mac, ed713Data) {
		return http.StatusBadRequest, "没有对应记录"
	}
	return http.StatusOK, ed713Data
}
