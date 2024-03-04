/******************************************************************************
 * Author: liguoqiang
 * Date: 2023-12-04 09:36:43
 * LastEditors: liguoqiang
 * LastEditTime: 2023-12-18 21:34:41
 * Description:
********************************************************************************/
package log

import (
	"agent/cfg"
	"fmt"
	"time"

	rotatelogs "github.com/lestrrat-go/file-rotatelogs"
	"github.com/rifflock/lfshook"
	"github.com/sirupsen/logrus"
)

var (
	Log = logrus.New()
)

func Init() {
	if cfg.This.Log.Format == "text" {
		Log.SetFormatter(&logrus.TextFormatter{})
	} else if cfg.This.Log.Format == "json" {
		Log.SetFormatter(&logrus.JSONFormatter{})
	} else {
		Log.SetFormatter(&logrus.TextFormatter{})
	}
	Log.SetReportCaller(true)
	var logLevel logrus.Level
	err := logLevel.UnmarshalText([]byte(cfg.This.Log.Level))
	if err != nil {
		Log.SetLevel(logrus.DebugLevel)
	} else {
		Log.SetLevel(logLevel)
	}
	hook := lfshook.NewHook(lfshook.WriterMap{
		logrus.DebugLevel: LogWriter(cfg.This.Log.File, "debug", cfg.This.Log.Maxbackups),
		logrus.InfoLevel:  LogWriter(cfg.This.Log.File, "info", cfg.This.Log.Maxbackups),
		logrus.WarnLevel:  LogWriter(cfg.This.Log.File, "warn", cfg.This.Log.Maxbackups),
		logrus.ErrorLevel: LogWriter(cfg.This.Log.File, "error", cfg.This.Log.Maxbackups),
		logrus.FatalLevel: LogWriter(cfg.This.Log.File, "fatal", cfg.This.Log.Maxbackups),
		logrus.PanicLevel: LogWriter(cfg.This.Log.File, "panic", cfg.This.Log.Maxbackups),
	}, &logrus.TextFormatter{DisableColors: true, TimestampFormat: "2006-01-02 15:04:05"})
	Log.AddHook(hook)
}

func Close() {
	Log.Writer().Close()
}

func LogWriter(logFile string, level string, backups int) *rotatelogs.RotateLogs {
	writter, err := rotatelogs.New(
		logFile+".%Y%m%d",
		rotatelogs.WithLinkName(logFile),
		rotatelogs.WithRotationCount(uint(backups)),
		rotatelogs.WithRotationTime(time.Duration(24)*time.Hour),
	)
	if err != nil {
		fmt.Printf("config local file system logger error. %v", err)
	}
	return writter
}
