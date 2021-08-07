   
```
        _  _    _         _     
  __ _ (_)| |_ | |  __ _ | |__  
 / _` || || __|| | / _` || '_ \ 
| (_| || || |_ | || (_| || |_) |
 \__, ||_| \__||_| \__,_||_.__/ 
 |___/                      
```
## 准备
1. 确定python 版本 3.7 以上
2. 执行安装依赖
```bash
pip3 install -r requirements.txt -i  
```
3. 填写env.py

* gitlab 地址格式为:http://gitlab.xx.com 不要带/ 
* access token 参照 https://docs.gitlab.com/ee/user/profile/personal_access_tokens.html#create-a-personal-access-token
* namespace 一般为项目和base url 之间的部分
## 执行 
```bash
./gitlabctl run-ci --project=ALL --branch=master

OK:触发CI成功,分支: sit url: http://gitlab.***.cn/pipelines/593364 项目: ouser-web 
OK:触发CI成功,分支: sit url: http://gitlab.***.cn/pipelines/593365 项目: oms-task 
OK:触发CI成功,分支: sit url: http://gitlab.***.cn/pipelines/593366 项目: oms-dataex 
.............................................................................................................................................................................
版本号:593364-2021.8.7-135841-sit ouser-web项目CI已完成
.
版本号:593365-2021.8.7-135846-sit oms-task项目CI已完成
..................
版本号:593366-2021.8.7-135926-sit oms-dataex项目CI已完成
所有CI任务结束:
service:
  oms-dataex: 593366-2021.8.7-135926-sit
  oms-task: 593365-2021.8.7-135846-sit
  ouser-web: 593364-2021.8.7-135841-sit
```

