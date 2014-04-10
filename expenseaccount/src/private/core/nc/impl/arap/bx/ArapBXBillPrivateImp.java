package nc.impl.arap.bx;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nc.bd.accperiod.InvalidAccperiodExcetion;
import nc.bs.arap.bx.BXZbBO;
import nc.bs.arap.bx.ContrastBO;
import nc.bs.arap.bx.IBXBusItemBO;
import nc.bs.arap.bx.VoucherRsChecker;
import nc.bs.arap.util.SqlUtils;
import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.er.util.BXBsUtil;
import nc.bs.er.util.SqlUtil;
import nc.bs.framework.common.NCLocator;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.arap.pub.ISqdlrKeyword;
import nc.itf.er.indauthorize.IIndAuthorizeQueryService;
import nc.itf.org.IDeptQryService;
import nc.itf.resa.costcenter.ICostCenterQueryOpt;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.md.model.MetaDataException;
import nc.md.persist.framework.MDPersistenceService;
import nc.pubitf.accperiod.AccountCalendar;
import nc.pubitf.org.IOrgUnitPubService;
import nc.pubitf.rbac.IFunctionPermissionPubService;
import nc.pubitf.rbac.IRolePubService;
import nc.pubitf.rbac.IUserPubService;
import nc.pubitf.resa.costcenter.ICostCenterPubService;
import nc.pubitf.uapbd.IPsndocPubService;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXUtil;
import nc.vo.arap.bx.util.CurrencyControlBO;
import nc.vo.bd.psn.PsndocVO;
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
import nc.vo.erm.common.MessageVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.util.VOUtils;
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
import nc.vo.pub.lang.UFDate;
import nc.vo.resa.costcenter.CostCenterVO;
import nc.vo.sm.UserVO;
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
//		List<JKBXHeaderVO> name = queryHeadersByPrimaryKeys(keys, djdl);
//		List<JKBXVO> vos = retriveItems(name);
//		return vos;
		
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
		List<JKBXVO> list = new ArrayList<JKBXVO>();
		if(!isCommonBill){
		    if (djdl == null || BXConstans.BX_DJDL.equals(djdl)) {
		        list = queryJKBXHeadVOBypks(keys, BXConstans.BX_DJDL);
		        if (list == null || (list != null && list.size() != keys.length)) {
		            List<JKBXVO> result2 = queryJKBXHeadVOBypks(keys, BXConstans.JK_DJDL);
		            list.addAll(result2);
		        }
		    } else {
		        list = queryJKBXHeadVOBypks(keys, djdl);
		    }
		}else{
		    String inStr = SqlUtils.getInStr(JKBXHeaderVO.PK_JKBX, keys);
		    List<InitHeaderVO> headList = (List<InitHeaderVO>) new BaseDAO().retrieveByClause(InitHeaderVO.class, inStr);
		    for (InitHeaderVO initHeaderVO : headList)
            {
               if(BXConstans.BX_DJDL.equals(initHeaderVO.getDjdl())){
                   BXHeaderVO bxHeaderVO = new BXHeaderVO();
                   convertCommonBillVo(initHeaderVO, bxHeaderVO);
                   list.add(new BXVO(bxHeaderVO));
               }else if(BXConstans.JK_DJDL.equals(initHeaderVO.getDjdl())){
                   JKHeaderVO jkHeaderVO = new JKHeaderVO();
                   convertCommonBillVo(initHeaderVO, jkHeaderVO);
                   list.add(new JKVO(jkHeaderVO));
               }
            }
		}
		try {
			if (djCondVO != null && djCondVO.getVoucherFlags()!=null && djCondVO.getVoucherFlags().length!=0) {
				//����ƾ֤״̬����
				JKBXHeaderVO[] newVO = dealVoucherInfo(djCondVO.isLinkPz(), list, djCondVO.getVoucherFlags());
				List<String> pk_jkbx=new ArrayList<String>();
				for(JKBXHeaderVO vo:newVO){
					pk_jkbx.add(vo.getPk_jkbx());
				}
				List<JKBXVO> newList =new ArrayList<JKBXVO>();
				for(JKBXVO vo:list){
					if(pk_jkbx.contains(vo.getParentVO().getPk_jkbx())){
						newList.add(vo);
					}
				}
				return newList;
			}else if (djCondVO != null && djCondVO.isLinkPz) {
				// ����ƾ֤��
				Integer[] voucherFlag = new Integer[] {
						VoucherRsChecker.VOUCHER_EXIST_FLAG,
						VoucherRsChecker.VOUCHER_EXIST_JZ_FLAG };
				dealVoucherInfo(djCondVO.isLinkPz(), list, voucherFlag);
			}
		} catch (SQLException e) {
			ExceptionHandler.handleException(e);
		}
		return list;
	}

	private JKBXHeaderVO[] dealVoucherInfo(boolean showVoucherNo, List<JKBXVO> list,
			Integer[] voucherFlag) throws SQLException {
		JKBXHeaderVO[] tempJKBXHeaderVO = new JKBXHeaderVO[list.size()];
		for (int i = 0; i < list.size(); i++) {
			tempJKBXHeaderVO[i] = list.get(i).getParentVO();
		}
		CircularlyAccessibleValueObject[] reslut = new VoucherRsChecker(showVoucherNo, voucherFlag).getReslut(tempJKBXHeaderVO);
		JKBXHeaderVO[] dealVoucher=new JKBXHeaderVO[reslut.length];
		for(int j=0;j<reslut.length;j++){
			if(reslut[j] instanceof JKBXHeaderVO){
				dealVoucher[j]=(JKBXHeaderVO)reslut[j];
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
    private void convertCommonBillVo(InitHeaderVO initHeaderVO, JKBXHeaderVO vo)
    {
        String[] attributeNames = vo.getAttributeNames();
           for (String attributeName : attributeNames)
           {
               vo.setAttributeValue(attributeName, initHeaderVO.getAttributeValue(attributeName));
           }
           vo.setInit(true);
    }

	/**
	 * ��ѯ��������vo
	 * 
	 * @param keys
	 * @throws MetaDataException
	 * @author: wangyhh@ufida.com.cn
	 */
	@SuppressWarnings("unchecked")
	private List<JKBXVO> queryJKBXHeadVOBypks(String[] keys,String djdl) throws MetaDataException {
		String where = SqlUtils.getInStr("pk_jkbx", keys) + " and dr=0 order by djrq desc, djbh desc";
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
			bxvo.setContrastVO(contrastVoList == null ? null : contrastVoList
					.toArray(new BxcontrastVO[] {}));
			bxvo.setcShareDetailVo(cShareList == null ? null : cShareList
					.toArray(new CShareDetailVO[] {}));
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
	public String[] queryPKsByWhereForBillManageNode(String sql,String pk_group,String loginUser) throws BusinessException {
		//���ݹ���sql��׷������Ȩ�޿��ƣ�ֻ�����ѯ����ǰ�û��������˵ĵ���
		String workflowSql = " PK_GROUP= '" + pk_group + 
							"' AND (APPROVER= '" + loginUser + "'OR (PK_JKBX IN(SELECT BILLID FROM PUB_WORKFLOWNOTE WF WHERE WF.CHECKMAN='" + loginUser + "' AND ISNULL(WF.DR,0)   = 0 AND WF.ACTIONTYPE <> 'MAKEBILL')))";
		if(sql == null){
			sql = workflowSql;
		}else{
			sql += " and " + workflowSql;
			sql +=" order by djrq desc, djbh desc ";
		}
		 
		String[] result = queryPKsByWhereSql(sql,null);
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
				String insql = SqlUtils.getInStr("PK_DEPT", agentDeptList.toArray(new String[0]));
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
		String insql = SqlUtils.getInStr("JKBXR", authPsnList.toArray(new String[0]));
		
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
		
		String inStr = SqlUtils.getInStr("PK_ROLER", roles.toArray(new String[0]));
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
		boolean isResInstalled = BXUtil.isProductInstalled(pk_group,
				BXConstans.FI_RES_FUNCODE);
		if (!isResInstalled) {
			return null;
		}
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

	@Override
	public boolean isExistJKBXVOByMtappPks(String[] mtappPks, String[] billstatus) throws BusinessException {
		if (mtappPks == null || mtappPks.length == 0) {
			return false;
		}

		StringBuffer sql = new StringBuffer(SqlUtil.buildInSql(JKBXHeaderVO.PK_ITEM, mtappPks));

		if (billstatus != null && billstatus.length > 0) {
			sql.append(" and " + SqlUtil.buildInSql(JKBXHeaderVO.SXBZ, billstatus));
		}

		@SuppressWarnings("rawtypes")
		Collection jkVos = MDPersistenceService.lookupPersistenceQueryService().queryBillOfVOByCond(JKVO.class,
				sql.toString(), false);

		if (jkVos != null && jkVos.size() > 0) {
			return true;
		}

		@SuppressWarnings("rawtypes")
		Collection bxVos = MDPersistenceService.lookupPersistenceQueryService().queryBillOfVOByCond(BXVO.class,
				sql.toString(), false);

		if (bxVos != null && bxVos.size() > 0) {
			return true;
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
		String whereSQL = sql+" and djlxbm <>'2647' "+" and pk_jkbx not in (select src_id from er_costshare) order by djrq desc";
		 return (List<JKBXVO>) MDPersistenceService.lookupPersistenceQueryService().
			queryBillOfVOByCond(BXVO.class, whereSQL , false);
	}
}