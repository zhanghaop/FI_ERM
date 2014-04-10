package nc.impl.erm.closeaccount;

import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.businessevent.IEventType;
import nc.bs.businessevent.bd.BDCommonEvent;
import nc.bs.framework.common.NCLocator;
import nc.bs.uif2.BusinessExceptionAdapter;
import nc.bs.uif2.validation.IValidationService;
import nc.bs.uif2.validation.ValidationException;
import nc.itf.bd.commoninfo.accperiod.IAccPeriodQueryServicer;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.erm.closeacc.ErmGLCloseAccListener;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;

public class BXAntiCloseAccountValidateServiceImpl implements IValidationService, IBusinessListener {

	@Override
	public void validate(Object value) throws ValidationException {
		if (value != null && value instanceof nc.vo.org.CloseAccBookVO) {
			nc.vo.org.CloseAccBookVO vo = (nc.vo.org.CloseAccBookVO) value;
			//报销管理模块
			if(BXConstans.ERM_MODULEID.equals(vo.getModuleid())){
				final String pk_accperiodmonth = vo.getPk_accperiodmonth();
				try {
					String yearAndMonth = NCLocator.getInstance().lookup(IAccPeriodQueryServicer.class).getYearMthByMthPk(pk_accperiodmonth);
					if(yearAndMonth != null && yearAndMonth.split("[-]").length == 2){
						yearAndMonth = yearAndMonth + "-01";
					}
					UFDate startDate = new UFDate(yearAndMonth);
					
					ErmGLCloseAccListener listener = new ErmGLCloseAccListener();
					final String year = "" + startDate.getYear();
					final int iMonth = startDate.getMonth();
					String month = "" + iMonth;
					if (iMonth < 10) {
						month = "0" + month;
					}
					listener.checkUnCloseAcc(year, month, vo.getPk_org());
				} catch (BusinessException e) {
					throw new BusinessExceptionAdapter(e);
				}
			}
		}
	}

	@Override
	public void doAction(IBusinessEvent evt) throws BusinessException {
		//反关账前
		if(IEventType.TYPE_UNCLOSEACCBOOK_BEFORE.equals(evt.getEventType()) && evt instanceof BDCommonEvent){
			BDCommonEvent be = (BDCommonEvent) evt;
			Object[] objects = be.getNewObjs();
			validate(objects[0]);
		}else if(IEventType.TYPE_BATCHUNCLOSEACCBOOK_BEFORE.equals(evt.getEventType()) && evt instanceof BDCommonEvent){
			//批量反关账
			BDCommonEvent be = (BDCommonEvent) evt;
			Object[] objects = be.getNewObjs();
			
			for(int i = 0; i < objects.length ; i++){
				validate(objects[i]);
			}
		}
	}
}
