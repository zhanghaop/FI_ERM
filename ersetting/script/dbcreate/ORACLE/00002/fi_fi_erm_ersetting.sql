/* indexcode: i_er_jkbx_init_001 */
create  index i_er_jkbx_init_001 on er_jkbx_init (djlxbm asc,
pk_group asc,
pk_org asc)
/

/* indexcode: i_er_jkbx_init_002 */
create  index i_er_jkbx_init_002 on er_jkbx_init (szxmid asc,
pk_group asc,
pk_org asc)
/

/* indexcode: i_er_jsconstras */
create  index i_er_jsconstras on er_jsconstras (pk_bxd asc,
pk_corp asc)
/

/* indexcode: i_er_qryobj_001 */
create  index i_er_qryobj_001 on er_qryobj (funnode asc,
obj_datatype asc)
/

