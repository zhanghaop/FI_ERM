package nc.impl.erm.expamortize;

import nc.bs.businessevent.EventDispatcher;
import nc.bs.dao.BaseDAO;
import nc.bs.erm.event.ErmBusinessEvent;
import nc.bs.erm.event.ErmEventType;
import nc.bs.erm.expamortize.ExpAmoritizeConst;
import nc.bs.erm.util.ErAccperiodUtil;
import nc.bs.erm.util.ErLockUtil;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.itf.fi.pub.Currency;
import nc.pubitf.erm.expamortize.IExpAmortize;
import nc.pubitf.erm.expamortize.IExpAmortizeinfoQuery;
import nc.pubitf.erm.expamortize.IExpAmortizeprocManage;
import nc.pubitf.fip.service.IFipMessageService;
import nc.util.erm.expamortize.ExpAmortizeprocUtil;
import nc.util.erm.expamortize.ExpamtVoChecker;
import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.bd.period2.AccperiodmonthVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.common.MessageVO;
import nc.vo.erm.expamortize.AggExpamtinfoVO;
import nc.vo.erm.expamortize.ExpamtDetailVO;
import nc.vo.erm.expamortize.ExpamtinfoVO;
import nc.vo.erm.expamortize.ExpamtprocVO;
import nc.vo.fip.service.FipMessageVO;
import nc.vo.fip.service.FipRelationInfoVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.util.AuditInfoUtil;
import nc.vo.util.BDVersionValidationUtil;

public class ExpAmortizeImpl implements IExpAmortize {

	private BaseDAO basedao;

	@Override
	public MessageVO[] amortize(String pk_org, String currYearMonth,
			ExpamtinfoVO[] vos) throws BusinessException {
		if (vos == null || vos.length == 0) {
			return null;
		}
		//校验是否结账
		ExpamtVoChecker.checkEndAcc(vos[0],currYearMonth);
		MessageVO[] resultMessageVOs = new MessageVO[vos.length];

		IExpAmortize service = NCLocator.getInstance().lookup(
				IExpAmortize.class);
		for (int i = 0; i < vos.length; i++) {
			// 每个报销单的摊销都起独立的事务进行处理
			MessageVO result = null;
			try {
				result = service.amortize_RequiresNew(pk_org, currYearMonth, vos[i]);
			} catch (Exception e) {
				ExceptionHandler.consume(e);
				String errMsg = e.getMessage();
				AggExpamtinfoVO aggExpamt = new AggExpamtinfoVO();
				aggExpamt.setParentVO(vos[i]);
				result = new MessageVO(aggExpamt, ActionUtils.EXPAMORTIZE, false, errMsg);
				result.setShowField(ExpamtinfoVO.BX_BILLNO);
			}
			resultMessageVOs[i] = result;
		}

		return resultMessageVOs;
	}

