package nc.impl.arap.bx;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.bs.arap.bx.BXZbBO;
import nc.bs.dao.BaseDAO;
import nc.bs.er.util.SqlUtil;
import nc.bs.erm.util.ErBudgetUtil;
import nc.bs.framework.common.NCLocator;
import nc.itf.arap.pub.IBXBillPublic;
import nc.itf.tb.control.IAccessableOrgsBusiVO;
import nc.itf.tb.control.IBudgetControl;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.pubitf.erm.matterapp.IErmMatterAppBillQuery;
import nc.pubitf.erm.matterappctrl.IMtapppfVOQryService;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BXHeaderVO;
import nc.vo.ep.bx.BXVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.pub.IFYControl;
import nc.vo.erm.common.MessageVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.matterapp.MatterAppYsControlVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.erm.matterappctrl.MtapppfVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.tb.control.DataRuleVO;

import org.apache.commons.lang.ArrayUtils;

/**
 * nc.impl.arap.bx.ArapBXBillPublicImp
 * 
 * @author twei
 * 
 *         借款报销类单据公共业务处理接口实现
 */
public class ArapBXBillPublicImp implements IBXBillPublic {
	private BXZbBO bxZbBO = new BXZbBO();

	public MessageVO[] deleteBills(JKBXVO[] bxvos) throws BusinessException {

		return bxZbBO.delete(bxvos);
	}

	public JKBXVO[] save(JKBXVO[] vos) throws BusinessException {
		return bxZbBO.save(vos);
	}

	public JKBXVO[] tempSave(JKBXVO[] vos) throws BusinessException {
		return bxZbBO.tempSave(vos);
	}

	public JKBXVO[] update(JKBXVO[] vos) throws BusinessException {

		return bxZbBO.update(vos);
	}

	public void updateQzzt(JKBXVO[] vos) throws BusinessException {
		bxZbBO.updateQzzt(vos);
	}

	public JKBXHeaderVO updateHeader(JKBXHeaderVO header, String[] fields) throws BusinessException {
		return bxZbBO.updateHeader(header, fields);
	}

	public void updateDataAfterImport(String[] pk_tradetype) throws BusinessException {
	}

	@Override
	public JKBXVO[] queryBxBill4ProjBudget(String pk_group, String pk_project, String[] djlxbms) throws BusinessException {
		return queryBxBill4ProjBudget2(pk_group, pk_project, !ArrayUtils.isEmpty(djlxbms) ? null : djlxbms, false);
	}

	@Override
	public JKBXVO[] queryBxApproveBill4ProjBudget(String pk_group, String pk_Project, String[] djlxbms) throws BusinessException {
		return queryBxBill4ProjBudget2(pk_group, pk_Project, !ArrayUtils.isEmpty(djlxbms) ? null : djlxbms, true);
	}

	@SuppressWarnings("unchecked")
	private JKBXVO[] queryBxBill4ProjBudget2(String pk_group, String pk_project, String[] djlxbms, boolean isSign) throws BusinessException {
		if (StringUtil.isEmpty(pk_project) || StringUtil.isEmpty(pk_group)) {
			return null;
		}
		StringBuffer sqlBuf = new StringBuffer();
		sqlBuf.append("select distinct zb.pk_jkbx from er_bxzb zb,er_busitem bs where zb.PK_JKBX = bs.PK_JKBX and zb.dr = 0 and bs.dr = 0 ");

		// 集团
		sqlBuf.append(" and zb.pk_group='" + pk_group + "'");
		// 项目
		sqlBuf.append(" and bs.jobid ='" + pk_project + "'");

		if (isSign) {
			sqlBuf.append(" and zb.djzt >= " + BXStatusConst.DJZT_Verified);
		} else {
			sqlBuf.append(" and zb.djzt >= " + BXStatusConst.DJZT_Saved);
		}

		if (!ArrayUtils.isEmpty(djlxbms)) {
			sqlBuf.append(" and ").append(SqlUtil.buildInSql("zb.djlxbm", djlxbms));
		}

		final List<String> pks = new ArrayList<String>();

		new BaseDAO().executeQuery(sqlBuf.toString(), new ResultSetProcessor() {
			private static final long serialVersionUID = 1L;

			@Override
			public Object handleResultSet(ResultSet rs) throws SQLException {
				while (rs.next()) {
					pks.add(rs.getString(JKBXHeaderVO.PK_JKBX));
				}
				return pks;
			}
		});

		if (pks.size() == 0) {
			return null;
		}

		Collection<BXHeaderVO> headers = new BaseDAO().retrieveByClause(BXHeaderVO.class, SqlUtil.buildInSql(JKBXHeaderVO.PK_JKBX, pks));
		
		//排序好，便于信息处理
		Collection<BXBusItemVO> busitems = new BaseDAO().retrieveByClause(BXBusItemVO.class, SqlUtil.buildInSql(JKBXHeaderVO.PK_JKBX, pks) 
					+ " and  dr = 0 and jobid ='" + pk_project + "'" + " order by "  + JKBXHeaderVO.PK_JKBX);
		
		List<JKBXVO> result = new ArrayList<JKBXVO>();
		for (JKBXHeaderVO headVO : headers) {
			BXVO vo = new BXVO();
			vo.setParentVO(headVO);
			// 业务行
			BXBusItemVO[] busItemVOs = getBxBusitems(headVO, busitems);
			vo.setChildrenVO(busItemVOs);
			result.add(vo);
		}
		return result.toArray(new JKBXVO[] {});
	}

