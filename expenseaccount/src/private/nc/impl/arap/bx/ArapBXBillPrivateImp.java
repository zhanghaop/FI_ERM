package nc.impl.arap.bx;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import nc.bd.accperiod.InvalidAccperiodExcetion;
import nc.bs.arap.bx.BXZbBO;
import nc.bs.arap.bx.ContrastBO;
import nc.bs.arap.bx.IBXBusItemBO;
import nc.bs.arap.bx.VoucherRsChecker;
import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.er.util.BXBsUtil;
import nc.bs.er.util.SqlUtil;
import nc.bs.erm.util.ErUtil;
import nc.bs.erm.util.ErmBillPubUtil;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.pub.pf.PfUtilTools;
import nc.bs.trade.billsource.IBillDataFinder;
import nc.bs.trade.billsource.IBillFinder;
import nc.impl.pubapp.linkquery.BillTypeSetBillFinder;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.arap.pub.IBXBillPublic;
import nc.itf.arap.pub.ISqdlrKeyword;
import nc.itf.er.indauthorize.IIndAuthorizeQueryService;
import nc.itf.org.IDeptQryService;
import nc.itf.resa.costcenter.ICostCenterQueryOpt;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.md.persist.framework.MDPersistenceService;
import nc.pubitf.accperiod.AccountCalendar;
import nc.pubitf.cmp.settlement.ICmpSettlementPubQueryService;
import nc.pubitf.org.IOrgUnitPubService;
import nc.pubitf.rbac.IFunctionPermissionPubService;
import nc.pubitf.rbac.IRolePubService;
import nc.pubitf.rbac.IUserPubService;
import nc.pubitf.resa.costcenter.ICostCenterPubService;
import nc.pubitf.uapbd.IPsndocPubService;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.arap.bx.util.BXUtil;
import nc.vo.arap.bx.util.CurrencyControlBO;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.cmp.NetPayExecInfo;
import nc.vo.cmp.settlement.SettlementAggVO;
import nc.vo.cmp.settlement.SettlementBodyVO;
import nc.vo.cmp.settlement.SettlementHeadVO;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BXHeaderVO;
import nc.vo.ep.bx.BXVO;
import nc.vo.ep.bx.BatchContratParam;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.InitHeaderVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.JKHeaderVO;
import nc.vo.ep.bx.JKVO;
import nc.vo.ep.bx.JsConstrasVO;
import nc.vo.ep.bx.SqdlrVO;
import nc.vo.ep.bx.VOFactory;
import nc.vo.ep.dj.DjCondVO;
import nc.vo.ep.dj.ERMDjCondVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.indauthorize.IndAuthorizeVO;
import nc.vo.er.reimrule.ReimRuleVO;
import nc.vo.er.util.StringUtils;
import nc.vo.erm.accruedexpense.AccruedVerifyVO;
import nc.vo.erm.common.MessageVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.util.VOUtils;
import nc.vo.fi.pub.SqlUtils;
import nc.vo.fipub.utils.ArrayUtil;
import nc.vo.fipub.utils.VOUtil;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.org.DeptVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;
import nc.vo.pub.bill.BillOperaterEnvVO;
import nc.vo.pub.bill.BillTempletVO;
import nc.vo.pub.billtype.BilltypeVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.pub.MathTool;
import nc.vo.resa.costcenter.CostCenterVO;
import nc.vo.sm.UserVO;
import nc.vo.trade.billsource.LightBillVO;
import nc.vo.uap.rbac.constant.IRoleConst;
import nc.vo.uap.rbac.role.RoleVO;

import org.apache.commons.lang.ArrayUtils;

/**
 * nc.impl.arap.bx.ArapBXBillPrivateImp
 * 
 * @author twei
 * 
 *         �����൥��˽��ҵ����ӿ�ʵ��
 */
/** 
 * <b>Application name:</b>NC60<br>
 * <b>Application describing:</b> <br>
 * <b>Copyright:</b>Copyright &copy; 2013 ��������ɷ����޹�˾��Ȩ���С�<br>
 * <b>Company:</b>Ufida<br>
 * <b>Date:</b>2013-4-10<br>
 * @author��wangyhh@ufida.com.cn
 * @version $Revision$
 */ 
public class ArapBXBillPrivateImp implements IBXBillPrivate {
	public BXZbBO bxZbBO = new BXZbBO();

	private BaseDAO baseDao = null;

	public List<JKBXHeaderVO> queryHeaders(Integer start, Integer count,
			DjCondVO condVO) throws BusinessException {
		return bxZbBO.queryHeaders(start, count, condVO);
	}

	public List<JKBXHeaderVO> queryHeadersByWhereSql(String sql, String djdl)
			throws BusinessException {
		return bxZbBO.queryHeadersByWhereSql(sql, djdl);
	}

	public Collection<BxcontrastVO> queryContrasts(JKBXHeaderVO header)
			throws BusinessException {
		return bxZbBO.queryContrasts(header);
	}

	public Collection<JsConstrasVO> queryJsContrasts(JKBXHeaderVO header)
			throws BusinessException {
		return bxZbBO.queryJsContrasts(header);
	}

	public MessageVO[] audit(JKBXVO[] bxvos) throws BusinessException {
		return bxZbBO.audit(bxvos);
	}

	public BXBusItemVO[] queryItems(JKBXHeaderVO header)
			throws BusinessException {
		return getBxBusitemBO(header.getDjlxbm(), header.getDjdl())
				.queryByHeaders(new JKBXHeaderVO[] { header });
	}

	private IBXBusItemBO getBxBusitemBO(String djlxbm, String djdl)
			throws BusinessException {
		return bxZbBO.getBxBusitemBO(djlxbm, djdl);
	}

	public MessageVO[] unAudit(JKBXVO[] bxvos) throws BusinessException {
		return bxZbBO.unAudit(bxvos);
	}

	public List<JKBXHeaderVO> queryHeadersByPrimaryKeys(String[] keys,
			String djdl) throws BusinessException {
		return bxZbBO.queryHeadersByPrimaryKeys(keys, djdl);
	}

	public List<JKBXVO> queryVOs(Integer start, Integer count, DjCondVO condVO)
			throws BusinessException {

		List<JKBXHeaderVO> headers = queryHeaders(start, count, condVO);

		List<JKBXVO> vos = retriveItems(headers);

		return vos;
	}

	public int querySize(DjCondVO condVO) throws BusinessException {
		return bxZbBO.querySize(condVO);
	}

	public List<JKBXVO> queryVOsByPrimaryKeys(String[] keys, String djdl)
			throws BusinessException {
		return queryVOsByPrimaryKeysForNewNode(keys,djdl,false,null);
	}
	
	public List<JKBXVO> queryVOsByPrimaryKeysForNewNode(String[] keys, String djdl,boolean isCommon,ERMDjCondVO djCondVO)
	throws BusinessException {
		List<JKBXVO> jkbxVoList = queryHeadVOsByPrimaryKeys(keys, djdl,isCommon,djCondVO);
		List<JKBXHeaderVO> headVoList = new ArrayList<JKBXHeaderVO>();
		if (jkbxVoList != null && jkbxVoList.size() > 0) {
			for (JKBXVO vo : jkbxVoList) {
				headVoList.add(vo.getParentVO());
			}
		}
		List<JKBXVO> vos = retriveItems(headVoList);
		return vos;
	}
	/**
	 * ���ؽ�����ͷ���� 
	 * @param keys
	 * @param djdl
	 * @param isCommonBill���Ƿ��ǳ��õ���
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
    public List<JKBXVO> queryHeadVOsByPrimaryKeys(String[] keys, String djdl,boolean isCommonBill,ERMDjCondVO djCondVO) throws BusinessException {
		if (keys == null && djdl == null) {
			return null;
		}
		List<JKBXVO> resultList = new ArrayList<JKBXVO>();
		if(!isCommonBill){
		    if (djdl == null || BXConstans.BX_DJDL.equals(djdl)) {
		        resultList = queryJKBXHeadVOBypks(keys, BXConstans.BX_DJDL);
		        if (resultList == null || (resultList != null && resultList.size() != keys.length)) {
		            List<JKBXVO> jkResultList = queryJKBXHeadVOBypks(keys, BXConstans.JK_DJDL);
		            resultList.addAll(jkResultList);
		        }
		    } else {
		        resultList = queryJKBXHeadVOBypks(keys, djdl);
		    }
		}else{
		    String inStr = SqlUtils.getInStr(JKBXHeaderVO.PK_JKBX, keys, true);
		    List<InitHeaderVO> headList = (List<InitHeaderVO>) new BaseDAO().retrieveByClause(InitHeaderVO.class, inStr);
			for (InitHeaderVO initHeaderVO : headList) {
				if (BXConstans.BX_DJDL.equals(initHeaderVO.getDjdl())) {
					BXHeaderVO bxHeaderVO = new BXHeaderVO();
					convertCommonBillVo(initHeaderVO, bxHeaderVO);
					resultList.add(new BXVO(bxHeaderVO));
				} else if (BXConstans.JK_DJDL.equals(initHeaderVO.getDjdl())) {
					JKHeaderVO jkHeaderVO = new JKHeaderVO();
					convertCommonBillVo(initHeaderVO, jkHeaderVO);
					resultList.add(new JKVO(jkHeaderVO));
				}
			}
		}
		
		//ֻ��δ����ĵ��ݹ��˳���:û�а�װ������Ϣʱ�������κδ���
		if (djCondVO != null && !djCondVO.isIsjs()) {
			SettlementAggVO[] bills =null;
			boolean iscmpused = BXUtil.isProductInstalled(resultList.get(0).getParentVO().getPk_group(), BXConstans.TM_CMP_FUNCODE);
			if (iscmpused) {
				for (int i = 0; i < resultList.size(); i++) {
					String pk_jkbx = resultList.get(i).getParentVO().getPk_jkbx();
					//TODO ����Ч������
					bills = NCLocator.getInstance().lookup(ICmpSettlementPubQueryService.class).queryBillsBySourceBillID(new String[] { pk_jkbx });
					if (bills != null && bills.length > 0  && ((SettlementHeadVO) bills[0].getParentVO()).getSettlestatus() != 0) {
						resultList.remove(i);
					}
				}
			}
		}
		try {
			if (djCondVO != null) {
				Integer[] voucherFlag = null;
				if((djCondVO.getVoucherFlags() == null || djCondVO.getVoucherFlags().length == 0) && djCondVO.isLinkPz){
					voucherFlag = new Integer[] {//����ƾ֤��
							VoucherRsChecker.VOUCHER_EXIST_FLAG,
							VoucherRsChecker.VOUCHER_EXIST_JZ_FLAG };
				}else{
					voucherFlag = djCondVO.getVoucherFlags();
				}
				
				if(voucherFlag != null && voucherFlag.length > 0){
					//����ƾ֤״̬����
					JKBXHeaderVO[] newHeadVOs = dealVoucherInfo(djCondVO.isLinkPz(), resultList, djCondVO.getVoucherFlags());
					
					//���ѡ����ƾ֤״̬������в�ѯ�������
					if (djCondVO.getVoucherFlags() != null && djCondVO.getVoucherFlags().length > 0) {
						List<String> pk_jkbxList = new ArrayList<String>();
						for (JKBXHeaderVO vo : newHeadVOs) {
							pk_jkbxList.add(vo.getPk_jkbx());
						}

						List<JKBXVO> newList = new ArrayList<JKBXVO>();
						for (JKBXVO vo : resultList) {
							if (pk_jkbxList.contains(vo.getParentVO().getPk_jkbx())) {
								newList.add(vo);
							}
						}
						resultList = newList;
					}
				}
			}
		} catch (SQLException e) {
			ExceptionHandler.handleException(e);
		}
		
		if(djdl == null && resultList != null){//�����ڡ����ݺŽ�������
			Collections.sort(resultList, new Comparator<JKBXVO>(){
				@Override
				public int compare(JKBXVO bxvo1, JKBXVO bxvo2) {
					JKBXHeaderVO head1 = bxvo1.getParentVO();
					JKBXHeaderVO head2 = bxvo2.getParentVO();
					if(head1.getDjrq() != null && head2.getDjrq() != null){
						if(head1.getDjrq().compareTo(head2.getDjrq()) > 0){
							return -1;
						}else if(head1.getDjrq().compareTo(head2.getDjrq()) < 0){
							return 1;
						}else{
							if(head1.getDjbh() != null && head2.getDjbh() != null){
								if(head1.getDjbh().compareTo(head2.getDjbh()) > 0){
									return -1;
								}else if(head1.getDjbh().compareTo(head2.getDjbh()) < 0){
									return  1;
								}
							}
						}
					}
					return 0;
				}
			});
		}
		
		return resultList;
	}
	
	/**
	 * ��ƾ֤״̬���˲�ѯ���
	 * @param showVoucherNo
	 *            �Ƿ���ʾƾ֤��
	 * @param list
	 *            ���ݼ���
	 * @param voucherFlags
	 *            ƾ֤״̬
	 * @return
	 * @throws SQLException
	 */
	private JKBXHeaderVO[] dealVoucherInfo(boolean showVoucherNo, List<JKBXVO> list, Integer[] voucherFlags) throws SQLException {
		JKBXHeaderVO[] tempJKBXHeaderVO = new JKBXHeaderVO[list.size()];
		for (int i = 0; i < list.size(); i++) {
			tempJKBXHeaderVO[i] = list.get(i).getParentVO();
		}
		CircularlyAccessibleValueObject[] reslut = new VoucherRsChecker(showVoucherNo, voucherFlags).getReslut(tempJKBXHeaderVO);
		JKBXHeaderVO[] dealVoucher = new JKBXHeaderVO[reslut.length];
		for (int j = 0; j < reslut.length; j++) {
			if (reslut[j] instanceof JKBXHeaderVO) {
				dealVoucher[j] = (JKBXHeaderVO) reslut[j];
			}
		}
		return dealVoucher;

	}

