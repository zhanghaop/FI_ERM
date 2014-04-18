package nc.ui.erm.billpub.action;

import nc.bs.erm.util.ErmDjlxCache;
import nc.bs.erm.util.ErmDjlxConst;
import nc.ui.erm.action.RapidShareAbstractAction;
import nc.ui.erm.billpub.model.ErmBillBillManageModel;
import nc.ui.erm.costshare.common.ErmForCShareUiUtil;
import nc.ui.pub.bill.BillData;
import nc.ui.pub.bill.BillModel;
import nc.ui.uif2.editor.BillForm;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.BXHeaderVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.erm.accruedexpense.AccruedVerifyVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;

public class RapidShareBXAction extends RapidShareAbstractAction {

    private static final long serialVersionUID = 1L;

    private JKBXHeaderVO headervo;

    @Override
    protected void initDataBefore() throws ValidationException {
        ((BillForm) getEditor()).getBillCardPanel().stopEditing();
        
        // 已经核销预提情况，不可快速分摊
		BillModel billModel = getEditor().getBillCardPanel().getBillModel(BXConstans.AccruedVerify_PAGE);
		AccruedVerifyVO[] vos = null;
		if(billModel !=null){
			vos = (AccruedVerifyVO[]) billModel.getBodyValueVOs(AccruedVerifyVO.class.getName());
		}
		if(vos != null && vos.length > 0){
			throw new ValidationException("报销单已经核销预提，不可快速分摊");
		}
        
        BillData data = ((BillForm) getEditor()).getBillCardPanel().getBillData();
        data.dataNotNullValidate();
        headervo =  ((JKBXVO)getEditor().getValue()).getParentVO();
        setDjlx(headervo.getDjlxbm());
        setParentDjlx(BXConstans.BX_DJLXBM);
    }
    
    @Override
    protected boolean isActionEnable()
    {
        
        DjLXVO currentDjLXVO = ((ErmBillBillManageModel)getModel()).getCurrentDjLXVO();
		if (BXConstans.JK_DJDL.equals(currentDjLXVO.getDjdl()))
        {
            return false;
        }
		if(ErmDjlxCache.getInstance().isNeedBxtype(currentDjLXVO, ErmDjlxConst.BXTYPE_ADJUST)){
			// 调整单情况，不可快速分摊
			return false;
		}
        return true;
    }

    @Override
    protected void initDataByWhole() throws ValidationException {
        UFDouble shareAmount = (UFDouble) headervo.getAttributeValue(BXHeaderVO.YBJE);// 分摊金额为表头原币金额
        setShareAmount(shareAmount);
        setTotalAmount(shareAmount);
        String refPK = (String) headervo.getAttributeValue(BXHeaderVO.FYDWBM);// 分摊规则参照过滤pk为费用承担单位
        setRefPK(refPK);
        setSelectvo(new CShareDetailVO());
    }

    @Override
    protected void initDataByPart() throws BusinessException {
        super.initDataByPart();
        UFDouble totalAmount = (UFDouble) headervo.getAttributeValue(BXHeaderVO.YBJE);// 分摊金额为表头原币金额
        setTotalAmount(totalAmount);
    }
    
    @Override
    protected void beforeShareByWhole(SuperVO[] vos) {
        setHeadValue(BXHeaderVO.ISCOSTSHARE, UFBoolean.TRUE);
        ErmForCShareUiUtil.setCostPageShow(getEditor().getBillCardPanel(), true);
        getEditor().getBillCardPanel().getHeadItem(JKBXHeaderVO.FYDWBM_V).getComponent().setEnabled(false);// 设置表头费用承担单位不可编辑
    }
    
    @Override
    protected void validateCShareCondition() throws BusinessException {
        UFDouble totalAmount = (UFDouble) headervo.getAttributeValue(BXHeaderVO.YBJE);// 分摊金额为表头原币金额
//        UFDouble totalAmount = ((JKBXVO)getEditor().getValue()).getParentVO().getTotal();
        if (totalAmount == null || totalAmount.compareTo(UFDouble.ZERO_DBL) <= 0) {
            throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0152")/*@res "分摊金额必须大于0"*/);
        }
    }

}