	private BXBusItemVO[] getBxBusitems(JKBXHeaderVO headVO, Collection<BXBusItemVO> busitems) {
		if(headVO != null){
			String pk_jkbx = headVO.getPk_jkbx();
			List<BXBusItemVO> resultList = new ArrayList<BXBusItemVO>();
			
			BXBusItemVO[] busItems = busitems.toArray(new BXBusItemVO[]{});
			for (BXBusItemVO bxBusItemVO: busItems) {
				if(bxBusItemVO.getPk_jkbx().equals(pk_jkbx)){
					resultList.add(bxBusItemVO);
					busitems.remove(bxBusItemVO);
				}else if(resultList.size() > 0){
					break;
				}
			}
			
			return resultList.toArray(new BXBusItemVO[]{});
		}
		return null;
	}

	@Override
	public Map<String, UFDouble> queryAmount4ProjFinal(String pkGroup, String pkProject) throws BusinessException {
		if (StringUtil.isEmpty(pkGroup) || StringUtil.isEmpty(pkProject)) {
			return null;
		}
		final Map<String, UFDouble> result = new HashMap<String, UFDouble>();

		StringBuffer buf = new StringBuffer();
		buf.append(" select zb.bzbm, sum(bs.ybje) ybje from er_bxzb zb, er_busitem bs ");
		buf.append(" where zb.dr=0 ");
		buf.append(" and zb.pk_jkbx = bs.pk_jkbx ");// 单据状态签字
		buf.append(" and zb.djzt=" + BXStatusConst.DJZT_Sign);// 单据状态签字
		buf.append(" and zb.sxbz=" + BXStatusConst.SXBZ_VALID);// 生效标识为生效
		buf.append(" and zb.pk_group='" + pkGroup + "'");// 集团
		buf.append(" and bs.jobid ='" + pkProject + "'");// 项目
		buf.append(" group by zb.bzbm ");

		new BaseDAO().executeQuery(buf.toString(), new ResultSetProcessor() {
			private static final long serialVersionUID = 1L;

			@Override
			public Object handleResultSet(ResultSet rs) throws SQLException {
				while (rs.next()) {
					String bzbm = rs.getString(JKBXHeaderVO.BZBM);
					UFDouble amount = new UFDouble(rs.getBigDecimal(JKBXHeaderVO.YBJE));
					result.put(bzbm, amount);
				}
				return result;
			}
		});
		return result;
	}
	
