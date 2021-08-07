# gitlab base url 地址
URL = None
# gitlab namespace
NAMESPACE = None
# gitlab access token
TOKEN = None
GROUPS = {
    # 填写你想CI的所有项目 并可在下面新增自定义分组 ALL 不能为空
    'ALL':['project1','project2','project3'],
    'FRAMEWORK':['f1','f2']
}
# ci 默认参数
CI_VARIABLE = [{'key': 'BUILD_ARCH', 'value': 'false'}, {'key': 'BUILD_SUPPORT', 'value': 'false'}]
# 展示视图
VIEW = '''  
        _  _    _         _     
  __ _ (_)| |_ | |  __ _ | |__  
 / _` || || __|| | / _` || '_ \ 
| (_| || || |_ | || (_| || |_) |
 \__, ||_| \__||_| \__,_||_.__/ 
 |___/       
Usage:
    gitlab.py [command] [option]
Available Commands:
    protect               set protect branch for project 
    run-ci                trigger ci for project
    unset-protect         unset protect branch for project 
Available Option:
    --branch    branch name
    --env       environment name
    --project   project list split with ","
    --file      deploy yaml file
    ...
'''

