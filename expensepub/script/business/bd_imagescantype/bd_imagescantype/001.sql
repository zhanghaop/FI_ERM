insert into bd_imagescantype(pk_id,code,dr,name,orgfiltersql,pk_billtypecode,pk_parent,ts) values('1001Z0100000000VTPDD','01',0,'集团管控',null,null,null,null)
go

insert into bd_imagescantype(pk_id,code,dr,name,orgfiltersql,pk_billtypecode,pk_parent,ts) values('1001Z3100000000V2611','01010101',0,'费用申请单','select pk_financeorg from org_financeorg','2611','1001Z3100000000V261X',null)
go

insert into bd_imagescantype(pk_id,code,dr,name,orgfiltersql,pk_billtypecode,pk_parent,ts) values('1001Z3100000000V261X','010101',0,'费用申请单','select pk_financeorg from org_financeorg','261X','1001Z3100000000VTPDE',null)
go

insert into bd_imagescantype(pk_id,code,dr,name,orgfiltersql,pk_billtypecode,pk_parent,ts) values('1001Z3100000000V2621','01010201',0,'费用预提单','select pk_financeorg from org_financeorg','2621','1001Z3100000000V262X',null)
go

insert into bd_imagescantype(pk_id,code,dr,name,orgfiltersql,pk_billtypecode,pk_parent,ts) values('1001Z3100000000V262X','010102',0,'主预提单','select pk_financeorg from org_financeorg','262X','1001Z3100000000VTPDE',null)
go

insert into bd_imagescantype(pk_id,code,dr,name,orgfiltersql,pk_billtypecode,pk_parent,ts) values('1001Z3100000000V2631','01010301',0,'差旅费借款单','select pk_financeorg from org_financeorg','2631','1001Z3100000000V263X',null)
go

insert into bd_imagescantype(pk_id,code,dr,name,orgfiltersql,pk_billtypecode,pk_parent,ts) values('1001Z3100000000V2632','01010302',0,'会议费借款单','select pk_financeorg from org_financeorg','2632','1001Z3100000000V263X',null)
go

insert into bd_imagescantype(pk_id,code,dr,name,orgfiltersql,pk_billtypecode,pk_parent,ts) values('1001Z3100000000V263X','010103',0,'主借款单','select pk_financeorg from org_financeorg','263X','1001Z3100000000VTPDE',null)
go

insert into bd_imagescantype(pk_id,code,dr,name,orgfiltersql,pk_billtypecode,pk_parent,ts) values('1001Z3100000000V2641','01010401',0,'差旅费报销单','select pk_financeorg from org_financeorg','2641','1001Z3100000000V264X',null)
go

insert into bd_imagescantype(pk_id,code,dr,name,orgfiltersql,pk_billtypecode,pk_parent,ts) values('1001Z3100000000V2642','01010402',0,'交通费报销单','select pk_financeorg from org_financeorg','2642','1001Z3100000000V264X',null)
go

insert into bd_imagescantype(pk_id,code,dr,name,orgfiltersql,pk_billtypecode,pk_parent,ts) values('1001Z3100000000V2643','01010403',0,'通讯费报销单','select pk_financeorg from org_financeorg','2643','1001Z3100000000V264X',null)
go

insert into bd_imagescantype(pk_id,code,dr,name,orgfiltersql,pk_billtypecode,pk_parent,ts) values('1001Z3100000000V2644','01010404',0,'礼品费报销单','select pk_financeorg from org_financeorg','2644','1001Z3100000000V264X',null)
go

insert into bd_imagescantype(pk_id,code,dr,name,orgfiltersql,pk_billtypecode,pk_parent,ts) values('1001Z3100000000V2645','01010405',0,'招待费报销单','select pk_financeorg from org_financeorg','2645','1001Z3100000000V264X',null)
go

insert into bd_imagescantype(pk_id,code,dr,name,orgfiltersql,pk_billtypecode,pk_parent,ts) values('1001Z3100000000V2646','01010406',0,'会议费报销单','select pk_financeorg from org_financeorg','2646','1001Z3100000000V264X',null)
go

insert into bd_imagescantype(pk_id,code,dr,name,orgfiltersql,pk_billtypecode,pk_parent,ts) values('1001Z3100000000V2647','01010407',0,'还款单','select pk_financeorg from org_financeorg','2647','1001Z3100000000V264X',null)
go

insert into bd_imagescantype(pk_id,code,dr,name,orgfiltersql,pk_billtypecode,pk_parent,ts) values('1001Z3100000000V264A','01010408',0,'费用调整单','select pk_financeorg from org_financeorg','264a','1001Z3100000000V264X',null)
go

insert into bd_imagescantype(pk_id,code,dr,name,orgfiltersql,pk_billtypecode,pk_parent,ts) values('1001Z3100000000V264X','010104',0,'主报销单','select pk_financeorg from org_financeorg','264X','1001Z3100000000VTPDE',null)
go

insert into bd_imagescantype(pk_id,code,dr,name,orgfiltersql,pk_billtypecode,pk_parent,ts) values('1001Z3100000000VTPDE','0101',0,'费用管理',null,null,'1001Z0100000000VTPDD',null)
go

