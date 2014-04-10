package nc.ui.erm.billpub.action;

import nc.ui.erm.action.RapidShareAbstractAction;
import nc.ui.erm.billpub.model.ErmBillBillManageModel;
import nc.ui.erm.costshare.common.ErmForCShareUiUtil;
import nc.ui.pub.bill.BillData;
import nc.ui.uif2.editor.BillForm;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.BXHeaderVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
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
        BillData data = ((BillForm) getEditor()).getBillCardPanel().getBillData();
        data.dataNotNullValidate();
        headervo =  ((JKBXVO)getEditor().getValue()).getParentVO();
        setDjlx(headervo.getDjlxbm());
        setParentDjlx(BXConstans.BX_DJLXBM);
    }
    
    @Override
    protected boolean isActionEnable()
    {
        
        if (BXConstans.JK_DJDL.equals(((ErmBillBillManageModel)getModel()).getCurrentDjLXVO().getDjdl()))
        {
            return false;
        }
        return true;
    }

    @Override
    protected void initDataByWhole() throws ValidationException {
        UFDouble shareAmount = (UFDouble) headervo.getAttributeValue(BXHeaderVO.YBJE);// ��̯���Ϊ��ͷԭ�ҽ��
        setShareAmount(shareAmount);
        setTotalAmount(shareAmount);
        String refPK = (String) headervo.getAttributeValue(BXHeaderVO.FYDWBM);// ��̯������չ���pkΪ���óе���λ
        setRefPK(refPK);
        setSelectvo(new CShareDetailVO());
    }

    @Override
    protected void initDataByPart() throws BusinessException {
        super.initDataByPart();
        UFDouble totalAmount = (UFDouble) headervo.getAttributeValue(BXHeaderVO.YBJE);// ��̯���Ϊ��ͷԭ�ҽ��
        setTotalAmount(totalAmount);
    }
    
    @Override
    protected void beforeShareByWhole(SuperVO[] vos) {
        setHeadValue(BXHeaderVO.ISCOSTSHARE, UFBoolean.TRUE);
        ErmForCShareUiUtil.setCostPageShow(getEditor().getBillCardPanel(), true);
        getEditor().getBillCardPanel().getHeadItem(JKBXHeaderVO.FYDWBM_V).getComponent().setEnabled(false);// ���ñ�ͷ���óе���λ���ɱ༭
    }
    
    @Override
    protected void validateCShareCondition() throws BusinessException {
        UFDouble totalAmount = (UFDouble) headervo.getAttributeValue(BXHeaderVO.YBJE);// ��̯���Ϊ��ͷԭ�ҽ��
//        UFDouble totalAmount = ((JKBXVO)getEditor().getValue()).getParentVO().getTotal();
        if (totalAmount == null || totalAmount.compareTo(UFDouble.ZERO_DBL) <= 0) {
            throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0152")/*@res "��̯���������0"*/);
        }
    }

}
