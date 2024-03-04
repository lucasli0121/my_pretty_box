/*
 * @Author: liguoqiang
 * @Date: 2021-03-07 09:34:20
 * @LastEditors: liguoqiang
 * @LastEditTime: 2023-09-24 16:05:52
 * @Description: 实现 数据库的主函数, 连接mysql 操作
 */

package mysql

import (
	mylog "agent/log"
	"bytes"
	"database/sql"
	"fmt"
	"math"
	"reflect"
	"strconv"
	"strings"
	"time"

	"agent/cfg"
	"agent/mdb/common"

	"github.com/gin-gonic/gin"
	_ "github.com/go-sql-driver/mysql"
)

/*
* MysqlDao... mysql所有数据对象的基类
 */
type Dao interface {
	SetID(int64)
	QueryByID(int64) bool
	Insert() bool
	Update() bool
	Delete() bool
	DecodeFromGin(c *gin.Context)
	DecodeFromRow(row *sql.Row) error
	DecodeFromRows(rows *sql.Rows) error
}

/*
**********************************************************************************

	自定义一个float64类型，用来处理字符串和float转换时null或者nan的情况

**********************************************************************************
*/
type float64JSON float64

func (me *float64JSON) UnmarshalJSON(b []byte) error {
	b = bytes.Trim(b, "\"")
	strval := strings.ToLower(string(b))
	if strval == "nan" || strval == "null" {
		*me = 0.0
	} else {
		val, err := strconv.ParseFloat(strval, 64)
		if err != nil {
			return err
		}
		*me = float64JSON(val)
	}
	return nil
}

var mDb *sql.DB = nil

func Open() bool {
	dsn := cfg.This.DB.Username + ":" + cfg.This.DB.Password + "@" + cfg.This.DB.Url + "/" + cfg.This.DB.Dbname
	db, err := sql.Open("mysql", dsn)
	if err != nil {
		mylog.Log.Errorln("open mysql driver error:", err)
		return false
	}
	/* 连接数据库 */
	err = db.Ping()
	if err != nil {
		mylog.Log.Errorln("ping to mysql error:", err)
		return false
	}
	mDb = db
	mDb.SetConnMaxLifetime(time.Second * 30) // 每个连接最大存活时间
	mDb.SetConnMaxIdleTime(time.Second * 30) // 每个连接最大空闲时间
	mDb.SetMaxIdleConns(256)                 // 最大空闲连接数
	mDb.SetMaxOpenConns(1024)                // 连接池最大连接数
	return true
}
func Close() {
	err := mDb.Close()
	if err != nil {
		mylog.Log.Errorln(err)
	}
}

/******************************************************************************
 * function: OnlineDevice
 * description:
 * return {*}
********************************************************************************/
func OnlineDevice(mac string) {
	sql := "update " + common.DeviceTbl + " set online=1, online_time=? where mac=?"
	result, err := mDb.Exec(sql, time.Now().Format(cfg.TmFmtStr), mac)
	mylog.Log.Infoln("online device, sql:", sql)
	if err != nil {
		mylog.Log.Errorln(err)
		return
	}
	_, err = result.RowsAffected()
	if err != nil {
		mylog.Log.Errorln(err)
		return
	}
}

/********************************************************************
* 分页查询功能
* 通过limit, skip 实现简单分页
* pageNo==1时返回总页数
********************************************************************/
func QueryPage(table string, page *common.PageDao, filter interface{}, sort interface{}, cb func(*sql.Rows)) bool {
	totalPages := int64(0)
	sql := "select SQL_CALC_FOUND_ROWS * from " + table
	if filter != nil && len(filter.(string)) > 0 {
		sql += " where " + filter.(string)
	}
	if sort != nil && len(sort.(string)) > 0 {
		sql += " order by " + sort.(string)
	}
	sql += fmt.Sprintf(" limit %d offset %d", page.PageSize, page.PageNo-1)
	rows, err := mDb.Query(sql)
	if err != nil {
		mylog.Log.Errorln(err)
		return false
	}
	defer rows.Close()
	for rows.Next() {
		cb(rows)
	}
	row := mDb.QueryRow("select FOUND_ROWS()")
	totalCount := int64(0)
	if row != nil {
		row.Scan(&totalCount)
	}
	totalPages = int64(float32(totalCount)/float32(page.PageSize) + float32(0.5))
	page.TotalPages = totalPages
	return true
}

/*
 * func Query, support method for any query
 *
 */
func QueryDao(table string, filter interface{}, sort interface{}, limited interface{}, cb func(*sql.Rows)) bool {
	sql := "select * from " + table
	if filter != nil && len(filter.(string)) > 0 {
		sql += " where " + filter.(string)
	}
	if sort != nil && len(sort.(string)) > 0 {
		sql += " order by " + sort.(string)
	}
	if limited != nil && len(limited.(string)) > 0 {
		sql += " limit " + limited.(string)
	}
	rows, err := mDb.Query(sql)
	if err != nil {
		mylog.Log.Errorln(err)
		return false
	}
	defer rows.Close()
	for rows.Next() {
		cb(rows)
	}
	return true
}

