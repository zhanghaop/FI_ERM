package nc.bs.erm.bx.outer;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.er.outer.ArapItemSsVO;
import nc.bs.er.util.BXBsUtil;
import nc.bs.framework.common.NCLocator;
import nc.bs.pfxx.ISwapContext;
import nc.cmp.utils.CmpUtils;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.arap.pub.IBXBillPublic;
import nc.itf.erm.prv.IArapCommonPrivate;
import nc.itf.uap.IVOPersistence;
import nc.itf.uap.pf.IPFBusiAction;
import nc.itf.uap.pfxx.IPFxxEJBService;
import nc.pubitf.accperiod.AccountCalendar;
import nc.pubitf.org.IDeptPubService;
import nc.pubitf.org.IOrgUnitPubService;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.cmp.settlement.SettlementBodyVO;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.check.VOChecker;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.reimtype.ReimTypeHeaderVO;
import nc.vo.erm.util.VOUtils;
import nc.vo.fipub.billcode.FinanceBillCodeInfo;
import nc.vo.fipub.billcode.FinanceBillCodeUtils;
import nc.vo.pfxx.auxiliary.AggxsysregisterVO;
import nc.vo.pfxx.idcontrast.IDContrastVO;
import nc.vo.pfxx.util.PfxxPluginUtils;
import nc.vo.pfxx.util.PfxxUtils;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.workflownote.WorkflownoteVO;
/**
 * <p>
 * TODO 报销管理与外部信息应用平台插件类。
 * </p>
 *
 * 修改记录：<br>
 * <li>修改人：修改日期：修改内容：</li>
 * <br><br>
 *
 * @see
 * @author liansg
 * @version V6.0
 * @since V6.0 创建时间：2011-3-23 下午03:40:39
 */
public class ErmExpPfxxPlugin extends nc.bs.pfxx.plugin.AbstractPfxxPlugin  {

