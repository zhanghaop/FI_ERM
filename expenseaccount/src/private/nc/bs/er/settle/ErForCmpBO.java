package nc.bs.er.settle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import nc.bs.arap.bx.BXBusItemBO;
import nc.bs.arap.bx.BXZbBO;
import nc.bs.dao.BaseDAO;
import nc.bs.er.djlx.DjLXDMO;
import nc.bs.erm.util.CacheUtil;
import nc.bs.framework.common.NCLocator;
import nc.bs.pf.pub.PfDataCache;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.cmp.busi.ISettleNotifyPayTypeBusiBillService;
import nc.itf.cmp.settlement.ISettlement;
import nc.itf.er.pub.IArapBillTypePublic;
import nc.itf.uap.busibean.SysinitAccessor;
import nc.pubitf.fip.service.IFipBillQueryService;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.cmp.BusiInfo;
import nc.vo.cmp.BusiStateTrans;
import nc.vo.cmp.BusiStatus;
import nc.vo.cmp.CMPExecStatus;
import nc.vo.cmp.NetPayExecInfo;
import nc.vo.cmp.ReturnBill4BusiVO;
import nc.vo.cmp.ReturnBillRetDetail;
import nc.vo.cmp.fts.MoneyDetail;
import nc.vo.cmp.settlement.SettlementAggVO;
import nc.vo.cmp.settlement.SettlementBatchOperateVO;
import nc.vo.cmp.settlement.SettlementBodyVO;
import nc.vo.cmp.settlement.batch.BusiStateChangeVO;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.VOFactory;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.er.settle.SettleUtil;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.fip.external.FipExtendAggVO;
import nc.vo.fip.service.FipRelationInfoVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;

public class ErForCmpBO implements ISettleNotifyPayTypeBusiBillService {

	private final BXZbBO bo = new BXZbBO();
	private JKBXVO jkbxVO = null;

	// bx无委托收款,不处理
	public void billChange(BusiInfo busiInfo, Map<String, MoneyDetail> value) throws BusinessException {
	}

	private String getDJDL(BusiInfo busiInfo) {
		String billtype = busiInfo.getBill_type();
		if (billtype.startsWith("263")) {
			return BXConstans.JK_DJDL;
		} else if (billtype.startsWith("264")) {
			return BXConstans.BX_DJDL;
		}

		return null;
	}

	// 委托付款
	@Override
	public void execStatuesChange(BusiInfo busiInfo, CMPExecStatus status)
			throws BusinessException {
		// 更新报销单据支付状态
		List<JKBXHeaderVO> voList = new ArrayList<JKBXHeaderVO>();
		List<JKBXHeaderVO> vos = bo.queryHeadersByPrimaryKeys(
				new String[] { busiInfo.getPk_bill() }, getDJDL(busiInfo));
		for (JKBXHeaderVO vo : vos) {
			vo.setPayflag(status.getStatus());
			voList.add(vo);
		}
		//委托办理支付成功时生成凭证
		if (voList.get(0).getPayflag().intValue() == BXStatusConst.PAYFLAG_PayFinish) {
			String param = SysinitAccessor.getInstance().getParaString(
					voList.get(0).getPk_org(), "CMP37");
			if (BXStatusConst.VounterCondition_ZF.equals(param)) {
				List<JKBXVO> jkbxvo = NCLocator.getInstance()
						.lookup(IBXBillPrivate.class).retriveItems(voList);
				forwardToFip(BXZbBO.MESSAGE_SETTLE, jkbxvo);// 生成凭证的正向操作
			}
		}
		// 更新支付状态
		bo.updateHeaders(voList.toArray(new JKBXHeaderVO[0]), new String[] {
				JKBXHeaderVO.PAYFLAG, JKBXHeaderVO.VOUCHERTAG });
	}

	// 承兑汇票退票,不处理
	@Override
	public List<ReturnBillRetDetail> processReturnBill(ReturnBill4BusiVO bill4BusiVO) throws BusinessException {
		return null;
	}

	/**
	 * 结算红冲
	 */
	@Override
	public void setoffRed(NetPayExecInfo payInfo, Map<String, SettlementBodyVO[]> value) throws BusinessException {
		if(payInfo!=null && value!=null){
			NCLocator.getInstance().lookup(IBXBillPrivate.class).settleRedHandleSaveAndSign(payInfo, value);
		}
	}
	