    /**
     * ���ڳ��õ��ݣ�û��Ԫ������Ҫ����Ĵ���
     * 
     * @param initHeaderVO
     * @param vo
     * @author: wangyhh@ufida.com.cn
     */
	private void convertCommonBillVo(InitHeaderVO initHeaderVO, JKBXHeaderVO vo) {
		String[] attributeNames = vo.getAttributeNames();
		for (String attributeName : attributeNames) {
			vo.setAttributeValue(attributeName, initHeaderVO.getAttributeValue(attributeName));
		}
		vo.setInit(true);
	}

	/**
	 * ��ѯ��������vo
	 * 
	 * @param keys
	 * @author: wangyhh@ufida.com.cn
	 * @throws BusinessException 
	 */
	@SuppressWarnings("unchecked")
	private List<JKBXVO> queryJKBXHeadVOBypks(String[] keys,String djdl) throws BusinessException {
		String where = SqlUtils.getInStr("pk_jkbx", keys,true) + " and dr=0 order by djrq desc, djbh desc";
		if(BXConstans.BX_DJDL.equals(djdl)){
			return (List<JKBXVO>) MDPersistenceService.lookupPersistenceQueryService().queryBillOfVOByCond(BXVO.class, where , true);
		}else{
			return (List<JKBXVO>) MDPersistenceService.lookupPersistenceQueryService().queryBillOfVOByCond(JKVO.class, where , true);
		}
	}
	
	public List<JKBXVO> retriveItems(List<JKBXHeaderVO> headers)
			throws BusinessException {

		if (headers == null || headers.size() == 0)
			return null;

		List<JKBXVO> vos = new ArrayList<JKBXVO>();

		JKBXHeaderVO[] headArray = headers.toArray(new JKBXHeaderVO[] {});
		BXBusItemVO[] bxItems = queryItems(headArray);// ҵ������

		Collection<BxcontrastVO> contrasts = bxZbBO.queryContrasts(headArray,
				headers.get(0).getDjdl());// ����
		Collection<CShareDetailVO> cShares = bxZbBO.queryCSharesVOS(headArray);// ��̯��ϸ
		Collection<AccruedVerifyVO> accvvos = bxZbBO.queryAccruedVerifyVOS(headArray);// ����Ԥ����ϸ

		Map<String, List<CircularlyAccessibleValueObject>> bxItemMap = VOUtils
				.changeArrayToMapList(bxItems,
						new String[] { JKBXHeaderVO.PK_JKBX });

		Map<String, List<SuperVO>> contrastMap = VOUtils
				.changeCollectionToMapList(contrasts, headers.get(0).getDjdl()
						.equals(BXConstans.BX_DJDL) ? BxcontrastVO.PK_BXD
						: BxcontrastVO.PK_JKD);
		Map<String, List<SuperVO>> cShareMap = VOUtils
				.changeCollectionToMapList(cShares,
						new String[] { CShareDetailVO.SRC_ID });
		Map<String, List<SuperVO>> accvMap = VOUtils
		.changeCollectionToMapList(accvvos,
				new String[] { AccruedVerifyVO.PK_BXD});

		// ���ݱ��ִ���VO����
		CurrencyControlBO currencyControlBO = new CurrencyControlBO();

		for (Iterator<JKBXHeaderVO> iter = headers.iterator(); iter.hasNext();) {
			JKBXHeaderVO header = iter.next();
			String pk = header.getPrimaryKey();
			List<CircularlyAccessibleValueObject> itemList = bxItemMap.get(pk);
			JKBXVO bxvo = VOFactory.createVO(header,
					itemList == null ? new BXBusItemVO[] {} : itemList
							.toArray(new BXBusItemVO[] {}));

			List<SuperVO> contrastVoList = contrastMap.get(pk);
			List<SuperVO> cShareList = cShareMap.get(pk);// ��̯��ϸ
			List<SuperVO> accvList = accvMap.get(pk);// ���� Ԥ����ϸ
			bxvo.setContrastVO(contrastVoList == null ? null : contrastVoList
					.toArray(new BxcontrastVO[] {}));
			bxvo.setcShareDetailVo(cShareList == null ? null : cShareList
					.toArray(new CShareDetailVO[] {}));
			bxvo.setAccruedVerifyVO(accvList == null ? null : accvList
					.toArray(new AccruedVerifyVO[] {}));
			bxvo.setChildrenFetched(true);

			currencyControlBO.dealBXVOdigit(bxvo);// ���ȴ���

			vos.add(bxvo);
		}
		return vos;
	}