	@Override
	public MessageVO amortize_RequiresNew(String pk_org, String currYearMonth,
			ExpamtinfoVO vo) throws BusinessException {
		MessageVO result = null;

		if (currYearMonth == null) {
			currYearMonth = ErAccperiodUtil.getAccperiodmonthByAccMonth(pk_org,
					currYearMonth).getYearmth();
		}
		
		//处理本期摊销金额
		checkCurrAmount(vo);

		IExpAmortizeinfoQuery qryService = NCLocator.getInstance().lookup(
				IExpAmortizeinfoQuery.class);
		
		// 查询待摊信息，保证信息完整,补充信息
		AggExpamtinfoVO aggvo = qryService.fillUpAggExpamtinfo(vo, currYearMonth);
		
		// 修改加锁
		ErLockUtil.lockAggVOByPk("ERM_expamortize", aggvo);
		// 版本校验
		BDVersionValidationUtil.validateVersion(vo);
		
		// vo校验
		ExpamtVoChecker checker = new ExpamtVoChecker();
		boolean flag=checker.checkAmortize(aggvo, currYearMonth);
		if(flag){
			//已摊销，则仅发送会计平台生成凭证,查询摊销过程记录
//			ExpamtDetailVO[] childrenVO = (ExpamtDetailVO[]) aggvo.getChildrenVO();
//			String[] infoPks = VOUtil.getAttributeValues(childrenVO, ExpamtDetailVO.PK_EXPAMTDETAIL);
//			ExpamtprocVO[] expamtproc = NCLocator.getInstance().lookup(IExpAmortizeprocQuery.class).queryByInfoPksAndAccperiod(infoPks, currYearMonth);
//			for (ExpamtDetailVO expamtdetailVO : childrenVO) {
//				for(ExpamtprocVO proc : expamtproc){
//					if(expamtdetailVO.getPk_expamtdetail().equals(proc.getPk_expamtinfo())){
//						expamtdetailVO.setCurr_amount(proc.getCurr_amount());
//						expamtdetailVO.setCurr_orgamount(proc.getCurr_orgamount());
//						expamtdetailVO.setCurr_groupamount(proc.getCurr_groupamount());
//						expamtdetailVO.setCurr_globalamount(proc.getCurr_globalamount());
//					}
//				}
//			}
			
			sendMessageToFip(aggvo,FipMessageVO.MESSAGETYPE_DEL);
			sendMessageToFip(aggvo,FipMessageVO.MESSAGETYPE_ADD);
			
			result = new MessageVO(aggvo, ActionUtils.EXPAMORTIZE);
			result.setShowField(ExpamtinfoVO.BX_BILLNO);
			return result;
		}
		
		beforeAmortize(aggvo);
		// 事前控制
		fireBeforeAmortizeEvent(aggvo);

		// 设置剩余摊销金额，剩余摊销期
		setResValues(aggvo);

		// 数据更新
		updateVOsByFields(aggvo, new String[] { ExpamtinfoVO.BILLSTATUS,
				ExpamtinfoVO.RES_AMOUNT, ExpamtinfoVO.RES_ORGAMOUNT,
				ExpamtinfoVO.RES_GROUPAMOUNT, ExpamtinfoVO.RES_GLOBALAMOUNT,
				ExpamtinfoVO.RES_PERIOD }, currYearMonth);

		// 保存摊销记录信息
		saveExpamtProcInfo(aggvo, currYearMonth, false);

		// 事后控制
		fireAfterAmortizeEvent(aggvo);

		// 发送消息到会计平台
		sendMessageToFip(aggvo, FipMessageVO.MESSAGETYPE_ADD);

		// 查询最新消息
		AggExpamtinfoVO aggExpamtInfo = qryService.queryByPk(aggvo
				.getParentVO().getPrimaryKey(), currYearMonth);
		
		result = new MessageVO(aggExpamtInfo, ActionUtils.EXPAMORTIZE);
		result.setShowField(ExpamtinfoVO.BX_BILLNO);
		return result;
	}
	
	// 校验本期摊销金额
	private void checkCurrAmount(ExpamtinfoVO newVo) throws BusinessException {
		UFDouble currAmount = newVo.getCurr_amount() == null ? UFDouble.ZERO_DBL : newVo.getCurr_amount();
		if (currAmount.compareTo(UFDouble.ZERO_DBL) <= 0 || currAmount.compareTo(newVo.getRes_amount()) > 0) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0187")/*@res "本期摊销金额应大于0,小于剩余摊销金额!"*/);
		}
		
