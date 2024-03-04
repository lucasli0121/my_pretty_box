/*
 * @Author: liguoqiang
 * @Date: 2022-06-15 14:27:42
 * @LastEditors: liguoqiang
 * @LastEditTime: 2023-09-26 11:42:16
 * @Description:
 */
/**********************************************************
* 此文件定义股票相关结构
* 包含： 股票信息，股票综述，股票行情，股票历史等
**********************************************************/
package mysql

import (
	"agent/cfg"
	"agent/exception"
	mylog "agent/log"
	"agent/mdb/common"
	"database/sql"
	"encoding/hex"
	"encoding/json"
	"fmt"
	"math"
	"net/http"
	"reflect"
	"time"

	"github.com/gin-gonic/gin"
	"github.com/gin-gonic/gin/binding"
)

const (
	HeatRateType  = "heart_rate"
	FallCheckType = "fall_check"
	LampType      = "lamp"
)

func GetAllDeviceType(types *[]string) {
	*types = append(*types, HeatRateType)
	*types = append(*types, FallCheckType)
	*types = append(*types, LampType)
}

/******************************************************
* 为mysql 数据库提供的结构
* device基本信息结构体
*******************************************************/
type Device struct {
	ID         int64  `json:"id" mysql:"id" binding:"omitempty"`
	Name       string `json:"name" mysql:"name" binding:"required"`
	Type       string `json:"type" mysql:"type" binding:"required"`
	Mac        string `json:"mac" mysql:"mac"`
	RoomNum    string `json:"room_num" mysql:"room_num"`
	Online     int    `json:"online" mysql:"online"`
	OnlineTime string `json:"online_time" mysql:"online_time"`
	CreateTime string `json:"create_time" mysql:"create_time"`
	Remark     string `json:"remark" mysql:"remark"`
}

func NewDevice() *Device {
	return &Device{
		ID:         0,
		Name:       "",
		Type:       "",
		Mac:        "",
		RoomNum:    "",
		Online:     0,
		OnlineTime: time.Now().Format(cfg.TmFmtStr),
		CreateTime: time.Now().Format(cfg.TmFmtStr),
		Remark:     "",
	}
}

/*
*  QueryAllDevice...
*  查询所有Device基本信息
 */
func QueryAllDevice(results *[]Device) bool {
	res := QueryDao(common.DeviceTbl, nil, nil, nil, func(rows *sql.Rows) {
		var v *Device = NewDevice()
		err := v.DecodeFromRows(rows)
		if err != nil {
			mylog.Log.Errorln(err)
		} else {
			*results = append(*results, *v)
		}
	})
	return res
}

/*
QueryDeviceByCond...
根据条件查询股票基本信息
*/
func QueryDeviceByCond(filter interface{}, page *common.PageDao, sort interface{}, results *[]Device) bool {
	res := false
	backFunc := func(rows *sql.Rows) {
		obj := NewDevice()
		err := obj.DecodeFromRows(rows)
		if err != nil {
			mylog.Log.Errorln(err)
		} else {
			*results = append(*results, *obj)
		}
	}
	if page == nil {
		res = QueryDao(common.DeviceTbl, filter, sort, nil, backFunc)
	} else {
		res = QueryPage(common.DeviceTbl, page, filter, sort, backFunc)
	}
	return res
}

func (me *Device) DecodeFromRows(rows *sql.Rows) error {
	err := rows.Scan(&me.ID, &me.Name, &me.Type, &me.Mac, &me.RoomNum, &me.Online, &me.OnlineTime, &me.CreateTime, &me.Remark)
	return err
}
func (me *Device) DecodeFromRow(row *sql.Row) error {
	err := row.Scan(&me.ID, &me.Name, &me.Type, &me.Mac, &me.RoomNum, &me.Online, &me.OnlineTime, &me.CreateTime, &me.Remark)
	return err
}

/*
Decode 解析从gin获取的数据 转换成Device
*/
func (me *Device) DecodeFromGin(c *gin.Context) {
	if err := c.ShouldBindBodyWith(me, binding.JSON); err != nil {
		exception.Throw(http.StatusAccepted, err.Error())
	}
	if me.Name == "" || me.Mac == "" {
		exception.Throw(http.StatusAccepted, "name or mac is empty!")
	}
}

