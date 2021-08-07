#!/usr/bin/env python3
import asyncio
import random
import gitlab
import env
import sys
import requests
import yaml


def gitlabInstance():
    return gitlab.Gitlab(url=env.URL, private_token=env.TOKEN)


async def getVersionFromLog(project, pipeline):
    build_job = None
    for job in pipeline.jobs.list():

        if job.name == 'docker-build':
            build_job = job

    if build_job is None:
        return project.name, None
    while build_job.status != 'success':
        await asyncio.sleep(random.uniform(1, 3))
        print(".", end='', flush=True)
        build_job = project.jobs.get(build_job.id)

    result = requests.get(url=build_job.web_url + '/raw', headers={"PRIVATE-TOKEN": env.TOKEN})
    if result.ok:
        for line in result.text.split('\n'):
            if line.startswith("Successfully tagged"):
                version = line.split(":")[1]
                print(f"\n版本号:{version} {project.name}项目CI已完成", flush=True)
                return project.name, version
    return project.name, None


def has_protect(project, branch):
    pbs = project.protectedbranches.list()
    for p_branch in pbs:
        if p_branch.name == branch:
            return True
    return False


def protect_branch(arg_dicts):
    gl = gitlabInstance()
    branch = arg_dicts.get('branch')
    if branch is None:
        print("--branch is require")
        help_msg()
    project_list = arg_dicts.get('project')
    if len(project_list) <= 0:
        print("--project is require")
        help_msg()
    for project_name in project_list:
        project = gl.projects.get(env.NAMESPACE + "/" + project_name)
        if has_protect(project, branch):
            print(f"OK_:项目 {project_name} {branch} 已经是保护分支")
            continue
        project.protectedbranches.create({
            'name': branch,
            'merge_access_level': gitlab.MAINTAINER_ACCESS,
            'push_access_level': gitlab.MAINTAINER_ACCESS
        })
        print(f"\nOK:项目 {project_name} 设置保护分支 {branch} 成功")


async def fork_join_pipeline(jobs):
    tasks = [getVersionFromLog(project, pipeline) for project, pipeline in jobs]
    results = await asyncio.gather(*tasks)
    print("所有CI任务结束:")
    deploy_yaml = dict(service={project_name: version for project_name, version in results})
    print(yaml.dump(deploy_yaml))
    with open('deploy.yaml', 'w+') as stream:
        yaml.dump(deploy_yaml, stream=stream, explicit_start=True)


def run_ci(arg_dicts):
    gl = gitlabInstance()
    branch = arg_dicts.get('branch')
    if branch is None:
        print("--branch is require")
        help_msg()
    project_list = arg_dicts.get('project')
    if len(project_list) <= 0:
        print("--project is require")
        help_msg()
    jobs = []
    for project_name in project_list:
        project = gl.projects.get(env.NAMESPACE + "/" + project_name)
        try:
            pipeline = project.pipelines.create({'ref': branch, 'variables': env.CI_VARIABLE})
            print(f"OK:触发CI成功,分支: {branch} url: {pipeline.web_url} 项目: {project_name} ")
            if project_name in env.WEB_PROJECTS:
                jobs.append((project, pipeline))
        except Exception as ex:
            print(f"ERROR:项目 {project_name} CI 失败,原因为:{ex}")
    asyncio.run(fork_join_pipeline(jobs))

def help_msg():
    print(env.VIEW)
    sys.exit(0)

def unset_protect(arg_dicts):
    gl = gitlabInstance()
    branch = arg_dicts.get('branch')
    if branch is None:
        print("--branch is require")
        help_msg()
    project_list = arg_dicts.get('project')
    if len(project_list) <= 0:
        print("--project is require")
        help_msg()
    for project_name in project_list:
        project = gl.projects.get(env.NAMESPACE + "/" + project_name)
        if has_protect(project, branch):
            project.protectedbranches.get(branch).delete()
            print(f"OK:项目 {project_name} 取消保护分支 {branch} 成功")
        else:
            print(f"OK_: {project_name} 没有 {branch} 保护分支")

def parse_project(value):
    if  env.GROUPS.get(value) is not None:
        return env.GROUPS.get(value)
    else:
        project_list=[]
        for pro in value.split(','):
            if pro not in env.GROUPS['ALL']:
                print(f"项目 {pro} 不是业务中台项目")
                sys.exit(127)
            project_list.append(pro)
        return project_list

def parse_arg(cliArgs):
    arg_dicts = {}
    for arg in cliArgs:
        param=arg.split('=')
        if param[0] == "--project":
            project_list=parse_project(param[1])
            if len(param) > 0:
                arg_dicts['project'] = project_list
        else:
            arg_dicts[param[0][2:]] = param[1]
    return arg_dicts
def check_env():
    if env.URL is None:
        print("completed gitlab base url from env.py!!!")
    if env.TOKEN is None:
        print("completed gitlab access token from env.py!!!")
    if env.NAMESPACE is None:
        print("completed gitlab namespace from env.py!!!")

if __name__ == '__main__':
    if len(sys.argv) < 3:
        help_msg()
    arg_dicts=parse_arg(sys.argv[2:])
    if sys.argv[1] == 'protect':
        check_env()
        protect_branch(arg_dicts)
    elif sys.argv[1] == 'run-ci':
        check_env()
        run_ci(arg_dicts)
    elif sys.argv[1] == 'unset-protect':
        check_env()
        unset_protect(arg_dicts)
    else:
        help_msg()
