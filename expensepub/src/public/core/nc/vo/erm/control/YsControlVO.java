package nc.vo.erm.control;

import java.util.ArrayList;
import java.util.Hashtable;

import nc.bs.logging.Log;
import nc.itf.fi.pub.SysInit;
import nc.itf.tb.control.IAccessableBusiVO;
import nc.itf.tb.control.IBillDataGeter;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.er.pub.IFYControl;
import nc.vo.er.util.StringUtils;
import nc.vo.fibill.outer.FiBillAccessableBusiVO;
import nc.vo.fibill.outer.FiBillAccessableBusiVOProxy;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;

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

		if (attr.indexOf(".") != -1) {
			attr = attr.substring(3);
		}

		Object value = getItem().getItemValue(attr);

		return value == null ? null : value.toString();
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

		return getItem().getItemJe();
	}

	public boolean isUnInure() {
		if (getIAcc_tb() != null) {
			return getIAcc_tb().isUnInure();
		}
		return getItem().isSaveControl();
	}

	public boolean[][] isBillControl(String[] sDjlx, IAccessableBusiVO[][] vos)
			throws Exception {
		if (vos == null || vos.length <= 0) {
			return null;
		}
		boolean[][] bControls = new boolean[vos.length][];
		Hashtable<String, String> hash = getControldjlx(vos);
		FiBillAccessableBusiVOProxy vo = null;
		for (int i = 0; i < vos.length; i++) {
			bControls[i] = new boolean[vos[i].length];
			for (int j = 0; j < vos[i].length; j++) {
				vo = (FiBillAccessableBusiVOProxy) vos[i][j];
				if (!StringUtils.isNullWithTrim(vo.getItem_bill_pk())
						&& hash.get(vo.getItem_bill_pk().trim()) != null
						&& sDjlx[i].indexOf(hash.get(
								vo.getItem_bill_pk().trim()).toString()) != -1) {
					bControls[i][j] = false;
				} else {
					bControls[i][j] = true;
				}
			}
		}

		return bControls;
	}

	private Hashtable<String, String> getControldjlx(IAccessableBusiVO[][] djs) {
		Hashtable<String, String> hash = new Hashtable<String, String>();
		ArrayList<String> alBill_Item_pk = new ArrayList<String>();
		ArrayList<String> alDdlx = new ArrayList<String>();
		IBillDataGeter vo = null;
		for (int i = 0; i < djs.length; i++) {
			for (int j = 0; j < djs[i].length; j++) {
				vo = (IBillDataGeter) djs[i][j];
				if (vo.getItem_bill_pk() != null
						&& vo.getItem_bill_pk().trim().length() > 0) {
					alBill_Item_pk.add(vo.getItem_bill_pk().trim());
				}
			}
		}
		if (alBill_Item_pk.size() == 0 && alDdlx.size() == 0) {
			return hash;
		}
		return hash;
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

	public String getPKOrg() {
		String paraString="借款报销单位";/*-=notranslate=-*/
		try {
			paraString = SysInit.getParaString(getPKGroup(), "ERY");
		} catch (BusinessException e) {
			Log.getInstance(this.getClass()).error(e);
		}
		if("借款报销单位" .equals(paraString)){/*-=notranslate=-*/
			return getItem().getPk_org();
		}else if("借款报销人单位".equals(paraString)){/*-=notranslate=-*/
			return getItem().getDwbm();
		}else if("费用承担单位".equals(paraString)){/*-=notranslate=-*/
			return getItem().getFydwbm();
		}
		return getItem().getPk_org();
	}

	public String getDataType() {
		return methodCode;
	}
	
	public UFDouble[] getExeData(String direction, String obj, String extObj) {
		int mod = 1;
		if (!isAdd()) {
			//减少
			mod = mod * (-1);
		}
		UFDouble[] value = new UFDouble[4];
		UFDouble ybje = BXConstans.DOUBLE_ZERO, bbje = BXConstans.DOUBLE_ZERO;
		UFDouble glbbbje = BXConstans.DOUBLE_ZERO, grpbbje = BXConstans.DOUBLE_ZERO;
		IFYControl item = getItem();
		if (getIAcc_tb() == null) {
			glbbbje = item.getItemJe()[0];
			grpbbje = item.getItemJe()[1];
			bbje = item.getItemJe()[2];
			ybje = item.getItemJe()[3];

		} else if (((YsControlVO) getIAcc_tb()).getYwje() != null) {
			glbbbje = ((YsControlVO) getIAcc_tb()).getYwje()[0];
			grpbbje = ((YsControlVO) getIAcc_tb()).getYwje()[1];
			bbje = ((YsControlVO) getIAcc_tb()).getYwje()[2];
			ybje = ((YsControlVO) getIAcc_tb()).getYwje()[3];
		}

		value[0] = glbbbje.multiply(new UFDouble(mod));
		value[1] = grpbbje.multiply(new UFDouble(mod));
		value[2] = bbje.multiply(new UFDouble(mod));
		value[3] = ybje.multiply(new UFDouble(mod));
		return value;
	}

}
