/* indexcode: I_ERM_CSD_SRC */
create  index I_ERM_CSD_SRC on er_cshare_detail (src_type asc,
src_id asc)
;

/* indexcode: I_ERM_CS_ORG */
create  index I_ERM_CS_ORG on er_costshare (pk_org asc,
billdate asc)
;

