/******************************************************************************
 * Author: liguoqiang
 * Date: 2024-02-29 10:33:09
 * LastEditors: liguoqiang
 * LastEditTime: 2024-02-29 10:35:57
 * Description:
********************************************************************************/
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
	"net/http"

	"agent/exception"

	"github.com/gin-gonic/gin"
)

/**
 * @description: handle heart rate data reported by remote device
 * @return {*}
 */
func reportHeartRateData(c *gin.Context) {
	exception.TryEx{
		Try: func() {
			status := http.StatusOK
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
func ed719Alarm(c *gin.Context) {
	exception.TryEx{
		Try: func() {
			status := http.StatusOK
			respInt(c, status)
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
func ed719GetPushSwitch(c *gin.Context) {
	exception.TryEx{
		Try: func() {
			status := http.StatusOK
			respInt(c, status)
		},
		Catch: func(e exception.Exception) {
			respInt(c, -2)
		},
	}.Run()
}

/**
 * @description: fall check device report fall data
 * @return {*}
 */
func ed719PushStatus(c *gin.Context) {
	exception.TryEx{
		Try: func() {
			status := http.StatusOK
			respInt(c, status)
		},
		Catch: func(e exception.Exception) {
			respFall(c, -2)
		},
	}.Run()
}
