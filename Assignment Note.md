## Search Engine - Phase 1

Lucene

- Field: asin, reviewText, summary

  | Field / Attribute | Analysed | Indexed | Stored |
  | ----------------- | -------- | ------- | ------ |
  | asin              | N        | Y       | Y      |
  | reviewText        | Y        | Y       | Y/N    |
  | summary           | Y        | Y       | Y      |

  all stored in Lucene or get ID and then search? stored in lucene is OK since it's not big.

- Analyser: choose analyser used in Lucene, stemming, case, stopwords

- Index time:  end_time - start_time is OK

- Query:  单关键字、词组、asin-based 查询

  query language rule



#### Index

参数：index_storage_path, src_document_path 

- 配置 Analyzer 和 IndexWriter
  - anlayser 控制分词处理操作：大小写，停用词，分词方式（取决于可能的查询方式）
  - indexWriter:  通过 indexWriterConfig 控制

- 导入数据并创建为 document 对象
  - 配置 Field 

#### Search

- QueryParser
  - Analyser: 解析 query语句内容，只能解析符合 Lucene 查询语法的内容
  - 也可以通过程序手动实现一些复杂查询

- IndexSearcher



### 配置 Lucene 

- 环境：Mac OSX + IntelliJ IDEA + Maven + Lucene9.3.0

- Download bin version 放到本地目录下

- 添加 CLASSPATH

  当前用户目录下 /Users/xxx，修改 vi .bash_profile 文件

  ```sh
  export LUCENE_PATH=/Users/seanking/Apache/apache-lucene-9.3.0/modules
  export CLASSPATH=$CLASSPATH:$LUCENE_PATH/lucene-demo-9.3.0.jar:$LUCENE_PATH/lucene-core-9.3.0.jar:$LUCENE_PATH/lucene-analysis-common-9.3.0.jar:$LUCENE_PATH/lucene-queryparser-9.3.0.jar
  ```

  **Note:**  

  - CLASSPATH 和 PATH 一样，都是属于当前用户的环境变量，terminal enter 'echo $PATH' 查看，CLASSPATH 用于 Java 指令执行时的文件路径搜索

  - .bash_profile 中保存一些常用环境变量记录文件路径，.zshrc 中保存一些常用指令操作记录可执行文件路径，在 .zshrc 中添加 source ~/.bash_profile 可以获取 .bash_profile 中的路径
  - 当前用户目录为 /Users/username，系统用户目录为 /usr/local

- 尝试 command line 执行 lucene-demo，参见官网

- IDEA maven 添加依赖

  ```sh
  mvn install:install-file 
    -Dfile=/Users/seanking/Apache/apache-lucene-9.3.0/modules/lucene-queryparser-9.3.0.jar 
    -DgroupId=org.apache.lucene 
    -DartifactId=lucene-queryparser 
    -Dversion=9.3.0 
    -Dpackaging=jar
   
  # groupId means the field.company.project name, artifactId means module name
  ```
  
  **Note:**
  
  - maven build 的 Java project 直接通过 IDEA project structure 添加 dependency 会在 maven reload 时失效，可以通过上述 maven 指令添加本地 jar 包依赖，然后直接在 pom.xml 引入即可

- searchIndex

  ```java
  Exception in thread "main" org.apache.lucene.index.IndexNotFoundException: no segments* file found in MMapDirectory
  // 报错原因：通常是由于创建索引时 indexWriter 未 .close() 正常关闭导致索引创建失败
  ```

- Luke 使用

  ```sh
  Type "/path/to/lucene-9.3.0/bin/luke.{sh|cmd}" in terminal 
  ```

- GitHub 本地项目创建 Repository 至 remote

  - IDEA 中 git->create git repository
  
  - 远端创建对应 repository
- commit, push 关联至远端分支，通过 generated  token 验证连接




## Search Engine - Phase 2

**Detailed Design for the Project**

- 选择停用词列表和 Analyser 语言学处理
  - 通过 StandardAnalyser 实现，这是最复杂的 Analyser，能处理的姓名，电子邮件地址等，它小写每个标记，并删除常用词和标点符号
  - stop_word_list: 可能需要自己定义，导入通过 Lucene 源码中的类实现
    - stopwords in NLP
    - 使用 Luke 查看数据集并将前 1000 个定义为 stopwords
- 选择建索引的 field
  - 由于缺少数据库支持，Document 对象中的 Field 应该与查询所需要显示的内容一致。
  - TopN Document Objects:  rank, **scores**, docID, and **snippets** **???**
- 根据数据集中需要索引的 Document 数量，**记录每添加 10% Document 用时** **???**
  - 在创建 Document 对象时统计实现
- 程序交互方式：cmdLine-based vs. main function，input & output
  - 大概率实现为命令行形式，需要对命令行参数进行解析以及交互式输入处理
  - 实现后考虑把项目打包成 jar 包应用
- 查询方式实现：单关键字、词组、asin-based 查询（暂不考虑实现多种方式混合查询）
  - 查询语法：
    - asin-based: 通过指定参数关键词 -asin
    - phrase query:  通过 PhraseQuery Class 实现
    - 其他可能出现的搜索操作：multiField search, fuzzy search, ...