	/**
	 * 自动结算
	 */
	@Override
	public boolean isAutoSettle(String pk_group, String pk_tradetype,
			SettlementAggVO... settlementAggVOs) throws BusinessException {
		DjLXVO[] vos = CacheUtil.getValueFromCacheByWherePart(DjLXVO.class,
				"pk_group = '" + pk_group + "' and djlxbm = '" + pk_tradetype
						+ "'");
		if (vos == null || vos.length == 0) {
			return false;
		} else {
			return vos[0].getAutosettle() == null ? false : vos[0]
					.getAutosettle().booleanValue() ? true : false;
		}
	}
	
	@Override
	public void billStateChange(BusiInfo busiInfo, BusiStateTrans trans) throws BusinessException {

		// 审核->签字
		if (BusiStatus.Audit.equals(trans.getFrom()) && BusiStatus.Sign.equals(trans.getTo())) {
			jkbxVO = getBxVO(busiInfo, trans);
			bo.settle(busiInfo.getOperator(), busiInfo.getOperatorDate(), jkbxVO);

		}
		// 签字->反签字
		else if (BusiStatus.Sign.equals(trans.getFrom()) && BusiStatus.Audit.equals(trans.getTo())) {
			jkbxVO = getBxVO(busiInfo, trans);
			bo.unSettle(new JKBXVO[] { jkbxVO }, true);
		}

	}
	
	@Override
	public void effectStateChange(BusiInfo busiInfo, BusiStateTrans trans) throws BusinessException {
		if (jkbxVO == null || !jkbxVO.getParentVO().getPrimaryKey().equals(busiInfo.getPk_bill())) {
			jkbxVO = getBxVO(busiInfo, trans);
		}
		String flag = "";
		// 生效
		if (BusiStatus.Effet.equals(trans.getTo())) {
			flag = BXZbBO.MESSAGE_SETTLE;
		}
		// 反生效
		else if (BusiStatus.EffectNever.equals(trans.getTo())) {
			flag = BXZbBO.MESSAGE_UNSETTLE;
		}
		String param = SysinitAccessor.getInstance().getParaString(jkbxVO.getParentVO().getPk_org(), "CMP37");
		if(BXStatusConst.VounterCondition_QZ.equals(param)){
			if(BXZbBO.MESSAGE_SETTLE.equals(flag) && 
					(jkbxVO.getParentVO().getVouchertag()==null 
							||jkbxVO.getParentVO().getVouchertag()==BXStatusConst.SXFlag )){
				jkbxVO.getParentVO().setVouchertag(BXStatusConst.SXFlag);
				bo.updateHeader(jkbxVO.getParentVO(), new String[]{JKBXHeaderVO.VOUCHERTAG});
				bo.effectToFip(Arrays.asList(new JKBXVO[] { jkbxVO }), flag);
			}
		}
		//取消签字与凭证环节没有关系
		if(BXZbBO.MESSAGE_UNSETTLE.equals(flag)){
			if(jkbxVO.getParentVO().getVouchertag()!= null && 
					jkbxVO.getParentVO().getVouchertag() == BXStatusConst.MEDeal){
				//凭证环节是月末凭证时，将单据的凭证都删除（暂估凭证，月末凭证）
				bo.effectToFip(Arrays.asList(new JKBXVO[] { jkbxVO }), flag); // 删除月末凭证
				
				//删除暂估凭证：
//				jkbxVO.getParentVO().setVouchertag(BXStatusConst.ZGDeal);
//				bo.effectToFip(Arrays.asList(new JKBXVO[] { jkbxVO }), flag);
				
			}else{
				bo.effectToFip(Arrays.asList(new JKBXVO[] { jkbxVO }), flag);
			}
			//判断是否有暂估凭证
			FipExtendAggVO[] datavos = datavos();
			if(datavos!=null && datavos.length!=0){
				jkbxVO.getParentVO().setVouchertag(BXStatusConst.ZGDeal);
			}else{
				jkbxVO.getParentVO().setVouchertag(null);
			}
			bo.updateHeader(jkbxVO.getParentVO(), new String[]{JKBXHeaderVO.VOUCHERTAG});
		}
	}
	
