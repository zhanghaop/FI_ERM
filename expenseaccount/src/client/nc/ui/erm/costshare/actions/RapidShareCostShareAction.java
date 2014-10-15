package nc.ui.erm.costshare.actions;

import java.math.BigDecimal;

import nc.ui.erm.action.RapidShareAbstractAction;
import nc.ui.pub.bill.BillCardPanel;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFDouble;

public class RapidShareCostShareAction extends RapidShareAbstractAction {

	private static final long serialVersionUID = 1L;

    @Override
    protected void beforeShareByWhole(SuperVO[] vos) {
    }

    @Override
    protected void validateCShareCondition() throws BusinessException {
        BillCardPanel billCardPanel = getEditor().getBillCardPanel();
        UFDouble totalAmount = (UFDouble) billCardPanel.getHeadItem(CostShareVO.YBJE).getValueObject();
        if (totalAmount == null || totalAmount.compareTo(UFDouble.ZERO_DBL) <= 0) {
            throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0152")/*@res "分摊金额必须大于0"*/);
        }
        setTotalAmount(totalAmount);
    }

    @Override
    protected void initDataBefore() throws ValidationException {
        BillCardPanel billCardPanel = getEditor().getBillCardPanel();
        String djlx = (String)billCardPanel.getHeadItem("djlxbm").getValueObject();
        setDjlx(djlx);
//        setParentDjlx(IErmCostShareConst.COSTSHARE_BILLTYPE);
        setParentDjlx(BXConstans.BX_DJLXBM);
    }

    @Override
    protected void initDataByWhole() throws ValidationException {
        BillCardPanel billCardPanel = getEditor().getBillCardPanel();
        int nDecimalDigit = billCardPanel.getHeadItem("ybje").getDecimalDigits();
        UFDouble shareAmount = (UFDouble) billCardPanel.getHeadItem("ybje").getValueObject();
        shareAmount = shareAmount.setScale(nDecimalDigit, BigDecimal.ROUND_HALF_UP);
        setShareAmount(shareAmount);
        setTotalAmount(shareAmount);
        String refPK = (String) billCardPanel.getHeadItem(CostShareVO.FYDWBM).getValueObject();
        setRefPK(refPK);
        setSelectvo(new CShareDetailVO());
    }

    @Override
    protected void initDataByPart() throws BusinessException {
        super.initDataByPart();
        BillCardPanel billCardPanel = getEditor().getBillCardPanel();
        UFDouble totalAmount = (UFDouble) billCardPanel.getHeadItem(CostShareVO.YBJE).getValueObject();
        setTotalAmount(totalAmount);
    }
    
}