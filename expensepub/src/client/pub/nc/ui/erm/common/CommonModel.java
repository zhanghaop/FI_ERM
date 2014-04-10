package nc.ui.erm.common;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.SuperVO;

/**
 * @author twei
 *
 * nc.ui.arap.common.CommonModel
 * 
 * ʵ�ּ򵥵Ĺ������
 * 1. ����VO		,�̳�CommonSuperVO    						@see LoanControlVO
 * 2. ʵ�ֿ�Ƭ����, ��Ҫʵ�ַ��� setVo, getVo        			@see LoanControlCard
 * 3. ʵ���б����, ��Ҫʵ�ַ��� getHeader, getHeaderColumns     @see LoanControlList
 * 4. ʵ�ֹ�����棬 ���ÿ�Ƭ�б����      						@see LoanControlMailPanel
 * 
 * @see CommonSuperVO
 * @see CommonCard
 * @see CommonList
 * @see CommonModel
 * @see CommonModelListener
 * @see CommonUI
 */
public class CommonModel {
	
	public static final int STATUS_ADD = 0;
	public static final int STATUS_MOD = 1;
	public static final int STATUS_LIST = 2;
	public static final int STATUS_CARD = 3;
	
	private CommonModelListener modeListener ;
	
	private List<SuperVO> vos;
	
	private SuperVO selectedvo;
	
	private int status;
	
	
	
	public CommonModelListener getModeListener() {
		return modeListener;
	}
	public void setModeListener(CommonModelListener modeListener) {
		this.modeListener = modeListener;
	}
	public int getStatus() {
		return status;
	}
	public SuperVO getSelectedvo() {
		return selectedvo;
	}
	public Collection<SuperVO> getVos() {
		return vos;
	}
	
	
	public void setStatus(int status) {
		
		int oldStatus = this.status;
		this.status = status;
		try{
			modeListener.updateStatus();;
		}catch (Exception e) {
			this.status = oldStatus;
			throw new BusinessRuntimeException(e.getMessage(),e);
		}
	}

	public void setSelectedvo(SuperVO selectedvo) {
		this.selectedvo = selectedvo;
	}
	
	public void setVos(List<SuperVO> vos) {
		this.vos = vos;
		
		modeListener.updateVos();
	}
	
	
	public void init(){
		this.vos=null;
		this.selectedvo=null;
		setStatus(STATUS_LIST);
	}
	
	public void updateVO(SuperVO vo) {
		for(int i=0;i<vos.size();i++){
			SuperVO oldVO=vos.get(i);
			if(oldVO.getPrimaryKey().equals(vo.getPrimaryKey())){
				vos.set(i, vo);
				break;
			}
		}
	}
	
	public void setSelectedvopk(String selectVoPk) {
		if(selectVoPk==null){
			setSelectedvo(null) ;
			return;
		}
		for (Iterator<SuperVO> iter = vos.iterator(); iter.hasNext();) {
			SuperVO vo = iter.next();
			if(vo.getPrimaryKey().equals(selectVoPk)){
				setSelectedvo(vo);
				break;
			}
		}
	}
	
	
	
}
