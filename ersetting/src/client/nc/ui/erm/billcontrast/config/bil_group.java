package nc.ui.erm.billcontrast.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.ui.uif2.factory.AbstractJavaBeanDefinition;

public class bil_group extends AbstractJavaBeanDefinition{
	private Map<String, Object> context = new HashMap();
public nc.vo.uif2.LoginContext getContext(){
 if(context.get("context")!=null)
 return (nc.vo.uif2.LoginContext)context.get("context");
  nc.vo.uif2.LoginContext bean = new nc.vo.uif2.LoginContext();
  context.put("context",bean);
  bean.setNodeType(nc.vo.bd.pub.NODE_TYPE.ORG_NODE);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.erm.billcontrast.model.BillcontrastModelService getService(){
 if(context.get("service")!=null)
 return (nc.ui.erm.billcontrast.model.BillcontrastModelService)context.get("service");
  nc.ui.erm.billcontrast.model.BillcontrastModelService bean = new nc.ui.erm.billcontrast.model.BillcontrastModelService();
  context.put("service",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.erm.billcontrast.model.BillcontrastModelDataManager getModelDataManager(){
 if(context.get("modelDataManager")!=null)
 return (nc.ui.erm.billcontrast.model.BillcontrastModelDataManager)context.get("modelDataManager");
  nc.ui.erm.billcontrast.model.BillcontrastModelDataManager bean = new nc.ui.erm.billcontrast.model.BillcontrastModelDataManager();
  context.put("modelDataManager",bean);
  bean.setEditor(getBilTable());
  bean.setModel(getBilModel());
  bean.setExceptionHandler(getExeceptionHandler());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.FunNodeClosingHandler getClosingListener(){
 if(context.get("ClosingListener")!=null)
 return (nc.ui.uif2.FunNodeClosingHandler)context.get("ClosingListener");
  nc.ui.uif2.FunNodeClosingHandler bean = new nc.ui.uif2.FunNodeClosingHandler();
  context.put("ClosingListener",bean);
  bean.setModel(getBilModel());
  bean.setSaveaction(getBilSaveAction());
  bean.setCancelaction(getBilCancelAction());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.erm.billcontrast.view.BillContrastBatchBillTable getBilTable(){
 if(context.get("bilTable")!=null)
 return (nc.ui.erm.billcontrast.view.BillContrastBatchBillTable)context.get("bilTable");
  nc.ui.erm.billcontrast.view.BillContrastBatchBillTable bean = new nc.ui.erm.billcontrast.view.BillContrastBatchBillTable();
  context.put("bilTable",bean);
  bean.setModel(getBilModel());
  bean.setClosingListener(getClosingListener());
  bean.setNodekey("20110ABC");
  bean.setAddLineAction(getBilAddAction());
  bean.setDelLineAction(getBilDeleteAction());
  bean.setBodyMultiSelectEnable(true);
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.model.BatchBillTableModel getBilModel(){
 if(context.get("bilModel")!=null)
 return (nc.ui.uif2.model.BatchBillTableModel)context.get("bilModel");
  nc.ui.uif2.model.BatchBillTableModel bean = new nc.ui.uif2.model.BatchBillTableModel();
  context.put("bilModel",bean);
  bean.setContext(getContext());
  bean.setService(getService());
  bean.setBusinessObjectAdapterFactory(getBDObjectAdpaterFactory_1ef9f1d());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.vo.bd.meta.BDObjectAdpaterFactory getBDObjectAdpaterFactory_1ef9f1d(){
 if(context.get("nc.vo.bd.meta.BDObjectAdpaterFactory#1ef9f1d")!=null)
 return (nc.vo.bd.meta.BDObjectAdpaterFactory)context.get("nc.vo.bd.meta.BDObjectAdpaterFactory#1ef9f1d");
  nc.vo.bd.meta.BDObjectAdpaterFactory bean = new nc.vo.bd.meta.BDObjectAdpaterFactory();
  context.put("nc.vo.bd.meta.BDObjectAdpaterFactory#1ef9f1d",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.DefaultExceptionHanler getExeceptionHandler(){
 if(context.get("execeptionHandler")!=null)
 return (nc.ui.uif2.DefaultExceptionHanler)context.get("execeptionHandler");
  nc.ui.uif2.DefaultExceptionHanler bean = new nc.ui.uif2.DefaultExceptionHanler();
  context.put("execeptionHandler",bean);
  bean.setContext(getContext());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.funcnode.ui.action.SeparatorAction getBilNullAction(){
 if(context.get("bilNullAction")!=null)
 return (nc.funcnode.ui.action.SeparatorAction)context.get("bilNullAction");
  nc.funcnode.ui.action.SeparatorAction bean = new nc.funcnode.ui.action.SeparatorAction();
  context.put("bilNullAction",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.erm.billcontrast.action.BilAddAction getBilAddAction(){
 if(context.get("bilAddAction")!=null)
 return (nc.ui.erm.billcontrast.action.BilAddAction)context.get("bilAddAction");
  nc.ui.erm.billcontrast.action.BilAddAction bean = new nc.ui.erm.billcontrast.action.BilAddAction();
  context.put("bilAddAction",bean);
  bean.setModel(getBilModel());
  bean.setVoClassName("nc.vo.erm.billcontrast.BillcontrastVO");
  bean.setExceptionHandler(getExeceptionHandler());
  bean.setEditor(getBilTable());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.erm.billcontrast.action.BilSetAction getBilSetAction(){
 if(context.get("bilSetAction")!=null)
 return (nc.ui.erm.billcontrast.action.BilSetAction)context.get("bilSetAction");
  nc.ui.erm.billcontrast.action.BilSetAction bean = new nc.ui.erm.billcontrast.action.BilSetAction();
  context.put("bilSetAction",bean);
  bean.setBilTable(getBilTable());
  bean.setDataManager(getModelDataManager());
  bean.setModel(getBilModel());
  bean.setExceptionHandler(getExeceptionHandler());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.actions.batch.BatchCancelAction getBilCancelAction(){
 if(context.get("bilCancelAction")!=null)
 return (nc.ui.pubapp.uif2app.actions.batch.BatchCancelAction)context.get("bilCancelAction");
  nc.ui.pubapp.uif2app.actions.batch.BatchCancelAction bean = new nc.ui.pubapp.uif2app.actions.batch.BatchCancelAction();
  context.put("bilCancelAction",bean);
  bean.setModel(getBilModel());
  bean.setEditor(getBilTable());
  bean.setExceptionHandler(getExeceptionHandler());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.erm.billcontrast.action.BilDeleteAction getBilDeleteAction(){
 if(context.get("bilDeleteAction")!=null)
 return (nc.ui.erm.billcontrast.action.BilDeleteAction)context.get("bilDeleteAction");
  nc.ui.erm.billcontrast.action.BilDeleteAction bean = new nc.ui.erm.billcontrast.action.BilDeleteAction();
  context.put("bilDeleteAction",bean);
  bean.setModel(getBilModel());
  bean.setBatchBillTable(getBilTable());
  bean.setExceptionHandler(getExeceptionHandler());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.actions.batch.BatchEditAction getBilEditAction(){
 if(context.get("bilEditAction")!=null)
 return (nc.ui.pubapp.uif2app.actions.batch.BatchEditAction)context.get("bilEditAction");
  nc.ui.pubapp.uif2app.actions.batch.BatchEditAction bean = new nc.ui.pubapp.uif2app.actions.batch.BatchEditAction();
  context.put("bilEditAction",bean);
  bean.setModel(getBilModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.erm.billcontrast.action.BilSaveAction getBilSaveAction(){
 if(context.get("bilSaveAction")!=null)
 return (nc.ui.erm.billcontrast.action.BilSaveAction)context.get("bilSaveAction");
  nc.ui.erm.billcontrast.action.BilSaveAction bean = new nc.ui.erm.billcontrast.action.BilSaveAction();
  context.put("bilSaveAction",bean);
  bean.setModel(getBilModel());
  bean.setEditor(getBilTable());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.actions.batch.BatchRefreshAction getBilRefreshAction(){
 if(context.get("bilRefreshAction")!=null)
 return (nc.ui.pubapp.uif2app.actions.batch.BatchRefreshAction)context.get("bilRefreshAction");
  nc.ui.pubapp.uif2app.actions.batch.BatchRefreshAction bean = new nc.ui.pubapp.uif2app.actions.batch.BatchRefreshAction();
  context.put("bilRefreshAction",bean);
  bean.setModel(getBilModel());
  bean.setModelManager(getModelDataManager());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.erm.billcontrast.view.BillcontrastFinOrgPanel getToppane(){
 if(context.get("toppane")!=null)
 return (nc.ui.erm.billcontrast.view.BillcontrastFinOrgPanel)context.get("toppane");
  nc.ui.erm.billcontrast.view.BillcontrastFinOrgPanel bean = new nc.ui.erm.billcontrast.view.BillcontrastFinOrgPanel();
  context.put("toppane",bean);
  bean.setDataManager(getModelDataManager());
  bean.setModel(getBilModel());
  bean.setLabelName(getI18nFB_c5c3ac());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_c5c3ac(){
 if(context.get("nc.ui.uif2.I18nFB#c5c3ac")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#c5c3ac");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#c5c3ac",bean);  bean.setResDir("201107_0");
  bean.setResId("0201107-0130");
  bean.setDefaultValue("财务组织");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#c5c3ac",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

public nc.ui.uif2.TangramContainer getContainer(){
 if(context.get("container")!=null)
 return (nc.ui.uif2.TangramContainer)context.get("container");
  nc.ui.uif2.TangramContainer bean = new nc.ui.uif2.TangramContainer();
  context.put("container",bean);
  bean.setTangramLayoutRoot(getVSNode_1f436f5());
  bean.setEditActions(getManagedList0());
  bean.setActions(getManagedList1());
  bean.setModel(getBilModel());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_1f436f5(){
 if(context.get("nc.ui.uif2.tangramlayout.node.VSNode#1f436f5")!=null)
 return (nc.ui.uif2.tangramlayout.node.VSNode)context.get("nc.ui.uif2.tangramlayout.node.VSNode#1f436f5");
  nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
  context.put("nc.ui.uif2.tangramlayout.node.VSNode#1f436f5",bean);
  bean.setUp(getCNode_1b16e52());
  bean.setDown(getCNode_1c1ea29());
  bean.setDividerLocation(0.058f);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_1b16e52(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#1b16e52")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#1b16e52");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#1b16e52",bean);
  bean.setComponent(getToppane());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_1c1ea29(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#1c1ea29")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#1c1ea29");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#1c1ea29",bean);
  bean.setComponent(getBilTable());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList0(){  List list = new ArrayList();  list.add(getBilAddAction());  list.add(getBilDeleteAction());  list.add(getBilSaveAction());  list.add(getBilNullAction());  list.add(getBilCancelAction());  return list;}

private List getManagedList1(){  List list = new ArrayList();  list.add(getBilAddAction());  list.add(getBilEditAction());  list.add(getBilDeleteAction());  list.add(getBilNullAction());  list.add(getBilRefreshAction());  list.add(getBilNullAction());  return list;}

}
