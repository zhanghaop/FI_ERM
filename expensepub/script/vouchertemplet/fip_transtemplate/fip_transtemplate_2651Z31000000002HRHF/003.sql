insert into fip_templatecell(pk_templatecell,attrcode,attrname,cellvalue,columnindex,datatype,dr,pk_templaterow,ts) values('1001Z310000000023RO7','excrate2',null,'$bbhl@',null,31,0,'1001Z310000000023RO0','2014-04-29 09:52:01')
go

insert into fip_templatecell(pk_templatecell,attrcode,attrname,cellvalue,columnindex,datatype,dr,pk_templaterow,ts) values('1001Z310000000023RO5','pk_currtype',null,'$bzbm@',null,204,0,'1001Z310000000023RO0','2014-04-29 09:52:01')
go

insert into fip_templatecell(pk_templatecell,attrcode,attrname,cellvalue,columnindex,datatype,dr,pk_templaterow,ts) values('1001Z310000000023RO9','excrate4',null,'$globalbbhl@',null,31,0,'1001Z310000000023RO0','2014-04-29 09:52:01')
go

insert into fip_templatecell(pk_templatecell,attrcode,attrname,cellvalue,columnindex,datatype,dr,pk_templaterow,ts) values('1001Z310000000023RO8','excrate3',null,'$groupbbhl@',null,31,0,'1001Z310000000023RO0','2014-04-29 09:52:01')
go

insert into fip_templatecell(pk_templatecell,attrcode,attrname,cellvalue,columnindex,datatype,dr,pk_templaterow,ts) values('1001Z310000000023RO6','explanation',null,'@zy@',null,1,0,'1001Z310000000023RO0','2014-04-29 09:52:01')
go

insert into fip_templatecell(pk_templatecell,attrcode,attrname,cellvalue,columnindex,datatype,dr,pk_templaterow,ts) values('1001Z310000000023ROA','debitamount',null,'iif(getcurrentbusiunit()==#assume_org@,#assume_amount@,0)',null,31,0,'1001Z310000000023RO0','2014-04-29 09:52:01')
go

insert into fip_templatecell(pk_templatecell,attrcode,attrname,cellvalue,columnindex,datatype,dr,pk_templaterow,ts) values('1001Z310000000023ROB','localdebitamount',null,'iif(getcurrentbusiunit()==#assume_org@,#bbje@,0)',null,31,0,'1001Z310000000023RO0','2014-04-29 09:52:01')
go

insert into fip_templatecell(pk_templatecell,attrcode,attrname,cellvalue,columnindex,datatype,dr,pk_templaterow,ts) values('1001Z310000000023ROD','globaldebitamount',null,'iif(getcurrentbusiunit()==#assume_org@,#globalbbje@,0)',null,31,0,'1001Z310000000023RO0','2014-04-29 09:52:01')
go

insert into fip_templatecell(pk_templatecell,attrcode,attrname,cellvalue,columnindex,datatype,dr,pk_templaterow,ts) values('1001Z310000000023ROC','groupdebitamount',null,'iif(getcurrentbusiunit()==#assume_org@,#groupbbje@,0)',null,31,0,'1001Z310000000023RO0','2014-04-29 09:52:01')
go

insert into fip_templatecell(pk_templatecell,attrcode,attrname,cellvalue,columnindex,datatype,dr,pk_templaterow,ts) values('1001Z310000000023RO4','pk_accasoa',null,'matchview("ERM03","费用科目","0001Z31000000000HIG7")',null,204,0,'1001Z310000000023RO0','2014-04-29 09:52:01')
go

insert into fip_templatecell(pk_templatecell,attrcode,attrname,cellvalue,columnindex,datatype,dr,pk_templaterow,ts) values('1001Z310000000023ROH','excrate2',null,'$bbhl@',null,31,0,'1001Z310000000023RO1','2014-04-29 09:52:01')
go

insert into fip_templatecell(pk_templatecell,attrcode,attrname,cellvalue,columnindex,datatype,dr,pk_templaterow,ts) values('1001Z310000000023ROF','pk_currtype',null,'$bzbm@',null,204,0,'1001Z310000000023RO1','2014-04-29 09:52:01')
go

insert into fip_templatecell(pk_templatecell,attrcode,attrname,cellvalue,columnindex,datatype,dr,pk_templaterow,ts) values('1001Z310000000023ROJ','excrate4',null,'$globalbbhl@',null,31,0,'1001Z310000000023RO1','2014-04-29 09:52:01')
go

insert into fip_templatecell(pk_templatecell,attrcode,attrname,cellvalue,columnindex,datatype,dr,pk_templaterow,ts) values('1001Z310000000023ROI','excrate3',null,'$groupbbhl@',null,31,0,'1001Z310000000023RO1','2014-04-29 09:52:01')
go

