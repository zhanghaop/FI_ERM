package nc.impl.erm.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.itf.er.pub.IArapBillTypePublic;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.md.model.MetaDataException;
import nc.md.persist.framework.IMDPersistenceQueryService;
import nc.md.persist.framework.MDPersistenceService;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.event.IArapBSEventType;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.er.expensetype.ExpenseTypeVO;
import nc.vo.fi.pub.SqlUtils;
import nc.vo.fip.billitem.BillItemVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.sm.createcorp.CreateOrgInfo;
import nc.vo.sm.createcorp.CreatecorpVO;

import org.apache.commons.lang.ArrayUtils;

/**
 * 
 * @author liansg
 * @see 本类主要完成以下功能：监听集团，当增加集团时，设置集团单据类型预置数据
 * @see 监听集团，当增加集团时，设置报销费用类型的预置数据
 * @param
 */
public class ErmBillTypeManaListener implements IBusinessListener {
	public static final String CMP = "3607";

	public void doAction(IBusinessEvent event) throws BusinessException {

		//组织初始化前事件
		if (IArapBSEventType.TYPE_ORGIINITIALIZE_BEFORE.equals(event.getEventType())) {
			nc.bs.businessevent.BusinessEvent evt = (nc.bs.businessevent.BusinessEvent) event;
			CreateOrgInfo info = (CreateOrgInfo) evt.getObject();
			//是否拷贝结算信息单据项目 (装结算和装报销管理时均尝试拷贝)
			boolean copyCmpBillItem = false;
			if (info != null && !ArrayUtils.isEmpty(info.getCreatecorpVOs())) {
				for (CreatecorpVO vo : info.getCreatecorpVOs()) {
					if (String.valueOf(BXConstans.ERM_MODULEID).equals(vo.getFunccode())) {
						//新增单据类型
						insertDjlx(vo.getPk_org());
						
						//装报销管理时拷贝
						copyCmpBillItem = true;
					} else if (String.valueOf(CMP).equals(vo.getFunccode())) {
						//装结算时也拷贝
						copyCmpBillItem = true;
					}
				}
			}
			if (copyCmpBillItem) {
				copyCmpSettleBillItem();
			}
		}
	}

	/**
	 * 拷贝结算信息的单据项目到报销单据上
	 */
	@SuppressWarnings("unchecked")
	private void copyCmpSettleBillItem() {
		List<String> billtypes = new ArrayList<String>();
		billtypes.add("263X");
		billtypes.add("264X");
		List<?> existsBillType = null;
		BaseDAO baseDAO = new BaseDAO();
		try {
			final String inSql = SqlUtils.getInStr("pk_billtype", billtypes.toArray(new String[0]), true);
			//查询是否已经复制过单据类型的结算信息
			StringBuffer sql = new StringBuffer(" select distinct pk_billtype from fip_billitem where attrcode ='zb.settlenum' ");
			if(inSql!=null && inSql.length()>0){
				sql.append(" and ");
				sql.append(inSql);
			}
			existsBillType = (List<?>) baseDAO.executeQuery(sql.toString(),new ColumnListProcessor());
		} catch (Exception e) {
			ExceptionHandler.consume(e);
		}
		//待复制的单据类型
		List<String> preCopyBillType = new ArrayList<String>();
		for (String billtype : billtypes) {
			if (existsBillType == null) {
				existsBillType = new ArrayList<String>();
			}
			if (existsBillType.size() == 0) {
				preCopyBillType.addAll(billtypes);
				break;
			}
			if (!existsBillType.contains(billtype)) {
				preCopyBillType.add(billtype);
			}
		}
		if (preCopyBillType.size() > 0) {
			IMDPersistenceQueryService qryservice = MDPersistenceService.lookupPersistenceQueryService();
			boolean isLazy = false;
			try {
				//查询结算信息的单据项目
				final Collection<BillItemVO> billItemVOList = qryservice.queryBillOfVOByCond(BillItemVO.class," pk_billtype = '2201'", isLazy);
                List<BillItemVO> preCopyItemVOList = new ArrayList<BillItemVO>();
				for (String billtype : preCopyBillType) {
					for (BillItemVO billItemVO : billItemVOList) {
						//克隆
						BillItemVO preCopyItemVO = (BillItemVO) billItemVO.clone();
						if (preCopyItemVO.getPosition() == 1) {
							// 表头
							preCopyItemVO.setAttrcode("zb."+ preCopyItemVO.getAttrcode());
						} else if (preCopyItemVO.getPosition() == 2) {
							// 表体
							preCopyItemVO.setAttrcode("fb."+ billItemVO.getAttrcode());
						}
						preCopyItemVO.setPk_billtype(billtype);
						preCopyItemVO.setPk_billitem(null);
						//新增
						preCopyItemVO.setStatus(VOStatus.NEW);
						preCopyItemVOList.add(preCopyItemVO);
					}
				}
                //批量插入
                baseDAO.insertVOArray(preCopyItemVOList.toArray(new BillItemVO[preCopyItemVOList.size()]));
			} catch (MetaDataException e) {
				ExceptionHandler.consume(e);
			} catch (DAOException e) {
				ExceptionHandler.consume(e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void insertDjlx(String pk_group) throws BusinessException {

		BaseDAO baseDAO = new BaseDAO();
//		Collection<DjLXVO> sourcevos = baseDAO
//				.retrieveByClause(
//						DjLXVO.class,
//						" pk_group='@@@@'" 
////						+" and djlxbm in ('2631','2632','2641','2642','2643','2644','2645','2646','2647')"
//						);
		DjLXVO[] sourcevos = NCLocator.getInstance().lookup(IArapBillTypePublic.class).queryByWhereStr(
				" pk_group='@@@@'" 
//						+" and djlxbm in ('2631','2632','2641','2642','2643','2644','2645','2646','2647')"
		);
		Collection<ExpenseTypeVO> expensevos = baseDAO.retrieveByClause(
				ExpenseTypeVO.class, " pk_group='global00000000000000'");
		// 增加对应集团的交易类型
		for (DjLXVO sysvo : sourcevos) {
			sysvo.setPk_group(pk_group);
		}
		@SuppressWarnings("rawtypes")
        List list = new ArrayList();
		list.addAll(Arrays.asList(sourcevos));
		baseDAO.insertVOList(list);
		// 增加对应集团的报销费用类型
		for (ExpenseTypeVO vos : expensevos) {
			vos.setPk_group(pk_group);
		}
		list.clear();
		list.addAll(expensevos);
		baseDAO.insertVOList(list);
	}
	
	
}