	private FipExtendAggVO[] datavos() throws BusinessException {
		FipRelationInfoVO srcinfovo = new FipRelationInfoVO();
		srcinfovo.setPk_group(jkbxVO.getParentVO().getPk_group());
		srcinfovo.setPk_org(jkbxVO.getParentVO().getPk_payorg());
		srcinfovo.setRelationID(jkbxVO.getParentVO().getPk()+"_"+BXStatusConst.ZGDeal);
		srcinfovo.setPk_billtype(jkbxVO.getParentVO().getDjlxbm());
		IFipBillQueryService ip = NCLocator.getInstance().lookup(IFipBillQueryService.class);
		FipExtendAggVO[] datavos = ip.queryDesBillBySrc(new FipRelationInfoVO[]{srcinfovo}, null);
		return datavos;
	}
	

	private JKBXHeaderVO getBxHeaderVO(BusiInfo busiInfo) throws BusinessException {
		JKBXHeaderVO head = null;

		if (busiInfo.getRawBill() == null) {
			List<JKBXHeaderVO> vos = bo.queryHeadersByPrimaryKeys(new String[] { busiInfo.getPk_bill() }, getDJDL(busiInfo));

			if (vos != null && vos.size() > 0)
				head = vos.get(0);
		} else {
			head = ((JKBXVO) busiInfo.getRawBill()).getParentVO();
		}
		return head;
	}

	/**
	 * @param busiInfo
	 * @param trans
	 * @return
	 * @throws BusinessException
	 * 
	 *             从busiinfo中取报销的vo
	 */
	private JKBXVO getBxVO(BusiInfo busiInfo, BusiStateTrans trans) throws BusinessException {
		JKBXHeaderVO head = null;
		JKBXVO rawbill = null;
		if (busiInfo.getRawBill() == null) {
			List<JKBXHeaderVO> vos = bo.queryHeadersByPrimaryKeys(new String[] { busiInfo.getPk_bill() }, getDJDL(busiInfo));
			if (vos != null && vos.size() > 0){
				head = vos.get(0);
			}
		} else {
			rawbill = ((JKBXVO) busiInfo.getRawBill());
			head = rawbill.getParentVO();
		}
		if(head == null){
			return null;
		}
		
		BXBusItemVO[] queryBxFinItemVO = new BXBusItemBO().queryByHeaders(new JKBXHeaderVO[] { head });
		JKBXVO bxvo = VOFactory.createVO(head, queryBxFinItemVO);

		// 添加冲借款信息
		Collection<BxcontrastVO> collection = bo.queryContrasts(head);
		bxvo.setContrastVO(collection.toArray(new BxcontrastVO[0]));

		Collection<CShareDetailVO> cShares = bo.queryCSharesVOS(new JKBXHeaderVO[] { bxvo.getParentVO() });// 分摊明细
		bxvo.setcShareDetailVo(cShares.toArray(new CShareDetailVO[] {}));

		if (rawbill != null) {
			boolean hasNtbCheck = rawbill.getHasNtbCheck();
			boolean hasZjjhCheck = rawbill.getHasZjjhCheck();
			boolean hasJkCheck = rawbill.getHasJkCheck();

			boolean hasZjjhCheck2 = busiInfo.isHasZjjhCheck();
			boolean budgetCheck = busiInfo.isBudgetCheck();
			boolean hasJkCheck2 = busiInfo.isJkCheck();

			bxvo.setHasZjjhCheck(hasZjjhCheck || hasZjjhCheck2);
			bxvo.setHasNtbCheck(hasNtbCheck || budgetCheck);
			bxvo.setHasJkCheck(hasJkCheck || hasJkCheck2);

			bxvo.authList = rawbill.authList;
		} else {
			boolean hasZjjhCheck2 = busiInfo.isHasZjjhCheck();
			boolean budgetCheck = busiInfo.isBudgetCheck();

			bxvo.setHasZjjhCheck(hasZjjhCheck2);
			bxvo.setHasNtbCheck(budgetCheck);
			bxvo.setHasJkCheck(busiInfo.isJkCheck());
		}

		if (trans != null)
			bxvo.setSettlementMap(trans.getDetailMap());

		return bxvo;
	}
	
	@Override
	public boolean checkCancelEffect(BusiInfo info) throws BusinessException {
		return true;
	}
	
	@Override
	public boolean checkCancelSign(BusiInfo info) throws BusinessException {
		return true;
	}
	
	@Override
	public void coerceDelete(BusiInfo busiInfo) throws BusinessException {
		JKBXHeaderVO head = getBxHeaderVO(busiInfo);
		bo.delete(new JKBXVO[] { VOFactory.createVO(head) });
	}
	
