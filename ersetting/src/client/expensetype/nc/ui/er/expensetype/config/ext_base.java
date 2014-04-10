package nc.ui.er.expensetype.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.ui.uif2.factory.AbstractJavaBeanDefinition;

public class ext_base extends AbstractJavaBeanDefinition{
	private Map<String, Object> context = new HashMap();
public nc.ui.uif2.FunNodeClosingHandler getClosingListener(){
 if(context.get("ClosingListener")!=null)
 return (nc.ui.uif2.FunNodeClosingHandler)context.get("ClosingListener");
  nc.ui.uif2.FunNodeClosingHandler bean = new nc.ui.uif2.FunNodeClosingHandler();
  context.put("ClosingListener",bean);
  bean.setModel(getExtModel());
  bean.setSaveaction(getExtSaveAction());
  bean.setCancelaction(getExtCancelAction());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.er.expensetype.model.ExpenseTypeBatchModelService getService(){
 if(context.get("service")!=null)
 return (nc.ui.er.expensetype.model.ExpenseTypeBatchModelService)context.get("service");
  nc.ui.er.expensetype.model.ExpenseTypeBatchModelService bean = new nc.ui.er.expensetype.model.ExpenseTypeBatchModelService();
  context.put("service",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.model.BatchBillTableModel getExtModel(){
 if(context.get("extModel")!=null)
 return (nc.ui.uif2.model.BatchBillTableModel)context.get("extModel");
  nc.ui.uif2.model.BatchBillTableModel bean = new nc.ui.uif2.model.BatchBillTableModel();
  context.put("extModel",bean);
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

public nc.ui.uif2.editor.BatchBillTable getExpenseTypeTable(){
 if(context.get("expenseTypeTable")!=null)
 return (nc.ui.uif2.editor.BatchBillTable)context.get("expenseTypeTable");
  nc.ui.uif2.editor.BatchBillTable bean = new nc.ui.uif2.editor.BatchBillTable();
  context.put("expenseTypeTable",bean);
  bean.setModel(getExtModel());
  bean.setNodekey("20110ETS");
  bean.setIsBodyAutoAddLine(true);
  bean.setAddLineAction(getExtAddAction());
  bean.setDelLineAction(getExtDeleteAction());
  bean.setBodyMultiSelectEnable(true);
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.er.expensetype.model.ExpenseTypeModelDataManager getModelDataManager(){
 if(context.get("modelDataManager")!=null)
 return (nc.ui.er.expensetype.model.ExpenseTypeModelDataManager)context.get("modelDataManager");
  nc.ui.er.expensetype.model.ExpenseTypeModelDataManager bean = new nc.ui.er.expensetype.model.ExpenseTypeModelDataManager();
  context.put("modelDataManager",bean);
  bean.setModel(getExtModel());
  bean.setExceptionHandler(getExeceptionHandler());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.er.expensetype.view.ControlAreaOrgPanel getOrgPanel(){
 if(context.get("orgPanel")!=null)
 return (nc.ui.er.expensetype.view.ControlAreaOrgPanel)context.get("orgPanel");
  nc.ui.er.expensetype.view.ControlAreaOrgPanel bean = new nc.ui.er.expensetype.view.ControlAreaOrgPanel();
  context.put("orgPanel",bean);
  bean.setModel(getExtModel());
  bean.setDataManager(getModelDataManager());
  bean.setLabelName("¹Ü¿Ø·¶Î§");
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.er.expensetype.view.ExpenseTypeEditor getExtTable(){
 if(context.get("extTable")!=null)
 return (nc.ui.er.expensetype.view.ExpenseTypeEditor)context.get("extTable");
  nc.ui.er.expensetype.view.ExpenseTypeEditor bean = new nc.ui.er.expensetype.view.ExpenseTypeEditor();
  context.put("extTable",bean);
  bean.setModel(getExtModel());
  bean.setNodekey("20110ETS");
  bean.setContext((nc.vo.uif2.LoginContext)findBeanInUIF2BeanFactory("context"));
  bean.setCaOrgPanel(getOrgPanel());
  bean.setVoClassName("nc.vo.er.expensetype.ExpenseTypeVO");
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.er.expensetype.model.ExpenseTypeValidationService getValidationService(){
 if(context.get("validationService")!=null)
 return (nc.ui.er.expensetype.model.ExpenseTypeValidationService)context.get("validationService");
  nc.ui.er.expensetype.model.ExpenseTypeValidationService bean = new nc.ui.er.expensetype.model.ExpenseTypeValidationService();
  context.put("validationService",bean);
  bean.setEditor(getExtTable());
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

public nc.funcnode.ui.action.SeparatorAction getExtNullAction(){
 if(context.get("extNullAction")!=null)
 return (nc.funcnode.ui.action.SeparatorAction)context.get("extNullAction");
  nc.funcnode.ui.action.SeparatorAction bean = new nc.funcnode.ui.action.SeparatorAction();
  context.put("extNullAction",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.er.expensetype.action.extAddAction getExtAddAction(){
 if(context.get("extAddAction")!=null)
 return (nc.ui.er.expensetype.action.extAddAction)context.get("extAddAction");
  nc.ui.er.expensetype.action.extAddAction bean = new nc.ui.er.expensetype.action.extAddAction();
  context.put("extAddAction",bean);
  bean.setModel(getExtModel());
  bean.setVoClassName("nc.vo.er.expensetype.ExpenseTypeVO");
  bean.setEditor(getExtTable());
  bean.setCaOrgPanel(getOrgPanel());
  bean.setExceptionHandler(getExeceptionHandler());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.actions.batch.BatchCancelAction getExtCancelAction(){
 if(context.get("extCancelAction")!=null)
 return (nc.ui.pubapp.uif2app.actions.batch.BatchCancelAction)context.get("extCancelAction");
  nc.ui.pubapp.uif2app.actions.batch.BatchCancelAction bean = new nc.ui.pubapp.uif2app.actions.batch.BatchCancelAction();
  context.put("extCancelAction",bean);
  bean.setModel(getExtModel());
  bean.setEditor(getExtTable());
  bean.setExceptionHandler(getExeceptionHandler());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.er.expensetype.action.extDeleteAction getExtDeleteAction(){
 if(context.get("extDeleteAction")!=null)
 return (nc.ui.er.expensetype.action.extDeleteAction)context.get("extDeleteAction");
  nc.ui.er.expensetype.action.extDeleteAction bean = new nc.ui.er.expensetype.action.extDeleteAction();
  context.put("extDeleteAction",bean);
  bean.setModel(getExtModel());
  bean.setEditor(getExtTable());
  bean.setExceptionHandler(getExeceptionHandler());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.er.expensetype.action.extEditAction getExtEditAction(){
 if(context.get("extEditAction")!=null)
 return (nc.ui.er.expensetype.action.extEditAction)context.get("extEditAction");
  nc.ui.er.expensetype.action.extEditAction bean = new nc.ui.er.expensetype.action.extEditAction();
  context.put("extEditAction",bean);
  bean.setModel(getExtModel());
  bean.setEditor(getExtTable());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.er.expensetype.action.extSaveAction getExtSaveAction(){
 if(context.get("extSaveAction")!=null)
 return (nc.ui.er.expensetype.action.extSaveAction)context.get("extSaveAction");
  nc.ui.er.expensetype.action.extSaveAction bean = new nc.ui.er.expensetype.action.extSaveAction();
  context.put("extSaveAction",bean);
  bean.setModel(getExtModel());
  bean.setEditor(getExtTable());
  bean.setValidationService(getValidationService());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.actions.batch.BatchRefreshAction getExtRefreshAction(){
 if(context.get("extRefreshAction")!=null)
 return (nc.ui.pubapp.uif2app.actions.batch.BatchRefreshAction)context.get("extRefreshAction");
  nc.ui.pubapp.uif2app.actions.batch.BatchRefreshAction bean = new nc.ui.pubapp.uif2app.actions.batch.BatchRefreshAction();
  context.put("extRefreshAction",bean);
  bean.setModel(getExtModel());
  bean.setModelManager(getModelDataManager());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.funcnode.ui.action.GroupAction getExtPrintGroupAction(){
 if(context.get("extPrintGroupAction")!=null)
 return (nc.funcnode.ui.action.GroupAction)context.get("extPrintGroupAction");
  nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
  context.put("extPrintGroupAction",bean);
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

private List getManagedList0(){  List list = new ArrayList();  list.add(getExtTemplatePrintAction());  list.add(getExtTemplatePreviewAction());  return list;}

public nc.ui.uif2.actions.TemplatePreviewAction getExtTemplatePreviewAction(){
 if(context.get("extTemplatePreviewAction")!=null)
 return (nc.ui.uif2.actions.TemplatePreviewAction)context.get("extTemplatePreviewAction");
  nc.ui.uif2.actions.TemplatePreviewAction bean = new nc.ui.uif2.actions.TemplatePreviewAction();
  context.put("extTemplatePreviewAction",bean);
  bean.setModel(getExtModel());
  bean.setCode("Print");
  bean.setDatasource(getDatasource());
  bean.setNodeKey("20110ETS");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.actions.TemplatePrintAction getExtTemplatePrintAction(){
 if(context.get("extTemplatePrintAction")!=null)
 return (nc.ui.uif2.actions.TemplatePrintAction)context.get("extTemplatePrintAction");
  nc.ui.uif2.actions.TemplatePrintAction bean = new nc.ui.uif2.actions.TemplatePrintAction();
  context.put("extTemplatePrintAction",bean);
  bean.setModel(getExtModel());
  bean.setCode("Preview");
  bean.setDatasource(getDatasource());
  bean.setNodeKey("20110ETS");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bd.pub.actions.print.MetaDataAllDatasSource getDatasource(){
 if(context.get("datasource")!=null)
 return (nc.ui.bd.pub.actions.print.MetaDataAllDatasSource)context.get("datasource");
  nc.ui.bd.pub.actions.print.MetaDataAllDatasSource bean = new nc.ui.bd.pub.actions.print.MetaDataAllDatasSource();
  context.put("datasource",bean);
  bean.setModel(getExtModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

}
