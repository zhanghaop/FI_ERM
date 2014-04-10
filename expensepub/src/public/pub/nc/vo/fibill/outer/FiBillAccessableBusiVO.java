package nc.vo.fibill.outer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import nc.itf.tb.control.IAccessableBusiVO;
import nc.itf.tb.sysmaintain.BdContrastCache;
import nc.pubitf.bd.accessor.GeneralAccessorFactory;
import nc.pubitf.bd.accessor.IGeneralAccessor;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.bd.accessor.IBDData;
import nc.vo.cmp.bdcontrastinfo.BdcontrastinfoVO;
import nc.vo.cmp.func.ARAPNTBBdContrastCache;
import nc.vo.tb.control.IdBdcontrastVO;


/**
 * 财务单据为预算控制交互vo接口实现的抽象类，提供部分方法的默认实现。
 * V5.5加入了控制日期选项，按预算的设计方案，
 * 在IAccessableBusiVO接口的getAttributesValue方法中要根据日期类型名称（IDateType实现类给出），返回对应的日期
 */
public abstract class FiBillAccessableBusiVO implements Serializable, IAccessableBusiVO
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private IAccessableBusiVO iAcc_tb = null;

	/**
	 * @roseuid 488596240148
	 */
	public FiBillAccessableBusiVO()
	{

	}

	/**
	 * 统一默认实现
	 * 用来查找下级的父类，包括自己本身
	 */
	public String[] getAllUpLevels(String fieldname, String pk) throws Exception {
		if(pk==null){
			return null;
		}
		String classTypeId  = null;
		/**通过PK_BDINFO获取classTypeId*/
		IdBdcontrastVO vo = BdContrastCache.getNewInstance().getVOByField("erm", fieldname);
		if(vo == null){
			return new String[]{pk};
		}
		classTypeId  = vo.getPk_bdinfo();
		IGeneralAccessor accessor = GeneralAccessorFactory.getAccessor(classTypeId);
		String pk_org = null;
		if(getIAcc_tb()!=null){
			pk_org = getIAcc_tb().getPKOrg();
		}else{
			pk_org = getPkOrgInBillHead();
		}
//		if(!(pk.indexOf("|")>=0)&&!pk.equals(OutEnum.NOSUCHBASEPKATSUBCORP)){
		if(pk_org==null)
			pk_org="";
		List<IBDData> fatherDocs = accessor.getFatherDocs(pk_org, pk, true);

		if (fatherDocs == null) {
			return new String[] { pk };
		} else {
			String[] bdpks = new String[fatherDocs.size()];
			for (int i = 0; i < bdpks.length; i++) {
				bdpks[i] = fatherDocs.get(i).getPk();
			}
			return bdpks;
		}
	}

	public String getBusiSys() {
		
		return BXConstans.ERM_PRODUCT_CODE_Lower;
	}

	public String getBusiType() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2004","UPP2004-000074")/*@res "发生额"*/;
	}

	public String[] getHasLevelFlds() {
		if(getIAcc_tb()!=null){
			return getIAcc_tb().getHasLevelFlds();
		}
		BdcontrastinfoVO[] bdinfos =  ARAPNTBBdContrastCache.getAllBdContrastvos();
		if(bdinfos!=null){
			ArrayList<String> al = new ArrayList<String>();
			for(int i=0;i<bdinfos.length;i++){
				if(bdinfos[i].IsLevelControl().booleanValue()){
					al.add(bdinfos[i].getAtt_fld());
				}
			}
			if(al.size()>0){
				String [] flds = new String[al.size()];
				flds = (String[])al.toArray(flds);
				return flds;
			}
		}
		return null;
	}

	public abstract String getPkOrgInBillHead();

	public IAccessableBusiVO getIAcc_tb() {
		return iAcc_tb;
	}

	public void setIAcc_tb(IAccessableBusiVO acc_tb) {
		iAcc_tb = acc_tb;
	}

}