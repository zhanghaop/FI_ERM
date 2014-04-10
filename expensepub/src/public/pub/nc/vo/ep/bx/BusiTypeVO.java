package nc.vo.ep.bx;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.vo.arap.engine.IConfigVO;

/**
 * @author twei
 * 
 *         <?xml version="1.0" encoding="GBK"?> <billtypes> <billtype
 *         code="****" templetflag="Y"> <interfaces> <interface
 *         name="IBodyPrint">
 *         <implementation>nc.ui.ep.bx.BodyPrint</implementation> </interface>
 *         <interface name="IBodyUIController">
 *         <implementation>nc.ui.ep.bx.BodyController</implementation>
 *         </interface> <interface name="IBXBusItemBO">
 *         <implementation>nc.bs.ep.bx.BXBusItemBO</implementation> </interface>
 *         </interfaces> <costentity_billitems> <billitem itemkey=""/> <billitem
 *         itemkey=""/> <billitem itemkey=""/> </costentity_billitems>
 *         <payentity_billitems> <billitem itemkey=""/> <billitem itemkey=""/>
 *         <billitem itemkey=""/> </payentity_billitems> <power_items> <billitem
 *         itemkey=""/> <billitem itemkey=""/> <billitem itemkey=""/>
 *         </power_items> <tablecodes> <tablecode code="" canAddRow="N"/>
 *         <tablecode code="" canAddRow="Y"/> </tablecodes> </billtype>
 *         </billtypes>
 * 
 */
public class BusiTypeVO implements Serializable, IConfigVO ,Cloneable {

	private static final long serialVersionUID = -7908537775853735639L;

	public static String key = "busitype";
	public static String costshare_key = "costshare";
	public static String mattapp_key = "mattapp";

	public static String IBodyPrint = "IBodyPrint";
	public static String IBodyUIController = "IBodyUIController";
	public static String IBXBusItemBO = "IBXBusItemBO";

	public BusiTypeVO(boolean init) {

		id = "2430";

		interfaces = new HashMap<String, String>();
		interfaces.put(IBodyPrint, "nc.ui.ep.bx.BodyPrint");
		interfaces.put(IBodyUIController, "nc.ui.arap.bx.BodyController");
		interfaces.put(IBXBusItemBO, "nc.bs.ep.bx.BXBusItemBO");

		costentity_billitems = new ArrayList<String>();
		costentity_billitems.add("");
		costentity_billitems.add("");

		payentity_billitems = new ArrayList<String>();
		payentity_billitems.add("");
		payentity_billitems.add("");

		payorgentity_billitems = new ArrayList<String>();
		payorgentity_billitems.add("");
		payorgentity_billitems.add("");
		
		useentity_billitems = new ArrayList<String>();
		payentity_billitems.add("");
		payentity_billitems.add("");

		power_items = new ArrayList<String>();
		power_items.add("");
		power_items.add("");

		isTableAddRow = new HashMap<String, Boolean>();
		isTableAddRow.put("", true);
		isTableAddRow.put("", false);

		isTableTemplet = new HashMap<String, Boolean>();
		isTableTemplet.put("", true);
		isTableTemplet.put("", false);

	}

	public BusiTypeVO() {
	}

	private String rule; // 报销制度
	private String limit; // 报销标准
	private String id; // 主键，单据类型编码

	private Map<String, String> interfaces;

	/**
	 * 费用承担单位单据项目key集合
	 */
	private List<String> costentity_billitems;

	/**
	 * 借款报销单位单据项目key集合
	 */
	private List<String> payentity_billitems;
	/**
	 * 支付单位单据项目key集合
	 */
	private List<String> payorgentity_billitems;

	/**
	 * 报销人单位单据项目key集合
	 */
	private List<String> useentity_billitems;

	private List<String> power_items;

	private Map<String, Boolean> isTableAddRow;

	private Map<String, Boolean> isTableTemplet;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getCostentity_billitems() {
		return costentity_billitems;
	}

	public void setCostentity_billitems(List<String> costentity_billitems) {
		this.costentity_billitems = costentity_billitems;
	}

	public Map<String, String> getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(Map<String, String> interfaces) {
		this.interfaces = interfaces;
	}

	public List<String> getPower_items() {
		return power_items;
	}

	public void setPower_items(List<String> power_items) {
		this.power_items = power_items;
	}

	public Map<String, Boolean> getIsTableAddRow() {
		return isTableAddRow;
	}

	public void setIsTableAddRow(Map<String, Boolean> isTableAddRow) {
		this.isTableAddRow = isTableAddRow;
	}

	public Map<String, Boolean> getIsTableTemplet() {
		return isTableTemplet;
	}

	public void setIsTableTemplet(Map<String, Boolean> isTableTemplet) {
		this.isTableTemplet = isTableTemplet;
	}

	@Override
	public Object clone() {
		BusiTypeVO ret;
		try {
			ret = (BusiTypeVO) super.clone();
			ret.setId(this.id);
			ret.setLimit(this.limit);
			ret.setRule(this.rule);
			ret.setCostentity_billitems(this.costentity_billitems);
			ret.setPayentity_billitems(this.payentity_billitems);
			ret.setPayorgentity_billitems(this.payorgentity_billitems);
			ret.setUseentity_billitems(this.useentity_billitems);
			ret.setPower_items(this.power_items);
			ret.setInterfaces(this.interfaces);
			ret.setIsTableAddRow(this.isTableAddRow);
			ret.setIsTableTemplet(this.isTableTemplet);
		} catch (CloneNotSupportedException e) {
			  throw new RuntimeException("clone not supported!");
		}
		return ret;
	}

	public String getLimit() {
		return limit;
	}

	public void setLimit(String limit) {
		this.limit = limit;
	}

	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	public List<String> getPayentity_billitems() {
		return payentity_billitems;
	}

	public void setPayentity_billitems(List<String> payentity_billitems) {
		this.payentity_billitems = payentity_billitems;
	}

	public List<String> getPayorgentity_billitems() {
		return payorgentity_billitems;
	}

	public void setPayorgentity_billitems(List<String> payorgentityBillitems) {
		payorgentity_billitems = payorgentityBillitems;
	}

	public List<String> getUseentity_billitems() {
		return useentity_billitems;
	}

	public void setUseentity_billitems(List<String> useentity_billitems) {
		this.useentity_billitems = useentity_billitems;
	}
}
