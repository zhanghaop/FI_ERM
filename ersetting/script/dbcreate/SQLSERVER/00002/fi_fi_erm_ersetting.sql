/* indexcode: i_er_jkbx_init_001 */
create  index i_er_jkbx_init_001 on er_jkbx_init (djlxbm,
pk_group,
pk_org)
go

/* indexcode: i_er_jkbx_init_002 */
create  index i_er_jkbx_init_002 on er_jkbx_init (szxmid,
pk_group,
pk_org)
go

/* indexcode: i_er_jsconstras */
create  index i_er_jsconstras on er_jsconstras (pk_bxd,
pk_corp)
go

/* indexcode: i_er_qryobj_001 */
create  index i_er_qryobj_001 on er_qryobj (funnode,
obj_datatype)
go

