/* indexcode: I_ERM_JK_ORG */
create  index I_ERM_JK_ORG on er_jkzb (pk_org,
djrq)
go

/* indexcode: I_ERM_JK_JKBXR */
create  index I_ERM_JK_JKBXR on er_jkzb (djrq,
jkbxr)
go

/* indexcode: I_ERM_JK_OPERATOR */
create  index I_ERM_JK_OPERATOR on er_jkzb (djrq,
operator)
go

/* indexcode: I_ERM_JK_PK_ITEM */
create  index I_ERM_JK_PK_ITEM on er_jkzb (pk_item)
go

/* indexcode: I_ERM_BXC_BXD */
create  index I_ERM_BXC_BXD on er_bxcontrast (pk_bxd)
go

/* indexcode: I_ERM_BXC_JKD */
create  index I_ERM_BXC_JKD on er_bxcontrast (pk_jkd)
go

/* indexcode: I_ERM_BSITEM_P */
create  index I_ERM_BSITEM_P on er_busitem (pk_jkbx)
go

/* indexcode: I_ERM_BX_ORG */
create  index I_ERM_BX_ORG on er_bxzb (pk_org,
djrq)
go

/* indexcode: I_ERM_BX_JKBXR */
create  index I_ERM_BX_JKBXR on er_bxzb (djrq,
jkbxr)
go

/* indexcode: I_ERM_BX_OPERATOR */
create  index I_ERM_BX_OPERATOR on er_bxzb (djrq,
operator)
go

/* indexcode: I_ERM_BX_PK_ITEM */
create  index I_ERM_BX_PK_ITEM on er_bxzb (pk_item)
go

/* indexcode: I_ERM_INIT_ORG */
create  index I_ERM_INIT_ORG on er_init (pk_org)
go

/* indexcode: I_ERM_INIT_ORG2 */
create  index I_ERM_INIT_ORG2 on er_init (pk_org,
close_status)
go