/*
QueryByID() 查询股票基本信息
*/
func (me *Device) QueryByID(id int64) bool {
	return QueryDaoByID(common.DeviceTbl, id, me)
}

/*
Insert 股票基本信息数据插入
*/
func (me *Device) Insert() bool {
	tblName := common.DeviceTbl
	if !CheckTableExist(tblName) {
		sql := `create table ` + tblName + ` (
            id MEDIUMINT NOT NULL AUTO_INCREMENT,
            name varchar(32) NOT NULL COMMENT '名称',
            type varchar(32) NOT NULL COMMENT '类型',
			mac char(32) NOT NULL COMMENT 'mac地址',
			room_num char(32) NOT NULL COMMENT '房间号',
			online int NOT NULL COMMENT '是否在线',
			online_time datetime COMMENT '在线时间',
            create_time datetime comment '新增日期',
			remark varchar(64) comment '备注',
            PRIMARY KEY (id, mac, create_time)
        )`
		CreateTable(sql)
	}
	return InsertDao(common.DeviceTbl, me)
}

/*
Update() 更新股票基本信息
*/
func (me *Device) Update() bool {
	return UpdateDaoByID(common.DeviceTbl, me.ID, me)
}

/*
Delete() 删除指数
*/
func (me *Device) Delete() bool {
	return DeleteDaoByID(common.DeviceTbl, me.ID)
}

/*
设置ID
*/
func (me *Device) SetID(id int64) {
	me.ID = id
}

/*
********************************************************************************

	HeatRateDevice 信息表

********************************************************************************
*/
type HeartRate struct {
	ID            int64  `json:"id" mysql:"id" binding:"omitempty"`
	Mac           string `json:"mac" mysql:"mac" binding:"required"`
	PersonNum     int    `json:"person_num" mysql:"person_num"`
	PersonPos     int    `json:"person_pos" mysql:"person_pos"`
	PersonStatus  int    `json:"person_status" mysql:"person_status"`
	SleepFeatures int    `json:"sleep_features" mysql:"sleep_features"`
	HeartRate     int    `json:"heart_rate" mysql:"heart_rate"`
	BreatheRate   int    `json:"breathe_rate" mysql:"breathe_rate"`
	ActiveStatus  int    `json:"active_status" mysql:"active_status"`
	PhysicalRate  int    `json:"physical_rate" mysql:"physical_rate"`
	StagesStatus  int    `json:"stages_status" mysql:"stages_status"`
	DateTime      string `json:"create_time" mysql:"create_time" binding:"datetime=2006-01-02 15:04:05"`
}

/*
NewHeartRate...
构造实例
*/
func NewHeartRate() *HeartRate {
	return &HeartRate{
		ID:            0,
		Mac:           "",
		PersonNum:     0,
		PersonPos:     0,
		PersonStatus:  0,
		SleepFeatures: 0,
		HeartRate:     0,
		BreatheRate:   0,
		ActiveStatus:  0,
		PhysicalRate:  0,
		StagesStatus:  0,
		DateTime:      time.Now().Format(cfg.TmFmtStr),
	}
}

func (me *HeartRate) MarshalJSON() ([]byte, error) {
	var b []byte
	u := reflect.TypeOf(me)
	vf := reflect.ValueOf(me)
	numField := u.Elem().NumField()
	b = append(b, "{"...)
	for num := 0; num < numField; num++ {
		f := u.Elem().Field(num)
		v := vf.Elem().Field(num)
		switch v.Kind() {
		case reflect.Int64:
			var val string
			if f.Name == "ID" && v.Int() <= 0 {
				val = fmt.Sprintf("\"%v\":\"NaN\"", f.Tag.Get("json"))
			} else {
				val = fmt.Sprintf("\"%v\":\"%v\"", f.Tag.Get("json"), v.Int())
			}
			if num < (numField - 1) {
				val += ","
			}
			b = append(b, val...)
		case reflect.Int:
			var val string
			val = fmt.Sprintf("\"%v\":\"%v\"", f.Tag.Get("json"), v.Int())
			if num < (numField - 1) {
				val += ","
			}
			b = append(b, val...)
		case reflect.Float64:
			var val string
			if math.IsNaN(v.Float()) {
				val = fmt.Sprintf("\"%v\":\"NaN\"", f.Tag.Get("json"))
			} else {
				val = fmt.Sprintf("\"%v\":\"%v\"", f.Tag.Get("json"), v.Float())
			}
			if num < (numField - 1) {
				val += ","
			}
			b = append(b, val...)
		case reflect.String:
			val := fmt.Sprintf("\"%v\":\"%v\"", f.Tag.Get("json"), v.String())
			if num < (numField - 1) {
				val += ","
			}
			b = append(b, val...)
		}
	}
	b = append(b, "}"...)
	return b, nil
}

