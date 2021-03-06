package nc.bs.er.settle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import nc.bs.arap.bx.BXBusItemBO;
import nc.bs.arap.bx.BXZbBO;
import nc.bs.dao.BaseDAO;
import nc.bs.erm.util.CacheUtil;
import nc.bs.framework.common.NCLocator;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.cmp.busi.ISettleNotifyPayTypeBusiBillService;
import nc.itf.cmp.settlement.ISettlement;
import nc.itf.er.pub.IArapBillTypePublic;
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
import nc.vo.erm.accruedexpense.AccruedVerifyVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.fip.external.FipExtendAggVO;
import nc.vo.fip.service.FipRelationInfoVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;

public class ErForCmpBO implements ISettleNotifyPayTypeBusiBillService {

	private final BXZbBO bo = new BXZbBO();
	private JKBXVO jkbxVO = null;

	// 委托付款
	@Override
	public void execStatuesChange(BusiInfo busiInfo, CMPExecStatus status)
			throws BusinessException {
		// 更新报销单据支付状态
		List<JKBXHeaderVO> voList = new ArrayList<JKBXHeaderVO>();
		List<JKBXHeaderVO> vos = bo.queryHeadersByPrimaryKeys(new String[] { busiInfo.getPk_bill() }, getDJDL(busiInfo));
		for (JKBXHeaderVO vo : vos) {
			vo.setPayflag(status.getStatus());
			if(status.getStatus() == BXStatusConst.PAYFLAG_PayFinish){
				vo.setPayman(busiInfo.getOperator());
				vo.setPaydate(busiInfo.getOperatorDate());
			}else{
				vo.setPayman(null);
				vo.setPaydate(null);
			}
			voList.add(vo);
		}
		
		// 委托办理支付成功时生成凭证
		if (voList.get(0).getPayflag().intValue() == BXStatusConst.PAYFLAG_PayFinish) {
			if (SettleUtil.isJsToFip(voList.get(0))) {
				List<JKBXVO> jkbxvo = NCLocator.getInstance().lookup(IBXBillPrivate.class).retriveItems(voList);
				forwardToFip(BXZbBO.MESSAGE_SETTLE, jkbxvo);// 生成凭证的正向操作
			}
		}
		
		// 更新支付状态
		bo.updateHeaders(voList.toArray(new JKBXHeaderVO[0]), new String[] {
				JKBXHeaderVO.PAYFLAG,JKBXHeaderVO.PAYMAN, JKBXHeaderVO.PAYDATE, JKBXHeaderVO.VOUCHERTAG });
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
	
	@Override
	public void billStateChange(BusiInfo busiInfo, BusiStateTrans trans) throws BusinessException {
		// 审核->签字
		if (BusiStatus.Audit.equals(trans.getFrom()) && BusiStatus.Sign.equals(trans.getTo())) {
			jkbxVO = getBxVO(busiInfo, trans);
			bo.signVo(busiInfo.getOperator(), busiInfo.getOperatorDate(), jkbxVO);

		}
		// 签字->反签字
		else if (BusiStatus.Sign.equals(trans.getFrom()) && BusiStatus.Audit.equals(trans.getTo())) {
			jkbxVO = getBxVO(busiInfo, trans);
			bo.unSettle(new JKBXVO[] { jkbxVO });
		}
	}
	
	@Override
	public void effectStateChange(BusiInfo busiInfo, BusiStateTrans trans) throws BusinessException {
		JKBXVO jkbxVo = getBxVO(busiInfo, trans);
		String flag = null;
		if (BusiStatus.Effet.equals(trans.getTo())) {// 生效
			flag = BXZbBO.MESSAGE_SETTLE;
			bo.effectVo(jkbxVo);
		} else if (BusiStatus.EffectNever.equals(trans.getTo())) {// 反生效
			flag = BXZbBO.MESSAGE_UNSETTLE;
		}
		
		if (!SettleUtil.isJsToFip(jkbxVo.getParentVO()) && BXZbBO.MESSAGE_SETTLE.equals(flag)) {
			if (jkbxVo.getParentVO().getVouchertag() == null || jkbxVo.getParentVO().getVouchertag() == BXStatusConst.SXFlag) {
				jkbxVo.getParentVO().setVouchertag(BXStatusConst.SXFlag);
				bo.updateHeaders(new JKBXHeaderVO[] { jkbxVo.getParentVO() }, new String[] { JKBXHeaderVO.VOUCHERTAG });
				bo.effectToFip(Arrays.asList(new JKBXVO[] { jkbxVo }), flag);
			}
		}
		
		//取消签字与凭证环节没有关系
		if(BXZbBO.MESSAGE_UNSETTLE.equals(flag)){
			if(jkbxVo.getParentVO().getVouchertag()!= null && 
					jkbxVo.getParentVO().getVouchertag() == BXStatusConst.MEDeal){
				//凭证环节是月末凭证时，将单据的凭证都删除（暂估凭证，月末凭证）
				bo.effectToFip(Arrays.asList(new JKBXVO[] { jkbxVo }), flag); // 删除月末凭证
			}else{
				bo.effectToFip(Arrays.asList(new JKBXVO[] { jkbxVo }), flag);
			}
			
			//判断是否有暂估凭证
			boolean isExistZg = isExistVourcher(jkbxVo.getParentVO(), jkbxVo.getParentVO().getPk()+ "_"+BXStatusConst.ZGDeal);
			if(isExistZg){
				jkbxVo.getParentVO().setVouchertag(BXStatusConst.ZGDeal);
			}else{
				jkbxVo.getParentVO().setVouchertag(null);
			}
			bo.updateHeaders(new JKBXHeaderVO[]{jkbxVo.getParentVO()}, new String[]{JKBXHeaderVO.VOUCHERTAG});
		}
	}
	
	/**
	 * 是否存在凭证
	 * @param headerVO 表头VO
	 * @param relationId 关联ID
	 * @return
	 * @throws BusinessException
	 */
	private boolean isExistVourcher(JKBXHeaderVO headerVO , String relationId) throws BusinessException {
		FipRelationInfoVO srcinfovo = new FipRelationInfoVO();
		srcinfovo.setPk_group(headerVO.getPk_group());
		srcinfovo.setPk_org(headerVO.getPk_payorg());
		if(relationId != null && relationId.trim().length() > 0){
			srcinfovo.setRelationID(relationId);
		}else{
			srcinfovo.setRelationID(jkbxVO.getParentVO().getPk());
		}
		srcinfovo.setPk_billtype(headerVO.getDjlxbm());
		IFipBillQueryService ip = NCLocator.getInstance().lookup(IFipBillQueryService.class);
		FipExtendAggVO[] datavos = ip.queryDesBillBySrc(new FipRelationInfoVO[]{srcinfovo}, null);
		
		if(datavos != null && datavos.length > 0){
			return true;
		}else{
			return false;
		}
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
		
		//分摊信息
		Collection<CShareDetailVO> cShares = bo.queryCSharesVOS(new JKBXHeaderVO[] { bxvo.getParentVO() });// 分摊明细
		bxvo.setcShareDetailVo(cShares.toArray(new CShareDetailVO[] {}));
		
		//核销明细信息
		Collection<AccruedVerifyVO> accruedVerifyVOs = bo.queryAccruedVerifyVOS(bxvo.getParentVO());
		bxvo.setAccruedVerifyVO(accruedVerifyVOs.toArray(new AccruedVerifyVO[] {}));
		
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
		List<JKBXHeaderVO> vos = bo.queryHeadersByPrimaryKeys(new String[] { payInfo.getBillid() }, payInfo.getBilltype());
		JKBXHeaderVO head = vos.get(0);

		// 回写支付状态
		head.setPayflag(getPayStatus(payInfo));
		head.setPaydate(payInfo.getOperateDate());
		head.setPayman(payInfo.getOperator());

		// 回写结算信息,结算时仅回写支付信息
		String settleno = payInfo.getSettleno();
		head.setJsh(settleno);
		String djlxbm = payInfo.getBilltype();

		if (djlxbm == null || djlxbm.length() == 0) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0", "02011v61013-0028")/*
																																 * @
																																 * res
																																 * "结算单据回写借款/报销单据失败，网银支付信息中缺失单据类型"
																																 */);
		}
		try {
			// 这里对自定义交易类型进行下处理
			head.setDjlxbm(djlxbm);
			head.setDjdl(getDJDL(djlxbm));

			List<JKBXHeaderVO> voList = new ArrayList<JKBXHeaderVO>();
			voList.add(head);

			// 网银支付支付完成时处理生成凭证
			if (head.getPayflag().intValue() == BXStatusConst.PAYFLAG_PayFinish) {
				if (SettleUtil.isJsToFip(head)) {
					List<JKBXVO> jkbxvo = NCLocator.getInstance().lookup(IBXBillPrivate.class).retriveItems(voList);
					forwardToFip(BXZbBO.MESSAGE_SETTLE, jkbxvo);// 生成凭证的正向操作
				}
			}
			new BaseDAO().updateVOArray(voList.toArray(new JKBXHeaderVO[] {}), new String[] { JKBXHeaderVO.PAYFLAG, JKBXHeaderVO.PAYDATE, JKBXHeaderVO.PAYMAN, JKBXHeaderVO.JSH,
					JKBXHeaderVO.VOUCHERTAG });
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
	public void invokeCmp(JKBXVO jkbxVo, UFDate date, BusiStatus busiStatus) throws BusinessException {
		JKBXHeaderVO head = jkbxVo.getParentVO();
		if(head.isAdjustBxd()){
			// 报销类型为费用调整的单据，不进行支付结算
			return ;
		}
		if (SettleUtil.hasSettleInfo(head, head.getDjdl())) {
			ISettlement settle =  NCLocator.getInstance().lookup(ISettlement.class);
			DjLXVO djlxvo = NCLocator.getInstance().lookup(IArapBillTypePublic.class).getDjlxvoByDjlxbm(jkbxVo.getParentVO().getDjlxbm(), jkbxVo.getParentVO()
					.getPk_group());
			SettlementBatchOperateVO batchoperateVOs = new SettlementBatchOperateVO();
			batchoperateVOs.setBusibill(jkbxVo);
			batchoperateVOs.setMsg(SettleUtil.getCmpMsg(jkbxVo, djlxvo, date, busiStatus));
			batchoperateVOs.setSettlementAgg(jkbxVo.getSettlementInfo());
			if (busiStatus == BusiStatus.Save) {
				if (head.getDjzt() == BXStatusConst.DJZT_Saved) {
					//全额冲借款（zfje=0 && hkje=0）因为不生成结算信息，所以也不存在删除结算信息动作。
					// 支付单位没有变化的，也不删除结算信息。
					if (jkbxVo.getBxoldvo() != null
							&& !jkbxVo.getParentVO().getPk_payorg().equals(jkbxVo.getBxoldvo().getParentVO().getPk_payorg())
							&& !((jkbxVo.getBxoldvo().getParentVO().getHkybje().compareTo(UFDouble.ZERO_DBL) == 0) && (jkbxVo
									.getBxoldvo().getParentVO().getZfybje().compareTo(UFDouble.ZERO_DBL) == 0))
							&& jkbxVo.getBxoldvo().getParentVO().getDjzt() == BXStatusConst.DJZT_Saved) {
						settle.notifySettlementBatchDelete(SettleUtil.getCmpMsg(jkbxVo.getBxoldvo(), djlxvo, date, busiStatus));
					}
					settle.notifySettlementBatchSave(batchoperateVOs);
				} else {
					settle.notifySettlementBatchReserveAudit(SettleUtil.getCmpMsg(jkbxVo, djlxvo, date, busiStatus));
				}
			} else if (busiStatus == BusiStatus.Tempeorary) {
				settle.notifySettlementBatchTempSave(batchoperateVOs);
			} else if (busiStatus == BusiStatus.Deleted) {
				settle.notifySettlementBatchDelete(SettleUtil.getCmpMsg(jkbxVo, djlxvo, date, busiStatus));
			} else {
				settle.notifySettlementBatchAudit(SettleUtil.getCmpMsg(jkbxVo, djlxvo, date, busiStatus));
			}
		}
	}

	public void notify4HandSettle(List<String> idList, boolean isOpp, UFDate operateDate, String operator)
			throws BusinessException {
		if (idList == null || idList.size() == 0)
			return;
		List<JKBXHeaderVO> voList = new ArrayList<JKBXHeaderVO>();
		
		String flag = !isOpp ? BXZbBO.MESSAGE_SETTLE : BXZbBO.MESSAGE_UNSETTLE;
		JKBXHeaderVO head = null;
		for (String id : idList) {
			List<JKBXHeaderVO> name = new BXZbBO().queryHeadersByPrimaryKeys(new String[] { id }, BXConstans.BX_DJDL);
			if (name == null || name.size() == 0) {
				name = new BXZbBO().queryHeadersByPrimaryKeys(new String[] { id }, BXConstans.JK_DJDL);
			}
			
			head = name.get(0);
			head.setPk_jkbx(id);
			head.setPayflag(isOpp ? CMPExecStatus.UNPayed.getStatus() : CMPExecStatus.PayFinish.getStatus());
			head.setPaydate(isOpp ? null : operateDate);
			head.setPayman(isOpp ? null : operator);
			voList.add(head);
		}
		
		//根据表头查询整个聚合VO
		List<JKBXVO> jkbxvo = NCLocator.getInstance().lookup(IBXBillPrivate.class).retriveItems(voList);
		if(SettleUtil.isJsToFip(head)){
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
	private void forwardToFip(String flag, List<JKBXVO> jkbxvo) throws BusinessException {
		JKBXHeaderVO jkbxHeadVO = jkbxvo.get(0).getParentVO();
		if (jkbxHeadVO.getVouchertag() == null) {
			jkbxHeadVO.setVouchertag(BXStatusConst.ZFFlag);
		} else if (BXStatusConst.MEDeal == jkbxHeadVO.getVouchertag()) {
			jkbxHeadVO.setVouchertag(BXStatusConst.MEZFFlag);
		} else if (BXStatusConst.ZGDeal == jkbxHeadVO.getVouchertag()) {
			jkbxHeadVO.setVouchertag(BXStatusConst.ZGZFFlag);
		} else if (BXStatusConst.ZGMEFlag == jkbxHeadVO.getVouchertag()) {
			jkbxHeadVO.setVouchertag(BXStatusConst.ZGMEZFFlag);
		}
		
		Integer voucherTag = jkbxHeadVO.getVouchertag();
		// 正向
		if (voucherTag == null || (voucherTag != null && voucherTag != BXStatusConst.SXFlag)) {
			// 已经生成则不再生成，网银支付时，红冲时会重复调用
			if (voucherTag == null || !isExistVourcher(jkbxHeadVO, jkbxHeadVO.getPk() + "_" + voucherTag)) {
				bo.effectToFip(jkbxvo, flag);
			}
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
		return bodyList;
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
	
	// 承兑汇票退票,不处理
	@Override
	public List<ReturnBillRetDetail> processReturnBill(ReturnBill4BusiVO bill4BusiVO) throws BusinessException {
		return null;
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
	}

	@Override
	public void notifyPayTypeBillCancelInnertansferAndCancelEffect(BusiStateChangeVO... busiStateChangeVOs)
			throws BusinessException {
	}
	
	/**
	 * 自动结算
	 */
	public boolean isAutoSettle(String pk_group, String pk_tradetype, SettlementAggVO... settlementAggVOs) throws BusinessException {
		boolean isAutoSettle = false;
		DjLXVO[] vos = CacheUtil.getValueFromCacheByWherePart(DjLXVO.class, "pk_group = '" + pk_group + "' and djlxbm = '" + pk_tradetype + "'");
		if (vos == null || vos.length == 0) {
			return false;
		} else {
			isAutoSettle = vos[0].isAutoSettle();
		}
		return isAutoSettle;
	}
	
	// bx无委托收款,不处理
	public void billChange(BusiInfo busiInfo, Map<String, MoneyDetail> value) throws BusinessException {
	}
	
	private String getDJDL(BusiInfo busiInfo) {
		String billtype = busiInfo.getBill_type();
		return getDJDL(billtype);

	}

	private String getDJDL(String billtype) {
		if (billtype.startsWith("263")) {
			return BXConstans.JK_DJDL;
		} else if (billtype.startsWith("264")) {
			return BXConstans.BX_DJDL;
		}
		return null;
	}
}
