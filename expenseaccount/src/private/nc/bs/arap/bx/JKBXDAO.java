package nc.bs.arap.bx;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.naming.NamingException;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.er.pub.IRSChecker;
import nc.bs.er.pub.PubDAO;
import nc.bs.er.pub.PubMethods;
import nc.bs.er.settle.ErForCmpBO;
import nc.bs.er.util.SqlUtils;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.pub.pf.CheckStatusCallbackContext;
import nc.bs.pub.pf.ICheckStatusCallback;
import nc.itf.org.IDeptQryService;
import nc.itf.org.IOrgConst;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.pubitf.org.IOrgUnitPubService;
import nc.pubitf.rbac.IDataPermissionPubService;
import nc.pubitf.rbac.IRolePubService;
import nc.uap.rbac.core.dataperm.DataPermConfig;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.arap.bx.util.BXUtil;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BXHeaderVO;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.JKVO;
import nc.vo.ep.bx.JsConstrasVO;
import nc.vo.ep.bx.VOFactory;
import nc.vo.ep.dj.DjCondVO;
import nc.vo.er.settle.SettleUtil;
import nc.vo.er.util.StringUtils;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.erm.mapping.Er_jkbx_initVOMeta;
import nc.vo.erm.mapping.Er_jsconstrasVOMeta;
import nc.vo.fipub.billcode.FinanceBillCodeInfo;
import nc.vo.fipub.billcode.FinanceBillCodeUtils;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.fipub.mapping.IArapMappingMeta;
import nc.vo.org.DeptVO;
import nc.vo.org.OrgManagerVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.pf.IPfRetCheckInfo;
import nc.vo.uap.rbac.role.RoleVO;

/**
 * @author twei
 * 
 *         nc.bs.ep.bx.BXZbDMO
 * 
 *         �������ݶ�ȡ����
 */
public class JKBXDAO extends BXSuperDAO implements ICheckStatusCallback {

	public String getPkField() {
		return new BXHeaderVO().getPKFieldName();
	}

	public JKBXDAO() throws NamingException {
		super();
	}

	private void getBillNo(JKBXVO bxvo) throws BusinessException {
		JKBXHeaderVO parent = bxvo.getParentVO();
		FinanceBillCodeInfo info = new FinanceBillCodeInfo(JKBXHeaderVO.DJDL, JKBXHeaderVO.DJBH, JKBXHeaderVO.PK_GROUP,
				JKBXHeaderVO.PK_ORG, parent.getTableName());
		FinanceBillCodeUtils util = new FinanceBillCodeUtils(info);
		util.createBillCode(new AggregatedValueObject[] { bxvo });
	}

	public static FinanceBillCodeUtils getBillCodeUtil(JKBXHeaderVO parent) {
		FinanceBillCodeUtils util = null;

		FinanceBillCodeInfo info = new FinanceBillCodeInfo(JKBXHeaderVO.DJLXBM, JKBXHeaderVO.DJBH, JKBXHeaderVO.PK_GROUP,
				JKBXHeaderVO.PK_ORG, parent.getTableName());

		util = new FinanceBillCodeUtils(info);
		return util;
	}

	public JKBXVO[] save(JKBXVO[] vos) throws SQLException, BusinessException {

		List<BxcontrastVO> contrasts = new ArrayList<BxcontrastVO>();
		List<JKBXHeaderVO> parentList = new ArrayList<JKBXHeaderVO>(vos.length);
		for (int i = 0; i < vos.length; i++) {

			JKBXVO bxvo = vos[i];
			BXBusItemVO[] items = bxvo.getBxBusItemVOS();
			JKBXHeaderVO parentVO = bxvo.getParentVO();
			parentList.add(parentVO);
			
			if (!parentVO.isInit()) {
				// ���ɵ��ݺ�
				getBillNo(bxvo);
				String billcode = bxvo.getParentVO().getDjbh();
				if (billcode.length() > BXConstans.BILLCODE_LENGTH) {
					String errMsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0",
							"02011v61013-0024", null,
							new String[] { String.valueOf(billcode.length()), String.valueOf(BXConstans.BILLCODE_LENGTH) })/*
																															 * @res
																															 * "���ݺų���[i]���ܴ������ݿⶨ�����󳤶�[i]�����޸ĵ��ݺŹ���"
																															 */;
					ExceptionHandler.handleException(getClass(), new BusinessException(errMsg));
				}
			}
			
//			getMdDaoService().saveBill(parentVO); //���õ�ԭ�򣺳��õ���û��Ԫ���ݣ������ַ�ʽ���治�ϳ��õ���
			if(parentVO.getPk_jkbx() != null){
				baseDao.insertVOWithPK(parentVO );
			}else{
				baseDao.insertVO(parentVO );
			}
			
			String bxpk = parentVO.getPk_jkbx();

			if (items != null) {
				for (int j = 0; j < items.length; j++) {
					items[j].setPk_jkbx(bxpk);
					items[j].setRowno(j + 1);
					// ����ҵ�������Զ�������Ϣ
					dealBusitemDefitem(items[j]);
				}
			}

			// ��������������MAP
			Map<BXBusItemVO, String> cmpMap = new HashMap<BXBusItemVO, String>();
			if (items != null) {
				for (BXBusItemVO item : items) {
					cmpMap.put(item, item.getPrimaryKey() == null ? "" : item.getPrimaryKey());
				}
				for (int j = 0; j < items.length; j++) {
					items[j].setPk_jkbx(bxpk);
					items[j].setPk_busitem(null);
				}
			}
			if(items != null && items.length >0){
				baseDao.insertVOArray(items);
			}

			// ��ʱ����ʵ�����Ķ���
			Map<String, String> idMap = new HashMap<String, String>();
			if (items != null) {
				for (BXBusItemVO item : items) {
					idMap.put(cmpMap.get(item).length() == 0 ? item.getPrimaryKey() : cmpMap.get(item), item.getPrimaryKey());
				}
			} else {
				idMap.put(BXConstans.TEMP_ZB_PK, bxpk);
			}

			// ���������Ϣ
			if (bxvo.getContrastVO() != null && bxvo.getContrastVO().length != 0) {
				List<BxcontrastVO> newContrasts = ErContrastUtil.dealContrastForNew(parentVO, bxvo.getContrastVO(), items);
				bxvo.setContrastVO(newContrasts.toArray(new BxcontrastVO[] {}));
				contrasts.addAll(newContrasts);
			}

			//������ʱ����ʵ�����Ķ���
			bxvo.setCmpIdMap(idMap);

		}

