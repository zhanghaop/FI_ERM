package nc.ui.er.reimtype.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.ui.uif2.factory.AbstractJavaBeanDefinition;

public class ret_base extends AbstractJavaBeanDefinition{
	private Map<String, Object> context = new HashMap();
public nc.ui.uif2.FunNodeClosingHandler getClosingListener(){
 if(context.get("ClosingListener")!=null)
 return (nc.ui.uif2.FunNodeClosingHandler)context.get("ClosingListener");
  nc.ui.uif2.FunNodeClosingHandler bean = new nc.ui.uif2.FunNodeClosingHandler();
  context.put("ClosingListener",bean);
  bean.setModel(getRetModel());
  bean.setSaveaction(getRetSaveAction());
  bean.setCancelaction(getRetCancelAction());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.er.reimtype.model.ReimTypeBatchModelService getService(){
 if(context.get("service")!=null)
 return (nc.ui.er.reimtype.model.ReimTypeBatchModelService)context.get("service");
  nc.ui.er.reimtype.model.ReimTypeBatchModelService bean = new nc.ui.er.reimtype.model.ReimTypeBatchModelService();
  context.put("service",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.model.BatchBillTableModel getRetModel(){
 if(context.get("retModel")!=null)
 return (nc.ui.uif2.model.BatchBillTableModel)context.get("retModel");
  nc.ui.uif2.model.BatchBillTableModel bean = new nc.ui.uif2.model.BatchBillTableModel();
  context.put("retModel",bean);
  bean.setContext((nc.vo.uif2.LoginContext)findBeanInUIF2BeanFactory("context"));
  bean.setService(getService());
  bean.setBusinessObjectAdapterFactory(getBDObjectAdpaterFactory_1174b07());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.vo.bd.meta.BDObjectAdpaterFactory getBDObjectAdpaterFactory_1174b07(){
 if(context.get("nc.vo.bd.meta.BDObjectAdpaterFactory#1174b07")!=null)
 return (nc.vo.bd.meta.BDObjectAdpaterFactory)context.get("nc.vo.bd.meta.BDObjectAdpaterFactory#1174b07");
  nc.vo.bd.meta.BDObjectAdpaterFactory bean = new nc.vo.bd.meta.BDObjectAdpaterFactory();
  context.put("nc.vo.bd.meta.BDObjectAdpaterFactory#1174b07",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.editor.BatchBillTable getReimTypeTable(){
 if(context.get("reimTypeTable")!=null)
 return (nc.ui.uif2.editor.BatchBillTable)context.get("reimTypeTable");
  nc.ui.uif2.editor.BatchBillTable bean = new nc.ui.uif2.editor.BatchBillTable();
  context.put("reimTypeTable",bean);
  bean.setModel(getRetModel());
  bean.setNodekey("20110RTS");
  bean.setIsBodyAutoAddLine(true);
  bean.setAddLineAction(getRetAddAction());
  bean.setDelLineAction(getRetDeleteAction());
  bean.setBodyMultiSelectEnable(true);
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.er.reimtype.model.ReimTypeModelDataManager getModelDataManager(){
 if(context.get("modelDataManager")!=null)
 return (nc.ui.er.reimtype.model.ReimTypeModelDataManager)context.get("modelDataManager");
  nc.ui.er.reimtype.model.ReimTypeModelDataManager bean = new nc.ui.er.reimtype.model.ReimTypeModelDataManager();
  context.put("modelDataManager",bean);
  bean.setModel(getRetModel());
  bean.setExceptionHandler(getExeceptionHandler());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.er.reimtype.view.ControlAreaOrgPanel getOrgPanel(){
 if(context.get("orgPanel")!=null)
 return (nc.ui.er.reimtype.view.ControlAreaOrgPanel)context.get("orgPanel");
  nc.ui.er.reimtype.view.ControlAreaOrgPanel bean = new nc.ui.er.reimtype.view.ControlAreaOrgPanel();
  context.put("orgPanel",bean);
  bean.setModel(getRetModel());
  bean.setDataManager(getModelDataManager());
  bean.setLabelName("¹Ü¿Ø·¶Î§");
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.er.reimtype.view.ReimTypeEditor getRetTable(){
 if(context.get("retTable")!=null)
 return (nc.ui.er.reimtype.view.ReimTypeEditor)context.get("retTable");
  nc.ui.er.reimtype.view.ReimTypeEditor bean = new nc.ui.er.reimtype.view.ReimTypeEditor();
  context.put("retTable",bean);
  bean.setModel(getRetModel());
  bean.setNodekey("20110RTS");
  bean.setContext((nc.vo.uif2.LoginContext)findBeanInUIF2BeanFactory("context"));
  bean.setCaOrgPanel(getOrgPanel());
  bean.setVoClassName("nc.vo.er.reimtype.ReimTypeVO");
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.er.reimtype.model.ReimTypeValidationService getValidationService(){
 if(context.get("validationService")!=null)
 return (nc.ui.er.reimtype.model.ReimTypeValidationService)context.get("validationService");
  nc.ui.er.reimtype.model.ReimTypeValidationService bean = new nc.ui.er.reimtype.model.ReimTypeValidationService();
  context.put("validationService",bean);
  bean.setEditor(getRetTable());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.DefaultExceptionHanler getExeceptionHandler(){
 if(context.get("execeptionHandler")!=null)
 return (nc.ui.uif2.DefaultExceptionHanler)context.get("execeptionHandler");
  nc.ui.uif2.DefaultExceptionHanler bean = new nc.ui.uif2.DefaultExceptionHanler();
  context.put("execeptionHandler",bean);
  bean.setContext((nc.vo.uif2.LoginContext)findBeanInUIF2BeanFactory("context"));
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.actions.DeleteConfirmInterceptor getDeleteInterceptor(){
 if(context.get("deleteInterceptor")!=null)
 return (nc.ui.uif2.actions.DeleteConfirmInterceptor)context.get("deleteInterceptor");
  nc.ui.uif2.actions.DeleteConfirmInterceptor bean = new nc.ui.uif2.actions.DeleteConfirmInterceptor();
  context.put("deleteInterceptor",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.funcnode.ui.action.SeparatorAction getRetNullAction(){
 if(context.get("retNullAction")!=null)
 return (nc.funcnode.ui.action.SeparatorAction)context.get("retNullAction");
  nc.funcnode.ui.action.SeparatorAction bean = new nc.funcnode.ui.action.SeparatorAction();
  context.put("retNullAction",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.er.reimtype.action.retAddAction getRetAddAction(){
 if(context.get("retAddAction")!=null)
 return (nc.ui.er.reimtype.action.retAddAction)context.get("retAddAction");
  nc.ui.er.reimtype.action.retAddAction bean = new nc.ui.er.reimtype.action.retAddAction();
  context.put("retAddAction",bean);
  bean.setModel(getRetModel());
  bean.setVoClassName("nc.vo.er.reimtype.ReimTypeVO");
  bean.setEditor(getRetTable());
  bean.setCaOrgPanel(getOrgPanel());
  bean.setExceptionHandler(getExeceptionHandler());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.actions.batch.BatchCancelAction getRetCancelAction(){
 if(context.get("retCancelAction")!=null)
 return (nc.ui.pubapp.uif2app.actions.batch.BatchCancelAction)context.get("retCancelAction");
  nc.ui.pubapp.uif2app.actions.batch.BatchCancelAction bean = new nc.ui.pubapp.uif2app.actions.batch.BatchCancelAction();
  context.put("retCancelAction",bean);
  bean.setModel(getRetModel());
  bean.setEditor(getRetTable());
  bean.setExceptionHandler(getExeceptionHandler());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.er.reimtype.action.retDeleteAction getRetDeleteAction(){
 if(context.get("retDeleteAction")!=null)
 return (nc.ui.er.reimtype.action.retDeleteAction)context.get("retDeleteAction");
  nc.ui.er.reimtype.action.retDeleteAction bean = new nc.ui.er.reimtype.action.retDeleteAction();
  context.put("retDeleteAction",bean);
  bean.setModel(getRetModel());
  bean.setEditor(getRetTable());
  bean.setExceptionHandler(getExeceptionHandler());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.er.reimtype.action.retEditAction getRetEditAction(){
 if(context.get("retEditAction")!=null)
 return (nc.ui.er.reimtype.action.retEditAction)context.get("retEditAction");
  nc.ui.er.reimtype.action.retEditAction bean = new nc.ui.er.reimtype.action.retEditAction();
  context.put("retEditAction",bean);
  bean.setModel(getRetModel());
  bean.setEditor(getRetTable());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.er.reimtype.action.retSaveAction getRetSaveAction(){
 if(context.get("retSaveAction")!=null)
 return (nc.ui.er.reimtype.action.retSaveAction)context.get("retSaveAction");
  nc.ui.er.reimtype.action.retSaveAction bean = new nc.ui.er.reimtype.action.retSaveAction();
  context.put("retSaveAction",bean);
  bean.setModel(getRetModel());
  bean.setEditor(getRetTable());
  bean.setValidationService(getValidationService());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.actions.batch.BatchRefreshAction getRetRefreshAction(){
 if(context.get("retRefreshAction")!=null)
 return (nc.ui.pubapp.uif2app.actions.batch.BatchRefreshAction)context.get("retRefreshAction");
  nc.ui.pubapp.uif2app.actions.batch.BatchRefreshAction bean = new nc.ui.pubapp.uif2app.actions.batch.BatchRefreshAction();
  context.put("retRefreshAction",bean);
  bean.setModel(getRetModel());
  bean.setModelManager(getModelDataManager());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.funcnode.ui.action.MenuAction getRetPrintGroupAction(){
 if(context.get("retPrintGroupAction")!=null)
 return (nc.funcnode.ui.action.MenuAction)context.get("retPrintGroupAction");
  nc.funcnode.ui.action.MenuAction bean = new nc.funcnode.ui.action.MenuAction();
  context.put("retPrintGroupAction",bean);
  bean.setCode("print");
  bean.setName(getI18nFB_17f1ba3());
  bean.setActions(getManagedList0());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_17f1ba3(){
 if(context.get("nc.ui.uif2.I18nFB#17f1ba3")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#17f1ba3");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#17f1ba3",bean);  bean.setResDir("common");
  bean.setResId("UC001-0000007");
  bean.setDefaultValue("´òÓ¡");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#17f1ba3",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

private List getManagedList0(){  List list = new ArrayList();  list.add(getRetTemplatePreviewAction());  list.add(getRetTemplatePrintAction());  return list;}

public nc.ui.uif2.actions.TemplatePreviewAction getRetTemplatePreviewAction(){
 if(context.get("retTemplatePreviewAction")!=null)
 return (nc.ui.uif2.actions.TemplatePreviewAction)context.get("retTemplatePreviewAction");
  nc.ui.uif2.actions.TemplatePreviewAction bean = new nc.ui.uif2.actions.TemplatePreviewAction();
  context.put("retTemplatePreviewAction",bean);
  bean.setModel(getRetModel());
  bean.setCode("Preview");
  bean.setDatasource(getDatasource());
  bean.setNodeKey("20110RTS");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.actions.TemplatePrintAction getRetTemplatePrintAction(){
 if(context.get("retTemplatePrintAction")!=null)
 return (nc.ui.uif2.actions.TemplatePrintAction)context.get("retTemplatePrintAction");
  nc.ui.uif2.actions.TemplatePrintAction bean = new nc.ui.uif2.actions.TemplatePrintAction();
  context.put("retTemplatePrintAction",bean);
  bean.setModel(getRetModel());
  bean.setCode("Print");
  bean.setDatasource(getDatasource());
  bean.setNodeKey("20110RTS");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bd.pub.actions.print.MetaDataAllDatasSource getDatasource(){
 if(context.get("datasource")!=null)
 return (nc.ui.bd.pub.actions.print.MetaDataAllDatasSource)context.get("datasource");
  nc.ui.bd.pub.actions.print.MetaDataAllDatasSource bean = new nc.ui.bd.pub.actions.print.MetaDataAllDatasSource();
  context.put("datasource",bean);
  bean.setModel(getRetModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

}