func (me *HeartRate) MarshalBinary() ([]byte, error) {
	return json.Marshal(me)
}
func (me *HeartRate) UnmarshalBinary(data []byte) error {
	return json.Unmarshal(data, me)
}

/*
QueryHeartRateByCond...
根据条件查询HeartRate数据
*/
func QueryHeartRateByCond(filter interface{}, page *common.PageDao, sort interface{}, results *[]HeartRate) bool {
	res := false
	backFunc := func(rows *sql.Rows) {
		obj := NewHeartRate()
		err := obj.DecodeFromRows(rows)
		if err != nil {
			mylog.Log.Errorln(err)
		} else {
			*results = append(*results, *obj)
		}
	}
	if page == nil {
		res = QueryDao(common.DeviceRecordTbl(HeatRateType), filter, sort, nil, backFunc)
	} else {
		res = QueryPage(common.DeviceRecordTbl(HeatRateType), page, filter, sort, backFunc)
	}
	return res
}

/*
Decode 解析从gin获取的数据 转换成HeartRate
*/
func (me *HeartRate) DecodeFromGin(c *gin.Context) {
	if err := c.ShouldBindBodyWith(me, binding.JSON); err != nil {
		exception.Throw(http.StatusAccepted, err.Error())
	}
}

func (me *HeartRate) DecodeFromRows(rows *sql.Rows) error {
	return rows.Scan(&me.ID,
		&me.Mac,
		&me.PersonNum,
		&me.PersonPos,
		&me.PersonStatus,
		&me.SleepFeatures,
		&me.HeartRate,
		&me.BreatheRate,
		&me.ActiveStatus,
		&me.PhysicalRate,
		&me.StagesStatus,
		&me.DateTime)
}
func (me *HeartRate) DecodeFromRow(row *sql.Row) error {
	return row.Scan(&me.ID,
		&me.Mac,
		&me.PersonNum,
		&me.PersonPos,
		&me.PersonStatus,
		&me.SleepFeatures,
		&me.HeartRate,
		&me.BreatheRate,
		&me.ActiveStatus,
		&me.PhysicalRate,
		&me.StagesStatus,
		&me.DateTime)
}

/*
QueryByID() 查询股票实时行情
*/
func (me *HeartRate) QueryByID(id int64) bool {
	me.SetID(id)
	return QueryDaoByID(common.DeviceRecordTbl(HeatRateType), me.ID, me)
}

/*
Insert 股票行情数据插入
*/
func (me *HeartRate) Insert() bool {
	tblName := common.DeviceRecordTbl(HeatRateType)
	if !CheckTableExist(tblName) {
		sql := `create table ` + tblName + ` (
            id MEDIUMINT NOT NULL AUTO_INCREMENT,
            mac varchar(32) not null comment '设备mac,与设备表关联',
            person_num int not null comment '人数',
			person_pos int not null comment '人体位置',
			person_status int not null comment '人体状态',
			sleep_features int not null comment '睡眠特征',
			heart_rate int not null comment '心率',
			breathe_rate int not null comment '呼吸率',
			active_status int not null comment '活动状态',
			physical_rate int not null comment '体态评分',
			stages_status int not null comment '睡眠状态',
            create_time datetime comment '新增日期',
            PRIMARY KEY (id, mac, create_time)
        )`
		CreateTable(sql)
	}
	var ret = InsertDao(tblName, me)
	return ret
}

/*
Update() 更新指数表
*/
func (me *HeartRate) Update() bool {
	return UpdateDaoByID(common.DeviceRecordTbl(HeatRateType), me.ID, me)
}

