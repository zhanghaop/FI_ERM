package nc.ui.erm.closeacc.validator;

import nc.bd.accperiod.AccperiodmonthAccessor;
import nc.bs.uif2.validation.ValidationFailure;
import nc.bs.uif2.validation.Validator;
import nc.ui.erm.closeacc.model.CloseAccManageModel;
import nc.vo.bd.period2.AccperiodmonthVO;
import nc.vo.org.CloseAccBookVO;

public class CloseAccValidator implements Validator {

    private static final long serialVersionUID = -721080755758111106L;

    @Override
    public ValidationFailure validate(Object obj) {
        if (obj == null || !(obj instanceof CloseAccManageModel))
            return null;
        ValidationFailure validateMessage = null;
        CloseAccManageModel model = (CloseAccManageModel)obj;
        CloseAccBookVO closeAccBookVO = (CloseAccBookVO)model.getSelectedData();
        if (closeAccBookVO == null || 
                nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0004")/*@res "无"*/.equals(model.getMinNotAcc()))
            validateMessage = new ValidationFailure(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0007")/*@res "不存在可结账期间！"*/);
        else {
            String pkAccperiod = closeAccBookVO.getPk_accperiodmonth();
            AccperiodmonthVO monthVO =
                AccperiodmonthAccessor.getInstance().queryAccperiodmonthVOByPk(pkAccperiod);
            if (!model.getMinNotAcc().equals(monthVO.getYearmth()))
                validateMessage = new ValidationFailure(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0008")/*@res "可结账期间是"*/ + model.getMinNotAcc() + nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0009")/*@res "！"*/);
        }
        return validateMessage;
    }

}