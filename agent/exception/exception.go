/*
 * @Author: liguoqiang
 * @Date: 2022-06-02 08:39:11
 * @LastEditors: liguoqiang
 * @LastEditTime: 2022-06-02 14:27:59
 * @Description:
 */
package exception

import (
	"fmt"
)

type Exception struct {
	Code int
	Msg  string
}

func (me *Exception) Error() string {
	return fmt.Sprintf("%d:%s", me.Code, me.Msg)
}

type TryEx struct {
	Try     func()
	Catch   func(Exception)
	Finally func()
}

func (me TryEx) Run() {
	if me.Finally != nil {
		defer me.Finally()
	}
	if me.Catch != nil {
		defer func() {
			if e := recover(); e != nil {
				me.Catch(e.(Exception))
			}
		}()
	}
	me.Try()
}

func Throw(code int, msg string) {
	panic(Exception{code, msg})
}
