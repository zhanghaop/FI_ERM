package nc.ui.erm.sharerule.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.ui.erm.sharerule.view.ShareRuleBillForm;
import nc.ui.pub.bill.BillData;
import nc.ui.pub.bill.BillItem;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.SaveAction;
import nc.ui.uif2.editor.BillForm;
import nc.vo.bill.pub.MiscUtil;
import nc.vo.erm.sharerule.AggshareruleVO;
import nc.vo.erm.sharerule.ShareruleDataVO;
import nc.vo.erm.sharerule.ShareruleObjVO;
import nc.vo.erm.sharerule.ShareruleVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.VOStatus;

public class SaveShareruleAction extends SaveAction {

	private static final long serialVersionUID = 1L;

    @Override
	public void doAction(ActionEvent e) throws Exception {
        BillData data = ((BillForm) getEditor()).getBillCardPanel().getBillData();
        data.dataNotNullValidate();

        Object value = getEditor().getValue();
        
        
        Object validateValue = ((ShareRuleBillForm)getEditor()).getValidateValue();
        AggshareruleVO vo = (AggshareruleVO)validateValue;
        if (vo != null) {
            ShareruleVO rule = (ShareruleVO)vo.getParentVO();
            Object val = ((ShareRuleBillForm)getEditor()).getRefPane().getValueObj();
            if (val != null && !val.toString().equals(rule.getRuleobj_name())) {
                rule.setRuleobj_name(val.toString());
            }
        }
        validate(validateValue);

        if(getModel().getUiState()==UIState.ADD){
            doAddSave(value);
        }else if(getModel().getUiState()==UIState.EDIT){
            doEditSave(value);
        }

        showSuccessInfo();
    }

	@Override
	protected void doAddSave(Object value) throws Exception {
		AggshareruleVO aggvo = (AggshareruleVO) value;
		aggvo.setTableVO(aggvo.getTableCodes()[0], getNewSRuleObjVO());
		checkSave(aggvo);// 保存数据合理性校验
		super.doAddSave(aggvo);
	}

	@Override
	protected void doEditSave(Object value) throws Exception {
		AggshareruleVO aggvo = (AggshareruleVO) value;
		aggvo.setTableVO(aggvo.getTableCodes()[0], getEditSRuleObjVO());
		aggvo.setTableVO(aggvo.getTableCodes()[1], getEditSRuleDataVO());
		checkSave(aggvo); // 保存数据合理性校验
		super.doEditSave(aggvo);
	}

	private ShareruleDataVO[] getEditSRuleDataVO() {
		AggshareruleVO aggvo = (AggshareruleVO) getEditor().getValue();
		CircularlyAccessibleValueObject[] bodyvo = ((BillForm) getEditor()).getBillCardPanel().getBillModel()
				.getBodyValueVOs(ShareruleDataVO.class.getName());
		CircularlyAccessibleValueObject[] cs = aggvo.getTableVO(aggvo.getTableCodes()[1]);
		List<ShareruleDataVO> rs = new ArrayList<ShareruleDataVO>();
		if (cs != null && cs.length != 0) {
			// 重新包装vo，保存vo = 现表体vo+表体已改变vo
			CircularlyAccessibleValueObject[] changeBodyVos = ((BillForm) getEditor()).getBillCardPanel()
					.getBillModel().getBodyValueChangeVOs(ShareruleDataVO.class.getName());
			for (CircularlyAccessibleValueObject changeBodyVo : changeBodyVos) {
				if (changeBodyVo.getStatus() == VOStatus.DELETED) {
					rs.add((ShareruleDataVO) changeBodyVo);
				}
			}
		}
		for (CircularlyAccessibleValueObject vo : bodyvo) {
			rs.add((ShareruleDataVO) vo);
		}
		return rs.toArray(new ShareruleDataVO[rs.size()]);
	}

	private void checkSave(AggshareruleVO aggvo) throws Exception{
		CircularlyAccessibleValueObject[] bodyvo = ((BillForm) getEditor()).getBillCardPanel().getBillModel()
				.getBodyValueVOs(ShareruleDataVO.class.getName());
		if (bodyvo == null || bodyvo.length == 0){
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201100_0","0201100-0020")/*@res "表体数据不能为空"*/);
		}
	}

	private ShareruleObjVO[] getNewSRuleObjVO() {
		ShareRuleBillForm editor = (ShareRuleBillForm) getEditor();
		Map<String, String> map = editor.getRefPane().getDialog().getSelecteddatas();
		ShareruleObjVO[] srule_objs = null;
		int i = 0;
		if (map != null && map.size() != 0) {
			srule_objs = new ShareruleObjVO[map.size()];
			for (Map.Entry<String, String> entry : map.entrySet()) {
				srule_objs[i] = new ShareruleObjVO();
				srule_objs[i].setFieldcode(entry.getKey());
				srule_objs[i].setFieldname(entry.getValue());
				srule_objs[i].setStatus(VOStatus.NEW);
				i++;
			}
		}
		return srule_objs;
	}

	private ShareruleObjVO[] getEditSRuleObjVO() {
		AggshareruleVO aggvo = (AggshareruleVO) getModel().getSelectedData();
		ShareruleObjVO[] objs = (ShareruleObjVO[]) aggvo.getTableVO(aggvo.getTableCodes()[0]);
		BillItem ruleobjItem = ((BillForm) getEditor()).getBillCardPanel().getHeadItem(
				ShareruleVO.RULEOBJ_NAME);
		ShareruleObjVO[] rs;
		if (isSame(objs, ruleobjItem)) {// 规则对象未改变
			rs = objs;
		} else {// 规则对象改变,则将原规则对象数据状态改为deleted,再添加上新的规则对象一同返回
			ShareruleObjVO[] newobjs = getNewSRuleObjVO();
			for (ShareruleObjVO obj : objs) {
				obj.setStatus(VOStatus.DELETED);
			}
			rs = (ShareruleObjVO[]) MiscUtil.ArraysCat(objs, newobjs);
		}
		return rs;
	}

	private boolean isSame(ShareruleObjVO[] objs, BillItem ruleobjItem) {
		String ruleobjStr1 = ruleobjItem.getValueObject().toString();
		String ruleobjStr2 = "";
		StringBuffer objStr = new StringBuffer();
		for (ShareruleObjVO obj : objs) {
			objStr.append(obj.getFieldname() + ",");
		}
		if (objStr.length() > 0) {
			ruleobjStr2 = objStr.toString().substring(0, objStr.length() - 1);
		}
		return ruleobjStr2.equals(ruleobjStr1);
	}

}