		new ContrastBO().saveContrast(contrasts, null);
		// ����������Ԥ����ϸ����
		new BxVerifyAccruedBillBO().saveVerifyVOs(vos);

		addTsToBXVOs(parentList.toArray(new JKBXHeaderVO[parentList.size()]));

		return vos;
	}

	private void dealBusitemDefitem(BXBusItemVO busItemVO) {
		String[] attributeNames = busItemVO.getAttributeNames();
		for (String attr : attributeNames) {
			if (attr.indexOf("defitem") == 0) {
				if (busItemVO.getAttributeValue(attr) != null && busItemVO.getAttributeValue(attr) instanceof UFDouble) {
					busItemVO.setAttributeValue(attr, busItemVO.getAttributeValue(attr).toString());
				}
			}
		}
	}
	
	public void delete(JKBXHeaderVO[] vos) throws BusinessException {
//		getMdDaoService().deleteBillFromDB(vos);
		baseDao.deleteVOArray(vos);
	}

	public JKBXVO[] update(JKBXVO[] vos) throws SQLException, BusinessException {

		List<BxcontrastVO> contrasts = new ArrayList<BxcontrastVO>();

		Vector<JKBXHeaderVO> headVos = new Vector<JKBXHeaderVO>();

		Vector<JKBXHeaderVO> bxdvos = new Vector<JKBXHeaderVO>();

		List<JKBXHeaderVO> voList = new ArrayList<JKBXHeaderVO>();
		
		for (int i = 0; i < vos.length; i++) {

			JKBXVO bxvo = vos[i];
			BXBusItemVO[] childrenVO = bxvo.getBxBusItemVOS();
			JKBXVO bxoldvo = bxvo.getBxoldvo();
			BXBusItemVO[] childrenVO_old = null;
			if (bxoldvo != null) {
				childrenVO_old = bxoldvo.getBxBusItemVOS();
			}
			Map<String, BXBusItemVO> oldBusiMap = new HashMap<String, BXBusItemVO>();

			if (childrenVO_old != null) {
				for (BXBusItemVO vo : childrenVO_old) {
					oldBusiMap.put(vo.getPrimaryKey(), vo);
				}
			}

			Vector<BXBusItemVO> newBusItem = new Vector<BXBusItemVO>(); // ����ҵ����Ϣ
			BXBusItemVO[] delBusItem = null; // ɾ��ҵ����Ϣ
			Vector<BXBusItemVO> updBusItem = new Vector<BXBusItemVO>(); // ����ҵ����Ϣ

			if (childrenVO != null) {
				int rowno = 0;
				for (BXBusItemVO child : childrenVO) {
					rowno++;
					
					child.setRowno(rowno);
					// ����ҵ�������Զ�������Ϣ
					dealBusitemDefitem(child);

					if (StringUtils.isNullWithTrim(child.getPrimaryKey())
							|| child.getPrimaryKey().startsWith(BXConstans.TEMP_FB_PK)) {

						child.setPk_jkbx(bxvo.getParentVO().getPk_jkbx());
						newBusItem.add(child);
					} else if (oldBusiMap.containsKey(child.getPrimaryKey())) {
						updBusItem.add(child);
						oldBusiMap.remove(child.getPrimaryKey());
					}
				}
			}

			delBusItem = oldBusiMap.values().toArray(new BXBusItemVO[] {});

			// ��������������MAP
			Map<BXBusItemVO, String> cmpMap = new HashMap<BXBusItemVO, String>();
			if (childrenVO != null) {
				for (BXBusItemVO item : childrenVO) {
					cmpMap.put(item, item.getPrimaryKey() == null ? "" : item.getPrimaryKey());
				}
			}

			if (newBusItem != null)
				baseDao.insertVOArray(newBusItem.toArray(new BXBusItemVO[] {}));
			if (updBusItem != null)
				baseDao.updateVOArray(updBusItem.toArray(new BXBusItemVO[] {}));
			if (delBusItem != null)
				baseDao.deleteVOArray(delBusItem);

			headVos.add(bxvo.getParentVO());

			// ����������Ϣ
			if (bxvo.getParentVO().getDjdl().equals(BXConstans.BX_DJDL)) {
				bxdvos.add(bxvo.getParentVO());
				if (bxvo.getContrastVO() != null && bxvo.getContrastVO().length != 0) {
					List<BxcontrastVO> newContrasts = ErContrastUtil.dealContrastForNew(bxvo.getParentVO(), bxvo.getContrastVO(),
							childrenVO);
					bxvo.setContrastVO(newContrasts.toArray(new BxcontrastVO[] {}));
					contrasts.addAll(newContrasts);
				}

				if (!bxvo.isContrastUpdate()) {
					if (bxvo.getParentVO().getCjkybje().compareTo(bxvo.getBxoldvo().getParentVO().getCjkybje()) != 0) {
						throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
								"UPP2011-000389")/*
												 * @res
												 * "������������Ϣ�쳣����ͨ�����������в���������ֱ�ӽ��г�������޸�"
												 */);
					}
				}
			}

			// ��ʱ����ʵ�����Ķ���
			Map<String, String> idMap = new HashMap<String, String>();
			if (childrenVO != null) {
				for (BXBusItemVO item : childrenVO) {
					idMap.put(cmpMap.get(item).length() == 0 ? item.getPrimaryKey() : cmpMap.get(item), item.getPrimaryKey());
				}
			} else {
				idMap.put(bxvo.getParentVO().getPk_jkbx(), bxvo.getParentVO().getPk_jkbx());
			}

			voList.add(bxvo.getParentVO());
			
			// ������ʱ����ʵ�����Ķ���
			bxvo.setCmpIdMap(idMap);

			// �ж�CMP��Ʒ�Ƿ����� 14/12/03 ע������bxzbbo���ѵ��øýӿڣ���֪��Ϊʲô��Ҫ����
