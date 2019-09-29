## Neo4j


============================

load csv from 'file:///outputtable.csv' as line create (output:OUTPUT{outName:line[0]})

load csv from 'file:///table_lineage20190926.csv' as line match (input:INPUT{inName:line[1]}),(output:OUTPUT{outName:line[0]}) merge (input)-[in2out:IN2OUT]-(output)

============================


查询表被哪些表引用：
MATCH p=(input)-[r:IN2OUT]->(output) where input.inName="provider_api_db.third_request_history" RETURN p

查询表从哪些表加工来的：



在neo4j中，节点在默认情况下是没有类型。
- 1.9版本及以下，采取的策略是给每个节点增加一个type属性
- 2.0版本以后，采用了“标签”的新特性