/*
 * @Author: liguoqiang
 * @Date: 2024-03-03 19:56:06
 * @LastEditors: liguoqiang
 * @LastEditTime: 2024-03-03 21:32:07
 * @Description: 
 */
SET CGO_ENABLED=0
SET GOOS=linux
SET GOARCH=amd64
rem SET GOARCH=arm
rem SET GOARM=7
SET GIN_MODE=debug
go build -o boxmgr main.go