		if (currAmount.compareTo(newVo.getRes_amount()) == 0 && newVo.getRes_period() > 1 ) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0188")/*@res "剩余摊销期大于一期,不能将余额全部摊销!"*/);
		}
	}

	/**
	 * 保存摊销过程VO
	 * 
	 * @param aggvo
	 * @param currYearMonth
	 * @return
	 * @throws BusinessException
	 */
	private ExpamtprocVO[] saveExpamtProcInfo(AggExpamtinfoVO aggvo, String currYearMonth, boolean isUnAmt) throws BusinessException {
		ExpamtprocVO[] procVos = ExpAmortizeprocUtil.getExpamtProcVoFromExpamtinfoVO(aggvo, isUnAmt);

		for (ExpamtprocVO procVo : procVos) {
			procVo.setAccperiod(currYearMonth);
			procVo.setAmortize_user(AuditInfoUtil.getCurrentUser());
			UFDate newAmortize_date = new UFDate(InvocationInfoProxy.getInstance().getBizDateTime());
			procVo.setAmortize_date(newAmortize_date);

		}
		IExpAmortizeprocManage procService = NCLocator.getInstance().lookup(IExpAmortizeprocManage.class);

		return procService.insertVOs(procVos);
	}

	@Override
	public MessageVO[] unAmortize(String pk_org, String currYearMonth, ExpamtinfoVO[] vos) throws BusinessException {
		if (vos == null || vos.length == 0) {
			return null;
		}
		//校验是否结账
		ExpamtVoChecker.checkEndAcc(vos[0],currYearMonth);
		
		MessageVO[] resultMessageVOs = new MessageVO[vos.length];

		IExpAmortize service = NCLocator.getInstance().lookup(IExpAmortize.class);
		for (int i = 0; i < vos.length; i++) {
			// 每个报销单的摊销都起独立的事务进行处理
			MessageVO result = null;
			try {
				result = service.unAmortize_RequiresNew(pk_org, currYearMonth, vos[i]);
			} catch (Exception e) {
				ExceptionHandler.consume(e);
				String errMsg = e.getMessage();
				AggExpamtinfoVO aggExpamt = new AggExpamtinfoVO();
				aggExpamt.setParentVO(vos[i]);
				result = new MessageVO(aggExpamt, ActionUtils.UNEXPAMORTIZE, false, errMsg);
				result.setShowField(ExpamtinfoVO.BX_BILLNO);
			}
			resultMessageVOs[i] = result;
		}

		return resultMessageVOs;
	}

	@Override
	public MessageVO unAmortize_RequiresNew(String pk_org, String currYearMonth, ExpamtinfoVO vo) throws BusinessException {
		MessageVO result = null;

		if (currYearMonth == null) {
			currYearMonth = ErAccperiodUtil.getAccperiodmonthByAccMonth(pk_org, currYearMonth).getYearmth();
		}

		IExpAmortizeinfoQuery qryService = NCLocator.getInstance().lookup(IExpAmortizeinfoQuery.class);

		// 查询待摊信息，保证信息完整,补充信息
		AggExpamtinfoVO aggvo = qryService.fillUpAggExpamtinfo(vo, currYearMonth);

		// 修改加锁
		ErLockUtil.lockAggVOByPk("ERM_expamortize", aggvo);

		// 版本校验
		BDVersionValidationUtil.validateVersion(vo);
		
		//  vo校验
		new ExpamtVoChecker().checkUnAmortize(aggvo, currYearMonth);

		// 事前控制
		fireBeforeUnAmortizeEvent(aggvo);

		//  计算反摊销后摊销信息的金额、剩余摊销期等
		setUnResValues(aggvo);
		
		// 数据更新
		updateVOsByFields(aggvo, new String[] { ExpamtinfoVO.BILLSTATUS, ExpamtinfoVO.RES_AMOUNT, ExpamtinfoVO.RES_ORGAMOUNT, ExpamtinfoVO.RES_GROUPAMOUNT, ExpamtinfoVO.RES_GLOBALAMOUNT,
				ExpamtinfoVO.RES_PERIOD }, currYearMonth);

		// 摊销记录记录
		saveExpamtProcInfo(aggvo, currYearMonth, true);

		// 事后控制
		fireAfterUnAmortizeEvent(aggvo);
		
		//  VO要按原始数据进行反摊销，发送消息到会计平台
		sendMessageToFip(aggvo, FipMessageVO.MESSAGETYPE_DEL);

		// 查询最新消息
		AggExpamtinfoVO aggExpamtInfo = qryService.queryByPk(aggvo.getParentVO().getPrimaryKey(), currYearMonth);
		result = new MessageVO(aggExpamtInfo, ActionUtils.EXPAMORTIZE);
		result.setShowField(ExpamtinfoVO.BX_BILLNO);
		return result;
	}
	
	private void beforeAmortize(AggExpamtinfoVO aggvo) {
		ExpamtinfoVO info = (ExpamtinfoVO) aggvo.getParentVO();
		info.setAmortize_user(AuditInfoUtil.getCurrentUser());
		info.setAmortize_date(new UFDate(InvocationInfoProxy.getInstance().getBizDateTime()));
	}

	private void fireAfterAmortizeEvent(AggExpamtinfoVO aggvo)
			throws BusinessException {
		EventDispatcher.fireEvent(new ErmBusinessEvent(
				ExpAmoritizeConst.ExpamoritizeInfo_MDID,
				ErmEventType.TYPE_AMORTIZE_AFTER,
				new AggExpamtinfoVO[] { aggvo }));
	}

	private void fireBeforeAmortizeEvent(AggExpamtinfoVO aggvo)
			throws BusinessException {
		EventDispatcher.fireEvent(new ErmBusinessEvent(
				ExpAmoritizeConst.ExpamoritizeInfo_MDID,
				ErmEventType.TYPE_AMORTIZE_BEFORE,
				new AggExpamtinfoVO[] { aggvo }));
	}
	
	private void fireAfterUnAmortizeEvent(AggExpamtinfoVO aggvo)
			throws BusinessException {
		EventDispatcher.fireEvent(new ErmBusinessEvent(
				ExpAmoritizeConst.ExpamoritizeInfo_MDID,
				ErmEventType.TYPE_UNAMORTIZE_AFTER,
				new AggExpamtinfoVO[] { aggvo }));
	}

	private void fireBeforeUnAmortizeEvent(AggExpamtinfoVO aggvo)
			throws BusinessException {
		EventDispatcher.fireEvent(new ErmBusinessEvent(
				ExpAmoritizeConst.ExpamoritizeInfo_MDID,
				ErmEventType.TYPE_UNAMORTIZE_BEFORE,
				new AggExpamtinfoVO[] { aggvo }));
	}

	private void setResValues(AggExpamtinfoVO aggvo) {// 剩余金额，剩余摊销期
		ExpamtinfoVO parentVo = (ExpamtinfoVO) aggvo.getParentVO();
		ExpamtDetailVO[] children = (ExpamtDetailVO[]) aggvo.getChildrenVO();
		parentVo.setRes_period(parentVo.getRes_period() - 1);
		parentVo.setRes_amount(parentVo.getRes_amount().sub(
				parentVo.getCurr_amount()));
		parentVo.setRes_orgamount(parentVo.getRes_orgamount().sub(
				parentVo.getCurr_orgamount()));
		parentVo.setRes_groupamount(parentVo.getRes_groupamount().sub(
				parentVo.getCurr_groupamount()));
		parentVo.setRes_globalamount(parentVo.getRes_globalamount().sub(
				parentVo.getCurr_globalamount()));

		if (parentVo.getRes_period() == 0) {// 摊销信息状态设置
			parentVo.setBillstatus(ExpAmoritizeConst.Billstatus_Amted);
		} else if (parentVo.getRes_period() == (parentVo.getTotal_period() - 1)) {
			parentVo.setBillstatus(ExpAmoritizeConst.Billstatus_Amting);
		}

		if (children != null) {
			for (ExpamtDetailVO detail : children) {
				detail.setRes_period(detail.getRes_period() - 1);
				detail.setRes_amount(detail.getRes_amount().sub(
						detail.getCurr_amount()));
				detail.setRes_orgamount(detail.getRes_orgamount().sub(
						detail.getCurr_orgamount()));
				detail.setRes_groupamount(detail.getRes_groupamount().sub(
						detail.getCurr_groupamount()));
				detail.setRes_globalamount(detail.getRes_globalamount().sub(
						detail.getCurr_globalamount()));
			}
		}
	}

	/**
	 * 取消摊销数据修改
	 * @param aggvo
	 */
	private void setUnResValues(AggExpamtinfoVO aggvo) {// 剩余金额，剩余摊销期
		ExpamtinfoVO parentVo = (ExpamtinfoVO) aggvo.getParentVO();
		ExpamtDetailVO[] children = (ExpamtDetailVO[]) aggvo.getChildrenVO();
		parentVo.setRes_period(parentVo.getRes_period() + 1);
		parentVo.setRes_amount(parentVo.getRes_amount().add(
				parentVo.getCurr_amount()));
		parentVo.setRes_orgamount(parentVo.getRes_orgamount().add(
				parentVo.getCurr_orgamount()));
		parentVo.setRes_groupamount(parentVo.getRes_groupamount().add(
				parentVo.getCurr_groupamount()));
		parentVo.setRes_globalamount(parentVo.getRes_globalamount().add(
				parentVo.getCurr_globalamount()));

		if (parentVo.getRes_period() == parentVo.getTotal_period()) {// 摊销信息状态设置
			parentVo.setBillstatus(ExpAmoritizeConst.Billstatus_Init);
		} else {
			parentVo.setBillstatus(ExpAmoritizeConst.Billstatus_Amting);
		}

		if (children != null) {
			for (ExpamtDetailVO detail : children) {
				detail.setRes_period(detail.getRes_period() + 1);
				detail.setRes_amount(detail.getRes_amount().add(
						detail.getCurr_amount()));
				detail.setRes_orgamount(detail.getRes_orgamount().add(
						detail.getCurr_orgamount()));
				detail.setRes_groupamount(detail.getRes_groupamount().add(
						detail.getCurr_groupamount()));
				detail.setRes_globalamount(detail.getRes_globalamount().add(
						detail.getCurr_globalamount()));
			}
		}
	}
	
	public void updateVOsByFields(AggExpamtinfoVO vo, String[] fields,
			String currYearMonth) throws BusinessException {
		// 数据库字段更新主表
		getBaseDAO().updateVO((SuperVO) vo.getParentVO(), fields);
		// 数据库字段更新子表
		if (vo.getChildrenVO() != null) {
			getBaseDAO().updateVOArray((SuperVO[]) vo.getChildrenVO(), fields);
		}
	}

	private BaseDAO getBaseDAO() {
		if (basedao == null) {
			basedao = new BaseDAO();
		}
		return basedao;
	}
	
	/**
	 * 发送消息到会计平台
	 * 按摊销记录生成凭证
	 * @param vos
	 * @throws BusinessException
	 */
	private  void sendMessageToFip(AggExpamtinfoVO aggvo,  int messageType) throws BusinessException {
		ExpamtinfoVO parentVo = (ExpamtinfoVO)aggvo.getParentVO();
		// 包装消息
		FipRelationInfoVO reVO = new FipRelationInfoVO();
		reVO.setPk_group(parentVo.getPk_group());
		reVO.setPk_org(parentVo.getPk_org());
		
		AccperiodmonthVO accperiodmonthVO = ErAccperiodUtil.getAccperiodmonthByUFDate(parentVo.getPk_org(),
				parentVo.getAmortize_date());
		
		reVO.setRelationID(aggvo.getParentVO().getPrimaryKey() + "_" + accperiodmonthVO.getYearmth());// 关联ID设置为 pk+摊销期间

		reVO.setPk_system(BXConstans.ERM_PRODUCT_CODE_Lower);
		// 最后摊销时间作为摊销信息的业务日期使用
		UFDate busidate = parentVo.getAmortize_date();
		reVO.setBusidate(busidate);
		reVO.setPk_billtype(ExpAmoritizeConst.Expamoritize_BILLTYPE);

		// 最后摊销人作为操作人使用
		reVO.setPk_operator(parentVo.getAmortize_user());
		
		UFDouble total = parentVo.getCurr_amount();
		total = total.setScale(Currency.getCurrDigit(parentVo.getBzbm()), UFDouble.ROUND_HALF_UP);
		reVO.setFreedef3(String.valueOf(total));
		
		reVO.setFreedef2(((ExpamtinfoVO)aggvo.getParentVO()).getZy());//摘要
		
		FipMessageVO messageVO = new FipMessageVO();
		messageVO.setBillVO(aggvo);
		messageVO.setMessagetype(messageType);
		messageVO.setMessageinfo(reVO);
		// 发送到会计平台
		NCLocator.getInstance().lookup(IFipMessageService.class).sendMessage(messageVO);
	}

}
