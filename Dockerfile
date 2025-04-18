# 使用官方Ubuntu基础镜像
FROM ubuntu:latest

# 安装必要的软件包和Java环境
RUN apt-get update && \
    apt-get install -y openjdk-17-jdk && \
    rm -rf /var/lib/apt/lists/*

# 设置工作目录
WORKDIR /app

# 复制JAR文件到容器中
COPY recognition-server/target/recognition-server-1.0-SNAPSHOT.jar app.jar

# 暴露应用端口（如果需要）
EXPOSE 8888

# 启动应用
CMD ["java", "-jar", "app.jar"]