	@Override
	public AggregatedValueObject getBillVO(BusiInfo info) throws BusinessException {
		return getBxVO(info);
	}

	private AggregatedValueObject getBxVO(BusiInfo info) throws BusinessException {
		return getBxVO(info, null);
	}

	public boolean isAutoFillEbankInfo(String pk_corp, String pk_billtype) throws BusinessException {
		return false;
	}

	/**
	 * 根据结算返回的网银信息返回支付状态 <br/>
	 * 全部支付成功则成功； 全部支付失败则失败； 有的成功且有的失败则部分成功； 支付进行中则支付中；
	 * 
	 * @author chendya
	 * @param status
	 * @return
	 */
	private Integer getPayStatus(NetPayExecInfo payInfo) throws BusinessException {
		// 返回的状态
		Integer retStatus = nc.vo.cmp.CMPExecStatus.UNPayed.getStatus();
		if (payInfo.getExecStatusMap() == null) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0", "02011v61013-0027")/*
																																 * @res
																																 * "回写的网银支付状态不明"
																																 */);
		}
		nc.vo.cmp.CMPExecStatus[] status = payInfo.getExecStatusMap().values().toArray(new CMPExecStatus[0]);
		boolean isFinished = false;
		boolean isFailed = false;
		for (int i = 0; i < status.length; i++) {
			if (status[i] == nc.vo.cmp.CMPExecStatus.SomePayFinish) {
				return nc.vo.cmp.CMPExecStatus.SomePayFinish.getStatus();
			} else if (status[i] == nc.vo.cmp.CMPExecStatus.PayFinish) {
				isFinished = true;
				if (isFailed) {
					retStatus = nc.vo.cmp.CMPExecStatus.SomePayFinish.getStatus();
					break;
				}
			} else if (status[i] == nc.vo.cmp.CMPExecStatus.PayFail) {
				isFailed = true;
				if (isFinished) {
					retStatus = nc.vo.cmp.CMPExecStatus.SomePayFinish.getStatus();
					break;
				}
			} else if (status[i] == nc.vo.cmp.CMPExecStatus.Paying) {
				if (isFailed) {
					retStatus = nc.vo.cmp.CMPExecStatus.SomePayFinish.getStatus();
					break;
				}
				retStatus = nc.vo.cmp.CMPExecStatus.Paying.getStatus();
			}
		}
		
		//有支付中时，返回支付中
		if (retStatus.intValue() == nc.vo.cmp.CMPExecStatus.Paying.getStatus()){
			return retStatus;
		}
		//全部成功和部分成功都是支付成功
		if ((isFinished && !isFailed) ||(isFinished && isFailed)) {
			retStatus = nc.vo.cmp.CMPExecStatus.PayFinish.getStatus();
		} else if (isFailed && !isFinished) {
			retStatus = nc.vo.cmp.CMPExecStatus.PayFail.getStatus();
		}
		return retStatus;
	}

	// 回写单据网银支付状态
	public void netPayExecChange(NetPayExecInfo payInfo) throws BusinessException {
//		JKBXHeaderVO head = VOFactory.createHeadVO(payInfo.getBilltype());
//		head.setPk_jkbx(payInfo.getBillid());
		List<JKBXHeaderVO> vos = bo.queryHeadersByPrimaryKeys(new String[] { payInfo.getBillid() }, payInfo.getBilltype());
		JKBXHeaderVO head = vos.get(0);

		// 回写支付状态
		head.setPayflag(getPayStatus(payInfo));
		head.setPaydate(payInfo.getOperateDate());
		head.setPayman(payInfo.getOperator());

		// 回写结算信息
		head.setJsr(payInfo.getOperator());
		head.setJsrq(payInfo.getOperateDate());
		String settleno = payInfo.getSettleno();
		head.setJsh(settleno);
		String djlxbm = payInfo.getBilltype();

		if (djlxbm == null || djlxbm.length() == 0) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0", "02011v61013-0028")/*
																																 * @res
																																 * "结算单据回写借款/报销单据失败，网银支付信息中缺失单据类型"
																																 */);
		}
		try {
			head.setDjlxbm(djlxbm);
			// 这里对自定义交易类型进行下处理
			if (djlxbm.startsWith(BXConstans.BX_DJLXBM) || djlxbm.startsWith(BXConstans.JK_DJLXBM)) {
				head.setDjdl(new DjLXDMO().getDjlxvoByDjlxbm(djlxbm, BXConstans.GLOBAL_CODE).getDjdl());
			} else {
				head.setDjdl(new DjLXDMO().getDjlxvoByDjlxbm(djlxbm, BXConstans.GROUP_CODE).getDjdl());
			}
			List<JKBXHeaderVO> voList = new ArrayList<JKBXHeaderVO>();
			voList.add(head);
			
			//网银支付支付完成时处理生成凭证
			if(head.getPayflag().intValue()==BXStatusConst.PAYFLAG_PayFinish){
				String param = SysinitAccessor.getInstance().getParaString(head.getPk_org(), "CMP37");
				if(BXStatusConst.VounterCondition_ZF.equals(param)){
				
					List<JKBXVO> jkbxvo = NCLocator.getInstance().lookup(IBXBillPrivate.class).retriveItems(voList);
					forwardToFip(BXZbBO.MESSAGE_SETTLE,jkbxvo);//生成凭证的正向操作
				}
			}
			new BaseDAO().updateVOArray(voList.toArray(new JKBXHeaderVO[]{}), new String[] { JKBXHeaderVO.PAYFLAG,
					JKBXHeaderVO.PAYDATE, JKBXHeaderVO.PAYMAN, JKBXHeaderVO.JSR, JKBXHeaderVO.JSRQ, JKBXHeaderVO.JSH,JKBXHeaderVO.VOUCHERTAG });
		} catch (Exception e) {
			throw nc.vo.er.exception.ExceptionHandler.handleException(e);
		}
	}

	/**
	 * @param busiInfo
	 * @param trans
	 * @return
	 * @throws BusinessException
	 * 
	 *             调用结算接口
	 */
	public void invokeCmp(JKBXVO param, UFDate date, BusiStatus busiStatus) throws BusinessException {
		JKBXHeaderVO head = param.getParentVO();
		if(head.isAdjustBxd()){
			// 报销类型为费用调整的单据，不进行支付结算
			return ;
		}
		if (SettleUtil.hasSettleInfo(head, head.getDjdl())) {
			ISettlement settle =  NCLocator.getInstance().lookup(ISettlement.class);
			DjLXVO djlxvo = NCLocator.getInstance().lookup(IArapBillTypePublic.class).getDjlxvoByDjlxbm(param.getParentVO().getDjlxbm(), param.getParentVO()
					.getPk_group());
			SettlementBatchOperateVO batchoperateVOs = new SettlementBatchOperateVO();
			batchoperateVOs.setBusibill(param);
			batchoperateVOs.setMsg(SettleUtil.getCmpMsg(param, djlxvo, date, busiStatus));
			batchoperateVOs.setSettlementAgg(param.getSettlementInfo());
			if (busiStatus == BusiStatus.Save) {
				if (head.getDjzt() == BXStatusConst.DJZT_Saved) {
					//全额冲借款（zfje=0 && hkje=0）因为不生成结算信息，所以也不存在删除结算信息动作。
					// 支付单位没有变化的，也不删除结算信息。
					if (param.getBxoldvo() != null
							&& !param.getParentVO().getPk_payorg().equals(param.getBxoldvo().getParentVO().getPk_payorg())
							&& !((param.getBxoldvo().getParentVO().getHkybje().compareTo(UFDouble.ZERO_DBL) == 0) && (param
									.getBxoldvo().getParentVO().getZfybje().compareTo(UFDouble.ZERO_DBL) == 0))
							&& param.getBxoldvo().getParentVO().getDjzt() == BXStatusConst.DJZT_Saved) {
						settle.notifySettlementBatchDelete(SettleUtil.getCmpMsg(param.getBxoldvo(), djlxvo, date, busiStatus));
					}
					settle.notifySettlementBatchSave(batchoperateVOs);
				} else {
					settle.notifySettlementBatchReserveAudit(SettleUtil.getCmpMsg(param, djlxvo, date, busiStatus));
				}
			} else if (busiStatus == BusiStatus.Tempeorary) {
				settle.notifySettlementBatchTempSave(batchoperateVOs);
			} else if (busiStatus == BusiStatus.Deleted) {
				settle.notifySettlementBatchDelete(SettleUtil.getCmpMsg(param, djlxvo, date, busiStatus));
			} else {
				settle.notifySettlementBatchAudit(SettleUtil.getCmpMsg(param, djlxvo, date, busiStatus));
			}
		}
	}

	public void notify4HandSettle(List<String> idList, boolean isOpp, UFDate operateDate, String operator)
			throws BusinessException {
		if (idList == null || idList.size() == 0)
			return;
		List<JKBXHeaderVO> voList = new ArrayList<JKBXHeaderVO>();
		
		String flag = !isOpp ? BXZbBO.MESSAGE_SETTLE : BXZbBO.MESSAGE_UNSETTLE;
		String param = null;
		for (String id : idList) {
			List<JKBXHeaderVO> name = new BXZbBO().queryHeadersByPrimaryKeys(new String[] { id }, BXConstans.BX_DJDL);
			if (name == null || name.size() == 0) {
				name = new BXZbBO().queryHeadersByPrimaryKeys(new String[] { id }, BXConstans.JK_DJDL);
			}
			
			JKBXHeaderVO head = name.get(0);
			param = SysinitAccessor.getInstance().getParaString(head.getPk_org(), "CMP37");
			head.setPk_jkbx(id);
			head.setPayflag(isOpp ? CMPExecStatus.UNPayed.getStatus() : CMPExecStatus.PayFinish.getStatus());
			head.setPaydate(isOpp ? null : operateDate);
			head.setPayman(isOpp ? null : operator);
			voList.add(head);
		}
		
		//根据表头查询整个聚合VO
		List<JKBXVO> jkbxvo = NCLocator.getInstance().lookup(IBXBillPrivate.class).retriveItems(voList);
		if(BXStatusConst.VounterCondition_ZF.equals(param)){
			// 发送会计平台
			if(!isOpp){
				//
			}else{
				//反向
				backToFip(flag, jkbxvo);
			}
		}
		new BaseDAO().updateVOArray(voList.toArray(new JKBXHeaderVO[] {}), new String[] { JKBXHeaderVO.PAYFLAG,
				JKBXHeaderVO.PAYDATE, JKBXHeaderVO.PAYMAN,JKBXHeaderVO.VOUCHERTAG });
	}
	
	
	/**
	 * 传会计平台的正向处理
	 * @param flag
	 * @param jkbxvo
	 * @throws BusinessException
	 */
	private void forwardToFip(String flag, List<JKBXVO> jkbxvo)
			throws BusinessException {
		if(jkbxvo.get(0).getParentVO().getVouchertag()==null){
			jkbxvo.get(0).getParentVO().setVouchertag(BXStatusConst.ZFFlag);
		}else if(BXStatusConst.MEDeal==jkbxvo.get(0).getParentVO().getVouchertag()){
			jkbxvo.get(0).getParentVO().setVouchertag(BXStatusConst.MEZFFlag);
		}else if(BXStatusConst.ZGDeal==jkbxvo.get(0).getParentVO().getVouchertag()){
			jkbxvo.get(0).getParentVO().setVouchertag(BXStatusConst.ZGZFFlag);
		}else if(BXStatusConst.ZGMEFlag==jkbxvo.get(0).getParentVO().getVouchertag()){
			jkbxvo.get(0).getParentVO().setVouchertag(BXStatusConst.ZGMEZFFlag);
		}
		//正向
		if(jkbxvo.get(0).getParentVO().getVouchertag()==null || 
				(jkbxvo.get(0).getParentVO().getVouchertag()!=null 
				&& jkbxvo.get(0).getParentVO().getVouchertag()!=BXStatusConst.SXFlag) ){
			bo.effectToFip(jkbxvo, flag);
		}
	}
	
	/**
	 * 传会计平台的反向处理
	 * @param flag
	 * @param jkbxvo
	 * @throws BusinessException
	 */
	private void backToFip(String flag, List<JKBXVO> jkbxvo)
			throws BusinessException {
		//反向
		if(jkbxvo.get(0).getParentVO().getVouchertag()==null || 
			(jkbxvo.get(0).getParentVO().getVouchertag()!=null 
			&& jkbxvo.get(0).getParentVO().getVouchertag()!=BXStatusConst.SXFlag) ){
			bo.effectToFip(jkbxvo, flag);
		}
		if(jkbxvo.get(0).getParentVO().getVouchertag()==BXStatusConst.ZFFlag){
			jkbxvo.get(0).getParentVO().setVouchertag(null);
		}else if(jkbxvo.get(0).getParentVO().getVouchertag()==BXStatusConst.MEZFFlag ){
			jkbxvo.get(0).getParentVO().setVouchertag(BXStatusConst.MEDeal);
		} else if(BXStatusConst.ZGZFFlag==jkbxvo.get(0).getParentVO().getVouchertag()){
			jkbxvo.get(0).getParentVO().setVouchertag(BXStatusConst.ZGDeal);
		} else if(BXStatusConst.ZGMEZFFlag==jkbxvo.get(0).getParentVO().getVouchertag()){
			jkbxvo.get(0).getParentVO().setVouchertag(BXStatusConst.ZGMEFlag);
		}
	}
	


	public List<SettlementBodyVO> autoBX(List<SettlementBodyVO> bodyList) throws BusinessException {
		if (bodyList == null)
			return null;
		List<SettlementBodyVO> result = new ArrayList<SettlementBodyVO>();
		for (SettlementBodyVO vo : bodyList) {
			if (PfDataCache.getBillType(vo.getPk_billtype()).getParentbilltype().equals(BXConstans.BX_DJLXBM)) {
				result.add(vo);
			}
		}
		return result;
	}

	public List<SettlementBodyVO> autoUsed(List<SettlementBodyVO> bodyList) throws BusinessException {
		return bodyList;
	}

	public List<String> getFromNoticeBill(List<String> idList) throws BusinessException {

		return null;
	}
	
	@Override
	public boolean isAutoFillEbankInfo(String arg0, String arg1, String arg2) throws BusinessException {
		return false;
	}
	
	@Override
	public void writeBackInnerStatus(boolean isTransfer, SettlementAggVO... aggVOs) throws BusinessException {

	}

	/**
	 * 提交资金组织操作类型
	 * 
	 * @author chendya
	 * 
	 */
	private static enum ECommitFtsType {
		COMMIT, CANCEL_COMMIT;
	}

	/**
	 * 提交资金组织
	 */
	@Override
	public void notifyPayTypeBillCommitToFts(BusiInfo... busiInfos) throws BusinessException {
		notifyErmCommitToFts(ECommitFtsType.COMMIT, busiInfos);
	}

	/**
	 * 取消提交资金组织
	 */
	@Override
	public void notifyPayTypeBillCancelCommitToFts(BusiInfo... busiInfos) throws BusinessException {
		notifyErmCommitToFts(ECommitFtsType.CANCEL_COMMIT, busiInfos);
	}

	/**
	 * 提交资金组织和取消提交资金组织
	 * 
	 * @param busiInfos
	 * @throws BusinessException
	 */
	private void notifyErmCommitToFts(ECommitFtsType type, BusiInfo... busiInfos) throws BusinessException {
		// 更新报销单据支付状态
		List<String> pk_billList = new ArrayList<String>();

		for (BusiInfo busiInfo : busiInfos) {
			pk_billList.add(busiInfo.getPk_bill());
		}
		List<JKBXHeaderVO> vos = bo.queryHeadersByPrimaryKeys((String[]) pk_billList.toArray(new String[0]),
				getDJDL(busiInfos[0]));
		for (JKBXHeaderVO vo : vos) {
			vo.setPayflag(ECommitFtsType.CANCEL_COMMIT.equals(type) ? CMPExecStatus.UNPayed.getStatus() : CMPExecStatus.Paying
					.getStatus());
		}
		// 更新支付状态
		bo.updateHeaders((JKBXHeaderVO[]) vos.toArray(new JKBXHeaderVO[0]), new String[] { JKBXHeaderVO.PAYFLAG });
	}

	@Override
	public void notifyPayTypeBillFtsRefuseDeal(BusiInfo... busiInfos) throws BusinessException {

	}

	@Override
	public void notifyPayTypeBillInnertansferCancelForcePay(BusiInfo... busiInfos) throws BusinessException {

	}

	@Override
	public void notifyPayTypeBillInnertansferForcePay(BusiInfo... busiInfos) throws BusinessException {

	}

	@Override
	public void notifyPayTypeBillInnertansferRefuseCommisionPay(BusiInfo... busiInfos) throws BusinessException {

	}

	@Override
	public void notifyPayTypeBillInnertansferSuccessAndEffect(BusiStateChangeVO... busiStateChangeVOs) throws BusinessException {
		//
	}

	@Override
	public void notifyPayTypeBillCancelInnertansferAndCancelEffect(BusiStateChangeVO... busiStateChangeVOs)
			throws BusinessException {
		//
	}

}