//			JKBXHeaderVO headerVO = bxvo.getParentVO();
//
//			boolean iscmpused = BXUtil.isProductInstalled(headerVO.getPk_org(), BXConstans.TM_CMP_FUNCODE);
//			if (iscmpused) {
//				new ErForCmpBO().invokeCmp(bxvo, headerVO.getShrq().getDate(), SettleUtil
//						.getBillStatus(bxvo.getParentVO(), false));
//			}
		}
		baseDao.updateVOArray(voList.toArray(new JKBXHeaderVO[voList.size()]));

		JKBXHeaderVO[] toArray = headVos.toArray(new JKBXHeaderVO[] {});

		// �޸ĳ�����Ϣ�Ĵ���
		new ContrastBO().saveContrast(contrasts, bxdvos);
	
		// ����������Ԥ����ϸ����
		new BxVerifyAccruedBillBO().saveVerifyVOs(vos);
		
		addTsToBXVOs(toArray);

		return vos;
	}

	@SuppressWarnings("unchecked")
	public List<JKBXHeaderVO> queryHeadersByWhereSql(String sql, String djdl) throws DAOException {

		PubDAO pudao = new PubDAO();

		List<JKBXHeaderVO> ret = new ArrayList<JKBXHeaderVO>();

		if (djdl == null || (!djdl.equals(BXConstans.BX_DJDL) && !djdl.equals(BXConstans.JK_DJDL))) {
			djdl = BXConstans.BX_DJDL;
			String sqlNew = getJKBXHeadSQL(BXVOUtil.getMetaData(djdl)) + sql;
			ret.addAll((List<JKBXHeaderVO>) pudao.queryVOsBySql(BXVOUtil.getMetaData(djdl).getMetaClass(), BXVOUtil
					.getMetaData(djdl), sqlNew));

			djdl = BXConstans.JK_DJDL;
			sqlNew = getJKBXHeadSQL(BXVOUtil.getMetaData(djdl)) + sql;
			ret.addAll((List<JKBXHeaderVO>) pudao.queryVOsBySql(BXVOUtil.getMetaData(djdl).getMetaClass(), BXVOUtil
					.getMetaData(djdl), sqlNew));
		} else {
			sql = getJKBXHeadSQL(BXVOUtil.getMetaData(djdl)) + sql;

			ret = (List<JKBXHeaderVO>) pudao.queryVOsBySql(BXVOUtil.getMetaData(djdl).getMetaClass(), BXVOUtil.getMetaData(djdl),
					sql);
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	public List<JKBXHeaderVO> queryHeaders(Integer start, Integer count, DjCondVO condVO) throws DAOException {

		PubDAO pudao = new PubDAO();

		String sql;

		List<JKBXHeaderVO> ret = new ArrayList<JKBXHeaderVO>();

		IArapMappingMeta meta = null;

		if (condVO.isInit) {
			meta = new Er_jkbx_initVOMeta();
			sql = genBxSql(condVO, meta);
			ret = (List<JKBXHeaderVO>) pudao.queryVOsBySql(BXVOUtil.getMetaData(condVO.djdl).getMetaClass(), meta, sql, start,
					count);
		} else if (condVO.djdl == null || condVO.djdl.trim().length() == 0) {
			// ���ݹ������ݲ�ѯ�ڵ㣬��������ѯ
			condVO.djdl = BXConstans.BX_DJDL;
			sql = genBxSql(condVO);
			meta = BXVOUtil.getMetaData(condVO.djdl);
			ret.addAll((List<JKBXHeaderVO>) pudao.queryVOsBySql(BXVOUtil.getMetaData(condVO.djdl).getMetaClass(), meta, sql,
					start, count, getRsChecker(condVO)));

			if (ret.size() == count) {
				return ret;
			}
			// һҳ�м��б��������н�ʱ�����⴦��ע���ʱbxcount������ֵ˳��
			int bxcount = querySize(condVO);
			condVO.djdl = BXConstans.JK_DJDL;
			sql = genBxSql(condVO);
			meta = BXVOUtil.getMetaData(condVO.djdl);
			ret.addAll((List<JKBXHeaderVO>) pudao.queryVOsBySql(BXVOUtil.getMetaData(condVO.djdl).getMetaClass(), meta, sql,
					start - bxcount, count, getRsChecker(condVO)));
		} else {
			sql = genBxSql(condVO);
			ret = (List<JKBXHeaderVO>) pudao.queryVOsBySql(BXVOUtil.getMetaData(condVO.djdl).getMetaClass(), BXVOUtil
					.getMetaData(condVO.djdl), sql, start, count, getRsChecker(condVO));
		}
		return ret;
	}

	private IRSChecker getRsChecker(DjCondVO condVO) {
		if (condVO.isLinkPz || condVO.VoucherFlags != null) {
			return new VoucherRsChecker(condVO.isLinkPz, condVO.VoucherFlags);
		}
		return null;
	}

	public String genBxSql(DjCondVO condVO, IArapMappingMeta meta) throws DAOException {
		String sql = null;

		sql = getJKBXSelectSQL(meta);

		try {
			sql += getWhereSql(condVO, true);
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
		return sql;
	}

	public String genBxSql(DjCondVO condVO) throws DAOException {
		String sql = null;

		sql = getJKBXSelectSQL(BXVOUtil.getMetaData(condVO.djdl));

		try {
			sql += getWhereSql(condVO, true);

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
		return sql;
	}

	public static String getWhereSql(DjCondVO condVO, boolean needOrder) throws Exception {
		StringBuffer sb = new StringBuffer();

		String whereSql = condVO.defWhereSQL == null ? "" : condVO.defWhereSQL;

		StringTokenizer st = new StringTokenizer(whereSql, BXConstans.Query_Dataitem_Separator);

		if (st.hasMoreTokens()) {
			sb.append(st.nextToken());

			while (st.hasMoreTokens()) {
				sb.append("  fb.tablecode = '");
				sb.append(st.nextToken());
				sb.append("' and fb.");
				sb.append(st.nextToken());
				sb.append(st.nextToken());
			}
		}
		String whereClause = new PubMethods().getBillQuerySubSql_Jkbx(condVO.m_NorCondVos, sb.toString());

		String whereStr = null;

		if (condVO.isInit) {
			if (condVO.pk_org != null && condVO.pk_org.length != 0) {
				if (condVO.pk_org[0] != null && condVO.pk_org[0].length() != 0) {
					whereStr = " (zb.pk_org='" + condVO.pk_org[0] + "' ) ";
				}
			}
			if (condVO.pk_group != null && condVO.pk_group.length != 0) {
				if (condVO.pk_group[0] != null && condVO.pk_group[0].length() != 0) {
					if (whereStr != null) {
						whereStr = whereStr + " and ";
						whereStr = whereStr + " (zb.pk_group='" + condVO.pk_group[0] + "' ) ";
					} else {
						whereStr = " (zb.pk_group='" + condVO.pk_group[0] + "' ) ";
					}
				}

			}
		} else if (condVO.nodecode != null && BXConstans.BXMNG_NODECODE.equalsIgnoreCase(condVO.nodecode)) {

			// �����ж�����Ȩ�����ɶ�Ӧ������
			whereStr = getDirectionPerm(condVO);

			whereClause = " left join pub_workflownote wf on zb.pk_jkbx=wf.billid and wf.checkman= '" + condVO.operator
					+ "' and wf.dr=0  and wf.actiontype <> 'MAKEBILL'  " + whereClause;
		} else {

			IRolePubService roleQuery = (IRolePubService) NCLocator.getInstance().lookup(IRolePubService.class.getName());
			RoleVO[] roleVOs = new RoleVO[] {};
			String user = condVO.operator;

			try {
				roleVOs = roleQuery.queryRoleByUserID(user, null);
			} catch (BusinessException e1) {
				ExceptionHandler.consume(e1);
			}
			List<String> roles = new ArrayList<String>();

			for (RoleVO roleVO : roleVOs) {
				roles.add(roleVO.getPk_role());
			}
			// modified by chendya@ufida.com.cn ���ڳ����ݲ�������Ȩ������
			final String nodecode = condVO.nodecode;
			if (!BXConstans.BXLR_QCCODE.equals(nodecode) && !BXConstans.BXBILL_QUERY.equals(nodecode)) {
				// ��Ȩ������
				whereStr = " (zb.jkbxr='" + condVO.psndoc + "' or zb.operator in(select pk_user from er_indauthorize  where "
						+ SqlUtils.getInStr("pk_roler", roles.toArray(new String[] {})) + ") or zb.operator='" + condVO.operator
						+ "' )";
			}
			// --end
		}
		if (whereStr != null) {
			if (StringUtils.isNullWithTrim(whereClause)) {
				whereClause = " where " + whereStr;
			} else {
				whereClause = whereClause + " and " + whereStr;
			}
		}
		// added by chendya ׷������ʹ��Ȩ�޹�������
		if (condVO.getDataPowerSql() != null && condVO.getDataPowerSql().trim().length() > 0) {
			whereClause += " and " + "(" + condVO.getDataPowerSql() + ")";
		}

		if (whereClause != null) {
			whereClause += " and zb.pk_group='" + InvocationInfoProxy.getInstance().getGroupId() + "'";
		}
		// --end
		if (needOrder)
			whereClause += " order by zb.djrq desc,zb.djbh desc ";

		return whereClause;
	}

	private static String getDirectionPerm(DjCondVO condVO) throws BusinessException, SQLException {
		String whereStr;
		String pk_group = InvocationInfoProxy.getInstance().getGroupId();

		String cuserid = condVO.operator;
		String djdl = condVO.djdl;
		String resourceCode = null;
		if(djdl == null || djdl.equals(BXConstans.BX_DJDL)) {
			resourceCode = BXConstans.EXPENSERESOURCEID;
		} else if (djdl.equals(BXConstans.JK_DJDL)) {
			resourceCode = BXConstans.LOANRESOURCEID;
		}
		// Ĭ���������ʹ�����
		whereStr = " (zb.approver='" + condVO.operator + "' or wf.billid is not null ) ";

		if (djdl != null && resourceCode != null) {
			DataPermConfig config = NCLocator.getInstance().lookup(IDataPermissionPubService.class).queryDataPermConfig(cuserid,
					resourceCode, pk_group, true);

			if (config != null) {
				boolean isStartDirectorPerm = config.getSpecialPermissionConfig().isEnableDirectorPerm();
				if (isStartDirectorPerm) {
					String where = "";
					if (condVO.psndoc != null) {
						where = getDirectorWherePart(condVO, pk_group);
					}
					whereStr = "(zb.approver='" + condVO.operator + "' or wf.billid is not null  " + where + ") ";
				}
			}
		}
		return whereStr;
	}

	@SuppressWarnings("unchecked")
	private static String getDirectorWherePart(DjCondVO condVO, String pk_group) throws BusinessException, SQLException {
		String whereStr = "";
		// ��ѯ�ü��������еĲ���
		DeptVO[] deptVOs = NCLocator.getInstance().lookup(IDeptQryService.class).queryAllDeptVOSByGroupID(pk_group);
		ArrayList<String> deptFields = new ArrayList<String>();

		// ��ѯ�ü��������е���֯������Ϣ
		final String whereSql = "pk_group='" + pk_group + "'";
		@SuppressWarnings("rawtypes")
        Collection rs = NCLocator.getInstance().lookup(IUAPQueryBS.class).retrieveByClause(OrgManagerVO.class, whereSql);
		OrgManagerVO[] orgMgrVOs = (OrgManagerVO[]) rs.toArray(new OrgManagerVO[0]);
		// K=���Ż���֯PK,V=����VO
		HashMap<String, OrgManagerVO> map = new HashMap<String, OrgManagerVO>();
		if (orgMgrVOs != null && orgMgrVOs.length > 0) {
			for (int i = 0; i < orgMgrVOs.length; i++) {
				final String key = orgMgrVOs[i].getPk_org();
				if (!map.containsKey(key)) {
					map.put(key, orgMgrVOs[i]);
				}
			}
		}
		for (DeptVO vo : deptVOs) {
			final String key = vo.getPk_dept();
			if (map.get(key) != null && map.get(key).getCuserid() != null && map.get(key).getCuserid().equals(condVO.operator)) {
				deptFields.add(key);
			}
		}

		OrgVO[] orgVOs = NCLocator.getInstance().lookup(IOrgUnitPubService.class).getAllOrgVOSByGroupIDAndOrgTypes(pk_group,
				new String[] { IOrgConst.FINANCEORGTYPE });
		ArrayList<String> orgFields = new ArrayList<String>();

		for (OrgVO vo : orgVOs) {
			final String key = vo.getPk_org();
			if (map.get(key) != null && map.get(key).getCuserid() != null && map.get(key).getCuserid().equals(condVO.operator)) {
				orgFields.add(key);
			}
		}

		whereStr = " or  " + SqlUtils.getInStr("zb.deptid", deptFields.toArray(new String[0]), true) + " or "
				+ SqlUtils.getInStr("zb.pk_org", orgFields.toArray(new String[0]), true);
		return whereStr;
	}

	public String getCountSelectSQL(String tablename) {
		StringBuffer buf = new StringBuffer("SELECT count(distinct zb.pk_jkbx) ");
		return buf.toString() + " from " + tablename + " zb left outer join er_busitem fb on zb.pk_jkbx=fb.pk_jkbx ";
	}

	public String getJKBXSelectSQL(IArapMappingMeta meta) {
		StringBuffer buf = new StringBuffer("SELECT DISTINCT ");
		for (int i = 0, size = meta.getColumns().length; i < size; i++) {
			if (i != 0) {
				buf.append(",");
			}
			buf.append(" zb.").append(meta.getColumns()[i]);
		}
		return buf.toString() + " from " + meta.getTableName() + " zb left outer join er_busitem fb on zb.pk_jkbx=fb.pk_jkbx ";
	}

	public String getJKBXHeadSQL(IArapMappingMeta meta) {
		StringBuffer buf = new StringBuffer("SELECT DISTINCT ");
		for (int i = 0, size = meta.getColumns().length; i < size; i++) {
			if (i != 0) {
				buf.append(",");
			}
			buf.append(meta.getColumns()[i]);
		}
		return buf.toString() + " from " + meta.getTableName() + " zb ";
	}

	public boolean checkGoing(AggregatedValueObject vo, String ApproveId, String ApproveDate, String checkNote) throws Exception {

		new BXZbBO().compareTs(new JKBXVO[] { VOFactory.createVO((JKBXHeaderVO) vo.getParentVO()) });

		JKBXHeaderVO headerVO = (JKBXHeaderVO) vo.getParentVO();

		JKBXHeaderVO headerVO2 = (JKBXHeaderVO) headerVO.clone();
		// headerVO2.setSpzt(IPfRetCheckInfo.GOINGON);
		headerVO2.setDjzt(BXStatusConst.DJZT_Saved);
		// headerVO2.setApprover(ApproveId);
		// headerVO2.setShrq(new UFDateTime(ApproveDate));

		// �����ֽ���ƽ̨
		// FIXME ������ڸĶ�
		// new ErForCmpBO().invokeCmp((BXVO) vo,
		// headerVO.getShrq().getDate(),SettleUtil.getBillStatus(headerVO2,
		// true));
		update(new JKBXHeaderVO[] { headerVO2 }, new String[] { JKBXHeaderVO.SPZT, JKBXHeaderVO.APPROVER, JKBXHeaderVO.SHRQ });

		headerVO.setIsunAudit(headerVO.getDjzt().equals(BXStatusConst.DJZT_Verified)
				|| headerVO.getDjzt().equals(BXStatusConst.DJZT_Sign));
		headerVO.setTs(headerVO2.getTs());
		headerVO.setSpzt(IPfRetCheckInfo.GOINGON);
		headerVO.setDjzt(BXStatusConst.DJZT_Saved);
		headerVO.setApprover(ApproveId);
		headerVO.setShrq(new UFDateTime(ApproveDate));
		return false;
	}

	public boolean checkNoPass(AggregatedValueObject vo, String ApproveId, String ApproveDate, String checkNote) throws Exception {

		new BXZbBO().compareTs(new JKBXVO[] { VOFactory.createVO((JKBXHeaderVO) vo.getParentVO()) });

		JKBXHeaderVO headerVO = (JKBXHeaderVO) vo.getParentVO();

		if (headerVO.getDjzt() != BXStatusConst.DJZT_Saved) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0", "02011002-0070")/*
																															 * @res
																															 * "����״̬���������ܽ�����ˣ���������!"
																															 */);
		}

		JKBXHeaderVO headerVO2 = (JKBXHeaderVO) headerVO.clone();
		headerVO2.setSpzt(IPfRetCheckInfo.NOPASS);
		headerVO2.setDjzt(BXStatusConst.DJZT_Saved);
		headerVO2.setApprover("");
		headerVO2.setShrq(new UFDateTime(ApproveDate));
		// �����ֽ���ƽ̨
		// new ErForCmpBO().invokeCmp((BXVO) vo,
		// headerVO.getShrq().getDate(),SettleUtil.getBillStatus(headerVO2,
		// true));

		update(new JKBXHeaderVO[] { headerVO2 }, new String[] { JKBXHeaderVO.SPZT, JKBXHeaderVO.APPROVER, JKBXHeaderVO.SHRQ });
		headerVO.setTs(headerVO2.getTs());
		headerVO.setSpzt(IPfRetCheckInfo.NOPASS);
		headerVO.setDjzt(BXStatusConst.DJZT_Saved);
		headerVO.setApprover("");
		headerVO.setShrq(new UFDateTime(ApproveDate));
		return false;

	}

	public void callCheckStatus(CheckStatusCallbackContext cscc) throws BusinessException {
		JKBXVO billvo = (JKBXVO) cscc.getBillVo();

		if (billvo instanceof JKVO) {
			update(new JKBXHeaderVO[] { billvo.getParentVO() }, new String[] { JKBXHeaderVO.APPROVER,
					JKBXHeaderVO.SHRQ, JKBXHeaderVO.SPZT });
		} else {
			update(new JKBXHeaderVO[] { billvo.getParentVO() }, new String[] { JKBXHeaderVO.APPROVER,
					JKBXHeaderVO.SHRQ, JKBXHeaderVO.SPZT, JKBXHeaderVO.FLEXIBLE_FLAG });
		}
	}

	public boolean checkPass(AggregatedValueObject vo, String ApproveId, String ApproveDate, String checkNote) throws Exception {

		JKBXHeaderVO headerVO = (JKBXHeaderVO) vo.getParentVO();

		if (headerVO.getDjzt() != BXStatusConst.DJZT_Saved) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0", "02011002-0070")/*
																															 * @res
																															 * "����״̬���������ܽ�����ˣ���������!"
																															 */);
		}

		headerVO.setApprover(ApproveId);
		headerVO.setShrq(new UFDateTime(ApproveDate));

		return false;

	}

	@Deprecated
	public void backGoing(AggregatedValueObject vo, String approveId, String approveDate, String backNote) throws Exception {

		new BXZbBO().compareTs(new JKBXVO[] { VOFactory.createVO((JKBXHeaderVO) vo.getParentVO()) });

		JKBXHeaderVO headerVO = (JKBXHeaderVO) vo.getParentVO();
		JKBXHeaderVO headerVO2 = (JKBXHeaderVO) headerVO.clone();
		headerVO2.setDjzt(BXStatusConst.DJZT_Saved);
		headerVO2.setSxbz(BXStatusConst.SXBZ_NO);
		headerVO2.setSpzt(IPfRetCheckInfo.GOINGON);
		headerVO2.setApprover(approveId);
		headerVO2.setShrq(new UFDateTime(approveDate));

		update(new JKBXHeaderVO[] { headerVO2 }, new String[] { JKBXHeaderVO.SPZT, JKBXHeaderVO.DJZT, JKBXHeaderVO.SXBZ,
				JKBXHeaderVO.APPROVER, JKBXHeaderVO.SHRQ });

		headerVO.setTs(headerVO2.getTs());
		headerVO.setSpzt(IPfRetCheckInfo.GOINGON);
		headerVO.setApprover(approveId);
		headerVO.setShrq(new UFDateTime(approveDate));
	}

	public void backNoState(AggregatedValueObject vo, String approveId, String approveDate, String backNote) throws Exception {

		new BXZbBO().compareTs(new JKBXVO[] { VOFactory.createVO((JKBXHeaderVO) vo.getParentVO()) });

		JKBXHeaderVO headerVO = (JKBXHeaderVO) vo.getParentVO();
		JKBXHeaderVO headerVO2 = (JKBXHeaderVO) headerVO.clone();
		headerVO2.setApprover(null);
		headerVO2.setShrq(null);
		headerVO2.setSpzt(IPfRetCheckInfo.NOSTATE);

		update(new JKBXHeaderVO[] { headerVO2 }, new String[] { JKBXHeaderVO.SPZT, JKBXHeaderVO.APPROVER, JKBXHeaderVO.SHRQ });

		headerVO.setIsunAudit(headerVO.getDjzt().equals(BXStatusConst.DJZT_Verified)
				|| headerVO.getDjzt().equals(BXStatusConst.DJZT_Sign));
		headerVO.setTs(headerVO2.getTs());
		headerVO.setApprover(null);
		headerVO.setSpzt(IPfRetCheckInfo.NOSTATE);
	}

	@SuppressWarnings("unchecked")
	public Collection<BxcontrastVO> retrieveContrastByClause(String sql) throws DAOException {
		return baseDao.retrieveByClause(BxcontrastVO.class, sql);
	}

	@SuppressWarnings("unchecked")
	public Collection<JsConstrasVO> retrieveJsContrastByClause(String sql) throws DAOException {
		return baseDao.retrieveByClause(JsConstrasVO.class, sql);
	}

	@SuppressWarnings("unchecked")
	public Collection<JsConstrasVO> queryJsContrastByWhereSql(String fromWhereSql) throws DAOException {
		PubDAO pubdao = new PubDAO();
		Er_jsconstrasVOMeta meta = new Er_jsconstrasVOMeta();
		String selectSQL = PubDAO.getSelectSQL(meta, fromWhereSql);
		return (List<JsConstrasVO>) pubdao.queryVOsBySql(JsConstrasVO.class, meta, selectSQL);
	}

	public int querySize(DjCondVO condVO) throws DAOException {
		String selectFromsql = "";
		String whereSql;
		try {
			whereSql = getWhereSql(condVO, false);
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}

		if (condVO.isInit) {
			selectFromsql = getCountSelectSQL(BXConstans.JKBXINIT_TABLENAME);
			Object count = baseDao.executeQuery(selectFromsql + whereSql, new ColumnProcessor());
			return new Integer(count.toString()).intValue();
		} else if (condVO.djdl == null || (!condVO.djdl.equals(BXConstans.BX_DJDL) && !condVO.djdl.equals(BXConstans.JK_DJDL))) {
			condVO.djdl = BXConstans.BX_DJDL;
			selectFromsql = getCountSelectSQL(BXConstans.BX_TABLENAME);
			try {
				whereSql = getWhereSql(condVO, false);
			} catch (Exception e) {
				throw new DAOException(e.getMessage(), e);
			}
			Object count1 = baseDao.executeQuery(selectFromsql + whereSql, new ColumnProcessor());
			condVO.djdl = BXConstans.JK_DJDL;
			selectFromsql = getCountSelectSQL(BXConstans.JK_TABLENAME);
			try {
				whereSql = getWhereSql(condVO, false);
			} catch (Exception e) {
				throw new DAOException(e.getMessage(), e);
			}
			Object count2 = baseDao.executeQuery(selectFromsql + whereSql, new ColumnProcessor());
			return new Integer(count1.toString()).intValue() + new Integer(count2.toString()).intValue();
		} else {
			if (condVO.djdl.equals(BXConstans.BX_DJDL)) {
				selectFromsql = getCountSelectSQL(BXConstans.BX_TABLENAME);
			} else {
				selectFromsql = getCountSelectSQL(BXConstans.JK_TABLENAME);
			}
			Object count = baseDao.executeQuery(selectFromsql + whereSql, new ColumnProcessor());
			return new Integer(count.toString()).intValue();
		}
	}

	public CircularlyAccessibleValueObject[] queryAllBodyData(String key) throws BusinessException {
		return queryAllBodyData(key, null);
	}

	@SuppressWarnings("unchecked")
	public CircularlyAccessibleValueObject[] queryAllBodyData(String key, String whereString) throws BusinessException {
		if (whereString == null || whereString.trim().length() == 0)
			whereString = "";
		else
			whereString = " and " + whereString;

		return (CircularlyAccessibleValueObject[]) new BaseDAO().retrieveByClause(BXBusItemVO.class,
				new BXBusItemVO().getParentPKFieldName() + "='" + key + "' and dr=0 " + whereString)
				.toArray(new BXBusItemVO[] {});
	}

	public Map<String, String> getTsByPrimaryKeys(String[] key, String tableName, String pk_field) throws DAOException,
			SQLException {
		List<JKBXHeaderVO> headers = queryHeadersByWhereSql(" where " + SqlUtils.getInStr(pk_field, key), tableName
				.equals(BXConstans.BX_TABLENAME) ? BXConstans.BX_DJDL : BXConstans.JK_DJDL);
		Map<String, String> hashMap = new HashMap<String, String>();
		if (headers != null) {
			for (JKBXHeaderVO head : headers) {
				hashMap.put(head.getPrimaryKey(), head.getTs().toString());
			}
		}
		return hashMap;
	}

	/**
	 * ��ѯ��̯��ϸVO����
	 * 
	 * @param sql
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<CShareDetailVO> retrieveCShareVoByClause(String sql) throws DAOException {
		return baseDao.retrieveByClause(CShareDetailVO.class, sql);
	}

	/**
	 * ��ѯ��̯��ϸVO����
	 * 
	 * @param sql
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<CostShareVO> retrieveCostShareVoByClause(String sql) throws DAOException {
		return baseDao.retrieveByClause(CostShareVO.class, sql);
	}
	
//	private IMDPersistenceService getMdDaoService() {
//		return MDPersistenceService.lookupPersistenceService();
//	}
}