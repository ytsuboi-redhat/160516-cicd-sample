C:\dev\repository\kddi-stream-env\ansible\devel\README.md
C:\dev\repository\kddi-stream-env\ansible\devel\README.md
## 0. 事前準備
### インベントリファイルの設定
hosts ファイル (インベントリ) 中に Spark, Hadoop (YARN, HDFS) インストール対象サーバのアドレスを設定する。

###### ファイル設定例

```
[servers]
192.168.140.137
[hadoopservers]
192.168.140.137
[sparkservers]
192.168.140.137
[dbservers]
192.168.140.137
```

### Oracle JDK RPM ファイルの配置

以下のリンクから Oracle JDK RPM ファイルを入手し、roles/oraclejdk/install/files ディレクトリに同 rpm ファイルを配置する。

[Java SE Development Kit 8u66 RPM] (http://download.oracle.com/otn-pub/java/jdk/8u66-b17/jdk-8u66-linux-x64.rpm)

### Spark ビルド済みバイナリファイルの配置

以下のリンクから Apache Spark ビルド済みバイナリファイルを入手し、roles/spark/install/files ディレクトリに同 tgz ファイルを配置する。

[Spark 1.5.2 Pre-built for Hadoop 2.6] (http://www.apache.org/dyn/closer.lua/spark/spark-1.5.2/spark-1.5.2-bin-hadoop2.6.tgz)

## 1. 疎通確認

以下のコマンドにより ping/setup を実行する。

__$REMOTE_USERNAME__ には SSH 接続ユーザ名を指定する。

```
$ ansible sparkservers -i hosts -m ping -u $REMOTE_USERNAME -k
SSH password: 
192.168.140.137 | success >> {
    "changed": false, 
    "ping": "pong"
}

$ ansible sparkservers -i hosts -m setup -u yoshikazu -k
SSH password: 
192.168.140.137 | success >> {
    "ansible_facts": {
        "ansible_all_ipv4_addresses": [
            "192.168.140.137"
        ], 
        "ansible_all_ipv6_addresses": [
            "fe80::20c:29ff:fe08:97e6"
        ], 
        "ansible_architecture": "x86_64", 
        "ansible_bios_date": "07/31/2013", 
        "ansible_bios_version": "6.00", 
        "ansible_cmdline": {
            "BOOT_IMAGE": "/vmlinuz-3.10.0-229.7.2.el7.x86_64", 
            "LANG": "en_US.UTF-8", 
            "crashkernel": "auto", 
            "quiet": true, 
            "rd.lvm.lv": "rhel/root", 
            "rhgb": true, 
            "ro": true, 
            "root": "/dev/mapper/rhel-root", 
            "systemd.debug": true
        }, 
...
        "ansible_swapfree_mb": 2047, 
        "ansible_swaptotal_mb": 2047, 
        "ansible_system": "Linux", 
        "ansible_system_vendor": "VMware, Inc.", 
        "ansible_user_dir": "/home/yoshikazu", 
        "ansible_user_gecos": "Yoshikazu YAMADA", 
        "ansible_user_gid": 1000, 
        "ansible_user_id": "yoshikazu", 
        "ansible_user_shell": "/bin/bash", 
        "ansible_user_uid": 1000, 
        "ansible_userspace_architecture": "x86_64", 
        "ansible_userspace_bits": "64", 
        "ansible_virtualization_role": "guest", 
        "ansible_virtualization_type": "VMware", 
        "module_setup": true
    }, 
    "changed": false
}


```

## 2. OracleJDK, CDH, Spark の構築
以下のコマンドにより環境設定を行います。

__$REMOTE_USERNAME__ には SSH 接続ユーザ名を指定する。

```
ansible-playbook site.yml -i hosts -b -u $REMOTE_USERNAME -k --ask-become-pass
```

 - Oracle JDK のインストール (JAVA\_HOME 環境変数設定 含む)
 - Spark のインストール (spark user/group の作成, SPARK\_HOME 環境変数設定 含む)

###### 補足

 - SSH 接続ユーザがパスワード無で sudo 可能な場合 --ask-become-pass オプションは不要
 - SSH 接続 にパスワード不要の場合 -k オプションは不要
 - --private-key オプションを使用して SSH キーを指定
 - --skip-tags オプションを使用するとタグ付けされているタスクのスキップが可能
  - 冪等性の保証をしていないので再実行する場合に必要
  - --skip-tags "hdfs/format,postgresql/install" とすれば HDFS のフォーマットと PostgreSQL のインストールをスキップ

###### 実行例
```  
$ ansible-playbook site.yml -i hosts -b -u yoshikazu -k --ask-become-pass
SSH password: 
SUDO password[defaults to SSH password]: 

PLAY [sparkservers] *********************************************************** 

GATHERING FACTS *************************************************************** 
ok: [192.168.140.137]

TASK: [oraclejdk/install | copy Oracle JDK RPM File] ************************** 
ok: [192.168.140.137]

TASK: [oraclejdk/install | install Oracle JDK] ******************************** 
ok: [192.168.140.137]

TASK: [oraclejdk/install | set JAVA_HOME in /etc/profile.d/java.sh] *********** 
ok: [192.168.140.137]

TASK: [spark/install | create group] ****************************************** 
ok: [192.168.140.137]

TASK: [spark/install | create user] ******************************************* 
ok: [192.168.140.137]

TASK: [spark/install | create Spark Installation Directory] ******************* 
changed: [192.168.140.137]

TASK: [spark/install | unarchive Spark Pre-built Binary File] ***************** 
changed: [192.168.140.137]

TASK: [spark/install | rename Spark Home Directory] *************************** 
changed: [192.168.140.137]

TASK: [spark/install | chown /opt/spark] ************************************** 
changed: [192.168.140.137]

TASK: [spark/install | set SPARK_HOME in /etc/profile.d/spark.sh] ************* 
changed: [192.168.140.137]

PLAY RECAP ******************************************************************** 
192.168.140.137            : ok=11   changed=5    unreachable=0    failed=0   
```

## 3. Hadoop の 起動/停止
### Hadoop の起動
以下のコマンドにより Hadoop の起動を行います。

__$REMOTE_USERNAME__ には SSH 接続ユーザ名を指定する。

```
ansible-playbook service.yml -t "hadoop/start" -i hosts -b -u $REMOTE_USERNAME -k --ask-become-pass
```

###### 捕捉

 - hadoop-hdfs-datanode の起動
 - hadoop-hdfs-namenode の起動
 - hadoop-yarn-nodemanager の起動
 - hadoop-yarn-resourcemanager の起動


###### 実行例
```  
$ ansible-playbook service.yml -t "hadoop/start" -i hosts -b -u yoshikazu -k --ask-become-pass
SSH password: 
SUDO password[defaults to SSH password]: 

PLAY [hadoopservers] ********************************************************** 

GATHERING FACTS *************************************************************** 
ok: [192.168.140.139]

TASK: [hdfs/start | start HDFS DataNode] ************************************** 
changed: [192.168.140.139]

TASK: [hdfs/start | start HDFS NameNode] ************************************** 
changed: [192.168.140.139]

TASK: [hadoop/start | start YARN NodeManager] ********************************* 
changed: [192.168.140.139]

TASK: [hadoop/start | start YARN ResourceManager] ***************************** 
changed: [192.168.140.139]

PLAY RECAP ******************************************************************** 
192.168.140.139            : ok=5    changed=4    unreachable=0    failed=0 
```

### Hadoop の停止

以下のコマンドにより Hadoop の停止を行います。

__$REMOTE_USERNAME__ には SSH 接続ユーザ名を指定する。

```
ansible-playbook service.yml -t "hadoop/stop" -i hosts -b -u $REMOTE_USERNAME -k --ask-become-pass
```

###### 捕捉

 - hadoop-yarn-resourcemanager の停止
 - hadoop-yarn-nodemanager の停止
 - hadoop-hdfs-namenode の停止
 - hadoop-hdfs-datanode の停止

###### 実行例
```  
$ ansible-playbook service.yml -t "hadoop/stop" -i hosts -b -u yoshikazu -k --ask-become-pass
SSH password: 
SUDO password[defaults to SSH password]: 

PLAY [hadoopservers] ********************************************************** 

GATHERING FACTS *************************************************************** 
ok: [192.168.140.139]

TASK: [hadoop/stop | stop YARN ResourceManager] ******************************* 
changed: [192.168.140.139]

TASK: [hadoop/stop | stop YARN NodeManager] *********************************** 
changed: [192.168.140.139]

TASK: [hdfs/stop | stop HDFS NameNode] **************************************** 
changed: [192.168.140.139]

TASK: [hdfs/stop | stop HDFS DataNode] **************************************** 
changed: [192.168.140.139]

PLAY RECAP ******************************************************************** 
192.168.140.139            : ok=5    changed=4    unreachable=0    failed=0   
```

### サンプルの実行

#### core-site.xml の編集
fs.defaultFS の値をHadoopの環境に合わせて変更します。

##### 編集対象ファイル
 - commons-dao/src/main/resources/dev/core-site.xml
 - stream-configuration/src/main/resources/dev/core-site.xml

##### 編集例
【変更前】
```
<configuration>
  <property>
    <name>fs.defaultFS</name>
    <value>hdfs://D2QHM001:9000</value>
  </property>
```
【変更後】
```
<configuration>
  <property>
    <name>fs.defaultFS</name>
    <value>hdfs://192.168.56.101:8020</value>
  </property>
```

#### yarn-site.xml の編集
yarn.resourcemanager.address と yarn.resourcemanager.scheduler.address の値をHadoopの環境に合わせて変更します。

##### 編集対象ファイル
 - commons-dao/src/main/resources/dev/yarn-site.xml

##### 編集例
【変更前】
```
<configuration>

	<property>
		<name>yarn.resourcemanager.address</name>
		<value>192.168.140.139:8032</value>
	</property>
	<property>
		<name>yarn.resourcemanager.scheduler.address</name>
		<value>192.168.140.139:8030</value>
	</property>
```
【変更後】
```
<configuration>

	<property>
		<name>yarn.resourcemanager.address</name>
		<value>192.168.56.101:8032</value>
	</property>
	<property>
		<name>yarn.resourcemanager.scheduler.address</name>
		<value>192.168.56.101:8030</value>
	</property>
```

#### maven でビルド
アプリケーションをビルドします。
 - ユニットテストの実行を省略するため、システムプロパティ maven.test.skip を true に設定します。

```
$ cd /<path>/<to>/<repository>/kddi-stream-dev/stream
$ mvn clean install -Dmaven.test.skip=true
```

#### EAP にデプロイ
ビルドしたアプリケーションの ear ファイルを EAP にデプロイします。
```
$ cp /<path>/<to>/<repository>/kddi-stream-dev/stream/stream/ear/target/stream-1603-PoC-SNAPSHOT-dev.ear /<path>/<to>/<eap>/standalone/deployments/
```

#### EAP を起動
EAP を起動します。
```
$ cd /<path>/<to>/<eap>
$ ./standalone.sh
```

#### Hadoop を起動
Hadoop を起動します。

　詳細な手順は、3. Hadoop の 起動/停止の Hadoop の起動を参照してください。

#### from ファイルの配置
サンプル from ファイルを作成して data.gz として配置します。
 - サンプル from ファイルは Shift_JIS で記述され、gzip圧縮されている必要があります。

```
$ cp /<path>/<to>/<sample>.gz /var/tmp/stream/meisai/data.gz
```

#### マーカファイルの作成
data.gz.deploy を data.gz と同じ場所に作成します。
```
$ touch /var/tmp/stream/meisai/data.gz.deploy
```

#### Chunk Load I/F にリクエスト
EAP にHTTPリクエストを送信します。
```
$ curl http://<eap_host>:8080/stream/rs/chunk/load/JOB_00
```