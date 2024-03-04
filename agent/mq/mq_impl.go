/******************************************************************************
 * Author: liguoqiang
 * Date: 2023-09-06 17:50:12
 * LastEditors: liguoqiang
 * LastEditTime: 2023-12-18 21:55:25
 * Description:
********************************************************************************/
package mq

import (
	"agent/cfg"
	mylog "agent/log"
	"encoding/json"
	"fmt"
	"time"

	mqtt "github.com/eclipse/paho.mqtt.golang"
)

var mqttClient mqtt.Client

var mqConnected bool = false
var topicList []string

var msgHandler mqtt.MessageHandler = func(client mqtt.Client, msg mqtt.Message) {
}

var connectHandler mqtt.OnConnectHandler = func(client mqtt.Client) {
	mylog.Log.Println("mqtt connected")
	mqConnected = true
	for _, topic := range topicList {
		if token := client.Subscribe(topic, 0, nil); token.Wait() && token.Error() != nil {
			mylog.Log.Errorln(token.Error())
		}
	}
}

var connectLostHandler mqtt.ConnectionLostHandler = func(client mqtt.Client, err error) {
	mylog.Log.Println("mqtt disconnected")
	mqConnected = false
}

func InitMqtt() bool {
	opts := mqtt.NewClientOptions()
	opts.AddBroker(fmt.Sprintf("tcp://%s:%d", cfg.This.Mq.Host, cfg.This.Mq.Port))
	opts.SetClientID(cfg.This.Mq.ClientId)
	opts.SetUsername(cfg.This.Mq.Username)
	opts.SetPassword(cfg.This.Mq.Password)
	opts.SetDefaultPublishHandler(msgHandler)
	opts.OnConnect = connectHandler
	opts.OnConnectionLost = connectLostHandler
	opts.SetAutoReconnect(true)
	opts.SetKeepAlive(60 * time.Second)
	opts.SetPingTimeout(30 * time.Second)
	opts.SetCleanSession(true)
	opts.SetMaxReconnectInterval(10 * time.Second)
	opts.SetConnectTimeout(60 * time.Second)
	mqttClient = mqtt.NewClient(opts)
	if token := mqttClient.Connect(); token.Wait() && token.Error() != nil {
		mylog.Log.Errorln(token.Error())
		return false
	}
	return true
}

/******************************************************************************
 * function: Close
 * description: close mqtt client
 * return {*}
********************************************************************************/
func CloseMqtt() {
	if mqttClient != nil {
		mqttClient.Disconnect(250)
	}
}

/******************************************************************************
 * function: SubscribeTopic
 * description: subscribe custom topic, that is not default topic
 * return {*}
********************************************************************************/
func SubscribeTopic(topic string) bool {
	if mqttClient != nil && mqConnected {
		token := mqttClient.Subscribe(topic, 0, nil)
		return token.WaitTimeout(2 * time.Second)
	} else {
		topicList = append(topicList, topic)
	}
	return true
}

/******************************************************************************
 * function: PublicData
 * description:
 * return {*}
********************************************************************************/
func PublicData(topic string, payload interface{}) bool {
	jsBytes, err := json.Marshal(payload)
	if err != nil {
		mylog.Log.Errorln("json marshal failed, err:", err)
		return false
	}
	mylog.Log.Debugln("topic:", topic, ", payload:", string(jsBytes))
	if mqttClient != nil {
		token := mqttClient.Publish(topic, 0, false, jsBytes)
		return token.WaitTimeout(2 * time.Second)
	}
	return false
}