// Find one by ID
func QueryDaoByID(table string, id int64, obj Dao) bool {
	sql := "select * from " + table + " where id=?"
	row := mDb.QueryRow(sql, id)
	err := obj.DecodeFromRow(row)
	if err != nil {
		mylog.Log.Errorln(err)
		return false
	}
	return true
}

func CheckTableExist(tblName string) bool {
	sql := "show tables"
	rows, err := mDb.Query(sql)
	if err != nil {
		mylog.Log.Errorln(err)
		return false
	}
	defer rows.Close()
	var table string
	for rows.Next() {
		err := rows.Scan(&table)
		if err != nil {
			mylog.Log.Errorln(err)
		} else if strings.EqualFold(table, tblName) {
			return true
		}
	}
	return false
}

func CreateTable(sql string) error {
	_, err := mDb.Exec(sql)
	return err
}

/*
* insert...
 */
func InsertDao(tblName string, obj Dao) bool {
	sql := fmt.Sprintf("insert into %s ", tblName)
	u := reflect.TypeOf(obj)
	vf := reflect.ValueOf(obj)
	var fields string
	var values string
	numField := u.Elem().NumField()
	for num := 0; num < numField; num++ {
		f := u.Elem().Field(num)
		v := vf.Elem().Field(num)
		if len(fields) > 0 {
			fields += ","
		}
		if len(values) > 0 {
			values += ","
		}
		if f.Tag.Get("mysql") != "id" {
			fields += f.Tag.Get("mysql")
		}
		switch v.Kind() {
		case reflect.Int64:
			if f.Name != "ID" {
				values += fmt.Sprintf("%d", v.Int())
			}
		case reflect.Int:
			values += fmt.Sprintf("%d", v.Int())
		case reflect.Float64:
			if math.IsNaN(v.Float()) {
				values += "NULL"
			} else {
				values += fmt.Sprintf("%v", v.Float())
			}
		case reflect.String:
			values += "'" + v.String() + "'"
		}
	}
	sql += fmt.Sprintf(" (%s) values (%s)", fields, values)
	result, err := mDb.Exec(sql)
	if err != nil {
		mylog.Log.Errorln(err)
		return false
	}
	id, err := result.LastInsertId()
	if err != nil {
		mylog.Log.Errorln(err)
		return false
	}
	obj.SetID(id)
	return true
}

/*
* updateDaoById...
 */
func UpdateDaoByID(tblName string, id int64, obj Dao) bool {
	sql := fmt.Sprintf("update %s ", tblName)
	u := reflect.TypeOf(obj)
	vf := reflect.ValueOf(obj)
	var setsql string
	numField := u.Elem().NumField()
	for num := 0; num < numField; num++ {
		f := u.Elem().Field(num)
		v := vf.Elem().Field(num)
		var setval string
		if f.Tag.Get("mysql") != "id" {
			setval = fmt.Sprintf(" %s=", f.Tag.Get("mysql"))
		}
		switch v.Kind() {
		case reflect.Int64:
			if f.Name != "ID" {
				setval += fmt.Sprintf("%d", v.Int())
			}
		case reflect.Int:
			setval += fmt.Sprintf("%d", v.Int())
		case reflect.Float64:
			if math.IsNaN(v.Float()) {
				setval += "NULL"
			} else {
				setval += fmt.Sprintf("%v", v.Float())
			}
		case reflect.String:
			setval += "'" + v.String() + "'"
		}
		if len(setsql) > 0 {
			setsql += "," + setval
		} else {
			setsql = setval
		}
	}
	sql += fmt.Sprintf(" set %s where id=%d", setsql, id)
	result, err := mDb.Exec(sql)
	if err != nil {
		mylog.Log.Errorln(err)
		return false
	}
	count, err := result.RowsAffected()
	if err != nil {
		mylog.Log.Errorln(err)
		return false
	}
	mylog.Log.Infoln("Update table:", tblName, ", and affected rows:", count)
	return true
}

/*
* deleteDaoByID...
 */
func DeleteDaoByID(tblName string, id int64) bool {
	sql := fmt.Sprintf("delete from %s where id=%d", tblName, id)
	result, err := mDb.Exec(sql)
	if err != nil {
		mylog.Log.Errorln(err)
		return false
	}
	count, err := result.RowsAffected()
	if err != nil {
		mylog.Log.Errorln(err)
		return false
	}
	mylog.Log.Infoln("Delete table:", tblName, " count:", count)
	return true
}
