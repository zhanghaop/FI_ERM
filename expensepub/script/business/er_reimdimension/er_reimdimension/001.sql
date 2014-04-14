insert into er_reimdimension(pk_reimdimension,pk_billtype,displayname,datatype,datatypename,orders,correspondingitem,referential,billref,billrefcode,pk_org,pk_group,ts,dr,showflag,controlflag,beanname) values('1001Z31000000001YPIY','2631','费用类型','592c6233-8fec-4374-bf50-1939f785569e','费用类型',1,'PK_EXPENSETYPE',null,null,null,'~','GLOBLE00000000000000','2014-02-25 15:41:27',0,'Y','Y','ExpenseType')
go

insert into er_reimdimension(pk_reimdimension,pk_billtype,displayname,datatype,datatypename,orders,correspondingitem,referential,billref,billrefcode,pk_org,pk_group,ts,dr,showflag,controlflag,beanname) values('1001Z31000000001YPIZ','2631','报销类型','b2caa3e7-d4c3-4439-9ea7-84ff4b1cc83b','报销类型',2,'PK_REIMTYPE',null,'借款单业务行.报销类型标识','jk_busitem.pk_reimtype','~','GLOBLE00000000000000','2014-02-25 15:41:27',0,'Y','N','ReimType')
go

insert into er_reimdimension(pk_reimdimension,pk_billtype,displayname,datatype,datatypename,orders,correspondingitem,referential,billref,billrefcode,pk_org,pk_group,ts,dr,showflag,controlflag,beanname) values('1001Z31000000001YPJ0','2631','部门','b26fa3cb-4087-4027-a3b6-c83ab2a086a9','部门',3,'PK_DEPTID','部门','原借款人部门','deptid','~','GLOBLE00000000000000','2014-02-25 15:41:27',0,'N','N','dept')
go

insert into er_reimdimension(pk_reimdimension,pk_billtype,displayname,datatype,datatypename,orders,correspondingitem,referential,billref,billrefcode,pk_org,pk_group,ts,dr,showflag,controlflag,beanname) values('1001Z31000000001YPJ1','2631','职位','BS000010000100001001',null,4,'PK_POSITION',null,null,null,'~','GLOBLE00000000000000','2014-02-25 15:41:27',0,'N','N',null)
go

insert into er_reimdimension(pk_reimdimension,pk_billtype,displayname,datatype,datatypename,orders,correspondingitem,referential,billref,billrefcode,pk_org,pk_group,ts,dr,showflag,controlflag,beanname) values('1001Z31000000001YPJ2','2631','币种','b498bc9a-e5fd-4613-8da8-bdae2a05704a','币种档案',5,'PK_CURRTYPE',null,'币种','bzbm','~','GLOBLE00000000000000','2014-02-25 15:41:27',0,'N','N',null)
go

insert into er_reimdimension(pk_reimdimension,pk_billtype,displayname,datatype,datatypename,orders,correspondingitem,referential,billref,billrefcode,pk_org,pk_group,ts,dr,showflag,controlflag,beanname) values('1001Z31000000001YPJ3','2631','金额','BS000010000100001052','2',6,'AMOUNT',null,null,null,'~','GLOBLE00000000000000','2014-02-25 15:41:27',0,'Y','N',null)
go

insert into er_reimdimension(pk_reimdimension,pk_billtype,displayname,datatype,datatypename,orders,correspondingitem,referential,billref,billrefcode,pk_org,pk_group,ts,dr,showflag,controlflag,beanname) values('1001Z31000000001YPJ4','2631','备注','BS000010000100001001',null,7,'MEMO',null,null,null,'~','GLOBLE00000000000000','2014-02-25 15:41:27',0,'N','N',null)
go

