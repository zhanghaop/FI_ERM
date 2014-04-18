package nc.bs.arap.bx;

import java.util.HashMap;
import java.util.Map;

import nc.md.MDBaseQueryFacade;
import nc.md.model.IBean;
import nc.md.model.MetaDataException;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.JKBXVO;

/**
 * v6.1 业务日志工具类
 *
 */
public class BxBusiLogUtils {
	
	private static Map<String,IBean> map = new HashMap<String, IBean>();

	public static IBean getMetaDataIDByVoClassName(String voClassName)
			throws MetaDataException {
		if (!map.containsKey(voClassName)) {
			map.put(voClassName, MDBaseQueryFacade.getInstance().getBeanByFullClassName(voClassName));
		}
		return map.get(voClassName);
	}
	
	private static enum ErmBusiLogOperationType {
		/**
		 * 修改
		 */
		EDIT("66efc3ac-092f-419e-8d48-464d8003b156", "修改"),/*-=报销修改=-*//*-=notranslate=-*/

		/**
		 * 删除
		 */
		DELETE("78af39d8-92a3-4acc-92ea-3b3eee0dcbba", "删除"),/*-=报销删除=-*//*-=notranslate=-*/
		
		/**
		 * 删除
		 */
		JKEDIT("4e9bc7ae-a9fa-4f29-9846-a4afcf95ea31", "修改"),/*-=借款修改=-*//*-=notranslate=-*/
		
		/**
		 * 删除
		 */
		JKDELETE("69159c6e-4c8d-4fe0-91c5-b5b700dd0d4f", "删除");/*-=借款删除=-*//*-=notranslate=-*/

//		String name;

		String pk_operation;

		ErmBusiLogOperationType(String pk_operation, String name) {
			this.pk_operation = pk_operation;
//			this.name = name;
		}

		public String getPk_operation() {
			return pk_operation;
		}

//		public String getName() {
//			return name;
//		}
	}
	
	public static void insertSmartBusiLogs(boolean isUpdate, JKBXVO vo) {
			ErmBusiLogOperationType delType = null;//删除
			ErmBusiLogOperationType editType = null;//报销
			
			if(vo.getParentVO().getDjdl().equals(BXConstans.BX_DJDL)){//报销时的增改操作
				delType = ErmBusiLogOperationType.DELETE;
				editType = ErmBusiLogOperationType.EDIT;
			}else{//借款时的增改操作
				delType = ErmBusiLogOperationType.JKDELETE;
				editType = ErmBusiLogOperationType.JKEDIT;
			}
			
			String operationType = isUpdate ? editType.getPk_operation() :  delType.getPk_operation();
			BusiLogUtil.insertSmartBusiLog(operationType, new Object[]{vo}, new Object[]{vo.getBxoldvo()});
			
	}
}