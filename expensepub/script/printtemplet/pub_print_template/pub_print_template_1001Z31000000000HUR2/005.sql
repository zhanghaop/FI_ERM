insert into pub_print_variable(cvariableid,ctemplateid,dr,iformulatype,pk_corp,prepare1,prepare2,prepare3,prepare4,ts,vaimfieldname,vbasetablename,vdatakeyword,vexpress,vtablepkname,vvariablename) values('1001Z310000000009PGS','1001Z31000000000HUR2',0,1,'0001',null,null,null,null,'2013-09-23 13:40:17',null,null,null,'iif(dateformat(er_busitem.defitem2 , "yyyy-MM-dd HH:mm:ss")=="","",left(dateformat(er_busitem.defitem2 , "yyyy-MM-dd HH:mm:ss") ,10))',null,'到达日期')
go

insert into pub_print_variable(cvariableid,ctemplateid,dr,iformulatype,pk_corp,prepare1,prepare2,prepare3,prepare4,ts,vaimfieldname,vbasetablename,vdatakeyword,vexpress,vtablepkname,vvariablename) values('1001Z310000000009PGT','1001Z31000000000HUR2',0,1,'0001',null,null,null,null,'2013-09-23 13:40:17',null,null,null,'iif(dateformat(日期 , "yyyy-MM-dd HH:mm:ss")=="","",left(dateformat(日期 , "yyyy-MM-dd HH:mm:ss") ,10))',null,'打印日期')
go

insert into pub_print_variable(cvariableid,ctemplateid,dr,iformulatype,pk_corp,prepare1,prepare2,prepare3,prepare4,ts,vaimfieldname,vbasetablename,vdatakeyword,vexpress,vtablepkname,vvariablename) values('1001Z310000000009PGU','1001Z31000000000HUR2',0,1,'0001',null,null,null,null,'2013-09-23 13:40:17',null,null,null,'iif(dateformat(er_busitem.defitem1 , "yyyy-MM-dd HH:mm:ss")=="","",left(dateformat(er_busitem.defitem1 , "yyyy-MM-dd HH:mm:ss") ,10))',null,'出发日期')
go

insert into pub_print_variable(cvariableid,ctemplateid,dr,iformulatype,pk_corp,prepare1,prepare2,prepare3,prepare4,ts,vaimfieldname,vbasetablename,vdatakeyword,vexpress,vtablepkname,vvariablename) values('1001Z310000000009PGV','1001Z31000000000HUR2',0,1,'0001',null,null,null,null,'2013-09-23 13:40:17',null,null,null,'getcolvalue(md_enumvalue,name, value,er_busitem.defitem5 )',null,'交通工具')
go