insert into fip_templatecell(pk_templatecell,attrcode,attrname,cellvalue,columnindex,datatype,dr,pk_templaterow,ts) values('1001Z310000000023ROG','explanation',null,'@zy@',null,1,0,'1001Z310000000023RO1','2014-04-29 09:52:01')
go

insert into fip_templatecell(pk_templatecell,attrcode,attrname,cellvalue,columnindex,datatype,dr,pk_templaterow,ts) values('1001Z310000000023ROK','creditamount',null,'iif(getcurrentbusiunit()==#assume_org@,iif(getcurrentbusiunit()==@fydwbm@,0,#assume_amount@),0)',null,31,0,'1001Z310000000023RO1','2014-04-29 09:52:01')
go

insert into fip_templatecell(pk_templatecell,attrcode,attrname,cellvalue,columnindex,datatype,dr,pk_templaterow,ts) values('1001Z310000000023ROL','localcreditamount',null,'iif(getcurrentbusiunit()==#assume_org@,iif(getcurrentbusiunit()==@fydwbm@,0,#bbje@),0)',null,31,0,'1001Z310000000023RO1','2014-04-29 09:52:01')
go

insert into fip_templatecell(pk_templatecell,attrcode,attrname,cellvalue,columnindex,datatype,dr,pk_templaterow,ts) values('1001Z310000000023RON','globalcreditamount',null,'iif(getcurrentbusiunit()==#assume_org@,iif(getcurrentbusiunit()==@fydwbm@,0,#globalbbje@),0)',null,31,0,'1001Z310000000023RO1','2014-04-29 09:52:01')
go

insert into fip_templatecell(pk_templatecell,attrcode,attrname,cellvalue,columnindex,datatype,dr,pk_templaterow,ts) values('1001Z310000000023ROM','groupcreditamount',null,'iif(getcurrentbusiunit()==#assume_org@,iif(getcurrentbusiunit()==@fydwbm@,0,#groupbbje@),0)',null,31,0,'1001Z310000000023RO1','2014-04-29 09:52:01')
go

insert into fip_templatecell(pk_templatecell,attrcode,attrname,cellvalue,columnindex,datatype,dr,pk_templaterow,ts) values('1001Z310000000023ROE','pk_accasoa',null,'matchview("ERM04","其他应付款","1001Z31000000000DUQB")',null,204,0,'1001Z310000000023RO1','2014-04-29 09:52:01')
go

insert into fip_templatecell(pk_templatecell,attrcode,attrname,cellvalue,columnindex,datatype,dr,pk_templaterow,ts) values('1001Z310000000023ROR','excrate2',null,'$bbhl@',null,31,0,'1001Z310000000023RO2','2014-04-29 09:52:01')
go

insert into fip_templatecell(pk_templatecell,attrcode,attrname,cellvalue,columnindex,datatype,dr,pk_templaterow,ts) values('1001Z310000000023ROP','pk_currtype',null,'$bzbm@',null,204,0,'1001Z310000000023RO2','2014-04-29 09:52:01')
go

insert into fip_templatecell(pk_templatecell,attrcode,attrname,cellvalue,columnindex,datatype,dr,pk_templaterow,ts) values('1001Z310000000023ROT','excrate4',null,'$globalbbhl@',null,31,0,'1001Z310000000023RO2','2014-04-29 09:52:01')
go

insert into fip_templatecell(pk_templatecell,attrcode,attrname,cellvalue,columnindex,datatype,dr,pk_templaterow,ts) values('1001Z310000000023ROS','excrate3',null,'$groupbbhl@',null,31,0,'1001Z310000000023RO2','2014-04-29 09:52:01')
go

insert into fip_templatecell(pk_templatecell,attrcode,attrname,cellvalue,columnindex,datatype,dr,pk_templaterow,ts) values('1001Z310000000023ROQ','explanation',null,'@zy@',null,1,0,'1001Z310000000023RO2','2014-04-29 09:52:01')
go

insert into fip_templatecell(pk_templatecell,attrcode,attrname,cellvalue,columnindex,datatype,dr,pk_templaterow,ts) values('1001Z310000000023ROU','debitamount',null,'iif(getcurrentbusiunit()==@fydwbm@,iif(getcurrentbusiunit()==#assume_org@,0,#assume_amount@),0)',null,31,0,'1001Z310000000023RO2','2014-04-29 09:52:01')
go

insert into fip_templatecell(pk_templatecell,attrcode,attrname,cellvalue,columnindex,datatype,dr,pk_templaterow,ts) values('1001Z310000000023ROV','localdebitamount',null,'iif(getcurrentbusiunit()==@fydwbm@,iif(getcurrentbusiunit()==#assume_org@,0,#bbje@),0)',null,31,0,'1001Z310000000023RO2','2014-04-29 09:52:01')
go

