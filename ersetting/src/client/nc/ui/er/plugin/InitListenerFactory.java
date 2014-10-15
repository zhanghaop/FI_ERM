package nc.ui.er.plugin;

import nc.bs.logging.Log;


public class InitListenerFactory {

	public void initLiseners(IListenerController lc,String sNodeCode){
		IInfoInitializer initializer = getInitializer(sNodeCode);
		if(initializer!=null){
			initializer.initBtns(lc);
			initializer.initBillCardListeners(lc);
			initializer.initBillListListeners(lc);
			initializer.initTreeListeners(lc);
		}
		
	}
/* £¨·Ç Javadoc£©
 * @see nc.ui.fi.arap.plugin.IInitInfo#initTreeListeners(nc.ui.fi.arap.plugin.IListenerController)
 */
private IInfoInitializer getInitializer(String sNodecode){
	try {
		if(sNodecode.equals("20110005")){
			return (IInfoInitializer)Class.forName("nc.ui.er.djlx.DjlxListenerInitializer").newInstance();
		}else{
			return null;
		}
	} catch (InstantiationException e) {
		Log.getInstance(this.getClass()).error(e.getMessage(),e);
	} catch (IllegalAccessException e) {
		Log.getInstance(this.getClass()).error(e.getMessage(),e);
	} catch (ClassNotFoundException e) {
		Log.getInstance(this.getClass()).error(e.getMessage(),e);
	}
	return null;
}
}
