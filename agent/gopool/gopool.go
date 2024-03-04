/*
 * @Author: liguoqiang
 * @Date: 2023-04-17 16:32:55
 * @LastEditors: liguoqiang
 * @LastEditTime: 2023-09-25 16:35:34
 * @Description:
 */
package gopool

import (
	"agent/exception"
	mylog "agent/log"
	"errors"
	"sync"
	"sync/atomic"
	"time"
)

const (
	STOPED       = 0
	RUNNING      = 1
	MAX_CAPACITY = 65535
)

type Task struct {
	Params []interface{}
	Do     func(v ...interface{})
}

type Pool struct {
	capacity      uint16        // 池的容量
	runningNumber atomic.Uint32 // 运行数量
	status        uint          // 池的状态
	tasks         chan *Task    // 通道队列
	sync.Mutex
	DefaultDoFunc func(v ...interface{})
}

/*
* 初始化并产生一个pool全局对象
 */
func InitPool(cap uint16) (*Pool, error) {
	if cap <= 0 || cap >= MAX_CAPACITY {
		return nil, errors.New("invalid capacity number")
	}
	return &Pool{
		capacity:      cap,
		runningNumber: atomic.Uint32{},
		status:        RUNNING,
		tasks:         make(chan *Task, cap),
	}, nil
}
func (p *Pool) incRunNumber() {
	p.runningNumber.Add(1)
}
func (p *Pool) decRunNumber() {
	p.runningNumber.Add(^uint32(0))
}
func (p *Pool) GetRunNumber() uint32 {
	return p.runningNumber.Load()
}

/*
* 实现Pool的内部函数, 用run操作, 通过队列获取任务，并执行
 */
func (p *Pool) run() {
	p.incRunNumber()
	go func() {
		exception.TryEx{
			Try: func() {
				for {
					select {
					case task := <-p.tasks:
						if task != nil {
							if task.Do == nil {
								p.DefaultDoFunc(task.Params...)
							} else {
								task.Do(task.Params...)
							}
						}
					case <-time.After(1 * time.Second):
						return
					}
				}
			},
			Catch: func(e exception.Exception) {
				mylog.Log.Errorln(e)
			},
			Finally: func() {
				p.decRunNumber()
			},
		}.Run()
	}()
}

/*
* 实现put操作，put操作是公开的，用于第三方调用者向队列中添加任务
 */
func (p *Pool) Put(task *Task) error {
	p.Lock()
	defer p.Unlock()
	// 如果状态已经是close了，就不要再执行直接返回
	if p.status == STOPED {
		return errors.New("put failed, pool already closed")
	}
	// 如果池中run数量没达到最大值，就运行一个run
	// 否则就没必要再执行多余的run了
	if p.GetRunNumber() < uint32(p.capacity) {
		p.run()
	}
	// 放入队列等待消费，如果超时就返回错误
	select {
	case p.tasks <- task:
		return nil
	case <-time.After(1 * time.Second):
		return errors.New("put failed, timeout")
	}
}

/*
* 设置状态
 */
func (p *Pool) SetStatus(status uint) {
	p.Lock()
	defer p.Unlock()
	p.status = status
}

/*
* 实现 Pool 的 Close 方法
 */
func (p *Pool) Close() {
	p.SetStatus(STOPED)
	// 如果还有任务则等待 2 秒
	if len(p.tasks) > 0 {
		time.Sleep(2 * time.Second)
	}
	close(p.tasks)
}