insert into fip_templatecell(pk_templatecell,attrcode,attrname,cellvalue,columnindex,datatype,dr,pk_templaterow,ts) values('1001Z310000000023ROX','globaldebitamount',null,'iif(getcurrentbusiunit()==@fydwbm@,iif(getcurrentbusiunit()==#assume_org@,0,#globalbbje@),0)',null,31,0,'1001Z310000000023RO2','2014-04-29 09:52:01')
go

insert into fip_templatecell(pk_templatecell,attrcode,attrname,cellvalue,columnindex,datatype,dr,pk_templaterow,ts) values('1001Z310000000023ROW','groupdebitamount',null,'iif(getcurrentbusiunit()==@fydwbm@,iif(getcurrentbusiunit()==#assume_org@,0,#groupbbje@),0)',null,31,0,'1001Z310000000023RO2','2014-04-29 09:52:01')
go

insert into fip_templatecell(pk_templatecell,attrcode,attrname,cellvalue,columnindex,datatype,dr,pk_templaterow,ts) values('1001Z310000000023ROO','pk_accasoa',null,'matchview("ERM01","其他应收款","0001Z31000000000HIG9")',null,204,0,'1001Z310000000023RO2','2014-04-29 09:52:01')
go

insert into fip_templatecell(pk_templatecell,attrcode,attrname,cellvalue,columnindex,datatype,dr,pk_templaterow,ts) values('1001Z310000000023RP1','excrate2',null,'$bbhl@',null,31,0,'1001Z310000000023RO3','2014-04-29 09:52:01')
go

insert into fip_templatecell(pk_templatecell,attrcode,attrname,cellvalue,columnindex,datatype,dr,pk_templaterow,ts) values('1001Z310000000023ROZ','pk_currtype',null,'$bzbm@',null,204,0,'1001Z310000000023RO3','2014-04-29 09:52:01')
go

insert into fip_templatecell(pk_templatecell,attrcode,attrname,cellvalue,columnindex,datatype,dr,pk_templaterow,ts) values('1001Z310000000023RP3','excrate4',null,'$globalbbhl@',null,31,0,'1001Z310000000023RO3','2014-04-29 09:52:01')
go

insert into fip_templatecell(pk_templatecell,attrcode,attrname,cellvalue,columnindex,datatype,dr,pk_templaterow,ts) values('1001Z310000000023RP2','excrate3',null,'$groupbbhl@',null,31,0,'1001Z310000000023RO3','2014-04-29 09:52:01')
go

insert into fip_templatecell(pk_templatecell,attrcode,attrname,cellvalue,columnindex,datatype,dr,pk_templaterow,ts) values('1001Z310000000023RP0','explanation',null,'@zy@',null,1,0,'1001Z310000000023RO3','2014-04-29 09:52:01')
go

insert into fip_templatecell(pk_templatecell,attrcode,attrname,cellvalue,columnindex,datatype,dr,pk_templaterow,ts) values('1001Z310000000023RP4','creditamount',null,'iif(getcurrentbusiunit()==@fydwbm@,#assume_amount@,0)',null,31,0,'1001Z310000000023RO3','2014-04-29 09:52:01')
go

insert into fip_templatecell(pk_templatecell,attrcode,attrname,cellvalue,columnindex,datatype,dr,pk_templaterow,ts) values('1001Z310000000023RP5','localcreditamount',null,'iif(getcurrentbusiunit()==@fydwbm@,#bbje@,0)',null,31,0,'1001Z310000000023RO3','2014-04-29 09:52:01')
go

insert into fip_templatecell(pk_templatecell,attrcode,attrname,cellvalue,columnindex,datatype,dr,pk_templaterow,ts) values('1001Z310000000023RP7','globalcreditamount',null,'iif(getcurrentbusiunit()==@fydwbm@,#globalbbje@,0)',null,31,0,'1001Z310000000023RO3','2014-04-29 09:52:01')
go

insert into fip_templatecell(pk_templatecell,attrcode,attrname,cellvalue,columnindex,datatype,dr,pk_templaterow,ts) values('1001Z310000000023RP6','groupcreditamount',null,'iif(getcurrentbusiunit()==@fydwbm@,#groupbbje@,0)',null,31,0,'1001Z310000000023RO3','2014-04-29 09:52:01')
go

insert into fip_templatecell(pk_templatecell,attrcode,attrname,cellvalue,columnindex,datatype,dr,pk_templaterow,ts) values('1001Z310000000023ROY','pk_accasoa',null,'matchview("ERM05","费用结转","1001Z31000000000DUQA")',null,204,0,'1001Z310000000023RO3','2014-04-29 09:52:01')
go

