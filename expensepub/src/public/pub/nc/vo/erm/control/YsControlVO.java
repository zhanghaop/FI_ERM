package nc.vo.erm.control;

import nc.bs.erm.annotation.ErmBusinessDef;
import nc.bs.erm.cache.ErmBillFieldContrastCache;
import nc.bs.logging.Log;
import nc.itf.tb.control.IAccessableBusiVO;
import nc.itf.tb.control.IBillDataGeter;
import nc.itf.tb.control.IFormulaFuncName;
import nc.itf.uap.busibean.SysinitAccessor;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXParamConstant;
import nc.vo.er.pub.IFYControl;
import nc.vo.fibill.outer.FiBillAccessableBusiVO;
import nc.vo.fipub.annotation.Business;
import nc.vo.fipub.annotation.BusinessType;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;

@Business(business = ErmBusinessDef.TBB_CTROL, subBusiness = "", description = "预算控制VO"/*-=notranslate=-*/, type = BusinessType.NORMAL)
public class YsControlVO extends FiBillAccessableBusiVO {

	private IFYControl[] items;

	private static final long serialVersionUID = -1500898076243960481L;

	private IAccessableBusiVO iAcc_tb = null;

	private IBillDataGeter iBillDataGeter = null;

	public boolean iscontrary = false;

	private String methodCode = null;

	private boolean isAdd = false;

	public String getMethodCode() {
		return methodCode;
	}

	public void setMethodCode(String methodCode) {
		this.methodCode = methodCode;
	}

	public boolean isAdd() {
		return isAdd;
	}

	public void setAdd(boolean isAdd) {
		this.isAdd = isAdd;
	}

	public String getAttributesValue(String attr) {
		if (getIAcc_tb() != null) {
			return getIAcc_tb().getAttributesValue(attr);
		}

		if (attr.trim().equalsIgnoreCase("zb.pk_org") || attr.trim().equalsIgnoreCase("pk_org")
				|| attr.trim().equalsIgnoreCase("pkorg"))
			return getPKOrg();
		
		String newAttr = ErmBillFieldContrastCache.getSrcField(
				ErmBillFieldContrastCache.FieldContrast_SCENE_BudGetField, getBillType(), attr);
		
		String parentBilltype = getParentBillType();
		if (newAttr == null) {
			// 对照字段不存在,该中情况在于在预算对照表中未查找到对应字段
			if (attr.startsWith(BXConstans.BUDGET_DEFITEM_BODY_PREFIX)) {
				if(BXConstans.BX_DJLXBM.equals(parentBilltype) || BXConstans.JK_DJLXBM.equals(parentBilltype)){
					newAttr = BXConstans.BODY_USERDEF_PREFIX
					+ attr.substring(BXConstans.BUDGET_DEFITEM_BODY_PREFIX.length());
				}else{
					newAttr = "fb." + BXConstans.BODY_USERDEF_PREFIX
					+ attr.substring(BXConstans.BUDGET_DEFITEM_BODY_PREFIX.length());
				}
			} else if (attr.startsWith(BXConstans.BUDGET_DEFITEM_HEAD_PREFIX)) {
				if (BXConstans.BX_DJLXBM.equals(parentBilltype) || BXConstans.JK_DJLXBM.equals(parentBilltype)) {
					newAttr = BXConstans.HEAD_USERDEF_PREFIX
							+ attr.substring(BXConstans.BUDGET_DEFITEM_HEAD_PREFIX.length());
				} else {
					newAttr = BXConstans.BODY_USERDEF_PREFIX
							+ attr.substring(BXConstans.BUDGET_DEFITEM_HEAD_PREFIX.length());
				}
			} else {
				newAttr = attr;
			}
		}
		Object value = getItem().getItemValue(newAttr);

		return value == null ? null : value.toString();
	}

	public boolean isJKBXBillltype() {
		return BXConstans.BX_DJDL.equals(this.getDjdl()) || BXConstans.JK_DJDL.equals(this.getDjdl());
	}

	public String getBillType() {
		if (getIAcc_tb() != null) {
			return getIAcc_tb().getBillType();
		}
		return getItem().getDjlxbm();
	}

	public String getBusiDate() {
		if (getIAcc_tb() != null) {
			return getIAcc_tb().getBusiDate();
		}
		return getItem().getDjrq().toString();
	}

	public String getCurrency() {
		if (getIAcc_tb() != null) {
			return getIAcc_tb().getCurrency();
		}
		return getItem().getBzbm();
	}

	public UFDouble[] getYwje() {
		if (getMethodCode() != null && getMethodCode().equals(IFormulaFuncName.PREFIND)) {
			return getItem().getPreItemJe();
		} else {
			return getItem().getItemJe();
		}
	}

	public boolean isUnInure() {
		if (getIAcc_tb() != null) {
			return getIAcc_tb().isUnInure();
		}
		return getItem().isSaveControl();
	}

	public String getDdlx() {
		if (getIBillDataGeter() != null) {
			return getIBillDataGeter().getDdlx();
		}
		return getItem().getDdlx();
	}

