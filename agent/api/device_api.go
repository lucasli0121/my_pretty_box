/*
 * @Author: liguoqiang
 * @Date: 2023-09-22 22:12:53
 * @LastEditors: liguoqiang
 * @LastEditTime: 2023-09-26 12:09:21
 * @Description:
 */
/*
 * @Author: liguoqiang
 * @Date: 2023-09-22 22:12:53
 * @LastEditors: liguoqiang
 * @LastEditTime: 2023-09-24 15:27:21
 * @Description:
 */
/*********************************************************************
*
**********************************************************************/

package api

import (
	"time"

	"agent/exception"
	"agent/mdb"

	"github.com/gin-gonic/gin"
)

/**
 * @description: handle heart rate data reported by remote device
 * @return {*}
 */
func ed719HeartBeat(c *gin.Context) {
	exception.TryEx{
		Try: func() {
			status := mdb.InsertHeartRate(c)
			respInt(c, status)
		},
		Catch: func(e exception.Exception) {
			respInt(c, -2)
		},
	}.Run()
}

/**
 * @description: report ota information
 * @return {*}
 */
func reportOtaInfo(c *gin.Context) {
	exception.TryEx{
		Try: func() {
			status, result := mdb.InsertOtaInfo(c)
			respJSON(c, status, result)
		},
		Catch: func(e exception.Exception) {
			respJSON(c, e.Code, e.Msg)
		},
	}.Run()
}

/**
 * @description: get server time format is timestemp
 * @return {*}
 */
func getServerTime(c *gin.Context) {
	exception.TryEx{
		Try: func() {
			respInt64(c, time.Now().Unix())
		},
		Catch: func(e exception.Exception) {
			respInt64(c, time.Now().Unix())
		},
	}.Run()
}

/**
 * @description: fall check device report fall data
 * @return {*}
 */
func postHeart(c *gin.Context) {
	exception.TryEx{
		Try: func() {
			status := mdb.InsertFallHeart(c)
			respFall(c, status)
		},
		Catch: func(e exception.Exception) {
			respFall(c, -2)
		},
	}.Run()
}

func postEvent(c *gin.Context) {
	exception.TryEx{
		Try: func() {
			status := mdb.InsertEvent(c)
			respInt(c, status)
		},
		Catch: func(e exception.Exception) {
			respInt(c, -2)
		},
	}.Run()
}

/******************************************************************************
 * function: getED713Ota
 * description: query ed713 ota version information
 * param {*gin.Context} c
 * return {*}
********************************************************************************/

// getED713Ota godoc
//
//	@Summary	getED713Ota
//	@Schemes
//	@Description	get ED713 ota version information
//	@Tags			ED713
//	@Produce		json
//
//	@Param			in	body	mdb.Ed713OtqReq	 true	"OTA request"
//
//	@Success		200			{object}	mysql.Ed713OtaData
//	@Router			/v1/device/getED713Ota [post]
func getED713Ota(c *gin.Context) {
	exception.TryEx{
		Try: func() {
			status, result := mdb.GetED713Ota(c)
			respJSON(c, status, result)
		},
		Catch: func(e exception.Exception) {
			respJSON(c, e.Code, e.Msg)
		},
	}.Run()
}
