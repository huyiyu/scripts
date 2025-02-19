# 使用JAVA 实现 tail 命令方案
> 使用 graaalvm + native 模式编写tail,实现
```
tail

Display the last part of a file.
See also: `head`.
More information: <https://manned.org/man/freebsd-13.0/tail.1>.

- Show last 'count' lines in file:
    tail -n count path/to/file


- Print the last lines of a given file and keep reading file until `Ctrl + C`:
    tail -f path/to/file
```

## 快速启动方案
1. 安装 graalvm 21作为jdk  https://www.graalvm.org/downloads/#
2. 使用gradlew 编译 `./gradlew nativeCompile`
3. 找到编译好的文件 在 `build/native/nativeCompile/` 目录下 
4. 尝试tail README.md 
```bash
+ build/native/nativeCompile/tail -n 3 README.md

3. 找到编译好的文件 在 `build/native/nativeCompile/` 目录下 
4. 尝试tail README.md
```