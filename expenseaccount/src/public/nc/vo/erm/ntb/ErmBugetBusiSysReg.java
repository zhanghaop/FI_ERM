package nc.vo.erm.ntb;

import java.util.ArrayList;

import nc.bs.framework.common.NCLocator;
import nc.bs.framework.server.util.NewObjectService;
import nc.itf.er.pub.IArapBillTypePublic;
import nc.itf.tb.control.IBusiSysExecDataProvider;
import nc.itf.tb.control.IBusiSysReg;
import nc.itf.tb.control.IDateType;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.tb.control.ControlBillType;
import nc.vo.tb.control.ControlObjectType;

/**
 * <p>
 * TODO 
 * 报销管理部分实现预算接口
 *  * 功能描述:需要通过预算系统进行预算控制,通过预算系统进行控制的报销业务系统必须
 			提供该接口的VO实现类,并且需要在预算的业务系统注册表(ntb_id_sysreg)中以如下的方式
 			进行注册:
 			业务系统名称	业务系统标识	注册接口实现类 		                             可控标志
 			----------------------------------------------------------------------------------------------------------------------------------------
 			NC财务报销	 NC-ERM		nc.vo.erm.ntb.ErmBugetBusiSysReg	通过在控制节点上的功能按钮来进行检查,主要是匹配是否所有的主对象类型都已经对应到指标;
 			可以通过在各个业务产品的安装盘中提供相应的SQL语句:
 			insert into ntb_id_sysreg(pk_obj,sys_name,sys_id,regclass)
 			("ERM0010001ERM0010001","NC财务报销","ERM","nc.vo.erm.ntb.ErmBugetBusiSysReg");
 * </p>
 *
 * 修改记录：<br>
 * <li>修改人：修改日期：修改内容：</li>
 * <br><br>
 *
 * @see 
 * @author liansg
 * @version V6.0
 * @since V6.0 创建时间：2011-3-2 下午02:47:17
 */
public class ErmBugetBusiSysReg implements IBusiSysReg ,IDateType{


	public ErmBugetBusiSysReg() {
		super();
	}
	
	public ArrayList<ControlBillType> getBillType() {
		ArrayList<ControlBillType> result =new ArrayList<ControlBillType>();
		DjLXVO[] djlxs = null;
		try {
			djlxs = NCLocator.getInstance().lookup(IArapBillTypePublic.class).queryAllBillTypeByCorp("0001");
		} catch (Exception e) {
			ExceptionHandler.consume(e);
			return null;
		}
		if (djlxs == null || djlxs.length < 1)
			return null;

		
		for (int i = 0; i < djlxs.length; i++) {
			if (djlxs[i].getFcbz() != null && djlxs[i].getFcbz().booleanValue())
				continue;
			ControlBillType e = new ControlBillType();
			e.setPk_billType(djlxs[i].getDjlxbm());
			e.setBillType_code(djlxs[i].getDjlxbm());
			e.setBillType_name(djlxs[i].getDjlxmc());
			result.add(e);
		}
		return result;
	}


	public String getBusiSysDesc() {
		
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000399")/*@res "NC报销管理"*/;
	}


	public String getBusiSysID() {
		
		return "NC_ERM";
	}


	public String[] getBusiType() {

		return new String[] { "发生额" }; /*-=notranslate=-*/
	}


	public String[] getBusiTypeDesc() {

		return new String[] { nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000400")/*@res "发生额"*/ };
	}


	public String[] getControlableDirections() {

		return new String[] { "收", "付" }; /*-=notranslate=-*/
	}


	public String[] getControlableDirectionsDesc() {

		return new String[] { nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000401")/*@res "收"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000402")/*@res "付"*/ };
	}


	public ArrayList<ControlObjectType> getControlableObjects() {

		ArrayList<ControlObjectType> reArrayList=new ArrayList<ControlObjectType>();
		ControlObjectType e=new ControlObjectType("FI_BILL_EXEC",nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2004","UPP2004-000074")/*@res "发生额"*/,nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2004","UPP2004-000074")/*@res "发生额"*/);
		reArrayList.add(e);
		return reArrayList;
	}

	
	public String[] getControlableObjectsDesc() {
		return new String[] { nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000400")/*@res "发生额"*/ };
	}

	public IBusiSysExecDataProvider getExecDataProvider() {
		return (IBusiSysExecDataProvider) NewObjectService.newInstance(BXConstans.ERM_PRODUCT_CODE_Lower,"nc.bs.er.control.ErmNtbProvider");
	}

	public boolean isBudgetControling() {
		return true;
	}

	public String[] getDataType() {
		return new String[] { "djrq", "shrq" };
	}

	public String[] getDataTypeDesc() {
		return new String[]{nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000248")/*@res "单据日期"*/,nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPTcommon-000037")/*@res "审核日期"*/};
	}

	public ArrayList<ControlObjectType> getControlableObjects(String billtype) {

		return null;
	}


	public String getMainPkOrg() {

		return null;
	}


	public boolean isSupportMutiSelect() {

		return false;
	}


	public boolean isUseAccountDate(String billtype) {

		return false;
	}


	public String[] getDateType(String billtype) {

		return null;
	}

}
