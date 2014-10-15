package nc.ui.erm.closeacc.validator;

import nc.bd.accperiod.AccperiodmonthAccessor;
import nc.bs.uif2.validation.ValidationFailure;
import nc.bs.uif2.validation.Validator;
import nc.ui.erm.closeacc.model.CloseAccManageModel;
import nc.vo.bd.period2.AccperiodmonthVO;
import nc.vo.org.CloseAccBookVO;

public class UnCloseAccValidator implements Validator {

    private static final long serialVersionUID = -6790193246860762332L;

    @Override
    public ValidationFailure validate(Object obj) {
        if (obj ==null || !(obj instanceof CloseAccManageModel))
            return null;
        CloseAccManageModel model = (CloseAccManageModel)obj;
        CloseAccBookVO closeAccBookVO = (CloseAccBookVO)model.getSelectedData();
        AccperiodmonthVO monthVO = null;
        if (closeAccBookVO != null) {
            String pkAccperiod = closeAccBookVO.getPk_accperiodmonth();
            monthVO =
                AccperiodmonthAccessor.getInstance().queryAccperiodmonthVOByPk(pkAccperiod);
        }
        ValidationFailure validateMessage = null;
        if (closeAccBookVO == null || 
                nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0004")/*@res "无"*/.equals(model.getMaxAcc())) {
            validateMessage = new ValidationFailure();
            validateMessage.setMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0010")/*@res "没有可以取消结账的期间！"*/);
        }
        else if (!model.getMaxAcc().equals(monthVO.getYearmth())) {
            validateMessage = new ValidationFailure();
            validateMessage.setMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0011")/*@res "可取消结账期间是"*/ + model.getMaxAcc() + nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0009")/*@res "！"*/);
        }
        return validateMessage;
    }

}