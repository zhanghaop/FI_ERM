/* indexcode: I_ERM_EXB_MD5 */
create  index I_ERM_EXB_MD5 on er_expensebal (md5 asc)
/

/* indexcode: i_ERM_EXB_ORG_DATE */
create  index i_ERM_EXB_ORG_DATE on er_expensebal (billdate asc,
pk_org asc)
/

/* indexcode: I_ERM_EXP_ORG_DATE */
create  index I_ERM_EXP_ORG_DATE on er_expenseaccount (billdate asc,
pk_org asc)
/

/* indexcode: I_ERM_EXP_SRCID */
create  index I_ERM_EXP_SRCID on er_expenseaccount (src_id asc)
/