/*
Delete() 删除指数
*/
func (me *HeartRate) Delete() bool {
	return DeleteDaoByID(common.DeviceRecordTbl(HeatRateType), me.ID)
}

/*
设置ID
*/
func (me *HeartRate) SetID(id int64) {
	me.ID = id
}

/**
 * @description: parse raw heart rate data from client
 * @return {*}
 */
func ParseRawData(mac string, createTime string, rawData string) (*HeartRate, int) {
	data, err := hex.DecodeString(rawData)
	if err != nil {
		mylog.Log.Errorln("hex decode error:", err)
		return nil, -2
	}
	me := NewHeartRate()
	me.Mac = mac
	me.DateTime = createTime
	var pkSize int = 48
	var pkNum int = 0
	for i := 24; i < len(data); i++ {
		index := i - pkNum*pkSize
		switch index {
		case 24:
			me.PersonNum = int(data[i])
		case 25:
			me.PersonPos += int(data[i])
		case 26:
			me.PersonStatus = int(data[i])
		case 27:
			me.SleepFeatures = int(data[i])
		case 28:
			me.HeartRate += int(data[i])
		case 29:
			me.BreatheRate += int(data[i])
		case 30:
			me.ActiveStatus = int(data[i])
		case 35:
			me.PhysicalRate += int(data[i])
		case 36:
			me.StagesStatus = int(data[i])
		case 47:
			pkNum++
		}
	}
	if pkNum == 0 {
		return nil, pkNum
	}
	me.PersonPos /= pkNum
	me.HeartRate /= pkNum
	me.BreatheRate /= pkNum
	me.PhysicalRate /= pkNum
	return me, pkNum
}

/*******************************************************************************
* 定义FallCheck结构
******************************************************************************/
type FallCheck struct {
	ID          int64  `json:"id" mysql:"id" binding:"omitempty"`
	Mac         string `json:"mac" mysql:"mac" binding:"required"`
	Type        int    `json:"type" mysql:"type"`
	PersonState int    `json:"person_state" mysql:"person_state"`
	ActiveState int    `json:"active_state" mysql:"active_state"`
	FallState   int    `json:"fall_state" mysql:"fall_state"`
	DateTime    string `json:"create_time" mysql:"create_time" binding:"datetime=2006-01-02 15:04:05"`
}

/*
NewFallCheck...
构造实例
*/
func NewFallCheck() *FallCheck {
	return &FallCheck{
		ID:          0,
		Mac:         "",
		Type:        0,
		PersonState: 0,
		ActiveState: 0,
		FallState:   0,
		DateTime:    time.Now().Format(cfg.TmFmtStr),
	}
}

/*
QueryFallCheckByCond...
根据条件查询FallCheck数据
*/
func QueryFallCheckByCond(filter interface{}, page *common.PageDao, sort interface{}, results *[]FallCheck) bool {
	res := false
	backFunc := func(rows *sql.Rows) {
		obj := NewFallCheck()
		err := obj.DecodeFromRows(rows)
		if err != nil {
			mylog.Log.Errorln(err)
		} else {
			*results = append(*results, *obj)
		}
	}
	if page == nil {
		res = QueryDao(common.DeviceRecordTbl(FallCheckType), filter, sort, nil, backFunc)
	} else {
		res = QueryPage(common.DeviceRecordTbl(FallCheckType), page, filter, sort, backFunc)
	}
	return res
}

/*
Decode 解析从gin获取的数据 转换成HeartRate
*/
func (me *FallCheck) DecodeFromGin(c *gin.Context) {
	if err := c.ShouldBindBodyWith(me, binding.JSON); err != nil {
		exception.Throw(http.StatusAccepted, err.Error())
	}
}

func (me *FallCheck) DecodeFromRows(rows *sql.Rows) error {
	return rows.Scan(&me.ID,
		&me.Mac,
		&me.Type,
		&me.PersonState,
		&me.ActiveState,
		&me.FallState,
		&me.DateTime)
}
func (me *FallCheck) DecodeFromRow(row *sql.Row) error {
	return row.Scan(&me.ID,
		&me.Mac,
		&me.Type,
		&me.PersonState,
		&me.ActiveState,
		&me.FallState,
		&me.DateTime)
}