	@Override
	public IAccessableOrgsBusiVO[] queryMaYsBusiVOsByDetailPks(String[] details, String actionCode) throws BusinessException {
		if(ArrayUtils.isEmpty(details) || actionCode == null){
			return null;
		}
		
		boolean isContrary = true;
		if(BXConstans.ERM_NTB_CLOSE_KEY.equals(actionCode) || BXConstans.ERM_NTB_UNAPPROVE_KEY.equals(actionCode)){
			isContrary = false;
		}
		
		if(BXConstans.ERM_NTB_UNAPPROVE_KEY.equals(actionCode)){
			actionCode = BXConstans.ERM_NTB_APPROVE_KEY;
		}else if(BXConstans.ERM_NTB_UNCLOSE_KEY.equals(actionCode)){
			actionCode = BXConstans.ERM_NTB_CLOSE_KEY;
		}

		MtapppfVO[] pfVos = NCLocator.getInstance().lookup(IMtapppfVOQryService.class).queryMtapppfVoByBusiDetailPk(
				details);
		
		if(ArrayUtils.isEmpty(pfVos)){//无申请记录返回null
			return null;
		}

		// 申请单控制策略(这里不包括上游)
		DataRuleVO[] ruleVos = NCLocator.getInstance().lookup(IBudgetControl.class).queryControlTactics(
				pfVos[0].getMa_tradetype(), actionCode, false);

		if (ArrayUtils.isEmpty(ruleVos)) {
			return null;
		}
		
		List<IFYControl> fyControls = getMaFyControlVos(pfVos);
		
		if(fyControls != null && fyControls.size() > 0){
			return ErBudgetUtil.getCtrlVOs(fyControls.toArray(new IFYControl[]{}), isContrary, ruleVos);
		}
		
		return null;
	}
	
	private List<IFYControl> getMaFyControlVos(MtapppfVO[] pfVos) throws BusinessException {
		if (pfVos == null) {
			return null;
		}
		
		Set<String> parentPkSet = new HashSet<String>();//申请单PK
		List<String> childPkList = new ArrayList<String>();//申请单业务行PK集合
		Map<String, MatterAppVO> pk2MappVoMap = new HashMap<String, MatterAppVO>();
		Map<String, MtAppDetailVO> pk2MappDetailVoMap = new HashMap<String, MtAppDetailVO>();

		for (MtapppfVO pf : pfVos) {
			parentPkSet.add(pf.getPk_matterapp());
			childPkList.add(pf.getPk_mtapp_detail());
		}
		
		//查询申请单（为了补齐业务信息）
		IErmMatterAppBillQuery maQueryService = NCLocator.getInstance().lookup(IErmMatterAppBillQuery.class);
		MatterAppVO[] parentVos = maQueryService.queryMatterAppVoByPks(parentPkSet.toArray(new String[] {}));//查询申请记录关联的申请单表头
		MtAppDetailVO[] childrenVos = maQueryService.queryMtAppDetailVOVoByPks(childPkList.toArray(new String[] {}));
		
		if(parentVos == null || childrenVos == null){
			return null;
		}
		
		for (MatterAppVO appVo : parentVos) {
			pk2MappVoMap.put(appVo.getPk_mtapp_bill(), appVo);
		}
		
		for (MtAppDetailVO detail : childrenVos) {
			pk2MappDetailVoMap.put(detail.getPk_mtapp_detail(), detail);
		}

		//申请单业务行集合
		List<IFYControl> fyControlList = new ArrayList<IFYControl>();
		
		for (MtapppfVO pf : pfVos) {
			MatterAppVO headvo = pk2MappVoMap.get(pf.getPk_matterapp());
			MtAppDetailVO detail = pk2MappDetailVoMap.get(pf.getPk_mtapp_detail());
			
			if (headvo == null || detail == null) {
				continue;
			}
			
			detail = (MtAppDetailVO) detail.clone();
			headvo = (MatterAppVO) headvo.clone();
			
			// 按费用金额设置预算
			detail.setOrig_amount(pf.getExe_amount() == null ? UFDouble.ZERO_DBL : pf.getExe_amount().abs());
			detail.setOrg_amount(pf.getOrg_exe_amount() == null ? UFDouble.ZERO_DBL : pf.getOrg_exe_amount().abs());
			detail.setGroup_amount(pf.getGroup_exe_amount() == null ? UFDouble.ZERO_DBL : pf.getGroup_exe_amount()
					.abs());
			detail.setGlobal_amount(pf.getGlobal_exe_amount() == null ? UFDouble.ZERO_DBL : pf.getGlobal_exe_amount()
					.abs());
			MatterAppYsControlVO controlvo = new MatterAppYsControlVO(headvo, detail);
	
			fyControlList.add(controlvo);
		}
		
		return fyControlList;
	}

	@Override
	public JKBXVO invalidBill(JKBXVO jkbxvo) throws BusinessException {
		return bxZbBO.invalidBill(jkbxvo);
	}
}
