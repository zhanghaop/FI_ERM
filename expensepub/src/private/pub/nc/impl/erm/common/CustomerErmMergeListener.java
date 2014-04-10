package nc.impl.erm.common;

import nc.bs.bd.businessevent.MergeBusinessEvent;
import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.PersistenceManager;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.exception.DbException;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.SuperVO;

/**
 * <p>
 * 报销管理客户合并监听类
 * 需要采用事件注册机制，注册方式通过系统 业务插件注册-动态企业建模平台-客户基本信息-合并前
 * 此处需要注意：(1)注册对应的监听类，并且注意插件执行顺序号，此处是按照需要编码进行执行的，
 *                也就是说，当跟其他产品安装的时候注意编排需要，沟通uap实现。
 * </p>
 *
 * 修改记录：<br>
 * <li>修改人：修改日期：修改内容：</li>
 * <br><br>
 *
 * @see 
 * @author liansg
 * @version V6.0
 * @since V6.0 创建时间：2010-9-2 下午04:54:44
 */
public class CustomerErmMergeListener implements IBusinessListener  {

	public void doAction(IBusinessEvent event) throws BusinessException {
				
			MergeBusinessEvent mergeEvent = (MergeBusinessEvent) event;
			SuperVO source = mergeEvent.getSourceVO();
			SuperVO target = mergeEvent.getTargetVO();
			String  sourcesup = source.getPrimaryKey();
			String  targetsup = target.getPrimaryKey();			
			
		    SQLParameter par=new SQLParameter();        
		    par.addParam(targetsup);
		    par.addParam(sourcesup);
		    PersistenceManager pm=null;
		        
		    try {
			pm = PersistenceManager.getInstance(getds());
			JdbcSession session=pm.getJdbcSession();
			//因为采用了sqlparameter采用“？”形式，提高效率，也可以采用addBatch(sql)  
			String sql = "update er_bxzb set customer =?  where customer =? ";
			String sql1 = "update er_jkzb set customer =?  where customer =? ";
			session.addBatch (sql,par);
			session.addBatch(sql1,par);
			//采用批量方式执行sql
			session.executeBatch();
	} catch (DbException e) {			
			ExceptionHandler.consume(e);
			throw new BusinessRuntimeException(e.getMessage(),e);
	}      
	  finally{
	        pm.release();
	}										
}
	private String getds(){	
		    return InvocationInfoProxy.getInstance().getUserDataSource();
	}
}

