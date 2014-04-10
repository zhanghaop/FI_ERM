package nc.ui.erm.view;

import java.util.ArrayList;
import java.util.List;

import nc.ui.pub.bill.IBillItem;
import nc.ui.uif2.editor.UserdefQueryParam;
import nc.ui.uif2.userdefitem.QueryParam;
import nc.vo.pub.bill.BillStructVO;
import nc.vo.pub.bill.BillTabVO;
import nc.vo.pub.bill.BillTempletVO;

import org.apache.commons.lang.ArrayUtils;

public class UserdefitemContainerUtil {
	
	/**
	 * ͨ������ģ��VOȡ���Զ�����²���vo�����ڸ����û���������
	 * 
	 * @param billTempletVO
	 * @param prefix 
	 * @return
	 * @author: wangyhh@ufida.com.cn
	 */
	public static List<UserdefQueryParam> getUserdefQueryParams(BillTempletVO billTempletVO, String prefix) {
		List<UserdefQueryParam> params = new ArrayList<UserdefQueryParam>();
		
		if(billTempletVO == null || billTempletVO.getHeadVO() == null || billTempletVO.getHeadVO().getStructvo() == null){
			return params;
		}
		
		// �õ����е���ǩ
		BillTabVO[] billTabVOs = billTempletVO.getHeadVO().getStructvo().getBillTabVOs();
		
		if (!ArrayUtils.isEmpty(billTabVOs)) {
			for (BillTabVO singleVO : billTabVOs) {
				params.add(getTabUserdefQueryParam(singleVO,prefix));
			}
		}
		return params;
	}

	/**
	 * ͨ������ģ��VOȡ���Զ����ѯ����vo���û��������������õ�
	 * 
	 * @param billTempletVO
	 * @return
	 * @author: wangyhh@ufida.com.cn
	 */
	public static List<QueryParam> getQueryParams(BillTempletVO billTempletVO) {
		List<QueryParam> params = new ArrayList<QueryParam>();
		// �õ����е���ǩ
		if (billTempletVO != null && billTempletVO.getHeadVO() != null) {
			BillStructVO strucvo = billTempletVO.getHeadVO().getStructvo();
			if (strucvo != null) {
				BillTabVO[] billTabVOs = strucvo.getBillTabVOs();
				if (!ArrayUtils.isEmpty(billTabVOs)) {
					for (BillTabVO singleVO : billTabVOs) {
						params.add(getTabQueryParams(singleVO));
					}
				}
			}
		}
		return params;
	}
	
	/**
	 * ͨ��BILLTABVOȡ���Զ�����²���vo�����ڸ����û���������
	 * 
	 * @param billTabVO(����Ϊ��)
	 * @param prefix 
	 * @return
	 */
	public static UserdefQueryParam getTabUserdefQueryParam(BillTabVO billTabVO,String prefix) {
		UserdefQueryParam queryParam = new UserdefQueryParam();
		queryParam.setMdfullname(billTabVO.getMetadataclass());
		Integer pos = billTabVO.getPos();
		queryParam.setPos(pos);
		
		//���ݽ�����
		if("zyx".equals(prefix) && pos==IBillItem.BODY){
			queryParam.setPrefix("defitem");
		}else{
			queryParam.setPrefix(prefix);
		}
		queryParam.setTabcode(billTabVO.getTabcode());
		return queryParam;
	}
	
	/**
	 * ͨ��BILLTABVOȡ���Զ����ѯ����vo���û��������������õ�
	 * 
	 * @param billTabVO ���ɿ�
	 * @return
	 */
	public static QueryParam getTabQueryParams(BillTabVO billTabVO) {
		QueryParam queryParam = new QueryParam();
		queryParam.setMdfullname(billTabVO.getMetadataclass());
		return queryParam;
	}
}