/*
QueryByID() 查询股票实时行情
*/
func (me *FallCheck) QueryByID(id int64) bool {
	me.SetID(id)
	return QueryDaoByID(common.DeviceRecordTbl(FallCheckType), me.ID, me)
}

/*
Insert FallCheck数据插入
*/
func (me *FallCheck) Insert() bool {
	tblName := common.DeviceRecordTbl(FallCheckType)
	if !CheckTableExist(tblName) {
		sql := `create table ` + tblName + ` (
            id MEDIUMINT NOT NULL AUTO_INCREMENT,
            mac varchar(32) not null comment '设备mac,与设备表关联',
			type int comment '类型',
            person_state int not null comment '有无人',
			active_state int not null comment '活动状态',
			fall_state int not null comment '跌倒状态',
            create_time datetime comment '新增日期',
            PRIMARY KEY (id, mac, create_time)
        )`
		CreateTable(sql)
	}
	var ret = InsertDao(tblName, me)
	return ret
}

/*
Update() 更新指数表
*/
func (me *FallCheck) Update() bool {
	return UpdateDaoByID(common.DeviceRecordTbl(FallCheckType), me.ID, me)
}

/*
Delete() 删除指数
*/
func (me *FallCheck) Delete() bool {
	return DeleteDaoByID(common.DeviceRecordTbl(FallCheckType), me.ID)
}

/*
设置ID
*/
func (me *FallCheck) SetID(id int64) {
	me.ID = id
}

/*******************************************************************************
* 定义跌倒告警结构
******************************************************************************/
type FallAlarm struct {
	ID         int64  `json:"id" mysql:"id" binding:"omitempty"`
	Mac        string `json:"mac" mysql:"mac" binding:"required"`
	AlarmEvent int    `json:"alarm_event" mysql:"alarm_event"`
	DateTime   string `json:"create_time" mysql:"create_time" binding:"datetime=2006-01-02 15:04:05"`
}

/*
NewFallAlarm...
构造实例
*/
func NewFallAlarm() *FallAlarm {
	return &FallAlarm{
		ID:         0,
		Mac:        "",
		AlarmEvent: 0,
		DateTime:   time.Now().Format(cfg.TmFmtStr),
	}
}

/*
QueryFallAlarmByCond...
根据条件查询FallCheck数据
*/
func QueryFallAlarmByCond(filter interface{}, page *common.PageDao, sort interface{}, results *[]FallAlarm) bool {
	res := false
	backFunc := func(rows *sql.Rows) {
		obj := NewFallAlarm()
		err := obj.DecodeFromRows(rows)
		if err != nil {
			mylog.Log.Errorln(err)
		} else {
			*results = append(*results, *obj)
		}
	}
	if page == nil {
		res = QueryDao(common.FallAlarmTbl, filter, sort, nil, backFunc)
	} else {
		res = QueryPage(common.FallAlarmTbl, page, filter, sort, backFunc)
	}
	return res
}

/*
Decode 解析从gin获取的数据 转换成FallAlarm
*/
func (me *FallAlarm) DecodeFromGin(c *gin.Context) {
	if err := c.ShouldBindBodyWith(me, binding.JSON); err != nil {
		exception.Throw(http.StatusAccepted, err.Error())
	}
}

func (me *FallAlarm) DecodeFromRows(rows *sql.Rows) error {
	return rows.Scan(&me.ID,
		&me.Mac,
		&me.AlarmEvent,
		&me.DateTime)
}
func (me *FallAlarm) DecodeFromRow(row *sql.Row) error {
	return row.Scan(&me.ID,
		&me.Mac,
		&me.AlarmEvent,
		&me.DateTime)
}

/*
QueryByID() 查询股票实时行情
*/
func (me *FallAlarm) QueryByID(id int64) bool {
	me.SetID(id)
	return QueryDaoByID(common.FallAlarmTbl, me.ID, me)
}

/*
Insert FallAlarm数据插入
*/
func (me *FallAlarm) Insert() bool {
	tblName := common.FallAlarmTbl
	if !CheckTableExist(tblName) {
		sql := `create table ` + tblName + ` (
            id MEDIUMINT NOT NULL AUTO_INCREMENT,
            mac varchar(32) not null comment '设备mac,与设备表关联',
            alarm_event int not null comment '告警事件',
            create_time datetime comment '新增日期',
            PRIMARY KEY (id, mac, create_time)
        )`
		CreateTable(sql)
	}
	return InsertDao(tblName, me)
}

