/* indexcode: I_ERM_JK_ORG */
create  index I_ERM_JK_ORG on er_jkzb (pk_org asc,
djrq asc)
;

/* indexcode: I_ERM_JK_JKBXR */
create  index I_ERM_JK_JKBXR on er_jkzb (djrq asc,
jkbxr asc)
;

/* indexcode: I_ERM_JK_OPERATOR */
create  index I_ERM_JK_OPERATOR on er_jkzb (djrq asc,
operator asc)
;

/* indexcode: I_ERM_JK_PK_ITEM */
create  index I_ERM_JK_PK_ITEM on er_jkzb (pk_item asc)
;

/* indexcode: I_ERM_BXC_BXD */
create  index I_ERM_BXC_BXD on er_bxcontrast (pk_bxd asc)
;

/* indexcode: I_ERM_BXC_JKD */
create  index I_ERM_BXC_JKD on er_bxcontrast (pk_jkd asc)
;

/* indexcode: I_ERM_BSITEM_P */
create  index I_ERM_BSITEM_P on er_busitem (pk_jkbx asc)
;

/* indexcode: I_ERM_BX_ORG */
create  index I_ERM_BX_ORG on er_bxzb (pk_org asc,
djrq asc)
;

/* indexcode: I_ERM_BX_JKBXR */
create  index I_ERM_BX_JKBXR on er_bxzb (djrq asc,
jkbxr asc)
;

/* indexcode: I_ERM_BX_OPERATOR */
create  index I_ERM_BX_OPERATOR on er_bxzb (djrq asc,
operator asc)
;

/* indexcode: I_ERM_BX_PK_ITEM */
create  index I_ERM_BX_PK_ITEM on er_bxzb (pk_item asc)
;

/* indexcode: I_ERM_INIT_ORG */
create  index I_ERM_INIT_ORG on er_init (pk_org asc)
;

/* indexcode: I_ERM_INIT_ORG2 */
create  index I_ERM_INIT_ORG2 on er_init (pk_org asc,
close_status asc)
;

