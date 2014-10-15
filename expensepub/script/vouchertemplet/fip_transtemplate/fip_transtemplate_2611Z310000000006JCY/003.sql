insert into fip_templatecell(pk_templatecell,attrcode,attrname,cellvalue,columnindex,datatype,dr,pk_templaterow,ts) values('2611Z310000000006PJI','pk_factorasoa',null,'$mtapp_detail.pk_checkele@',null,204,0,'2611Z310000000006PJH','2014-01-07 09:10:03')
go

insert into fip_templatecell(pk_templatecell,attrcode,attrname,cellvalue,columnindex,datatype,dr,pk_templaterow,ts) values('2611Z310000000006PJJ','pk_costcenter',null,'$mtapp_detail.pk_resacostcenter@',null,204,0,'2611Z310000000006PJH','2014-01-07 09:10:03')
go

insert into fip_templatecell(pk_templatecell,attrcode,attrname,cellvalue,columnindex,datatype,dr,pk_templaterow,ts) values('2611Z310000000006PJK','pk_currtype',null,'$pk_currtype@',null,204,0,'2611Z310000000006PJH','2014-01-07 09:10:03')
go

insert into fip_templatecell(pk_templatecell,attrcode,attrname,cellvalue,columnindex,datatype,dr,pk_templaterow,ts) values('2611Z310000000006PJL','doriamount',null,'iif(getcurrentbusiunit()==$mtapp_detail.pk_pcorg@,$mtapp_detail.orig_amount@,0)',null,31,0,'2611Z310000000006PJH','2014-01-07 09:10:03')
go

insert into fip_templatecell(pk_templatecell,attrcode,attrname,cellvalue,columnindex,datatype,dr,pk_templaterow,ts) values('2611Z310000000006PJM','orgdebit',null,'iif(getcurrentbusiunit()==$mtapp_detail.pk_pcorg@,$mtapp_detail.org_amount@,0)',null,31,0,'2611Z310000000006PJH','2014-01-07 09:10:03')
go

insert into fip_templatecell(pk_templatecell,attrcode,attrname,cellvalue,columnindex,datatype,dr,pk_templaterow,ts) values('2611Z310000000006PJN','groupdebit',null,'iif(getcurrentbusiunit()==$mtapp_detail.pk_pcorg@,$mtapp_detail.group_amount@,0)',null,31,0,'2611Z310000000006PJH','2014-01-07 09:10:03')
go

insert into fip_templatecell(pk_templatecell,attrcode,attrname,cellvalue,columnindex,datatype,dr,pk_templaterow,ts) values('2611Z310000000006PJO','globaldebit',null,'iif(getcurrentbusiunit()==$mtapp_detail.pk_pcorg@,$mtapp_detail.global_amount@,0)',null,31,0,'2611Z310000000006PJH','2014-01-07 09:10:03')
go

insert into fip_templatecell(pk_templatecell,attrcode,attrname,cellvalue,columnindex,datatype,dr,pk_templaterow,ts) values('2611Z310000000006PJP','vouchertype',null,null,null,204,0,'2611Z310000000006PJG','2014-01-07 09:12:06')
go

insert into fip_templatecell(pk_templatecell,attrcode,attrname,cellvalue,columnindex,datatype,dr,pk_templaterow,ts) values('2611Z310000000006PJQ','makedate',null,'$billdate@',null,33,0,'2611Z310000000006PJG','2014-01-07 09:10:03')
go

insert into fip_templatecell(pk_templatecell,attrcode,attrname,cellvalue,columnindex,datatype,dr,pk_templaterow,ts) values('2611Z310000000006PJR','pk_makeuser',null,'$billmaker@',null,204,0,'2611Z310000000006PJG','2014-01-07 09:10:03')
go

