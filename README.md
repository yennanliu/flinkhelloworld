# FlinkHelloWorld
> Build variois Flink POC apps as well as infra/clusters set up

- IDE : IntelliJ
- Programming language
	- Scala : sbt
	- Java : Maven
	- Python :pip/Conda

## Concepts
- Client : send jobs to clusters (`via CLI or flink UI or JobManager's RPC endpoint`: ExecutionEnvironment ). client <--> JobManager
- JobManager : (`master node`), manage all jobs, and resources allocation. Ask TaskManagers to run jobs. All clusters can only has `1` active JobManager
- TaskManager : (`slave node`), be responsible for actual job execution, and workers resources management. 

- Architecture
<p ><img src ="https://github.com/yennanliu/flinkhelloworld/blob/master/doc/flink_architecture.png"></p>

```
                         <----> ... 
Client <----> JobManager <----> TaskManager & worker
                         <---->  ...
```

- [ref1](https://ci.apache.org/projects/flink/flink-docs-release-1.12/deployment/#per-job-mode)
- [ref2](https://codingnote.cc/zh-hk/p/38108/)

## Install
- https://ci.apache.org/projects/flink/flink-docs-stable/getting-started/tutorials/local_setup.html
- https://github.com/yennanliu/utility_shell/tree/master/flink

## Inspired from 
- https://github.com/phatak-dev/flink-examples
- https://github.com/streaming-with-flink/examples-scala
- https://github.com/apache/flink

## Start the Flink server (Scala)

```bash
# install  (Mac OSX)
# $ brew install apache-flink
# ...
# $ flink --version
# Version: 1.2.0, Commit ID: 1c659cf

# start a local flink cluster
bash script/start-cluster.sh   # Start Flink
bash script/stop-cluster.sh    # Stop flink

# visit UI via 
# http://localhost:8081
```

<details>
<summary>Quick Start</summary>

## Send the stream via CLI
```bash
# send to localhost:9000
nc -l 9000
```

## Quick start (Scala REPL)
```bash
bash script/start-scala-shell.sh local
```

## Quick start (docker)

```bash 
# V1
# https://ci.apache.org/projects/flink/flink-docs-stable/deployment/resource-providers/standalone/docker.html#enabling-python

FLINK_PROPERTIES="jobmanager.rpc.address: jobmanager"
docker network create flink-network

# TaskManager
docker run \
    -d \
    --rm \
    --name=jobmanager \
    --network flink-network \
    --publish 8081:8081 \
    --env FLINK_PROPERTIES="${FLINK_PROPERTIES}" \
    flink:1.12.0-scala_2.11 jobmanager

# TaskManager
docker run \
    -d \
    --rm \
    --name=taskmanager \
    --network flink-network \
    --env FLINK_PROPERTIES="${FLINK_PROPERTIES}" \
    flink:1.12.0-scala_2.11 taskmanager

# web UI : localhost:8080

# run some jobs

# batch
flink run examples/batch/ConnectedComponents.jar 
flink run examples/batch/EnumTriangles.jar 
flink run examples/batch/PageRank.jar 
flink run examples/batch/WebLogAnalysis.jar 
flink run examples/batch/DistCp.jar 
flink run examples/batch/KMeans.jar 
flink run examples/batch/TransitiveClosure.jar 
flink run examples/batch/WordCount.jar 
flink run examples/batch/ConnectedComponents.jar

# stream
flink run examples/streaming/WordCount.jar
flink run examples/streaming/SessionWindowing.jar
flink run examples/streaming/StateMachineExample.jar
flink run examples/streaming/Iteration.jar
flink run examples/streaming/SessionWindowing.jar
flink run examples/streaming/TopSpeedWindowing.jar
```

```bash
# V2
# pull the dokcer image
docker pull flink

# Method 1) run a JobManager (master)
docker run --name flink_jobmanager -d -t flink jobmanager
docker run -it flink bash
flink run examples/batch/WordCount.jar
flink run examples/batch/KMeans.jar 
flink run examples/streaming/SocketWindowWordCount.jar  --port 9000

# Method 2) run a TaskManager (worker). 
# Notice that workers need to register with the JobManager directly or via ZooKeeper so the master starts to send them tasks to execute.
docker run --name flink_taskmanager -d -t flink taskmanager

# Method 3) Running a cluster using Docker Compose
docker-compose up
```

```bash
# V3 
git clone https://github.com/yennanliu/flinkhelloworld.git
cd flinkhelloworld
docker-compose -f  docker-compose-dev.yml up --build -d

# should start a jobmanager, taskmanager
```

</details>

## Ref 

<details>
<summary>Ref</summary>

- Start Flink with SBT Scala
	- https://ci.apache.org/projects/flink/flink-docs-master/dev/project-configuration.html

- Flink Scala
	- https://ci.apache.org/projects/flink/flink-docs-release-1.10/dev/projectsetup/scala_api_quickstart.html

- Flink train
	- https://training.ververica.com/

- Flink example
	- https://ci.apache.org/projects/flink/flink-docs-release-1.10/getting-started/examples/
	- https://www.elastic.co/blog/building-real-time-dashboard-applications-with-apache-flink-elasticsearch-and-kibana?fbclid=IwAR0EzGMB-P_gazMyG2yG4GgmTjwxwz_aXE4vpbV51nY29e55jcMqezp_pvw

- Flink load json
	- https://flink.sojb.cn/dev/table/connect.html#json-format
	- https://flink-docs-cn.gitbook.io/project/05-ying-yong-kai-fa/04-table-api-and-sql/lian-jie-wai-bu-xi-tong
	- Example
		- https://gousios.gr/courses/bigdata/2017/assignment-streaming.html
		- https://gousios.org/courses/bigdata/2017/assignment-streaming-solutions.pdf

- json4s intro	
	- https://www.cnblogs.com/yyy-blog/p/11819302.html
	- https://blog.csdn.net/leehbing/article/details/74391308
	- https://code5.cn/so/scala/1794442

- Import Scala into an IDE
	- https://ci.apache.org/projects/flink/flink-docs-stable/flinkDev/ide_setup.html

</details>

### Infra Ref (Docker, k8s)

<details>
<summary>Ref</summary>

- Flink with docker
	- https://flink.apache.org/news/2020/08/20/flink-docker.html
	- https://ci.apache.org/projects/flink/flink-docs-stable/ops/deployment/docker.html

- Flink with K8S
	- https://ci.apache.org/projects/flink/flink-docs-stable/ops/deployment/kubernetes.html

</details>

## Dockerfile
- https://hub.docker.com/_/flink?tab=description
