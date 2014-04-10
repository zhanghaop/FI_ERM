package nc.impl.arap.bx;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nc.bs.arap.bx.BXBusItemBO;
import nc.bs.arap.bx.BXZbBO;
import nc.bs.dao.BaseDAO;
import nc.bs.er.util.SqlUtil;
import nc.itf.arap.pub.IBXBillPublic;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BXVO;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.MessageVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pm.budget.pub.IBudgetExecVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.billtype.BilltypeVO;
import nc.vo.pub.lang.UFDouble;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * nc.impl.arap.bx.ArapBXBillPublicImp
 * 
 * @author twei
 * 
 * 借款报销类单据公共业务处理接口实现
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
		return bxZbBO.updateHeader(header,fields);
	}

	public void updateDataAfterImport(String[] pk_tradetype) throws BusinessException {
		
	}

	private static List<IBudgetExecVO> getBudgetExecVOs(final JKBXVO vo) {
		List<IBudgetExecVO> resultVOs = new ArrayList<IBudgetExecVO>();

		for (final BXBusItemVO busItemVO : vo.getChildrenVO()) {
			resultVOs.add(new IBudgetExecVO() {
				public String getPk_wbs_exec() {
					return null;
				}

				public String getPk_wbs() {
					return vo.getParentVO().getProjecttask();
				}

				public String getPk_project() {
					return vo.getParentVO().getJobid();
				}

				public String getPk_factor() {
					return vo.getParentVO().getPk_checkele();
				}

				public String getPk_currtype() {
					return vo.getParentVO().getBzbm();
				}

				public UFDouble getNmoney() {
					return busItemVO.getYbje();
				}

				public String getBill_type() {
					final String djdl = vo.getParentVO().getDjdl();
					if (BXConstans.JK_DJDL.equals(djdl)) {
						return BXConstans.JK_DJLXBM;
					}
					return BXConstans.BX_DJLXBM;
				}

				public String getBill_transitype() {
					return vo.getParentVO().getDjlxbm();
				}

				public String getBill_id() {
					return vo.getParentVO().getPk_jkbx();
				}

				public String getBill_code() {
					return vo.getParentVO().getDjbh();
				}

				public String getBill_bid() {
					return null;
				}

				public Map getUserDefMap() {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put(JKBXHeaderVO.SZXMID, busItemVO.getSzxmid());
					map.put(BXBusItemVO.PK_REIMTYPE, busItemVO.getPk_reimtype());
					return map;
				}
			});
		}

		return resultVOs;
	}

	@Override
	public IBudgetExecVO[] queryBxBill4ProjBudget(String pk_group, String pk_project, String[] pk_billtypes) throws BusinessException {
		// 根据单据类型PK获取单据类型别名
		String[] billtypeCodes = null;
		if (!ArrayUtils.isEmpty(pk_billtypes)) {
			Collection<?> coll = new BaseDAO().retrieveByClause(BilltypeVO.class, SqlUtil.buildInSql("pk_billtypeid", pk_billtypes));
			if (coll != null && coll.size() > 0) {
				billtypeCodes = new String[coll.size()];
				
				Iterator iterator = coll.iterator();
				for(int i = 0; i < coll.size(); i ++){
					billtypeCodes[i] = ((BilltypeVO)iterator.next()).getPk_billtypecode();
				}
			}
		}
		return queryBxBill4ProjBudget2(pk_group, pk_project, !ArrayUtils.isEmpty(billtypeCodes) ? null : billtypeCodes);
	}

	@Override
	public IBudgetExecVO[] queryBxBill4ProjBudget2(String pk_group,
			String pk_project, String[] djlxbms) throws BusinessException {
		if (StringUtil.isEmpty(pk_project) || StringUtil.isEmpty(pk_group)) {
			return null;
		}
		StringBuffer buf = new StringBuffer();
		buf.append(" where dr=0 ");
		// 单据状态签字
		buf.append(" and djzt=" + BXStatusConst.DJZT_Sign);
		// 生效标识为生效
		buf.append(" and sxbz=" + BXStatusConst.SXBZ_VALID);
		// 集团
		buf.append(" and pk_group='" + pk_group + "'");
		//项目
		buf.append(" and jobid ='" + pk_project + "'");
		
		if (!ArrayUtils.isEmpty(djlxbms)) {
			buf.append(" and ").append(SqlUtil.buildInSql("djlxbm", djlxbms));
		}

		List<JKBXHeaderVO> headVOList = bxZbBO.queryHeadersByWhereSql(buf.toString(), BXConstans.BX_DJDL);
		if (headVOList.isEmpty()) {
			return null;
		}

		List<IBudgetExecVO> budgetVOList = new ArrayList<IBudgetExecVO>();
		for (JKBXHeaderVO headVO : headVOList) {
			BXVO vo = new BXVO();
			vo.setParentVO(headVO);
			// 业务行
			BXBusItemVO[] bxFinItemVOs = new BXBusItemBO().queryByHeaders(new JKBXHeaderVO[] { headVO });
			vo.setChildrenVO(bxFinItemVOs);

			// 冲借款页签
			Collection<BxcontrastVO> contrastVOList = bxZbBO.queryContrasts(headVO);
			vo.setContrastVO((BxcontrastVO[]) contrastVOList.toArray(new BxcontrastVO[0]));
			// 构造项目预算执行VO
			budgetVOList.addAll(getBudgetExecVOs(vo));
		}
		return (IBudgetExecVO[]) budgetVOList.toArray(new IBudgetExecVO[0]);
	}
}
