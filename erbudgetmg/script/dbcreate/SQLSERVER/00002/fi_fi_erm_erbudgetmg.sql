/* indexcode: I_ERM_PF_MADETAIL */
create  index I_ERM_PF_MADETAIL on er_mtapp_pf (pk_mtapp_detail)
go

/* indexcode: I_ERM_PF_BUSIPK */
create  index I_ERM_PF_BUSIPK on er_mtapp_pf (busi_pk)
go

/* indexcode: I_ERM_MTDETAIL_ORG_DATE */
create  index I_ERM_MTDETAIL_ORG_DATE on er_mtapp_detail (pk_org,
billdate)
go

/* indexcode: I_ERM_MTDETAIL_PK_MTAPP_BILL */
create  index I_ERM_MTDETAIL_PK_MTAPP_BILL on er_mtapp_detail (pk_mtapp_bill)
go

/* indexcode: I_ERM_MA_ORG */
create  index I_ERM_MA_ORG on er_mtapp_bill (pk_org,
billdate)
go

