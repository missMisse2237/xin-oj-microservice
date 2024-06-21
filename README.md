# xin-oj-backend-microservice

## 整体划分
将单体项目进行拆分
部署是打算分为环境依赖和服务依赖，服务依赖例如redis， mysql， nacos什么的，打包一个，服务模块再打包一个，不过没实现，可惜！
### 1.公共部分
- xinoj-backend-common: 项目公共部分

- xinoj-backend-model: 各种VO DTO POJO等等

### 2.服务模块
- xinoj-backend-user-service: 用户模块

- xinoj-backend-judge-service: 判题模块

- xinoj-backend-question-service: 题目模块（将单体项目中的题目提交模块[提交给代码沙箱]，题目模块[给前端对接的题目模块]

- xinoj-backend-gatway: SCG，对各类服务接口进行聚合和路由。具体实现模仿了尚医通的写法，使用AntPathMatcher。



## 具体细节
- 使用Ali云原生脚手架初始化微服务项目，并结合 Maven 子父模块的配置，保证了微服务各模块依赖的版本一致性，避免依赖冲突。

- 使用 Knife4j Gateway 在网关层实现了对各服务 Swagger 接口文档的统一聚合。

- 在做题页面，基于Webpack整合了Monaco Editor 代码编辑器组件，可以根据不同的语言实现不同的高亮显示。

- 在调用代码沙箱时，使用了策略模式、代理模式、工厂模式：
  - 工厂模式：通过传入的CodeSandboxEnum（枚举类）返回对象，借鉴了双检锁模式，不过因为是多个对象，通过Map缓存。
  - 代理模式：采用了静态代理，代理的目的是输出一下日志。
  - 策略模式：
    - 我们可以采用策略模式，针对不同的情况，定义独立的策略，便于分别修改策略和维护。而不是把所有的判题逻辑、if ... else ... 代码全部混在一起写。
    - 例如定义题目信息的时候限制了做题时间信息，而cpp天然的速度就会比java快一些，所以可以通过策略模式判断如果是java 就时间上宽松百分之20；等等



### 一些BUG的总结
- 在尝试部署的时候，虽然服务器内存不够没部署成功，但是在打包的时候要在Maven父模块中把如下代码注销掉，不然打包的时候子jar包没有主类！
```yaml
<configuration>
  <mainClass>com.xin.xinojbackendmicroservice.XinojBackendMicroserviceApplication</mainClass>
  <skip>true</skip>
</configuration>
```
- 在使用RabbitMQ时，特别卡，一开始以为是电脑内存不够，后来去了15672发现是RabbitMQ是问题，不要设置为失败直接进去队列。
```java
@SneakyThrows
    @RabbitListener(queues = {"code_queue"}, ackMode = "MANUAL")
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("receiveMessage message = {}", message);
        long questionSubmitId = Long.parseLong(message);
        try {
            judgeService.doJudge(questionSubmitId);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            channel.basicNack(deliveryTag, false, false);// 设置为false
        }
    }
```

- 在使用Knife4j 在中间接中的使用时，访问网站一直文档无法访问。
原因找了好久，原因是中间穿插使用了单体项目，导致redis污染，记得每次启动前flush redis

-  以后在使用通过 Nacos + OpenFeign 实现各模块之间的相互调用时，一定一定要复制保证一模一样！后来前后端联调时别的功能都没问题，就题目显示有问题，调试Judge都进不去服务，原来是path对不上。

- 使用MybatiPlus分页配置时，要注意不能放在common模块，一开始放在conmmon模块，倒是judgeService的bean不对，不知道为什么debug时Mybatis的bean，可能被覆盖了？还是冲突？
- 别的都记不清了 呜呜

### 启动准备
- 安装redis
```shell
redis-server redis.windows.conf
```
- 安装Nacos
```shell
startup.cmd -m standalone
```
- 安装RabbitMQ

### 端口
| Name                           | Port       | Version |
|--------------------------------|------------|---------|
| MySQL                          | 3306       | 8       |
| Redis                          | 6379       | 3.2.1   |
| RabbitMQ                       | 5672,15672 | 3.12.6  |
| nacos                          | 8848       | 2.2.0   |
| SCG                            | 8101       | jdk17   |
| xinoj-backend-user-service     | 8102       | jdk17   |
| xinoj-backend-question-service | 8103       | jdk17   |
| xinoj-backend-judge-service    | 8104       | jdk17   |