	public List<JKBXVO> queryVOsByWhereSql(String sql, String djdl)
			throws BusinessException {
		List<JKBXHeaderVO> name = queryHeadersByWhereSql(sql, djdl);
		List<JKBXVO> vos = retriveItems(name);
		return vos;
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public String[] queryPKsByWhereSql(String sql,String djdl)
			throws BusinessException {
		BaseDAO dao = new BaseDAO();
		Collection<JKBXHeaderVO> c=new ArrayList<JKBXHeaderVO>();
		if(BXConstans.BX_DJDL.equals(djdl)|| djdl==null){
			c= dao.retrieveByClause(BXHeaderVO.class, sql,new String[]{JKBXHeaderVO.PK_JKBX});
		}
		if(BXConstans.JK_DJDL.equals(djdl)|| djdl==null){
			Collection<JKBXHeaderVO> jks = dao.retrieveByClause(JKHeaderVO.class, sql,new String[]{JKBXHeaderVO.PK_JKBX});
			if(jks != null){
				c.addAll(jks) ;
			}
		}
		if(c != null && c.size() > 0){
			String[] pks = new String[c.size()];
			int i = 0;
			for (Object vo : c) {
				pks[i] = ((JKBXHeaderVO)vo).getPrimaryKey();
				i++;
			}
			return pks;
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String[] queryPKsByWhereSqlForComBill(String sql) throws BusinessException {
		BaseDAO dao = new BaseDAO();
		Collection<JKBXHeaderVO> c = dao.retrieveByClause(InitHeaderVO.class, sql,new String[]{JKBXHeaderVO.PK_JKBX});
		
		if(c != null && c.size() > 0){
			String[] pks = new String[c.size()];
			int i = 0;
			for (Object vo : c) {
				pks[i] = ((JKBXHeaderVO)vo).getPrimaryKey();
				i++;
			}
			return pks;
		}
		
		return null;
	
	}
	
	@Override
	public String[] queryPKsByWhereForBillManageNode(String sql, String pk_group, String loginUser)
			throws BusinessException {
		StringBuffer billMakerSql = new StringBuffer();
		String billMaker = NCLocator.getInstance().lookup(IUserPubService.class).queryPsndocByUserid(loginUser);

		billMakerSql.append(" (");
		billMakerSql.append(JKBXHeaderVO.CREATOR + "='" + loginUser + "'");
		billMakerSql.append(" or ");
		billMakerSql.append(JKBXHeaderVO.JKBXR + "='" + billMaker + "')");

		// ���ݹ���sql��׷������Ȩ�޿��ƣ�ֻ�����ѯ����ǰ�û��������˵ĵ���
		StringBuffer workflowSql = new StringBuffer(" PK_GROUP= '" + pk_group + "' ");

		workflowSql.append(" and (approver= '" + loginUser + "'");
		workflowSql.append(" or (pk_jkbx in(select billid from pub_workflownote wf where wf.checkman='" + loginUser
				+ "' and isnull(wf.dr,0)   = 0 and wf.actiontype <> 'MAKEBILL')) ");
		workflowSql.append(" or (pk_jkbx not in (select billid from pub_workflownote wf where wf.checkman='"
				+ loginUser + "' and isnull(wf.dr,0)   = 0 and wf.actiontype <> 'MAKEBILL')");
		workflowSql.append(" and " + billMakerSql.toString() + " and spzt = 3 )");
		workflowSql.append(" )");

		if (sql == null) {
			sql = workflowSql.toString();
		} else {
			sql += " and " + workflowSql.toString();
			sql += " order by djrq desc, djbh desc ";
		}

		String[] result = queryPKsByWhereSql(sql, null);
		return result;
	}
	
	@Override
	public String[] queryPKsByWhereForBillNode(String sql,String loginUser,String djdl) throws BusinessException {
		String whereStr = getAuthorizeSql(loginUser);
		
		if(sql == null){
			sql = whereStr;
		}else{
			sql += " AND " + whereStr;
		}
		sql +=" ORDER BY DJRQ DESC, DJBH DESC ";
		
		return queryPKsByWhereSql(sql,djdl);
	}

	/**
	 * Ȩ�޴���sql
	 * 
	 * @param loginUser
	 * @return
	 * @throws BusinessException
	 * @throws DAOException
	 * @author: wangyhh@ufida.com.cn
	 */
	private String getAuthorizeSql(String loginUser) throws BusinessException, DAOException {
		//pk_group��������ɾ����ʵ�ַ����в�û���õ�
		String[] result = NCLocator.getInstance().lookup(IBXBillPrivate.class).queryPsnidAndDeptid(loginUser, null);
		String pk_psn = result[0];
		String pk_group = result[3];
		
		//�������Ȩ��sql
//		String insql = constructAuthSql(loginUser, pk_psn, pk_dept, pk_fiorg, pk_group);
		
		// ���ڳ����ݲ�������Ȩ������
		String whereStr = " PK_GROUP= '" + pk_group + "' AND ( JKBXR='" + pk_psn + "' OR OPERATOR='" + loginUser + "' )";
		
		return whereStr;
	}

	/**
	 * �������Ȩ��sql
	 * 
	 * ������Ȩ������ǰ��¼�û����Բ�������Ȩ�Ĳ���Ա���ĵ��ݣ�ָ��ʱ�����ָ���������ͣ�
	 * ҵ��Ա��Ȩ��������ɫ�µ���Ա��Ȩ�޲���ҵ��Ա��PK_USER���ĵ���
	 * ������Ȩ��������ɫ�µ���Ա��Ȩ�޲���ָ�����ţ�PK_USER������Ա�ĵ���
	 * ���������ˣ�������ɫ�µ���Ա��Ȩ�޲�������˾��������Ա�ĵ���
	 * @param loginUser
	 * @param pk_psn
	 * @param pk_dept
	 * @param pk_fiorg
	 * @param pk_group
	 * @return
	 * @throws BusinessException
	 * @throws DAOException
	 * @author: wangyhh@ufida.com.cn
	 */
	@SuppressWarnings("unused")
	private String constructAuthSql(String loginUser, String pk_psn, String pk_dept, String pk_fiorg, String pk_group) throws BusinessException, DAOException {
		List<String> authPsnList = new ArrayList<String>();//��Ȩ����Աpk
		StringBuffer indAuthSql = new StringBuffer();
		//��ѯ��������
		IndAuthorizeVO[] indAuthorizes = getIndAuthorizeVO(pk_psn, pk_group,loginUser);
		if(!ArrayUtils.isEmpty(indAuthorizes)){
			boolean isAll = false;
			boolean isSelfDept = false;
			List<String> indAuthBillTypeList = new ArrayList<String>();//������Ȩ���� ��������
			List<String> indAuthOperatorList = new ArrayList<String>();//������Ȩ���� ����Ա
			List<String> agentDeptList = new ArrayList<String>();//������Ȩ���� ������pk
			for (IndAuthorizeVO indAuthorizeVO : indAuthorizes) {
				if(indAuthorizeVO.getType().intValue() == 0){
					if(ISqdlrKeyword.KEYWORD_ISALL.equals(indAuthorizeVO.getKeyword().trim())){
						//������˾��������Ա�ĵ���
						isAll = true;
					}else if(ISqdlrKeyword.KEYWORD_ISSAMEDEPT.equals(indAuthorizeVO.getKeyword().trim())){
						//��������
						isSelfDept = true;
					}else if(ISqdlrKeyword.KEYWORD_PK_DEPTDOC.equals(indAuthorizeVO.getKeyword().trim())){
						//������
						agentDeptList.add(indAuthorizeVO.getPk_user());
					}else if(ISqdlrKeyword.KEYWORD_BUSIUSER.equals(indAuthorizeVO.getKeyword().trim())){
						//ĳ�˴���ĳ��ɫ�µ�������
						authPsnList.add(indAuthorizeVO.getPk_user());
					}
				}else if(indAuthorizeVO.getType().intValue() == 1){
					//������Ȩ����,sqlBuf�Ѿ�����ָ��ʱ���
					indAuthBillTypeList.add(indAuthorizeVO.getBilltype());
					indAuthOperatorList.add(indAuthorizeVO.getPk_operator());
				}  
			}
			
			if(isAll){
				//����˾��������Ա,��ѯ����˾�����в���
				DeptVO[] deptVOs = NCLocator.getInstance().lookup(IDeptQryService.class).queryAllDeptVOsByOrgID(pk_fiorg);
				if(!ArrayUtils.isEmpty(deptVOs)){
					String[] deptPks = VOUtils.getAttributeValues(deptVOs, DeptVO.PK_DEPT);
					agentDeptList.addAll(Arrays.asList(deptPks));
				}
			} 
			//��ѯ����Ĳ���+������
			if(isSelfDept){
				agentDeptList.add(pk_dept);
			}
			if(agentDeptList.size() > 0){
				String insql = SqlUtils.getInStr("PK_DEPT", agentDeptList.toArray(new String[0]),true);
				String deptSql = "SELECT PK_PSNDOC FROM BD_PSNJOB WHERE ISMAINJOB = 'Y' AND " + insql;
				
				@SuppressWarnings("unchecked")
				List<Object[]> psnpkList = (List<Object[]>) new BaseDAO().executeQuery(deptSql, new ArrayListProcessor());
				if(psnpkList != null){
					for (Object[] psn : psnpkList) {
						authPsnList.add((String) psn[0]);
					}
				}
			}
			
			//���˴�������
			if(indAuthOperatorList.size() > 0){
				for (int i = 0; i < indAuthOperatorList.size(); i++) {
					indAuthSql.append(" OR (JKBXR='").append(indAuthOperatorList.get(i)).append("' AND DJLXBM='").append(indAuthBillTypeList.get(i)).append("' )");
				}
			}
		}
		
		authPsnList.add(pk_psn);
		String insql = SqlUtils.getInStr("JKBXR", authPsnList.toArray(new String[0]),true);
		
		if(indAuthSql.length() != 0){
			insql += indAuthSql;
		}
		return insql;
	}

	/**
	 * ��ѯ��������
	 * ����������Ȩ���ú���Ȩ��������
	 * 
	 * @param pk_psn
	 * @param pk_group
	 * @param loginUser 
	 * @return
	 * @throws BusinessException
	 * @author: wangyhh@ufida.com.cn
	 */
	private IndAuthorizeVO[] getIndAuthorizeVO(String pk_psn, String pk_group, String loginUser) throws BusinessException {
		List<String> roles = new ArrayList<String>();
		try {
			RoleVO[] roleVOs = NCLocator.getInstance().lookup(IRolePubService.class).queryRoleByUserID(loginUser, null);
			for (RoleVO roleVO : roleVOs) {
				roles.add(roleVO.getPk_role());
			}
		} catch (BusinessException e1) {
			ExceptionHandler.handleException(e1);
		}
		
		String inStr = SqlUtils.getInStr("PK_ROLER", roles.toArray(new String[0]), true);
		String date = new UFDate().toString();
		StringBuffer sqlBuf = new StringBuffer();
		sqlBuf.append(" PK_GROUP ='").append(pk_group).append("' ");
		sqlBuf.append("AND ((PK_USER = '").append(pk_psn).append("' AND '").append(date).append("'<= ENDDATE AND '").append(date).append("'>= STARTDATE AND TYPE = 1 ) OR ( TYPE= 0 AND ").append(inStr).append(" )) ");
		IndAuthorizeVO[] indAuthorizes = NCLocator.getInstance().lookup(IIndAuthorizeQueryService.class).queryIndAuthorizes(sqlBuf.toString());
		return indAuthorizes;
	}

	public BXBusItemVO[] queryItems(JKBXHeaderVO[] header)
			throws BusinessException {
		if (header == null || header.length == 0)
			return null;

		JKBXHeaderVO headerVO = header[0];
		return getBxBusitemBO(headerVO.getDjlxbm(), headerVO.getDjdl())
				.queryByHeaders(header);
	}

	public List<BxcontrastVO> batchContrast(JKBXVO[] selBxvos,
			List<String> mode_data, BatchContratParam param)
			throws BusinessException {
		return new ContrastBO().batchContrast(selBxvos, mode_data, param);
	}

	public void saveBatchContrast(List<BxcontrastVO> selectedData,
			boolean delete) throws BusinessException {
		//new ContrastBO().saveBatchContrast(selectedData, delete);
		new ContrastBO().saveBatchContrastVO(selectedData, delete);
	}

	public Collection<BxcontrastVO> queryJkContrast(JKBXVO[] selBxvos,
			boolean isBatch) throws BusinessException {
		return bxZbBO.queryJkContrast(selBxvos, isBatch);
	}

	public void saveSqdlrs(List<String> roles, SqdlrVO[] sqdlrVOs,
			Map<String, String[]> defMap) throws BusinessException {
		bxZbBO.saveSqdlrs(roles, sqdlrVOs);
		bxZbBO.savedefSqdlrs(roles, defMap);

	}

	public Map<String, List<SqdlrVO>> querySqdlr(String[] pk_roles)
			throws BusinessException {
		return bxZbBO.querySqdlr(pk_roles);
	}

	public void delSqdlrs(List<String> roles, SqdlrVO[] sqdlrVOs)
			throws BusinessException {
		bxZbBO.delSqdlrs(roles, sqdlrVOs);
	}

	public List<SqdlrVO> querySqdlr(String pk_user, String user_corp,
			String ywy_corp) throws BusinessException {
		return bxZbBO.querySqdlr(pk_user, user_corp, ywy_corp);
	}

	public void savedefSqdlrs(List<String> roles, Map<String, String[]> defMap)
			throws BusinessException {
		bxZbBO.savedefSqdlrs(roles, defMap);
	}

	public void copyDefused(BilltypeVO source, BilltypeVO target)
			throws DAOException {
		// BaseDAO baseDAO = new BaseDAO();
		// //ȡ��insert�������
		// Collection<DefusedSuperVO> defs =
		// baseDAO.retrieveByClause(DefusedSuperVO.class,
		// " objcode in ('"+source.getPk_billtypecode()+"','"+source.getPk_billtypecode()+"B')");
		// //�滻objcode��objname
		// String pk_head=null;
		// String pk_body=null;
		// String pk_head_source=null;
		// String pk_body_source=null;
		//
		// for(DefusedSuperVO def:defs){
		// if(def.getObjcode().equals(source.getPk_billtypecode())){
		// def.setObjcode(target.getPk_billtypecode());
		// def.setObjname(target.getBilltypename()+nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006","UPP2006-000530")/*@res
		// "����ͷ"*/);
		//
		//
		// pk_head_source=def.getPrimaryKey();//�������ݵ�����
		// pk_head = baseDAO.insertVO(def);//�仯�������
		// }else{
		// def.setObjcode(target.getPk_billtypecode()+"B");
		// def.setObjname(target.getBilltypename()+nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006","UPP2006-000531")/*@res
		// "������"*/);
		//
		// pk_body_source=def.getPrimaryKey();
		// pk_body = baseDAO.insertVO(def);
		// }
		// }
		//
		// Collection<DefcsttblnmeVO> def_head =
		// baseDAO.retrieveByClause(DefcsttblnmeVO.class,
		// " pk_defused='"+pk_head_source+"' ");
		// //�滻pk_defcsttblnme
		// for(DefcsttblnmeVO def:def_head){
		// def.setPk_defused(pk_head);
		// baseDAO.insertVO(def);
		// }
		// Collection<DefcsttblnmeVO> def_body =
		// baseDAO.retrieveByClause(DefcsttblnmeVO.class,
		// " pk_defused='"+pk_body_source+"' ");
		// //�滻pk_defcsttblnme
		// for(DefcsttblnmeVO def:def_body){
		// def.setPk_defused(pk_body);
		// baseDAO.insertVO(def);
		// }
		//
		// Collection<DefquoteSuperVO> defcsqu_head =
		// baseDAO.retrieveByClause(DefquoteSuperVO.class,
		// " pk_defused='"+pk_head_source+"' ");
		// //�滻pk_defcsttblnme
		// for(DefquoteSuperVO def:defcsqu_head){
		// def.setPk_defused(pk_head);
		// baseDAO.insertVO(def);
		// }
		// Collection<DefquoteSuperVO> defcsqu_body =
		// baseDAO.retrieveByClause(DefquoteSuperVO.class,
		// " pk_defused='"+pk_body_source+"' ");
		// //�滻pk_defcsttblnme
		// for(DefquoteSuperVO def:defcsqu_body){
		// def.setPk_defused(pk_body);
		// baseDAO.insertVO(def);
		// }

	}

	public JKBXVO retriveItems(JKBXHeaderVO header) throws BusinessException {
		JKBXVO bxvo = VOFactory.createVO(header, queryItems(header));
		Collection<BxcontrastVO> contrasts = queryContrasts(bxvo.getParentVO());
		BxcontrastVO[] contrast = contrasts.toArray(new BxcontrastVO[] {});
		bxvo.setContrastVO(contrast);
		bxvo.setChildrenFetched(true);

		Collection<CShareDetailVO> cShares = bxZbBO
				.queryCSharesVOS(new JKBXHeaderVO[] { bxvo.getParentVO() });// ��̯��ϸ
		bxvo.setcShareDetailVo(cShares.toArray(new CShareDetailVO[] {}));
		
		// ����Ԥ����ϸ
		Collection<AccruedVerifyVO> vaccrueds = bxZbBO.queryAccruedVerifyVOS(bxvo.getParentVO());
		bxvo.setAccruedVerifyVO(vaccrueds.toArray(new AccruedVerifyVO[] {}));

		return bxvo;
	}

	public Map<String, String> getTsByPrimaryKey(String[] key,
			String tableName, String pkfield) throws BusinessException {
		return bxZbBO.getTsByPrimaryKey(key, tableName, pkfield);
	}

	@SuppressWarnings("unchecked")
	public List<ReimRuleVO> queryReimRule(String billtype, String pk_org)
			throws BusinessException {
		try {
			StringBuffer buf = new StringBuffer();
			SQLParameter params = new SQLParameter();
			if (!StringUtil.isEmpty(billtype)) {
				if (buf.length() > 0) {
					buf.append(" and ");
				}
				buf.append(" pk_billtype = ? ");
				params.addParam(billtype);
			}

			if (buf.length() > 0) {
				buf.append(" and ");
			}

			if (!StringUtil.isEmpty(pk_org)) {
				buf.append(" pk_org = ? ");
				params.addParam(pk_org);
			} else {
				buf.append(" pk_org is null ");
			}
			return (List<ReimRuleVO>) getBaseDAO().retrieveByClause(
					ReimRuleVO.class, buf.toString(), params);
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public boolean getIsExpensetypeUsed(String pk_expensetype)
			throws BusinessException {
		try {
			// ͨ��ȡ��ҳ�������֯��ѯ��Ӧ�ı�����׼�Ƿ����÷������
			List<ReimRuleVO> reimrulevo = (List<ReimRuleVO>) getBaseDAO()
					.retrieveByClause(
							ReimRuleVO.class,
							"1=1" + " and pk_expensetype='" + pk_expensetype
									+ "' ");
			if (reimrulevo.size() > 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public boolean getIsReimtypeUsed(String pk_reimtype)
			throws BusinessException {
		try {
			// ͨ��ȡ��ҳ�������֯��ѯ��Ӧ�ı�����׼�Ƿ����÷������
			List<ReimRuleVO> reimrulevo = (List<ReimRuleVO>) getBaseDAO()
					.retrieveByClause(ReimRuleVO.class,
							"1=1" + " and pk_reimtype='" + pk_reimtype + "' ");
			if (reimrulevo.size() > 0) {
				return true;
			} else {
				// �ж��Ƿ��ڵ���������
				List<BXBusItemVO> list = (List<BXBusItemVO>) getBaseDAO()
						.retrieveByClause(
								BXBusItemVO.class,
								"1=1" + " and pk_reimtype='" + pk_reimtype
										+ "' ");
				if (list.size() > 0) {
					return true;
				} else {
					return false;
				}
			}
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
	}

	public List<ReimRuleVO> saveReimRule(String billtype, String pk_org,
			ReimRuleVO[] reimRuleVOs) throws BusinessException {
		try {
			if (billtype == null || billtype.trim().length() == 0) {
				return new ArrayList<ReimRuleVO>();
			}
			BaseDAO baseDAO = getBaseDAO();
			if (pk_org == null) {
				baseDAO.deleteByClause(ReimRuleVO.class, "pk_billtype='"
						+ billtype + "' and pk_org is null");
			} else {
				baseDAO.deleteByClause(ReimRuleVO.class, "pk_billtype='"
						+ billtype + "' and pk_org='" + pk_org + "'");
			}
			baseDAO.insertVOArray(reimRuleVOs);
			return queryReimRule(billtype, pk_org);
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
	}

	/**
	 * @author chendya
	 * @return ���ص�¼�û�����������Ա������ֵ1������Ա���ڵĲ��ţ�����ֵ2����������֯(����ֵ3),��������(����ֵ4)
	 * @param userid
	 *            �û�id
	 * @param pk_group
	 *            ��ǰ��¼����
	 */
	public String[] queryPsnidAndDeptid(String cuserid, String pk_group)
			throws BusinessException {
		String[] result = new String[4];
		String jkbxr = NCLocator.getInstance().lookup(IUserPubService.class)
				.queryPsndocByUserid(cuserid);
		result[0] = jkbxr;
		if (jkbxr != null) {
			IPsndocPubService pd = NCLocator.getInstance().lookup(
					IPsndocPubService.class);
			PsndocVO[] pm = pd.queryPsndocByPks(new String[] { jkbxr },
					new String[] { PsndocVO.PK_ORG, PsndocVO.PK_GROUP });
			result[1] = pd.queryMainDeptByPandocIDs(jkbxr).get("pk_dept");
			result[2] = pm[0].getPk_org();
			result[3] = pm[0].getPk_group();
		}
		return result;
	}

	@Override
	public String getAgentWhereString(String jkbxr, String rolersql,
			String billtype, String cuserid, String date, String pkOrg)
			throws BusinessException {
		String wherePart = "";
		try {
			if (jkbxr == null || jkbxr.trim().length() == 0) {
				String pk_psndoc = BXBsUtil.getPk_psndoc(cuserid);
				if (pk_psndoc != null) {
					jkbxr = pk_psndoc;
				}
			}
			if (rolersql == null || rolersql.length() == 0) {
				// ҵ�����ɫ
				rolersql = BXBsUtil.getRoleInStr(IRoleConst.BUSINESS_TYPE);
			}
			String dept = null;
			if (jkbxr != null && jkbxr.length() != 0) {
				dept = BXBsUtil.getPsnPk_dept(jkbxr);
			}
			wherePart += " and (bd_psndoc.pk_psndoc='"
					+ jkbxr
					+ "' "
					+ " or ( bd_psndoc.pk_psndoc in(select pk_user from er_indauthorize where type=0 and keyword = 'busiuser' and "
					+ rolersql
					+ ") ) "
					+ " or ( bd_psnjob.pk_dept in(select pk_user from er_indauthorize where type=0 and keyword = 'pk_deptdoc' and "
					+ rolersql
					+ ") )"
					+ " or ((select count(pk_user) from er_indauthorize where type=0 and keyword = 'isall' and pk_user like 'true%' and "
					+ rolersql
					+ ") > 0) "
					+ " or ((select count(pk_user) from er_indauthorize where type=0 and keyword = 'issamedept' and pk_user like 'true%' and "
					+ rolersql + ") > 0 and bd_psnjob.pk_dept ='" + dept
					+ "' ) ";

			String billtypeSql = "";
			if (!StringUtils.isNullWithTrim(billtype)) {
				billtypeSql = " and billtype='" + billtype + "' ";
			}

			wherePart += " or (bd_psndoc.pk_psndoc in(select pk_user from er_indauthorize where pk_operator='"
					+ cuserid
					+ "'"
					+ billtypeSql
					+ " and '"
					+ date
					+ "'<=enddate and '" + date + "'>=startdate)))";

		} catch (Exception e) {
			throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("2011", "UPP2011-000392")/*
																		 * @res
																		 * "���ý����˹��˴���¼����û�й�����ҵ��Ա!"
																		 */);
		}
		return wherePart;
	}

	@Override
	public void saveSqdlVO(List<SqdlrVO> preSaveVOList, String condition)
			throws BusinessException {
		BaseDAO dao = getBaseDAO();
		dao.deleteByClause(SqdlrVO.class, condition);
		dao.insertVOList(preSaveVOList);
	}

	@SuppressWarnings( { "unchecked", "serial" })
	@Override
	public Map<String, List<SqdlrVO>> querySqdlrVO(String sql)
			throws BusinessException {
		return (Map<String, List<SqdlrVO>>) getBaseDAO().executeQuery(
				sql.toString(), new ResultSetProcessor() {
					@Override
					public Object handleResultSet(ResultSet rs)
							throws SQLException {
						Collection<SqdlrVO> list = new ArrayList<SqdlrVO>();
						while (rs.next()) {
							SqdlrVO vo = new SqdlrVO();
							vo.setPk_roler((String) rs.getObject(1));
							vo.setPk_authorize((String) rs.getObject(2));
							vo.setPk_user((String) rs.getObject(3));
							vo.setPk_org((String) rs.getObject(4));
							vo.setKeyword((String) rs.getObject(5));
							list.add(vo);
						}
						SqdlrVO[] vos = list.toArray(new SqdlrVO[0]);
						Map<String, List<SqdlrVO>> map = new HashMap<String, List<SqdlrVO>>();
						for (int i = 0; i < vos.length; i++) {
							final String pk_role = vos[i].getPk_roler();
							if (map.containsKey(pk_role)) {
								List<SqdlrVO> valueList = map.get(pk_role);
								valueList.add(vos[i]);
								map.put(pk_role, valueList);
							} else {
								List<SqdlrVO> valueList = new ArrayList<SqdlrVO>();
								valueList.add(vos[i]);
								map.put(pk_role, valueList);
							}
						}
						return map;
					}
				});
	}

	@Override
	public Map<String, String> queryDefaultOrgAndQcrq(String pk_psndoc)
			throws BusinessException {
		Map<String, String> result = new HashMap<String, String>();
		UFDate startDate = null;
		if (pk_psndoc != null && pk_psndoc.length() > 0) {
			PsndocVO[] persons = NCLocator.getInstance().lookup(
					IPsndocPubService.class).queryPsndocByPks(
					new String[] { pk_psndoc },
					new String[] { PsndocVO.PK_ORG });
			// ��Ա������֯
			String pk_org = persons[0].getPk_org();
			try {
				String yearMonth = NCLocator.getInstance().lookup(
						IOrgUnitPubService.class)
						.getOrgModulePeriodByOrgIDAndModuleID(pk_org,
								BXConstans.ERM_MODULEID);
				if (yearMonth != null && yearMonth.length() != 0) {
					String year = yearMonth.substring(0, 4);
					String month = yearMonth.substring(5, 7);
					if (year != null && month != null) {
						// ������֯�Ļ������
						AccountCalendar calendar = AccountCalendar
								.getInstanceByPk_org(pk_org);
						if (calendar == null) {
							throw new BusinessException(
									nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
											.getStrByID("2011v61013_0",
													"02011v61013-0021")/*
																		 * @res
																		 * "��֯�Ļ���ڼ�Ϊ��"
																		 */);
						}
						calendar.set(year, month);
						if (calendar.getMonthVO() == null) {
							throw new BusinessException(
									nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
											.getStrByID("2011v61013_0",
													"02011v61013-0022")/*
																		 * @res
																		 * "��֯��ʼ�ڼ�Ϊ��"
																		 */);
						}
						startDate = calendar.getMonthVO().getBegindate();
					}
				}
			} catch (BusinessException e) {
				ExceptionHandler.consume(e);
			}
			if (startDate == null) {
				ExceptionHandler.consume(new BusinessException(
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
								"expensepub_0", "02011002-0001")/*
																 * @res
																 * "����֯ģ����������Ϊ��"
																 */));
				return null;
			}
			result.put(pk_org, startDate.toString());
		}
		return result;
	}

	@Override
	public BillTempletVO[] getBillListTplData(BillOperaterEnvVO[] envos)
			throws BusinessException {
		nc.itf.uap.billtemplate.IBillTemplateQry service = NCLocator
				.getInstance().lookup(
						nc.itf.uap.billtemplate.IBillTemplateQry.class);
		BillTempletVO[] billTempletVOs = service.findBillTempletDatas(envos);
		return billTempletVOs;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, SuperVO> getDeptRelCostCenterMap(String pk_group)
			throws BusinessException {
//		boolean isResInstalled = BXUtil.isProductInstalled(pk_group,
//				BXConstans.FI_RES_FUNCODE);
//		if (!isResInstalled) {
//			return null;
//		}
		SQLParameter param = new SQLParameter();
		param.addParam(pk_group);
		Collection<DeptVO> voList = getBaseDAO().retrieveByClause(DeptVO.class,
				"pk_group=?", new String[] { DeptVO.PK_DEPT }, param);
		// �ɱ����Ĳ�ѯ�ӿ�
		ICostCenterQueryOpt service = NCLocator.getInstance().lookup(
				ICostCenterQueryOpt.class);
		Map<String, SuperVO> map = new HashMap<String, SuperVO>();
		for (DeptVO vo : voList) {
			String key = vo.getPk_dept();
			SuperVO[] costCenterVOs = service
					.queryCostCenterVOByDept(new String[] { key });
			if (costCenterVOs != null && costCenterVOs.length > 0) {
				map.put(key, costCenterVOs[0]);
			}
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SuperVO> getVORemoteCall(Class<?> voClassName,
			String condition, String[] fields) throws BusinessException {
		return (List<SuperVO>) getBaseDAO().retrieveByClause(voClassName,
				condition, fields);
	}

	@Override
	public Map<String, String> getPermissonOrgMapCall(String pkUser,
			String nodeCode, String pkGroup) throws BusinessException {
		IFunctionPermissionPubService service = NCLocator.getInstance().lookup(
				nc.pubitf.rbac.IFunctionPermissionPubService.class);
		OrgVO[] orgVOs = service
				.getUserPermissionOrg(pkUser, nodeCode, pkGroup);
		Map<String, String> map = new HashMap<String, String>();

		for (OrgVO vo : orgVOs) {
			map.put(vo.getPk_vid(), vo.getPk_org());

			// //added by chenshuai ��ѯ�����а汾���Ժ�ƽ̨�ṩ�ӿں󣬿��Ի���ƽ̨����
			// if (vo.getPk_org() != null) {
			// StringBuffer buf = new StringBuffer();
			// SQLParameter param = new SQLParameter();
			// param.addParam(vo.getPk_org());
			// buf.append(" pk_org = ? ");
			//
			// List<OrgVersionVO> result = (List<OrgVersionVO>) new
			// BaseDAO().retrieveByClause(
			// OrgVersionVO.class, buf.toString(), param);
			// if (result != null) {
			// for (OrgVersionVO voTemp : result) {
			// map.put(voTemp.getPk_vid(), voTemp.getPk_org());
			// }
			// }
			// }
		}

		return map;
	}

	public List<String> getPassedDeptOfPerson(String[] depts, String userid)
			throws BusinessException {
		Map<String, List<String>> deptsUserMap = new HashMap<String, List<String>>();
		if (depts != null && userid != null) {
			for (int i = 0; i < depts.length; i++) {
				findUserOfDeptByDeptPk(depts[i], deptsUserMap);
			}
		}

		return deptsUserMap.get(userid) == null ? new ArrayList<String>()
				: deptsUserMap.get(userid);
	}

	/**
	 * ���ݹ�˾pk��ѯ�����Ÿ������û�pk�������������corpUserMap��
	 * 
	 * @param deptPk
	 *            ��˾pk
	 * @param deptsUserMap
	 *            <�û������ż���>
	 * @throws BusinessException
	 */
	private void findUserOfDeptByDeptPk(String deptPk,
			Map<String, List<String>> deptsUserMap) throws BusinessException {

		BaseDAO baseDAO = getBaseDAO();

		DeptVO deptdocVO = (DeptVO) baseDAO.retrieveByPK(DeptVO.class, deptPk);
		String user = null;
		if (deptdocVO != null) {
			String psn = deptdocVO.getPrincipal();
			if (psn != null) {
				IUserPubService ucQry = NCLocator.getInstance().lookup(
						IUserPubService.class);
				UserVO muser = ucQry.queryUserVOByPsnDocID(psn);
				if (muser != null) {
					user = muser.getPrimaryKey();

					List<String> deptList = deptsUserMap.get(user);

					if (deptList == null) {
						deptList = new ArrayList<String>();
						deptList.add(deptPk);
					} else {
						if (!deptList.contains(deptPk)) {
							deptList.add(deptPk);
						}
					}
					deptsUserMap.put(user, deptList);
				}
			}
		}
	}

	@Override
	public List<String> getPassedCorpOfPerson(String[] corps, String userid)
			throws BusinessException {
		Map<String, List<String>> corpUserMap = new HashMap<String, List<String>>();
		if (corps != null && userid != null) {
			for (int i = 0; i < corps.length; i++) {
				findUserOfCorpMapByCorp(corps[i], corpUserMap);
			}
		}

		return corpUserMap.get(userid) == null ? new ArrayList<String>()
				: corpUserMap.get(userid);
	}

	/**
	 * ���ݹ�˾id��ѯ����˾�������û�id�������������corpUserMap��
	 * 
	 * @param corpPk
	 *            ��˾id
	 * @param corpUserMap
	 *            <�û�����˾����>
	 * @throws BusinessException
	 */
	private void findUserOfCorpMapByCorp(String corpPk,
			Map<String, List<String>> corpUserMap) throws BusinessException {
		IOrgUnitPubService ogService = NCLocator.getInstance().lookup(
				IOrgUnitPubService.class);
		OrgVO[] orgs = ogService.getOrgs(new String[] { corpPk }, null);
		String user = null;
		if (orgs != null && orgs.length != 0&&orgs[0]!=null) {
			String psn = orgs[0].getPrincipal();
			if (psn != null) {
				IUserPubService ucQry = NCLocator.getInstance().lookup(
						IUserPubService.class);
				UserVO muser = ucQry.queryUserVOByPsnDocID(psn);
				if (muser != null) {
					user = muser.getPrimaryKey();

					List<String> corpList = corpUserMap.get(user);

					if (corpList == null) {
						corpList = new ArrayList<String>();
						corpList.add(corpPk);
					} else {
						if (!corpList.contains(corpPk)) {
							corpList.add(corpPk);
						}
					}
					corpUserMap.put(user, corpList);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public BXHeaderVO[] getBXdjByMthPk(String pkOrg, String begindate,
			String enddate) throws BusinessException {
		String sql = "pk_org=? and djrq>=? and djrq<=? and dr = 0";
		SQLParameter param = new SQLParameter();
		param.addParam(pkOrg);
		param.addParam(begindate);
		param.addParam(enddate);
		Collection<BXHeaderVO> c = getBaseDAO().retrieveByClause(
				BXHeaderVO.class, sql, param);
		if (c == null || c.isEmpty()) {
			return null;

		}
		return c.toArray(new BXHeaderVO[] {});
	}

	@SuppressWarnings("unchecked")
	@Override
	public JKHeaderVO[] getJKdjByMthPk(String pkOrg, String begindate,
			String enddate) throws BusinessException {
		String sql = "pk_org=? and djrq>=? and djrq<=? and dr = 0 ";
		SQLParameter param = new SQLParameter();
		param.addParam(pkOrg);
		param.addParam(begindate);
		param.addParam(enddate);
		Collection<JKHeaderVO> c = getBaseDAO().retrieveByClause(
				JKHeaderVO.class, sql, param);
		if (c == null || c.isEmpty()) {
			return null;

		}
		return c.toArray(new JKHeaderVO[] {});
	}

	private BaseDAO getBaseDAO() {
		if (baseDao == null) {
			baseDao = new BaseDAO();
		}
		return baseDao;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean isExistJKBXVOByMtappPks(String[] mtappPks, String[] billstatus) throws BusinessException {
		if (mtappPks == null || mtappPks.length == 0) {
			return false;
		}

		StringBuffer sql = new StringBuffer(SqlUtil.buildInSql(JKBXHeaderVO.PK_ITEM, mtappPks));

		if (billstatus != null && billstatus.length > 0) {
			sql.append(" and " + SqlUtil.buildInSql(JKBXHeaderVO.SXBZ, billstatus));

			Collection<?> jkVos = MDPersistenceService.lookupPersistenceQueryService().queryBillOfVOByCond(JKVO.class,
					sql.toString(), false);
			
			if (jkVos != null && jkVos.size() > 0) {
				return true;
			}
			
			Collection<?> bxVos = MDPersistenceService.lookupPersistenceQueryService().queryBillOfVOByCond(BXVO.class,
					sql.toString(), false);
			
			if (bxVos != null && bxVos.size() > 0) {
				return true;
			}
		}else{
			Collection<MatterAppVO> matterVos = MDPersistenceService.lookupPersistenceQueryService()
					.queryBillOfVOByPKs(MatterAppVO.class, mtappPks, false);

			if (matterVos != null && matterVos.size() > 0) {
				Map<String, List<MatterAppVO>> billtype2MatterVo = new HashMap<String, List<MatterAppVO>>();

				for (MatterAppVO matterVo : matterVos) {
					String billtype = matterVo.getPk_tradetype();

					if (billtype2MatterVo.get(billtype) == null) {
						List<MatterAppVO> matterList = new ArrayList<MatterAppVO>();
						matterList.add(matterVo);
						billtype2MatterVo.put(billtype, matterList);
					} else {
						billtype2MatterVo.get(billtype).add(matterVo);
					}
				}

				for (Map.Entry<String, List<MatterAppVO>> entry : billtype2MatterVo.entrySet()) {
					IBillFinder billFinder = (IBillFinder) PfUtilTools.findBizImplOfBilltype(entry.getKey(),
							BillTypeSetBillFinder.class.getName());

					String[] types = null;
					try {
						IBillDataFinder dataFinder = billFinder.createBillDataFinder(entry.getKey());
						types = dataFinder.getForwardBillTypes(entry.getKey());// ���ε�������
					} catch (Exception ex) {
						ExceptionHandler.handleException(ex);
					}

					if (types != null && types.length > 0) {
						for (String type : types) {
							LightBillVO[] lightVos = null;
							try {
								lightVos = ErUtil.queryForwardBills(entry.getKey(), new String[] { type }, VOUtil
										.getAttributeValues(entry.getValue().toArray(new MatterAppVO[] {}),
												MatterAppVO.PK_MTAPP_BILL));
							} catch (Exception e) {
								ExceptionHandler.handleException(e);
							}

							if (lightVos != null && lightVos.length > 0) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	@Override
	public List<String> getPassedCenterOfPerson(String[] centers, String userid)
			throws BusinessException {
		Map<String, List<String>> centerUserMap = new HashMap<String, List<String>>();

		if (centers != null && userid != null) {
			// ��ѯȫ���ɱ����ĵĸ�����
			ICostCenterPubService service = NCLocator.getInstance().lookup(
					ICostCenterPubService.class);
			CostCenterVO[] costcenters = service.queryCostCenterVOByPks(
					centers, new String[] { CostCenterVO.PK_COSTCENTER,
							CostCenterVO.PK_PRINCIPAL }, true);
			if (costcenters == null || costcenters.length == 0) {
				return null;
			}
			for (int i = 0; i < costcenters.length; i++) {
				findUserOfCenterByCenterPk(costcenters[i], centerUserMap);
			}
		}

		return centerUserMap.get(userid) == null ? new ArrayList<String>()
				: centerUserMap.get(userid);
	}

	/**
	 * ���ݹ�˾pk��ѯ�����Ÿ������û�pk�������������corpUserMap��
	 * 
	 * @param deptPk
	 *            ��˾pk
	 * @param deptsUserMap
	 *            <�û������ż���>
	 * @throws BusinessException
	 */
	private void findUserOfCenterByCenterPk(CostCenterVO centervo,
			Map<String, List<String>> centerUserMap) throws BusinessException {
		String user = null;
		String psn = centervo.getPk_principal();
		if (psn != null) {
			IUserPubService ucQry = NCLocator.getInstance().lookup(
					IUserPubService.class);
			UserVO muser = ucQry.queryUserVOByPsnDocID(psn);
			if (muser != null) {
				user = muser.getPrimaryKey();

				List<String> deptList = centerUserMap.get(user);

				if (deptList == null) {
					deptList = new ArrayList<String>();
					deptList.add(centervo.getPk_costcenter());
				} else {
					if (!deptList.contains(centervo.getPk_costcenter())) {
						deptList.add(centervo.getPk_costcenter());
					}
				}
				centerUserMap.put(user, deptList);
			}
		}
	}

	
	@Override
	public BXBusItemVO[] queryItemsByPks(String[] pks) throws BusinessException {
		if(pks == null || pks.length == 0){
			return new BXBusItemVO[]{};
		}
		
		@SuppressWarnings("unchecked")
		Collection<BXBusItemVO> result = MDPersistenceService.lookupPersistenceQueryService().queryBillOfVOByCond(
				BXBusItemVO.class, SqlUtil.buildInSql(BXBusItemVO.PK_BUSITEM, pks), false);
		
		return result.toArray(new BXBusItemVO[] {});
	}

	@Override
	public UFDate queryOrgStartDate(String pk_org) throws BusinessException, InvalidAccperiodExcetion {
		String yearMonth = NCLocator.getInstance().lookup(IOrgUnitPubService.class).getOrgModulePeriodByOrgIDAndModuleID(pk_org, BXConstans.ERM_MODULEID);
		UFDate startDate = null;
		if (yearMonth != null && yearMonth.length() != 0) {
			if (yearMonth != null && yearMonth.length() != 0) {
				String year = yearMonth.substring(0, 4);
				String month = yearMonth.substring(5, 7);
				if (year != null && month != null) {
					// ������֯�Ļ������
					AccountCalendar calendar = AccountCalendar.getInstanceByPk_org(pk_org);
					if (calendar == null) {
						throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0", "02011v61013-0021")/*
																																			 * @res
																																			 * "��֯�Ļ���ڼ�Ϊ��"
																																			 */);
					}
					calendar.set(year, month);
					if (calendar.getMonthVO() == null) {
						throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0", "02011v61013-0022")/*
																																			 * @res
																																			 * "��֯��ʼ�ڼ�Ϊ��"
																																			 */);
					}
					startDate = calendar.getMonthVO().getBegindate();
				}
			}
		}
		return startDate;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<JKBXVO> queryJKBXByWhereSql(String sql, boolean islazy)
			throws BusinessException {
		String whereSQL = sql + " and djlxbm <>'2647' and ybje>0 "
				+ " and pk_jkbx not in (select src_id from er_costshare) "
				+ " order by djrq desc";
		List<JKBXVO> vos = (List<JKBXVO>) MDPersistenceService
				.lookupPersistenceQueryService().queryBillOfVOByCond(
						BXVO.class, whereSQL, false);
		// ���˲��ɽ�ת�ı�����
		List<JKBXVO> result = new ArrayList<JKBXVO>();
		for (JKBXVO jkbxvo : vos) {
			if (!ArrayUtil.isArrayIsNull(jkbxvo.getAccruedVerifyVO())) {
				// ���ں���Ԥ����ϸ�ı����������ɽ�ת
				continue;
			}
			result.add(jkbxvo);
		}
		return result;
	}
	
	/**
	 * ������Ч�ĵ��ݣ�������ĩƾ֤
	 * ����ĩ��Ч�ĵ��ݣ������ݹ�ƾ֤
	 * ������Ч�����ݹ��ĵ��ݣ������ݹ���ĩƾ֤
	 */
	public List<JKBXVO> effectToFip(List<JKBXVO> jkbxvo) throws BusinessException{
		List<JKBXHeaderVO> header = new ArrayList <JKBXHeaderVO>();
		//�汾��tsУ��
		new BXZbBO().compareTs(jkbxvo.toArray(new JKBXVO[]{}));
		
		for (JKBXVO vo : jkbxvo) {
			if(BXStatusConst.SXBZ_VALID==vo.getParentVO().getSxbz()){
				if(vo.getParentVO().getVouchertag()!= null 
						&& (BXStatusConst.ZGDeal==vo.getParentVO().getVouchertag()
						|| BXStatusConst.ZGMEFlag==vo.getParentVO().getVouchertag())){
					vo.getParentVO().setVouchertag(BXStatusConst.ZGMEFlag);//��Ч�ĵ������ݹ�����ĩ�ݹ�,��ĩ�ݹ�ƾ֤
				}
				if(vo.getParentVO().getVouchertag()==null || BXStatusConst.MEDeal==vo.getParentVO().getVouchertag()){
					vo.getParentVO().setVouchertag(BXStatusConst.MEDeal);//��Ч�ĵ���û���ݹ�����ĩƾ֤
				}
			}else{
				vo.getParentVO().setVouchertag(BXStatusConst.ZGDeal);//δ��Ч�ĵ������ݹ�ƾ֤
			}
			header.add(vo.getParentVO());
		}
		getBaseDAO().updateVOArray(header.toArray(new JKBXHeaderVO[]{}), new String[]{JKBXHeaderVO.VOUCHERTAG});
		//���ݱ�ͷ��������Ϣ:�����ƽ̨ʱ���������ֵ
		List<JKBXVO> vos = retriveItems(header);
		
		new BXZbBO().effectToFip(vos, BXZbBO.MESSAGE_UNSETTLE);//��ɾ��ƾ֤
		
		new BXZbBO().effectToFip(vos, BXZbBO.MESSAGE_SETTLE);//������ƾ֤
		
		return vos;
	}
	
//	/**
//	 * ���õ���״̬
//	 * ɾ��������Ϣ
//	 * �ͷ���ռԤ��
//	 * ɾ��������
//	 */
//	@Override
//	public JKBXVO invalidBill(JKBXVO jkbxvos) throws BusinessException {
//		List<JKBXHeaderVO> header = new ArrayList <JKBXHeaderVO>();
//		//�汾��tsУ��
//		new BXZbBO().compareTs(jkbxvo.toArray(new JKBXVO[]{}));
//		
//		//1.�ȸ��µ��ݵĵ���״̬
//		for (JKBXVO jkbxVO : jkbxvo) {
//			jkbxVO.getParentVO().setDjzt(BXStatusConst.DJZT_Invalid);
//			header.add(jkbxVO.getParentVO());
//		}
//		getBaseDAO().updateVOArray(header.toArray(new JKBXHeaderVO[]{}), new String[]{JKBXHeaderVO.DJZT});
//		
//		List<String> pksList = new ArrayList<String>();
//
//		try {
//			for (JKBXVO vo : jkbxvo) {
//				// �ж�CMP��Ʒ�Ƿ�����
//				boolean isCmpInstalled = BXZbBO.isCmpInstall(vo.getParentVO());
//
//				// �Ƿ� �����տ�Ҳ�޸���
//				boolean notExistsPayOrRecv = (vo.getParentVO().getZfybje() == null || vo
//						.getParentVO().getZfybje().equals(new UFDouble(0)))
//						&& (vo.getParentVO().getHkybje() == null || vo.getParentVO().getHkybje().equals(new UFDouble(0)));
//
//				if (!notExistsPayOrRecv && isCmpInstalled) {
//					//2.��ɾ�����ݵĽ�����Ϣ
//					new ErForCmpBO().invokeCmp(vo, vo.getParentVO().getDjrq(),
//							BusiStatus.Deleted);
//				}
//
//				// ɾ�����������Ϣ
//				new ContrastBO().deleteByPK_bxd(new String[] { vo.getParentVO().getPk_jkbx() });
//
//				// ɾ���������� Ԥ����ϸ
//				new BxVerifyAccruedBillBO().deleteByBxdPks(vo.getParentVO().getPk_jkbx());
//				
//				
//				pksList.add(vo.getParentVO().getPk_jkbx());
//				
//			}
//		} catch (SQLException e) {
//			ExceptionHandler.handleException(e);
//		}
//		
//		List<JKBXVO> vos = retriveItems(header);
//		
//		//����ҵ���¼����
//		EventDispatcher.fireEvent(new ErmBusinessEvent(BXConstans.ERM_MDID_BX, ErmEventType.TYPE_InValid_AFTER, vos.toArray(new JKBXVO[]{})));
//		
//		return vos;
//	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public JKBXVO[] settleRedHandleSaveAndSign(NetPayExecInfo payInfo, Map map)
			throws BusinessException {
		
		@SuppressWarnings("unchecked")
		Map<String, SettlementBodyVO[]> copy = map;
		
		//������ϸ��Ϣpk_detail
		Map<String,SettlementBodyVO> pk_bill_detailMap = new HashMap<String,SettlementBodyVO>();
		
		//������Ϣ�����ֹ��Ļ�����Ҫ���кϲ�
		for(Map.Entry entry : copy.entrySet()) {
			SettlementBodyVO[] settlementBodyVOs = (SettlementBodyVO[]) entry.getValue();
			for (SettlementBodyVO settlementBodyVO : settlementBodyVOs) {
				if (!pk_bill_detailMap.containsKey(settlementBodyVO.getPk_billdetail())) {
					pk_bill_detailMap.put(settlementBodyVO.getPk_billdetail(), settlementBodyVO);
				} else {
					SettlementBodyVO bodyVO = (SettlementBodyVO) pk_bill_detailMap.get(settlementBodyVO.getPk_billdetail());
					getCombinationVO(bodyVO, settlementBodyVO);
				}
			}
		}
		
		//��ѯԭ��������ͷ����Ϣ��
		List<BXBusItemVO>  childs = new ArrayList<BXBusItemVO>();
		Set<Entry<String, SettlementBodyVO>> entrySet = pk_bill_detailMap.entrySet();
		for (Entry<String, SettlementBodyVO> entry : entrySet) {
			SettlementBodyVO settleBodyVo = entry.getValue();
			if(settleBodyVo== null){
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006v61020_0","02006v61020-0113")/*@res "�����������Ϣ����Ϊ��"*/);
			}else {
				childs.addAll(Arrays.asList(generateItems(settleBodyVo)));
			}
		}
		
		List<JKBXHeaderVO> oldJkbxHead = NCLocator.getInstance().lookup(IBXBillPrivate.class).queryHeadersByPrimaryKeys(new String[]{payInfo.getBillid()}, payInfo.getBilltype());
		
		JKBXHeaderVO header = generateHead(payInfo.getBilltype(), oldJkbxHead.get(0));
		
		JKBXVO bxvo = VOFactory.createVO(header,childs == null ? new BXBusItemVO[] {} : childs.toArray(new BXBusItemVO[] {}));
		
		List<JKBXVO>  writeBackBillVOs = new ArrayList<JKBXVO>();
		writeBackBillVOs.add(bxvo);
		return saveRedBills(writeBackBillVOs, oldJkbxHead.get(0));
	}
	/**
	 * �ϲ�������Ϣ
	 * @param cBodyVO
	 * @param settlementBodyVO
	 */
	private void getCombinationVO(SettlementBodyVO cBodyVO, SettlementBodyVO settlementBodyVO) {
		cBodyVO.setPay( MathTool.add(cBodyVO.getPay(), settlementBodyVO.getPay()));
		cBodyVO.setPaylocal( MathTool.add(cBodyVO.getPaylocal(), settlementBodyVO.getPaylocal()));
		cBodyVO.setGrouppaylocal( MathTool.add(cBodyVO.getGrouppaylocal(), settlementBodyVO.getGrouppaylocal()));
		cBodyVO.setGlobalpaylocal( MathTool.add(cBodyVO.getGlobalpaylocal(), settlementBodyVO.getGlobalpaylocal()));
	}
	/**
	 * �����嵥��
	 * @param writeBackBillVOs
	 * @param arrayList
	 * @return
	 * @throws BusinessException 
	 */
	private JKBXVO[] saveRedBills(List<JKBXVO> writeBackBillVOs,
			JKBXHeaderVO oldvo) throws BusinessException {
		JKBXVO[] writeBackBillVO = ErmBillPubUtil.getWriteBackBillVO(
				writeBackBillVOs.toArray(new JKBXVO[0]), new UFDate(
						InvocationInfoProxy.getInstance().getBizDateTime()),
				InvocationInfoProxy.getInstance().getUserId());
		// �����嵥��
		JKBXVO[] jkbxvos = NCLocator.getInstance().lookup(IBXBillPublic.class).save(writeBackBillVO);
		
		//��嵥�ݱ������Ч
		List<JKBXVO> returnVos = new ArrayList<JKBXVO>();
		MessageVO[] messageVO = new BXZbBO().audit(jkbxvos);
		for (MessageVO message : messageVO) {
			if(message.isSuccess()){
				returnVos.add((JKBXVO)message.getSuccessVO());
			}
		}
		//��ѯ�������ĵ���:�����ֶ�
		oldvo.setRed_status(BXStatusConst.RED_STATUS_REDED);
		oldvo.setRedbillpk(returnVos.get(0).getParentVO().getPrimaryKey());
		new BaseDAO().updateVOArray(new JKBXHeaderVO[]{oldvo}, new String[]{JKBXHeaderVO.RED_STATUS, JKBXHeaderVO.REDBILLPK});
		return returnVos.toArray(new JKBXVO[0]);
	}
	
	/**
	 * ���ñ�ͷ������
	 * @param billType
	 * @param sbodyVO
	 * @return
	 */
	private JKBXHeaderVO generateHead(String billType ,JKBXHeaderVO oldHeadVO) {
		JKBXHeaderVO headVO = VOFactory.createHeadVO(billType);
		
		headVO = (JKBXHeaderVO)oldHeadVO.clone();
		headVO.setRedbillpk(oldHeadVO.getPk_jkbx());//������ID
		headVO.setPk_jkbx(null);
		//��ͷ��Ϣ
		headVO.setDjbh(null);
		headVO.setPaydate(null);
		
		//��ת/̯����Ϣ������
		headVO.setIscostshare(UFBoolean.FALSE);
		headVO.setIsexpamt(UFBoolean.FALSE);
		headVO.setStart_period(null);
		headVO.setTotal_period(Integer.valueOf(0));
		
		//���������Ϣ
		headVO.setCreator(null);
		headVO.setCreationtime(null);
		headVO.setJsr(null);
		headVO.setJsrq(null);
		headVO.setModifiedtime(null);
		headVO.setModifier(null);
		
		//������Ϣɾ��
		headVO.setPk_item(null);
		headVO.setPk_mtapp_detail(null);
		headVO.setIsmashare(UFBoolean.FALSE);
		headVO.setSrcbilltype(null);
		headVO.setSrctype(null);
		
		// �������
		headVO.setTotal(UFDouble.ZERO_DBL);
		headVO.setYbje(UFDouble.ZERO_DBL);
		headVO.setBbje(UFDouble.ZERO_DBL);
		headVO.setGroupbbje(UFDouble.ZERO_DBL);
		headVO.setGlobalbbje(UFDouble.ZERO_DBL);

		headVO.setZfybje(UFDouble.ZERO_DBL);
		headVO.setZfbbje(UFDouble.ZERO_DBL);
		headVO.setGroupzfbbje(UFDouble.ZERO_DBL);
		headVO.setGlobalzfbbje(UFDouble.ZERO_DBL);

		headVO.setCjkybje(UFDouble.ZERO_DBL);
		headVO.setCjkbbje(UFDouble.ZERO_DBL);
		headVO.setGroupcjkbbje(UFDouble.ZERO_DBL);
		headVO.setGlobalcjkbbje(UFDouble.ZERO_DBL);
		
		headVO.setYjye(UFDouble.ZERO_DBL);
		
		
//		//���ñ�ͷ���ֶ�
//		headVO.setZy(oldHeadVO.getZy());
//		headVO.setDjlxbm(oldHeadVO.getDjlxbm());
//		headVO.setDjdl(oldHeadVO.getDjdl());
//		headVO.setCenter_dept(oldHeadVO.getCenter_dept());
//		headVO.setInit(oldHeadVO.isInit());
//		headVO.setIsinitgroup(oldHeadVO.getIsinitgroup());
//		headVO.setSzxmid(oldHeadVO.getSzxmid());
//		//֧����λ
//		headVO.setPk_payorg(oldHeadVO.getPk_payorg());
//		headVO.setPk_payorg_v(oldHeadVO.getPk_payorg_v());
//		//����֯
//		headVO.setPk_org(oldHeadVO.getPk_org());
//		headVO.setPk_org_v(oldHeadVO.getPk_org_v());
//		//���óе���λ
//		headVO.setFydwbm(oldHeadVO.getFydwbm());
//		headVO.setFydwbm_v(oldHeadVO.getFydwbm_v());
//		//�����˵�λ
//		headVO.setDwbm(oldHeadVO.getDwbm());
//		headVO.setDwbm_v(oldHeadVO.getDwbm_v());
//		//��������
//		headVO.setPk_pcorg(oldHeadVO.getPk_pcorg());
//		headVO.setPk_pcorg_v(oldHeadVO.getPk_pcorg_v());
//		//�ɱ�����
//		headVO.setPk_resacostcenter(oldHeadVO.getPk_resacostcenter());
//		//��Ʒ�ߺ�Ʒ��
//		headVO.setPk_proline(oldHeadVO.getPk_proline());
//		headVO.setPk_brand(oldHeadVO.getPk_brand());
//		//����
//		headVO.setFydeptid(oldHeadVO.getFydeptid());
//		headVO.setDeptid(oldHeadVO.getDeptid());
//		headVO.setFydeptid_v(oldHeadVO.getFydeptid_v());
//		headVO.setDeptid_v(oldHeadVO.getDeptid_v());
//		//��Ա
//		headVO.setCreator(oldHeadVO.getCreator());
//		headVO.setOperator(oldHeadVO.getOperator());
//		headVO.setJsr(oldHeadVO.getJsr());
//		headVO.setJkbxr(oldHeadVO.getJkbxr());
//		headVO.setReceiver(oldHeadVO.getReceiver());
//		headVO.setApprover(oldHeadVO.getApprover());
//		
//		headVO.setPk_fiorg(oldHeadVO.getPk_fiorg());
//		headVO.setPk_group(oldHeadVO.getPk_group());
//		headVO.setPjh(oldHeadVO.getPjh());
//		headVO.setChecktype(oldHeadVO.getChecktype());
//		headVO.setBzbm(oldHeadVO.getBzbm());
//		//����
//		headVO.setGroupbbhl(oldHeadVO.getGroupbbhl() == null ? UFDouble.ZERO_DBL : oldHeadVO.getGroupbbhl());
//		headVO.setGlobalbbhl(oldHeadVO.getGlobalbbhl() == null ? UFDouble.ZERO_DBL : oldHeadVO.getGlobalbbhl());
//		headVO.setBbhl(oldHeadVO.getBbhl()==null ? UFDouble.ZERO_DBL : oldHeadVO.getBbhl());
//		
//		headVO.setCashproj(oldHeadVO.getCashproj());
//		
//		headVO.setFkyhzh(oldHeadVO.getFkyhzh());
//		headVO.setPk_cashaccount(oldHeadVO.getPk_cashaccount());
//		headVO.setBusitype(oldHeadVO.getBusitype());
//		headVO.setCashitem(oldHeadVO.getCashitem());
//
//		headVO.setPaytarget(oldHeadVO.getPaytarget());
//		headVO.setSkyhzh(oldHeadVO.getSkyhzh());
//		headVO.setHbbm(oldHeadVO.getHbbm());
//		headVO.setCustaccount(oldHeadVO.getCustaccount());
//		headVO.setCustomer(oldHeadVO.getCustomer());
//		headVO.setFreecust(oldHeadVO.getFreecust());
//		
//		//�Զ�������Ϣ
//		String[] defKey= new String[]{JKBXHeaderVO.ZYX1,JKBXHeaderVO.ZYX2,JKBXHeaderVO.ZYX3,JKBXHeaderVO.ZYX4,JKBXHeaderVO.ZYX5,JKBXHeaderVO.ZYX6,JKBXHeaderVO.ZYX7,JKBXHeaderVO.ZYX8,JKBXHeaderVO.ZYX9,JKBXHeaderVO.ZYX10,
//				JKBXHeaderVO.ZYX11,JKBXHeaderVO.ZYX12,JKBXHeaderVO.ZYX13,JKBXHeaderVO.ZYX14,JKBXHeaderVO.ZYX15,JKBXHeaderVO.ZYX16,JKBXHeaderVO.ZYX17,JKBXHeaderVO.ZYX18,JKBXHeaderVO.ZYX19,JKBXHeaderVO.ZYX20,
//				JKBXHeaderVO.ZYX21,JKBXHeaderVO.ZYX22,JKBXHeaderVO.ZYX23,JKBXHeaderVO.ZYX24,JKBXHeaderVO.ZYX25,JKBXHeaderVO.ZYX26,JKBXHeaderVO.ZYX27,JKBXHeaderVO.ZYX28,JKBXHeaderVO.ZYX29,JKBXHeaderVO.ZYX30};
//		for (String def : defKey) {
//			headVO.setAttributeValue(def, oldHeadVO.getAttributeValue(def));
//		}
		return headVO;
	}
	/**
	 * ���ñ��������
	 * @param sbodyVOs
	 * @return
	 * @throws BusinessException 
	 */
	@SuppressWarnings("unchecked")
	private BXBusItemVO generateItems(SettlementBodyVO sbodyVO) throws BusinessException {
		String[] pk_busitem = VOUtils.getAttributeValues(new SettlementBodyVO[]{sbodyVO}, "pk_billdetail");
		String condition = nc.vo.fi.pub.SqlUtils.getInStr(BXBusItemVO.PK_BUSITEM, pk_busitem,false);
		
		List<BXBusItemVO> oriBusItemList = (List<BXBusItemVO>) new BaseDAO().retrieveByClause(BXBusItemVO.class, condition);
		//��ԭ�����ݵ�item��Ϣ�õ�
		BXBusItemVO bodyVO = (BXBusItemVO)oriBusItemList.get(0).clone();
		bodyVO.setPrimaryKey(null);
		bodyVO.setPk_jkbx(null);
		
		//�������
		bodyVO.setAmount(sbodyVO.getPay());
		bodyVO.setYbje(sbodyVO.getPay());
		bodyVO.setBbje(sbodyVO.getPaylocal());
		bodyVO.setGroupbbje(sbodyVO.getGrouppaylocal());
		bodyVO.setGlobalbbje(sbodyVO.getGlobalpaylocal());
		
		bodyVO.setZfybje(sbodyVO.getPay());
		bodyVO.setZfbbje(sbodyVO.getPaylocal());
		bodyVO.setGroupzfbbje(sbodyVO.getGrouppaylocal());
		bodyVO.setGlobalzfbbje(sbodyVO.getGlobalpaylocal());
		
		bodyVO.setCjkybje(UFDouble.ZERO_DBL);
		bodyVO.setCjkbbje(UFDouble.ZERO_DBL);
		bodyVO.setGroupcjkbbje(UFDouble.ZERO_DBL);
		bodyVO.setGlobalcjkbbje(UFDouble.ZERO_DBL);
		
		//������Ϣȥ��
		bodyVO.setSrctype(null);
		bodyVO.setSrcbilltype(null);
		bodyVO.setPk_item(null);
		bodyVO.setPk_mtapp_detail(null);
		
		return bodyVO;
	}
	
//	/**
//	 * ��������Ϣ�����VOתΪ����ҵ����VO
//	 * @param sbodyVO
//	 * @return
//	 */
//	private BXBusItemVO generateItems(BXBusItemVO oldBusitemVO) {
//		
////		bodyVO.setAmount(oldBusitemVO.getAmount());
////		bodyVO.setYbje(oldBusitemVO.getYbje());
////		bodyVO.setYbye(oldBusitemVO.getYbye());
////		bodyVO.setYjye(oldBusitemVO.getYjye());
////		bodyVO.setBbje(oldBusitemVO.getBbje());
////		bodyVO.setBbye(oldBusitemVO.getBbye());
////		bodyVO.setZfybje(oldBusitemVO.getZfybje());
////		bodyVO.setZfbbje(oldBusitemVO.getZfbbje());
////		bodyVO.setHkybje(oldBusitemVO.getHkybje());
////		bodyVO.setHkbbje(oldBusitemVO.getHkbbje());
////		bodyVO.setGroupbbje(oldBusitemVO.getGroupbbje());
////		bodyVO.setGroupbbye(oldBusitemVO.getGroupbbye());
////		bodyVO.setGroupzfbbje(oldBusitemVO.getGroupzfbbje());
////		bodyVO.setGrouphkbbje(oldBusitemVO.getGrouphkbbje());
////		bodyVO.setGlobalbbje(oldBusitemVO.getGlobalbbje());
////		bodyVO.setGlobalbbye(oldBusitemVO.getGlobalbbye());
////		bodyVO.setGlobalzfbbje(oldBusitemVO.getGlobalzfbbje());
////		bodyVO.setGlobalhkbbje(oldBusitemVO.getGlobalhkbbje());
//		//}
//		
////		bodyVO.setTablecode(oldBusitemVO.getTablecode());
//		
////		bodyVO.setDwbm(oldBusitemVO.getDwbm());
////		bodyVO.setDeptid(oldBusitemVO.getDeptid());
////		bodyVO.setJkbxr(oldBusitemVO.getJkbxr());
////		bodyVO.setSzxmid(oldBusitemVO.getSzxmid());
////		bodyVO.setJobid(oldBusitemVO.getJobid());
////		bodyVO.setProjecttask(oldBusitemVO.getProjecttask());
////		
////		bodyVO.setPaytarget(oldBusitemVO.getPaytarget());
////		bodyVO.setReceiver(oldBusitemVO.getReceiver());
////		bodyVO.setSkyhzh(oldBusitemVO.getSkyhzh());
////		bodyVO.setHbbm(oldBusitemVO.getHbbm());
////		bodyVO.setCustaccount(oldBusitemVO.getCustaccount());
////		bodyVO.setCustomer(oldBusitemVO.getCustomer());
////		bodyVO.setFreecust(oldBusitemVO.getFreecust());
////		bodyVO.setFreeaccount(oldBusitemVO.getFreeaccount());
//		
//		//�����Զ����ֶ�
////		String defitem_prefix="defitem";
////		for(int i=1 ; i<=50 ; i++){
////			bodyVO.setAttributeValue(defitem_prefix+i, oldBusitemVO.getAttributeValue(defitem_prefix+i));
////		}
//		return bodyVO;
//	}
}