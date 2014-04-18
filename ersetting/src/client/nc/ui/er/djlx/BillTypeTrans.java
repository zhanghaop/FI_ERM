package nc.ui.er.djlx;


/**
 * 将后台查询回来的数据补充信息以及多语言翻译。
 * @author st
 *
 */
public class BillTypeTrans {

//	private UIRefPane refSyscode =null;
//	private UIRefPane refTemplet = null;
//	private Hashtable SyscodeCache = null;
//	private Hashtable templetCache = null;
	
//private void fillDisplayinfo(BillTypeVO[] billtypes){
//	if(billtypes==null){
//		return ;
//	}
//	
//	int iLeg = billtypes.length;
//	BillTypeVO tmpvo = null;
////	DjLXVO djlxvo = null;
//	DjlxtempletVO[] templetvos = null;
//	
//	
//	
//	
//	for(int i=0;i<iLeg;i++){
//		tmpvo = billtypes[i];
////		djlxvo =(DjLXVO) tmpvo.getParentVO();
//		templetvos =(DjlxtempletVO[]) tmpvo.getChildrenVO();
//		fillTempletsinfo(templetvos);
//	}
//}

//private void fillTempletsinfo(DjlxtempletVO[] templetvos){
//	if(templetvos==null){
//		return;
//	}
//	int m=templetvos.length;
//	for(int j=0;j<m;j++){
//		if(templetvos[j]==null){
//			continue;
//		}
//		if(templetvos[j].getSyscode()!=null){
//			templetvos[j].setSyscodename(getSysName(templetvos[j].getSyscode().toString()));
//		}
//		if(templetvos[j].getPk_billtemplet()!=null){
//			templetvos[j].setBilltempletname(getTempletName(templetvos[j].getPk_billtemplet()));
//		}
//	}
//}
//private UIRefPane getRefSyscode() {
//	if(refSyscode==null){
//		refSyscode = new UIRefPane();
//		SyscodeRefModel sysrefmodel = new SyscodeRefModel();
//		refSyscode.setRefModel(sysrefmodel);
//	}
//	return refSyscode;
//}
//
//private UIRefPane getRefTemplet() {
//	if(refTemplet==null){
//		refTemplet = new UIRefPane();
//		BillTempletRefModel templetrefmodel = new BillTempletRefModel();
//		refTemplet.setRefModel(templetrefmodel);
//	}
//	return refTemplet;
//}

//private String getSysName(String pk){
//	if(getSyscodeCache().get(pk)==null){
//		getRefSyscode().setPK(pk);		
//		String s = getRefSyscode().getRefName();
//		if(s==null){
//			s="";
//		}
//		getSyscodeCache().put(pk,s);
//	}
//	
//	return (String)getSyscodeCache().get(pk);
//}
//private String getTempletName(String pk){
//	if(getTempletCache().get(pk)==null){
//		getRefTemplet().setPK(pk);		
//		String s = getRefTemplet().getRefName();
//		if(s==null){
//			s="";
//		}
//		getTempletCache().put(pk,s);
//	}
//	
//	return (String)getTempletCache().get(pk);
//}
//private Hashtable getSyscodeCache() {
//	if(SyscodeCache==null){
//		SyscodeCache = new Hashtable();
//	}
//	return SyscodeCache;
//}

//private Hashtable getTempletCache() {
//	if(templetCache==null){
//		templetCache = new Hashtable();
//	}
//	return templetCache;
//}
}