	/**
	 * 将由XML转换过来的VO导入NC系统。业务插件实现此方法即可。<br>
	 * 请注意，业务方法的校验一定要充分
	 *
	 * @param vo
	 *            转换后的vo数据，在NC系统中可能为ValueObject,SuperVO,AggregatedValueObject,IExAggVO等。
	 * @param swapContext
	 *            各种交换参数，组织，接受方，发送方，帐套等等
	 * @param aggxsysvo
	 *            辅助信息vo
	 * @return
	 * @throws BusinessException
	 */
	//FIXEM 注意处理相关的多语
	@Override
	protected Object processBill(Object vo, ISwapContext swapContext, AggxsysregisterVO aggxsysvo) throws BusinessException {
		JKBXVO bxvo = (JKBXVO) vo;
		dealSpecialField(bxvo);
		JKBXHeaderVO header = bxvo.getParentVO();
		
		//因为报销模板中tablecode与元数据属性中的name不同，造成保存页签不对，在查询中查询不到问题
		if(BXConstans.BX_DJDL.equals(bxvo.getParentVO().getDjdl()) && bxvo.getChildrenVO() != null){
			for (int i = 0; i < bxvo.getChildrenVO().length; i++) {
				bxvo.getChildrenVO()[i].setAttributeValue(BXBusItemVO.TABLECODE, BXConstans.BUS_PAGE);
			}
		}
		
		if(bxvo.getContrastVO()!=null){
			//因为导出模板中包含冲借款标签，需要人员手动删除，所以在这里做校验，如果金额为0，则设置为null
			if(bxvo.getChildrenVO().length == 1 && bxvo.getChildrenVO()[0].getCjkybje().equals(UFDouble.ZERO_DBL)){
				bxvo.setContrastVO(null);
			}else{
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0", "02011v61013-0067")/*@res "交换平台导入的数据中包含冲借款信息，系统暂不支持冲借款方式导入!"*/);
			}
		}
		boolean isCheck = true;
		if(header.getDjzt()==null){
			header.setDjzt(BXStatusConst.DJZT_Saved);
			isCheck = false;
		}
		//不支持保存以审核态
		if(isCheck && header.getDjzt()!=null && header.getDjzt()>BXStatusConst.DJZT_Saved){
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0", "02011v61013-0068")/*@res "只支持保存态(即djzt=1)的导入"*/);
		}
		
		if (header.djdl==null) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006pub_0","02006pub-0316")/*@res "单据大类字段不能为空，请输入值"*/);
		}
		if (header.djlxbm==null) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006pub_0","02006pub-0316")/*@res "单据的单据类型编码字段不能为空，请输入值"*/);
		}
		if (header.getPk_group()==null) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006pub_0","02006pub-0317")/*@res "单据的所属集团字段不能为空，请输入值"*/);
		}
		if (header.getPk_org()==null) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006pub_0","02006pub-0318")/*@res "单据的财务组织字段不能为空，请输入值"*/);
		}
		if (header.getFydwbm()==null) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0029")/*@res "费用承担单位字段不能为空，请输入值"*/);
		}
		if (header.getDwbm()==null) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0030")/*@res "报销人单位字段不能为空，请输入值"*/);
		}
		if (header.getDeptid()==null) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0031")/*@res "报销人部门字段不能为空，请输入值"*/);
		}
		if (header.getFydeptid()==null) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0032")/*@res "费用承担单位部门字段不能为空，请输入值"*/);
		}
		// 如果是期初单据，则需要校验业务单元是否设置了业务期初期间，如果没有不允许导入。如果设置了，将日期设为期初期间前一天
		if (header.getQcbz().booleanValue()) {
			String yearMonth = NCLocator.getInstance().lookup(IOrgUnitPubService.class)
					.getOrgModulePeriodByOrgIDAndModuleID(header.getPk_org(), BXConstans.ERM_MODULEID);
			if (yearMonth != null && yearMonth.length() != 0) {
				if (yearMonth != null && yearMonth.length() != 0) {
					String year = yearMonth.substring(0, 4);
					String month = yearMonth.substring(5, 7);
					if (year != null && month != null) {
						// 返回组织的会计日历
						AccountCalendar calendar = AccountCalendar.getInstanceByPk_org(header
								.getPk_org());
						if (calendar == null) {
							throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
									.getStrByID("2011v61013_0", "02011v61013-0021")/*
																					 * @res
																					 * "组织的会计期间为空"
																					 */);
						}
						calendar.set(year, month);
						if (calendar.getMonthVO() == null) {
							throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
									.getStrByID("2011v61013_0", "02011v61013-0022")/*
																					 * @res
																					 * "组织起始期间为空"
																					 */);
						}
						
						header.setDjrq(calendar.getMonthVO().getBegindate().getDateBefore(1));
					}
				}
			}
		}
		int newbillstat = header.getDjzt();
		UFBoolean isqc = header.getQcbz();
		JKBXVO tempvo = null;
		IBXBillPrivate billPri=NCLocator.getInstance().lookup(IBXBillPrivate.class);

			// 1、首先检查是否存在重复导入的情况
			String billtype = swapContext.getBilltype();
			String docid= swapContext.getDocID();
			String pk_org = null;
			String oldPk = PfxxPluginUtils.queryBillPKBeforeSaveOrUpdate(billtype, docid, pk_org);
			if (oldPk != null) {
				if (swapContext.getReplace().equalsIgnoreCase("N"))
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0072")/*@res "不允许重复导入单据，请检查是否是操作错误；如果想更新已导入单据，请把数据文件的replace标志设为‘Y’"*/);

				//设置主键
				header.setPrimaryKey(oldPk);

				List<JKBXVO> vos = billPri.queryVOsByPrimaryKeys(new String[]{oldPk},header.getDjdl());

				if(vos!=null && vos.size()!=0){
					tempvo=vos.get(0);
				}
				if (tempvo == null) {
					NCLocator.getInstance().lookup(IPFxxEJBService.class).deleteIDvsPKByDocPK(oldPk);
				}
				if (tempvo != null && tempvo.getParentVO() != null) {
					if (tempvo.getParentVO().getDjzt() == null) {
						throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0073")/*@res "单据状态不存在！"*/);
					}
					if (tempvo.getParentVO().getDjzt().intValue() > 1) {
						throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0074")/*@res "单据已经审核，不允许重复导入单据。单据状态："*/ + tempvo.getParentVO().getDjzt());
					}
					header.setPrimaryKey(oldPk);
					header.setDjbh(tempvo.getParentVO().getDjbh());
				}
			}

			if (bxvo.getSettlevo() != null) {
				CmpUtils.addModifyFlag((SettlementBodyVO[]) bxvo.getSettlevo().getChildrenVO());
			}

			if (isqc.booleanValue()) {
				header.setDjzt(BXStatusConst.DJZT_Sign);
				header.setSxbz(BXStatusConst.SXBZ_VALID);
			}
			
			header.setZy(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0076")/*@res "外部平台导入:"*/);
			
			//add by cs 设置单位，部门版本
			setDeptOrgVersion(header);
			//end
			
			if (oldPk == null) {
				try{
					
					//begin--added by chendya保存前先校验
					new VOChecker().checkSave(bxvo);
					//end
					
					JKBXVO[] bxvos = NCLocator.getInstance().lookup(IBXBillPublic.class).save(new JKBXVO[] { bxvo });
					
					String pk = bxvos[0].getParentVO().getPk_jkbx();
			 		PfxxPluginUtils.addDocIDVsPKContrast(swapContext.getBilltype(),swapContext.getDocID(),swapContext.getOrgPk(),pk);
					return bxvos[0].getParentVO().getDjbh();

				}catch (Exception e) {
					ExceptionHandler.handleException(e);
				}

			} else {
				bxvo.setBxoldvo(tempvo);

				try{
					IPFBusiAction pfbo = NCLocator.getInstance().lookup(IPFBusiAction.class);
					Object object =pfbo.processAction("EDIT",header.getDjlxbm(),null , bxvo, null,null );
				}catch (Exception e) {
					ExceptionHandler.handleException(e);
					throw new BusinessException(e.getMessage());
				}
			}
			if ((!isqc.booleanValue()) && newbillstat > 1) {
				header.setDjzt(BXStatusConst.DJZT_Saved);
				billPri.audit(new JKBXVO[]{bxvo});
			}
			if (oldPk == null) {
				IDContrastVO refVO = new IDContrastVO();
				refVO.setPk_idcontra(swapContext.getDocID());//FIXME 注意正确否
				refVO.setPk_bill(bxvo.getParentVO().getPrimaryKey());
				refVO.setBill_type(swapContext.getBilltype());
				PfxxUtils.lookUpPFxxEJBService().insertIDvsPK(refVO);//FIXME 新盘放开
			}
		WorkflownoteVO noteVO = new WorkflownoteVO();
		noteVO.setActiontype("Biz");
		noteVO.setBillid(bxvo.getParentVO().getPrimaryKey());
		noteVO.setBillno(header.getDjbh());
		noteVO.setCheckman(header.getOperator());
		noteVO.setChecknote(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0076")/*@res "外部平台导入"*/);
		noteVO.setIscheck("N");
		noteVO.setMessagenote(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0076")/*@res "外部平台导入"*/);
		noteVO.setPk_billtype(header.getDjlxbm());
//		noteVO.setPk_businesstype(IPFConfigInfo.STATEBUSINESSTYPE);//FIXME
		noteVO.setPk_org(header.getPk_org());
		noteVO.setReceivedeleteflag(UFBoolean.FALSE);
		noteVO.setSenddate(new UFDateTime(System.currentTimeMillis()));
		noteVO.setSenderman(header.getOperator());
		IVOPersistence vop = (IVOPersistence) NCLocator.getInstance().lookup(IVOPersistence.class.getName());
		vop.insertVO(noteVO);

		return header.getDjbh();
	}
	
	/**
	 * 设置VO中单位或部门的版本号
	 * @param header VO
	 * @throws BusinessException
	 */
	private void setDeptOrgVersion(final JKBXHeaderVO header) throws BusinessException {
		//设置单位、组织版本号
		String[] depts = new String[]{header.getFydeptid(),header.getDeptid()};
		String[] orgs = new String[]{header.getPk_org(), header.getDwbm(), header.getFydwbm(), header.getPk_pcorg(),header.getPk_payorg()};
		
		//单据日期为空时，用当前日期来查询
		//部门
		Map<String, String> deptMap = NCLocator.getInstance().lookup(IDeptPubService.class).getLastVIDSByDeptIDS(depts);
		//单位
		Map<String, String> orgMap = NCLocator.getInstance().lookup(IOrgUnitPubService.class).getNewVIDSByOrgIDSAndDate(
				orgs, header.getDjrq() == null ? new UFDate(): header.getDjrq());
		
		//部门修改后取消注销
		if(deptMap != null){
			header.setFydeptid_v(deptMap.get(header.getFydeptid()));
			header.setDeptid_v(deptMap.get(header.getDeptid()));
		}
		
		if(orgMap != null){
			header.setPk_org_v(orgMap.get(header.getPk_org()));
			header.setDwbm_v(orgMap.get(header.getDwbm()));
			header.setFydwbm_v(orgMap.get(header.getFydwbm()));
			header.setPk_pcorg_v(orgMap.get(header.getPk_pcorg()));
			header.setPk_payorg_v(orgMap.get(header.getPk_payorg()));
		}
	}
	/**
	 *  生成单据号
	 */
	private void getBillNo(JKBXVO bxvo) throws BusinessException {
		JKBXHeaderVO parent = bxvo.getParentVO();
		FinanceBillCodeInfo info = new FinanceBillCodeInfo(JKBXHeaderVO.DJDL,
				JKBXHeaderVO.DJBH, JKBXHeaderVO.PK_GROUP, JKBXHeaderVO.PK_ORG, parent
						.getTableName());
		FinanceBillCodeUtils util = new FinanceBillCodeUtils(info);
		util.createBillCode(new AggregatedValueObject[] { bxvo });
	}
	/**
	 *  处理特殊字段的参照和其他
	 */
	private void dealSpecialField(JKBXVO bxvo) throws BusinessException {
		//处理银行账号
//		dealSkyhzh(djvo);
//		dealFkyhzh(djvo);
//		//处理表体报销类型
//		dealReimtype(djvo);
//		//处理资金计划项目
//		dealCashProj(djvo);
//		//处理期初单据
//		dealQc(djvo);
//		//处理其他字段
		dealOtherField(bxvo);
		//生成单据号
		getBillNo(bxvo);
//		//处理事项审批单
//		dealPKItem(djvo);
	}


	private void dealPKItem(JKBXVO djvo) {
		String djbh = djvo.getParentVO().getPk_item();
		if(djbh!=null){
			String pk_item=null;
			try{
				Collection collection = new BaseDAO().retrieveByClause(ArapItemSsVO.class, " dr=0 and djbh='"+djbh+"'");
				if(collection!=null && collection.size()>0)
					pk_item=((ArapItemSsVO)collection.iterator().next()).getVouchid();
			}catch (Exception e) {
				ExceptionHandler.consume(e);
				throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0077")/*@res "查询事项审批单失败:"*/+e.getMessage());
			}

			djvo.getParentVO().setPk_item(pk_item);
		}
	}

	private void dealOtherField(JKBXVO djvo) {
		JKBXHeaderVO header = djvo.getParentVO();
		header.setDr(new Integer(0));
		if(header.getBbhl() == null || header.getBbhl().toDouble() == 0){
			header.setBbhl(new UFDouble(1.0));
		}
		if(header.getGroupbbhl() == null || header.getGroupbbhl().toDouble() == 0){
			header.setGroupbbhl(new UFDouble(1.0));
		}
		if(header.getGlobalbbhl() == null || header.getGlobalbbhl().toDouble() == 0){
			header.setGlobalbbhl(new UFDouble(1.0));
		}
		if(header.getPk_group() == null || header.getPk_group().length()==0){
			header.setPk_group(BXBsUtil.getPK_group());
		}
		if(header.getPk_payorg_v()==null){
			header.setPk_payorg_v(header.getPk_payorg());
		}

		if (header.getDjzt().equals(BXStatusConst.DJZT_Sign)) {
			header.setSxbz(BXStatusConst.SXBZ_VALID);
		}else{
			header.setSxbz(BXStatusConst.SXBZ_NO);
		}
	}

	private void dealReimtype(JKBXVO djvo) throws BusinessException {
		try{
			BXBusItemVO[] bxBusItemVOS = djvo.getBxBusItemVOS();
			if(bxBusItemVOS!=null && bxBusItemVOS.length!=0){
				Collection<SuperVO> reimType = NCLocator.getInstance().lookup(IArapCommonPrivate.class).getVOs(ReimTypeHeaderVO.class, "", false);
				Map<String, SuperVO> reimtypeCodeMap = VOUtils.changeCollectionToMap(reimType,ReimTypeHeaderVO.CODE);
				Map<String, SuperVO> reimtypeNameMap = VOUtils.changeCollectionToMap(reimType,ReimTypeHeaderVO.CODE);
				for(BXBusItemVO item:bxBusItemVOS){
					if(item.getPk_reimtype()!=null){
						SuperVO codeVO = reimtypeCodeMap.get(item.getPk_reimtype());
						SuperVO nameVO = reimtypeNameMap.get(item.getPk_reimtype());
						if(codeVO!=null)
							item.setPk_reimtype(codeVO.getPrimaryKey());
						else if(nameVO!=null)
							item.setPk_reimtype(nameVO.getPrimaryKey());
					}
				}
			}
		}catch (Exception e) {
			ExceptionHandler.handleException(e);
		}

	}

//	private void dealFkyhzh(BXVO djvo) throws BusinessException {
//		BXHeaderVO header=djvo.getParentVO();
//		ICMPBankaccQry iCMPBankaccQry = NCLocator.getInstance().lookup(ICMPBankaccQry.class);
//		List<String> zhlist=new ArrayList<String>();
//		if(header.getSkyhzh()!=null){
//			zhlist.add(header.getSkyhzh());
//		}
//		if(header.getFkyhzh()!=null){
//			zhlist.add(header.getFkyhzh());
//		}
//
//		Map<String, String> map = iCMPBankaccQry.queryPk_bankaccbassByAccountcodes(zhlist.toArray(new String[]{}));
//
//		if(header.getSkyhzh()!=null){
//			header.setSkyhzh(map.get(header.getSkyhzh())==null?header.getSkyhzh():map.get(header.getSkyhzh()));
//		}
//		if(header.getFkyhzh()!=null){
//			header.setFkyhzh(map.get(header.getFkyhzh())==null?header.getFkyhzh():map.get(header.getFkyhzh()));
//		}
//	}
//	private void dealSkyhzh(BXVO djvo) throws BusinessException {
//		BXHeaderVO header=djvo.getParentVO();
//		IPsnBankaccPubService iPsnBank = NCLocator.getInstance().lookup(IPsnBankaccPubService.class);
//		BankAccbasVO banAcc = null;
//		if(header.getSkyhzh()!=null){
//			banAcc = iPsnBank.queryDefaultBankAccByPsnDoc(header.getSkyhzh());
//		}
//
//
//
//		if(header.getSkyhzh()!=null){
//			header.setSkyhzh(map.get(header.getSkyhzh())==null?header.getSkyhzh():map.get(header.getSkyhzh()));
//		}
//		if(header.getFkyhzh()!=null){
//			header.setFkyhzh(map.get(header.getFkyhzh())==null?header.getFkyhzh():map.get(header.getFkyhzh()));
//		}
//	}

}