	public String getDjdl() {
		if (getIBillDataGeter() != null) {
			return getIBillDataGeter().getDjdl();
		}
		return getItem().getDjdl();
	}

	public Integer getItemFx() {
		return getFx();
	}

	public String getItem_bill_pk() {
		if (getIBillDataGeter() != null) {
			return getIBillDataGeter().getItem_bill_pk();
		}
		return getItem().getPk_item();
	}

	private Integer getFx() {
		if (getIBillDataGeter() != null) {
			return getIBillDataGeter().getItemFx();
		}
		return getItem().getFx();
	}

	public String getPkNcEntity() {
		return null;
	}

	public String[] getAttributesValue(String[] arg0) {
		return null;
	}

	private IFYControl getItem() {
		if (items == null || items.length == 0)
			return null;
		else
			return items[0];
	}

	public IBillDataGeter getIBillDataGeter() {
		return iBillDataGeter;
	}

	public void setIBillDataGeter(IBillDataGeter billDataGeter) {
		iBillDataGeter = billDataGeter;
	}

	@Override
	public IAccessableBusiVO getIAcc_tb() {
		return iAcc_tb;
	}

	@Override
	public void setIAcc_tb(IAccessableBusiVO acc_tb) {
		iAcc_tb = acc_tb;
	}

	public IFYControl[] getItems() {
		return items;
	}

	public void setItems(IFYControl[] items) {
		this.items = items;
	}

	public boolean isIscontrary() {
		return iscontrary;
	}

	public void setIscontrary(boolean iscontrary) {
		this.iscontrary = iscontrary;
	}

	@Override
	public String getPkOrgInBillHead() {
		return getItem().getFydwbm();
	}

	@Override
	public String getPKGroup() {
		return getItem().getPk_group();
	}

	public String getDateType() {
		return "djrq";
	}

	@Override
	public String getPKOrg() {
		if (BXConstans.BX_DJDL.equals(getDjdl()) || BXConstans.JK_DJDL.equals(getDjdl())) {
			String paraString = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0", "02011000-0031")/*
																													 * @
																													 * res
																													 * "借款报销单位"
																													 */;/*
																														 * -=
																														 * notranslate
																														 * =
																														 * -
																														 */
			try {
				paraString = SysinitAccessor.getInstance().getParaString(getPKGroup(),
						BXParamConstant.PARAM_ER_BUDGET_ORG);
			} catch (BusinessException e) {
				Log.getInstance(this.getClass()).error(e);
			}

			if (BXParamConstant.ER_BUDGET_ORG_PK_ORG.equals(paraString)) {// 借款报销单位
				return getItem().getPk_org();
			} else if (BXParamConstant.ER_BUDGET_ORG_OPERATOR_ORG.equals(paraString)) {// 借款报销人单位
				return getItem().getDwbm();
			} else if (BXParamConstant.ER_BUDGET_ORG_ASSUME_ORG.equals(paraString)) {// 费用承担单位
				return getItem().getFydwbm();
			} else if (BXParamConstant.ER_BUDGET_ORG_PK_PAYORG.equals(paraString)) {// 支付单位
				return getItem().getPk_payorg();
			}
		}
		return getItem().getPk_org();
	}

	@Override
	public String getPKOrg(String att_fld) {
		return getAttributesValue(att_fld);
	}

	public String getDataType() {
		return methodCode;
	}

	public UFDouble[] getExeData(String direction, String obj, String extObj) {
		int mod = 1;
		if (!isAdd()) {
			// 减少
			mod = mod * (-1);
		}
		UFDouble[] value = new UFDouble[4];
		IFYControl item = getItem();
		if (getIAcc_tb() == null) {
			if (getMethodCode() != null && getMethodCode().equals(IFormulaFuncName.PREFIND)) {
				value[0] = item.getPreItemJe()[0];
				value[1] = item.getPreItemJe()[1];
				value[2] = item.getPreItemJe()[2];
				value[3] = item.getPreItemJe()[3];
			} else {
				value[0] = item.getItemJe()[0];
				value[1] = item.getItemJe()[1];
				value[2] = item.getItemJe()[2];
				value[3] = item.getItemJe()[3];
			}
		} else if (((YsControlVO) getIAcc_tb()).getYwje() != null) {
			value = ((YsControlVO) getIAcc_tb()).getYwje();
		}

		value[0] = value[0] == null ? UFDouble.ZERO_DBL : value[0].multiply(new UFDouble(mod));
		value[1] = value[1] == null ? UFDouble.ZERO_DBL : value[1].multiply(new UFDouble(mod));
		value[2] = value[2] == null ? UFDouble.ZERO_DBL : value[2].multiply(new UFDouble(mod));
		value[3] = value[3] == null ? UFDouble.ZERO_DBL : value[3].multiply(new UFDouble(mod));
		return value;
	}

	/**
	 * 获得控制的单据所属单据类型
	 * 
	 * @return
	 */
	public String getParentBillType() {
		return getItem().getParentBillType();
	}

}