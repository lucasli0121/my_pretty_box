/******************************************************************************
 * Author: liguoqiang
 * Date: 2023-08-29 20:20:29
 * LastEditors: liguoqiang
 * LastEditTime: 2023-08-31 22:57:05
 * Description:
********************************************************************************/

package common

import (
	"crypto/md5"
	"fmt"
)

const (
	DeviceTbl    = "device_tbl"
	FallAlarmTbl = "fall_alarm_tbl"
)
const HEART_RATE_DATA_TOPIC_PREFIX string = "heart/real_data"
const FAIL_CHECK_DATA_TOPIC_PREFIX string = "fail_check/real_data"
const FALL_ALARM_DATA_TOPIC_PREFIX string = "fall_alarm/real_data"

func DeviceRecordTbl(deviceType string) string {
	return deviceType + "_record_tbl"
}

type PageDao struct {
	PageNo     int64
	PageSize   int64
	TotalPages int64
}

// 返回一个缺省的Page信息
func NewPageDao(pageNo, pageSize int64) *PageDao {
	return &PageDao{
		PageNo:     pageNo,
		PageSize:   pageSize,
		TotalPages: 0,
	}
}

/******************************************************************************
 * function: MakeMD5
 * description: encrypt string with md5
 * return {*}
********************************************************************************/
func MakeMD5(str string) string {
	data := []byte(str)
	md5Inst := md5.New()
	md5Inst.Write(data)
	result := md5Inst.Sum([]byte(""))
	md5Str := fmt.Sprintf("%x", result)
	return md5Str
}

func MakeHeartRateTopic(mac string) string {
	return HEART_RATE_DATA_TOPIC_PREFIX + "/" + mac
}
func MakeFallCheckTopic(mac string) string {
	return FAIL_CHECK_DATA_TOPIC_PREFIX + "/" + mac
}
func MakeFallAlarmTopic(mac string) string {
	return FALL_ALARM_DATA_TOPIC_PREFIX + "/" + mac
}
