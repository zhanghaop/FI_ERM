package nc.ui.arap.bx;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.bill.BillCardPanel;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.fipub.valueobject.ArapDynamicAggregatedValueObject;
import nc.vo.fipub.valueobject.ArapDynamicValueObject;
import nc.vo.pub.BusinessException;

/**
 * @author twei
 *
 * nc.ui.arap.bx.ContrastModePanel
 *
 * 批量冲借款匹配模式设定
 */
public class ContrastModePanel extends UIPanel {

	private static final long serialVersionUID = 1L;
	private BillCardPanel templet=null;


	private void init(){
		this.setLayout(new BorderLayout());
		this.add(this.gettemplet(),BorderLayout.CENTER);
	}

//	private DefVO[] getDefs_head() {
//	    try{
//	    	return ((IDef) NCLocator.getInstance().lookup(IDef.class.getName())).queryDefVO("erhead", ClientEnvironment.getInstance().getCorporation().getPrimaryKey());
//	    }catch(Exception e){
//	    	ExceptionHandler.consume(e);
//	        return new DefVO[0];
//	    }
//	}
//
//	private DefVO[] getDefs_item() {
//	    try{
//	        return ((IDef) NCLocator.getInstance().lookup(IDef.class.getName())).queryDefVO("erbody", ClientEnvironment.getInstance().getCorporation().getPrimaryKey());
//	    }catch(Exception e){
//	    	ExceptionHandler.consume(e);
//	        return new DefVO[0];
//	    }
//	}

	private BillCardPanel gettemplet()
	{
		if(this.templet==null)
		{
			this.templet=new BillCardPanel();
			
			
			this.templet.loadTemplet("arapZ3jkbx000000000a");
//			if(getDefs_head()!=null)
				 //FIXME
//				  this.templet.getBillData().updateItemByDef(getDefs_head(), "headzyx", true);
			
			 


//			    if (getDefs_item()!= null)
			    	//FIXME
//		    	this.templet.getBillData().updateItemByDef(getDefs_item(), "bodyzyx", false);
		}
		return this.templet;
	}
	private ArrayList<String> m_mode=new ArrayList<String>();

	public ContrastModePanel() {
		super();
		init();
	}

	/**
	 * @return 返回 m_mode。
	 */
	public List<String> getM_mode() {

		initModeValue();

		List<String> sm_mode = new ArrayList<String>();

		sm_mode.addAll(m_mode);
		sm_mode.add(JKBXHeaderVO.ISCHECK);
		sm_mode.add(JKBXHeaderVO.BZBM);

		return sm_mode;
	}

	private void initModeValue() {

		m_mode = new ArrayList<String>();
		ArapDynamicAggregatedValueObject vo = (ArapDynamicAggregatedValueObject)gettemplet().getBillValueVO(ArapDynamicAggregatedValueObject.class.getName(), ArapDynamicValueObject.class.getName(), ArapDynamicValueObject.class.getName());
		ArapDynamicValueObject head = null;
		if(vo!=null){
			head = (ArapDynamicValueObject)vo.getParentVO();
		}
		if(head!=null){
			String[] attr = head.getAttributeNames();
			int iLen = attr==null?0:attr.length;
			Object oValue = null;
			for(int i=0;i<iLen;i++){
				oValue=head.getAttributeValue(attr[i]);
				if(oValue!=null && (oValue.toString().trim().equalsIgnoreCase("true")|| oValue.toString().trim().equalsIgnoreCase("y"))){
					m_mode.add(attr[i]);
				}
			}
		}
	}

	public String check() throws Exception {

		initModeValue();

		if(m_mode.size()==0)
			throw new BusinessException( nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000183")/*@res "请至少选择一个冲借款匹配选项!"*/ );

		return null;
	}

}