/*
Update() 更新指数表
*/
func (me *FallAlarm) Update() bool {
	return UpdateDaoByID(common.FallAlarmTbl, me.ID, me)
}

/*
Delete() 删除指数
*/
func (me *FallAlarm) Delete() bool {
	return DeleteDaoByID(common.FallAlarmTbl, me.ID)
}

/*
设置ID
*/
func (me *FallAlarm) SetID(id int64) {
	me.ID = id
}

type UserDeviceAlarm struct {
	UserId     int64  `json:"user_id" mysql:"user_id"`
	NickName   string `json:"nick_name" mysql:"nick_name"`
	Phone      string `json:"phone" mysql:"phone"`
	DeviceId   int64  `json:"device_id" mysql:"device_id"`
	DeviceName string `json:"device_name" mysql:"device_name"`
	Mac        string `json:"mac" mysql:"mac"`
	DeviceType string `json:"device_type" mysql:"device_type"`
	Remark     string `json:"remark" mysql:"remark"`
	AlarmEvent int    `json:"alarm_event" mysql:"alarm_event"`
	CreateTime string `json:"create_time" mysql:"create_time" binding:"datetime=2006-01-02 15:04:05"`
}

func NewUserDeviceAlarm() *UserDeviceAlarm {
	return &UserDeviceAlarm{
		UserId:     0,
		NickName:   "",
		Phone:      "",
		DeviceId:   0,
		DeviceName: "",
		Mac:        "",
		DeviceType: "",
		Remark:     "",
		AlarmEvent: 0,
		CreateTime: time.Now().Format(cfg.TmFmtStr),
	}
}

/******************************************************************************
 * function: QueryUserDeviceByMac
 * description: query user device which mac belong to user
 * return {*}
********************************************************************************/
func (me *UserDeviceAlarm) QueryUserDeviceByMac(mac string) bool {
	sql := "select a.id as user_id, a.nick_name, a.phone, b.id as device_id, b.name as device_name, b.mac, b.type as device_type, b.remark" +
		" from user_tbl a, device_tbl b, user_device_relation_tbl c where a.id=c.user_id and b.id=c.device_id and b.mac=?"
	row := mDb.QueryRow(sql, mac)
	err := row.Scan(&me.UserId, &me.NickName, &me.Phone, &me.DeviceId, &me.DeviceName, &me.Mac, &me.DeviceType, &me.Remark)
	if err != nil {
		mylog.Log.Errorln(err)
		return false
	}
	return true
}

/******************************************************************************
* defile Ed713 Ota file struct
********************************************************************************/

// swagger:model Ed713OtaData
type Ed713OtaData struct {
	Mac          string `json:"mac" mysql:"mac"`
	Upgrade      int    `json:"upgrade" mysql:"upgrade"`
	BaseVersion  int    `json:"remoteBaseVersion" mysql:"base_version"`
	BaseOtaUrl   string `json:"baseOtaUrl" mysql:"base_ota_url"`
	BaseFileSize int64  `json:"baseFileSize" mysql:"base_file_size"`
	CoreVersion  int    `json:"remoteCoreVersion" mysql:"core_version"`
	CoreOtaUrl   string `json:"coreOtaUrl" mysql:"core_ota_url"`
	CoreFileSize int64  `json:"coreFileSize" mysql:"core_file_size"`
}

func QueryEd713OtaData(mac string, obj *Ed713OtaData) bool {
	sql := "select mac, upgrade, base_version, base_ota_url, base_file_size, core_version, core_ota_url, core_file_size from ed713_ota_tbl where mac=?"
	row := mDb.QueryRow(sql, mac)
	err := row.Scan(&obj.Mac, &obj.Upgrade, &obj.BaseVersion, &obj.BaseOtaUrl, &obj.BaseFileSize, &obj.CoreVersion, &obj.CoreOtaUrl, &obj.CoreFileSize)
	if err != nil {
		mylog.Log.Errorln(err)
		return false
	}
	return true
}
