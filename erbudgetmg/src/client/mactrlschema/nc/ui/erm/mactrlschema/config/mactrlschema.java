package nc.ui.erm.mactrlschema.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.ui.uif2.factory.AbstractJavaBeanDefinition;

public class mactrlschema extends AbstractJavaBeanDefinition{
	private Map<String, Object> context = new HashMap();
public nc.vo.uif2.LoginContext getContext(){
 if(context.get("context")!=null)
 return (nc.vo.uif2.LoginContext)context.get("context");
  nc.vo.uif2.LoginContext bean = new nc.vo.uif2.LoginContext();
  context.put("context",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.erm.mactrlschema.actions.FunNodeClosingHandler getClosingListener(){
 if(context.get("ClosingListener")!=null)
 return (nc.ui.erm.mactrlschema.actions.FunNodeClosingHandler)context.get("ClosingListener");
  nc.ui.erm.mactrlschema.actions.FunNodeClosingHandler bean = new nc.ui.erm.mactrlschema.actions.FunNodeClosingHandler();
  context.put("ClosingListener",bean);
  bean.setCtrlFieldTableModel(getCtrlFieldTableModel());
  bean.setCtrlBillTableModel(getCtrlBillTableModel());
  bean.setSaveFldAction(getSaveFldAction());
  bean.setSaveBilAction(getSaveBilAction());
  bean.setCancelFldAction(getCancelFldAction());
  bean.setCancelBilAction(getCancelBilAction());
  bean.setExceptionHandler(getExceptionHandler());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.DefaultExceptionHanler getExceptionHandler(){
 if(context.get("exceptionHandler")!=null)
 return (nc.ui.uif2.DefaultExceptionHanler)context.get("exceptionHandler");
  nc.ui.uif2.DefaultExceptionHanler bean = new nc.ui.uif2.DefaultExceptionHanler();
  context.put("exceptionHandler",bean);
  bean.setContext(getContext());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.vo.bd.meta.BDObjectAdpaterFactory getBoadatorfactory(){
 if(context.get("boadatorfactory")!=null)
 return (nc.vo.bd.meta.BDObjectAdpaterFactory)context.get("boadatorfactory");
  nc.vo.bd.meta.BDObjectAdpaterFactory bean = new nc.vo.bd.meta.BDObjectAdpaterFactory();
  context.put("boadatorfactory",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.editor.TemplateContainer getTemplateContainer(){
 if(context.get("templateContainer")!=null)
 return (nc.ui.uif2.editor.TemplateContainer)context.get("templateContainer");
  nc.ui.uif2.editor.TemplateContainer bean = new nc.ui.uif2.editor.TemplateContainer();
  context.put("templateContainer",bean);
  bean.setContext(getContext());
  bean.setNodeKeies(getManagedList0());
  bean.load();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList0(){  List list = new ArrayList();  list.add("20110MCSF");  list.add("20110MCSB");  return list;}

public nc.ui.erm.mactrlschema.model.MaCtrlAppModelDataManager getModelDataManager(){
 if(context.get("modelDataManager")!=null)
 return (nc.ui.erm.mactrlschema.model.MaCtrlAppModelDataManager)context.get("modelDataManager");
  nc.ui.erm.mactrlschema.model.MaCtrlAppModelDataManager bean = new nc.ui.erm.mactrlschema.model.MaCtrlAppModelDataManager();
  context.put("modelDataManager",bean);
  bean.setTreeModel(getTreeModel());
  bean.setCtrlfieldtableModel(getCtrlFieldTableModel());
  bean.setCtrlbilltableModel(getCtrlBillTableModel());
  bean.setOrgPanel(getOrgPanel());
  bean.setExceptionHandler(getExceptionHandler());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.model.HierachicalDataAppModel getTreeModel(){
 if(context.get("treeModel")!=null)
 return (nc.ui.uif2.model.HierachicalDataAppModel)context.get("treeModel");
  nc.ui.uif2.model.HierachicalDataAppModel bean = new nc.ui.uif2.model.HierachicalDataAppModel();
  context.put("treeModel",bean);
  bean.setBusinessObjectAdapterFactory(getBoadatorfactory());
  bean.setContext(getContext());
  bean.setTreeCreateStrategy(getTreeCreateStrategy());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.vo.bd.meta.BDObjectTreeCreateStrategy getTreeCreateStrategy(){
 if(context.get("treeCreateStrategy")!=null)
 return (nc.vo.bd.meta.BDObjectTreeCreateStrategy)context.get("treeCreateStrategy");
  nc.vo.bd.meta.BDObjectTreeCreateStrategy bean = new nc.vo.bd.meta.BDObjectTreeCreateStrategy();
  context.put("treeCreateStrategy",bean);
  bean.setFactory(getBoadatorfactory());
  bean.setRootName(getI18nFB_ecd7e());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_ecd7e(){
 if(context.get("nc.ui.uif2.I18nFB#ecd7e")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#ecd7e");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#ecd7e",bean);  bean.setResDir("201212_0");
  bean.setResId("0201212-0075");
  bean.setDefaultValue("费用申请单交易类型");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#ecd7e",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

public nc.ui.erm.mactrlschema.view.MaCtrlTreePanel getTreePanel(){
 if(context.get("treePanel")!=null)
 return (nc.ui.erm.mactrlschema.view.MaCtrlTreePanel)context.get("treePanel");
  nc.ui.erm.mactrlschema.view.MaCtrlTreePanel bean = new nc.ui.erm.mactrlschema.view.MaCtrlTreePanel();
  context.put("treePanel",bean);
  bean.setModel(getTreeModel());
  bean.init();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.erm.mactrlschema.view.MaCtrlOrgPanel getOrgPanel(){
 if(context.get("orgPanel")!=null)
 return (nc.ui.erm.mactrlschema.view.MaCtrlOrgPanel)context.get("orgPanel");
  nc.ui.erm.mactrlschema.view.MaCtrlOrgPanel bean = new nc.ui.erm.mactrlschema.view.MaCtrlOrgPanel();
  context.put("orgPanel",bean);
  bean.setLabelName(getI18nFB_1d520c4());
  bean.setModel(getTreeModel());
  bean.setDataManager(getModelDataManager());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_1d520c4(){
 if(context.get("nc.ui.uif2.I18nFB#1d520c4")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#1d520c4");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#1d520c4",bean);  bean.setResDir("201212_0");
  bean.setResId("0201212-0076");
  bean.setDefaultValue("财务组织");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#1d520c4",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

public nc.ui.erm.mactrlschema.model.MCtrlFieldService getCtrlFldservice(){
 if(context.get("ctrlFldservice")!=null)
 return (nc.ui.erm.mactrlschema.model.MCtrlFieldService)context.get("ctrlFldservice");
  nc.ui.erm.mactrlschema.model.MCtrlFieldService bean = new nc.ui.erm.mactrlschema.model.MCtrlFieldService();
  context.put("ctrlFldservice",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.model.BatchBillTableModel getCtrlFieldTableModel(){
 if(context.get("ctrlFieldTableModel")!=null)
 return (nc.ui.uif2.model.BatchBillTableModel)context.get("ctrlFieldTableModel");
  nc.ui.uif2.model.BatchBillTableModel bean = new nc.ui.uif2.model.BatchBillTableModel();
  context.put("ctrlFieldTableModel",bean);
  bean.setBusinessObjectAdapterFactory(getBoadatorfactory());
  bean.setContext(getContext());
  bean.setService(getCtrlFldservice());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.erm.mactrlschema.view.MaCtrlFieldTable getCtrlFieldTable(){
 if(context.get("ctrlFieldTable")!=null)
 return (nc.ui.erm.mactrlschema.view.MaCtrlFieldTable)context.get("ctrlFieldTable");
  nc.ui.erm.mactrlschema.view.MaCtrlFieldTable bean = new nc.ui.erm.mactrlschema.view.MaCtrlFieldTable();
  context.put("ctrlFieldTable",bean);
  bean.setName(getI18nFB_15a3d6b());
  bean.setNodekey("20110MCSF");
  bean.setTemplateContainer(getTemplateContainer());
  bean.setModel(getCtrlFieldTableModel());
  bean.setActions(getManagedList1());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_15a3d6b(){
 if(context.get("nc.ui.uif2.I18nFB#15a3d6b")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#15a3d6b");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#15a3d6b",bean);  bean.setResDir("201212_0");
  bean.setResId("0201212-0077");
  bean.setDefaultValue("控制维度");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#15a3d6b",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

private List getManagedList1(){  List list = new ArrayList();  list.add(getEditFldAction());  list.add(getAddFldAction());  list.add(getDeleteFldAction());  list.add(getSaveFldAction());  list.add(getCancelFldAction());  return list;}

public nc.ui.erm.mactrlschema.actions.EditFldAction getEditFldAction(){
 if(context.get("editFldAction")!=null)
 return (nc.ui.erm.mactrlschema.actions.EditFldAction)context.get("editFldAction");
  nc.ui.erm.mactrlschema.actions.EditFldAction bean = new nc.ui.erm.mactrlschema.actions.EditFldAction();
  context.put("editFldAction",bean);
  bean.setModel(getCtrlFieldTableModel());
  bean.setTreeModel(getTreeModel());
  bean.setExceptionHandler(getExceptionHandler());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.erm.mactrlschema.actions.CancelFldAction getCancelFldAction(){
 if(context.get("cancelFldAction")!=null)
 return (nc.ui.erm.mactrlschema.actions.CancelFldAction)context.get("cancelFldAction");
  nc.ui.erm.mactrlschema.actions.CancelFldAction bean = new nc.ui.erm.mactrlschema.actions.CancelFldAction();
  context.put("cancelFldAction",bean);
  bean.setModel(getCtrlFieldTableModel());
  bean.setEditor(getCtrlFieldTable());
  bean.setExceptionHandler(getExceptionHandler());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.erm.mactrlschema.actions.AddFldAction getAddFldAction(){
 if(context.get("addFldAction")!=null)
 return (nc.ui.erm.mactrlschema.actions.AddFldAction)context.get("addFldAction");
  nc.ui.erm.mactrlschema.actions.AddFldAction bean = new nc.ui.erm.mactrlschema.actions.AddFldAction();
  context.put("addFldAction",bean);
  bean.setModel(getCtrlFieldTableModel());
  bean.setTreeModel(getTreeModel());
  bean.setExceptionHandler(getExceptionHandler());
  bean.setVoClassName("nc.vo.erm.mactrlschema.MtappCtrlfieldVO");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.erm.mactrlschema.actions.DelLineFldAction getDeleteFldAction(){
 if(context.get("deleteFldAction")!=null)
 return (nc.ui.erm.mactrlschema.actions.DelLineFldAction)context.get("deleteFldAction");
  nc.ui.erm.mactrlschema.actions.DelLineFldAction bean = new nc.ui.erm.mactrlschema.actions.DelLineFldAction();
  context.put("deleteFldAction",bean);
  bean.setModel(getCtrlFieldTableModel());
  bean.setTreeModel(getTreeModel());
  bean.setExceptionHandler(getExceptionHandler());
  bean.setBatchBillTable(getCtrlFieldTable());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.erm.mactrlschema.actions.SaveFldAction getSaveFldAction(){
 if(context.get("saveFldAction")!=null)
 return (nc.ui.erm.mactrlschema.actions.SaveFldAction)context.get("saveFldAction");
  nc.ui.erm.mactrlschema.actions.SaveFldAction bean = new nc.ui.erm.mactrlschema.actions.SaveFldAction();
  context.put("saveFldAction",bean);
  bean.setModel(getCtrlFieldTableModel());
  bean.setTreeModel(getTreeModel());
  bean.setExceptionHandler(getExceptionHandler());
  bean.setEditor(getCtrlFieldTable());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.erm.mactrlschema.model.MCtrlBillService getCtrlBillservice(){
 if(context.get("ctrlBillservice")!=null)
 return (nc.ui.erm.mactrlschema.model.MCtrlBillService)context.get("ctrlBillservice");
  nc.ui.erm.mactrlschema.model.MCtrlBillService bean = new nc.ui.erm.mactrlschema.model.MCtrlBillService();
  context.put("ctrlBillservice",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.model.BatchBillTableModel getCtrlBillTableModel(){
 if(context.get("ctrlBillTableModel")!=null)
 return (nc.ui.uif2.model.BatchBillTableModel)context.get("ctrlBillTableModel");
  nc.ui.uif2.model.BatchBillTableModel bean = new nc.ui.uif2.model.BatchBillTableModel();
  context.put("ctrlBillTableModel",bean);
  bean.setBusinessObjectAdapterFactory(getBoadatorfactory());
  bean.setContext(getContext());
  bean.setService(getCtrlBillservice());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.erm.mactrlschema.view.MaCtrlBillTable getCtrlBillTable(){
 if(context.get("ctrlBillTable")!=null)
 return (nc.ui.erm.mactrlschema.view.MaCtrlBillTable)context.get("ctrlBillTable");
  nc.ui.erm.mactrlschema.view.MaCtrlBillTable bean = new nc.ui.erm.mactrlschema.view.MaCtrlBillTable();
  context.put("ctrlBillTable",bean);
  bean.setName(getI18nFB_b753f8());
  bean.setNodekey("20110MCSB");
  bean.setTemplateContainer(getTemplateContainer());
  bean.setModel(getCtrlBillTableModel());
  bean.setActions(getManagedList2());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_b753f8(){
 if(context.get("nc.ui.uif2.I18nFB#b753f8")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#b753f8");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#b753f8",bean);  bean.setResDir("201212_0");
  bean.setResId("0201212-0078");
  bean.setDefaultValue("控制对象");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#b753f8",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

private List getManagedList2(){  List list = new ArrayList();  list.add(getEditBilAction());  list.add(getAddBilAction());  list.add(getDeleteBilAction());  list.add(getSaveBilAction());  list.add(getCancelBilAction());  return list;}

public nc.ui.erm.mactrlschema.actions.EditBilAction getEditBilAction(){
 if(context.get("editBilAction")!=null)
 return (nc.ui.erm.mactrlschema.actions.EditBilAction)context.get("editBilAction");
  nc.ui.erm.mactrlschema.actions.EditBilAction bean = new nc.ui.erm.mactrlschema.actions.EditBilAction();
  context.put("editBilAction",bean);
  bean.setModel(getCtrlBillTableModel());
  bean.setTreeModel(getTreeModel());
  bean.setExceptionHandler(getExceptionHandler());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.actions.batch.BatchCancelAction getCancelBilAction(){
 if(context.get("cancelBilAction")!=null)
 return (nc.ui.uif2.actions.batch.BatchCancelAction)context.get("cancelBilAction");
  nc.ui.uif2.actions.batch.BatchCancelAction bean = new nc.ui.uif2.actions.batch.BatchCancelAction();
  context.put("cancelBilAction",bean);
  bean.setModel(getCtrlBillTableModel());
  bean.setEditor(getCtrlBillTable());
  bean.setExceptionHandler(getExceptionHandler());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.erm.mactrlschema.actions.AddBilAction getAddBilAction(){
 if(context.get("addBilAction")!=null)
 return (nc.ui.erm.mactrlschema.actions.AddBilAction)context.get("addBilAction");
  nc.ui.erm.mactrlschema.actions.AddBilAction bean = new nc.ui.erm.mactrlschema.actions.AddBilAction();
  context.put("addBilAction",bean);
  bean.setModel(getCtrlBillTableModel());
  bean.setTreeModel(getTreeModel());
  bean.setExceptionHandler(getExceptionHandler());
  bean.setVoClassName("nc.vo.erm.mactrlschema.MtappCtrlbillVO");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.erm.mactrlschema.actions.DelLineBilAction getDeleteBilAction(){
 if(context.get("deleteBilAction")!=null)
 return (nc.ui.erm.mactrlschema.actions.DelLineBilAction)context.get("deleteBilAction");
  nc.ui.erm.mactrlschema.actions.DelLineBilAction bean = new nc.ui.erm.mactrlschema.actions.DelLineBilAction();
  context.put("deleteBilAction",bean);
  bean.setModel(getCtrlBillTableModel());
  bean.setTreeModel(getTreeModel());
  bean.setExceptionHandler(getExceptionHandler());
  bean.setBatchBillTable(getCtrlBillTable());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.erm.mactrlschema.actions.SaveBilAction getSaveBilAction(){
 if(context.get("saveBilAction")!=null)
 return (nc.ui.erm.mactrlschema.actions.SaveBilAction)context.get("saveBilAction");
  nc.ui.erm.mactrlschema.actions.SaveBilAction bean = new nc.ui.erm.mactrlschema.actions.SaveBilAction();
  context.put("saveBilAction",bean);
  bean.setModel(getCtrlBillTableModel());
  bean.setTreeModel(getTreeModel());
  bean.setExceptionHandler(getExceptionHandler());
  bean.setEditor(getCtrlBillTable());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.erm.mactrlschema.actions.RefreshAction getRefreshAction(){
 if(context.get("refreshAction")!=null)
 return (nc.ui.erm.mactrlschema.actions.RefreshAction)context.get("refreshAction");
  nc.ui.erm.mactrlschema.actions.RefreshAction bean = new nc.ui.erm.mactrlschema.actions.RefreshAction();
  context.put("refreshAction",bean);
  bean.setModelManager(getModelDataManager());
  bean.setTreeModel(getTreeModel());
  bean.setTreePanel(getTreePanel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.TangramContainer getContainer(){
 if(context.get("container")!=null)
 return (nc.ui.uif2.TangramContainer)context.get("container");
  nc.ui.uif2.TangramContainer bean = new nc.ui.uif2.TangramContainer();
  context.put("container",bean);
  bean.setTangramLayoutRoot(getVSNode_17fa65e());
  bean.setActions(getManagedList5());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_17fa65e(){
 if(context.get("nc.ui.uif2.tangramlayout.node.VSNode#17fa65e")!=null)
 return (nc.ui.uif2.tangramlayout.node.VSNode)context.get("nc.ui.uif2.tangramlayout.node.VSNode#17fa65e");
  nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
  context.put("nc.ui.uif2.tangramlayout.node.VSNode#17fa65e",bean);
  bean.setUp(getCNode_1e9cb75());
  bean.setDown(getHSNode_6e3d60());
  bean.setDividerLocation(0.05f);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_1e9cb75(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#1e9cb75")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#1e9cb75");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#1e9cb75",bean);
  bean.setName("财务组织");
  bean.setComponent(getOrgPanel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.HSNode getHSNode_6e3d60(){
 if(context.get("nc.ui.uif2.tangramlayout.node.HSNode#6e3d60")!=null)
 return (nc.ui.uif2.tangramlayout.node.HSNode)context.get("nc.ui.uif2.tangramlayout.node.HSNode#6e3d60");
  nc.ui.uif2.tangramlayout.node.HSNode bean = new nc.ui.uif2.tangramlayout.node.HSNode();
  context.put("nc.ui.uif2.tangramlayout.node.HSNode#6e3d60",bean);
  bean.setLeft(getCNode_2c84d9());
  bean.setRight(getVSNode_197a37c());
  bean.setDividerLocation(0.2f);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_2c84d9(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#2c84d9")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#2c84d9");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#2c84d9",bean);
  bean.setName("左树");
  bean.setComponent(getTreePanel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_197a37c(){
 if(context.get("nc.ui.uif2.tangramlayout.node.VSNode#197a37c")!=null)
 return (nc.ui.uif2.tangramlayout.node.VSNode)context.get("nc.ui.uif2.tangramlayout.node.VSNode#197a37c");
  nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
  context.put("nc.ui.uif2.tangramlayout.node.VSNode#197a37c",bean);
  bean.setUp(getTBNode_1c1ea29());
  bean.setDown(getTBNode_1786e64());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.TBNode getTBNode_1c1ea29(){
 if(context.get("nc.ui.uif2.tangramlayout.node.TBNode#1c1ea29")!=null)
 return (nc.ui.uif2.tangramlayout.node.TBNode)context.get("nc.ui.uif2.tangramlayout.node.TBNode#1c1ea29");
  nc.ui.uif2.tangramlayout.node.TBNode bean = new nc.ui.uif2.tangramlayout.node.TBNode();
  context.put("nc.ui.uif2.tangramlayout.node.TBNode#1c1ea29",bean);
  bean.setTabs(getManagedList3());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList3(){  List list = new ArrayList();  list.add(getCNode_1b16e52());  return list;}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_1b16e52(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#1b16e52")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#1b16e52");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#1b16e52",bean);
  bean.setName(getI18nFB_c5c3ac());
  bean.setComponent(getCtrlFieldTable());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_c5c3ac(){
 if(context.get("nc.ui.uif2.I18nFB#c5c3ac")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#c5c3ac");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#c5c3ac",bean);  bean.setResDir("201212_0");
  bean.setResId("0201212-0077");
  bean.setDefaultValue("控制维度");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#c5c3ac",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

private nc.ui.uif2.tangramlayout.node.TBNode getTBNode_1786e64(){
 if(context.get("nc.ui.uif2.tangramlayout.node.TBNode#1786e64")!=null)
 return (nc.ui.uif2.tangramlayout.node.TBNode)context.get("nc.ui.uif2.tangramlayout.node.TBNode#1786e64");
  nc.ui.uif2.tangramlayout.node.TBNode bean = new nc.ui.uif2.tangramlayout.node.TBNode();
  context.put("nc.ui.uif2.tangramlayout.node.TBNode#1786e64",bean);
  bean.setTabs(getManagedList4());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList4(){  List list = new ArrayList();  list.add(getCNode_4413ee());  return list;}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_4413ee(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#4413ee")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#4413ee");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#4413ee",bean);
  bean.setName(getI18nFB_1f436f5());
  bean.setComponent(getCtrlBillTable());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_1f436f5(){
 if(context.get("nc.ui.uif2.I18nFB#1f436f5")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#1f436f5");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#1f436f5",bean);  bean.setResDir("201212_0");
  bean.setResId("0201212-0078");
  bean.setDefaultValue("控制对象");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#1f436f5",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

private List getManagedList5(){  List list = new ArrayList();  list.add(getRefreshAction());  return list;}

}
