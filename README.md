# MonitorToast2

一个用来监听Toast信息的工具，可用于UI自动化测试中。

使用方法：

1. 安装
2. 打开APP后点击唯一的那一个按钮
3. 跳转到设置页面后开启**Toast监听服务**
4. adb中发送监听广播

如下为在接下来的20000毫秒内监听内容为`再按一次退出程序！`的Toast信息：

    adb shell am broadcast -a param --es time_block 20000 --es filter_str 再按一次退出程序！

如果检测到则会以一个弹框的形式将包名和Toast全文展示出来，以供各类自动化测试框架监测
