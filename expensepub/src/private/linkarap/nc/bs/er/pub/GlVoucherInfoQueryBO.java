/*
 * �������� 2004-12-24
 *
 * TODO Ҫ���Ĵ����ɵ��ļ���ģ�壬��ת��
 * ���� �� ��ѡ�� �� Java �� ������ʽ �� ����ģ��
 */
package nc.bs.er.pub;

import nc.bs.logging.Log;

/**
 * @author st
 *
 * TODO Ҫ���Ĵ����ɵ�����ע�͵�ģ�壬��ת��
 * ���� �� ��ѡ�� �� Java �� ������ʽ �� ����ģ��
 */
public class GlVoucherInfoQueryBO  {

	/**
	 *
	 */
	public GlVoucherInfoQueryBO() {
		super();
	}

	/**
	 * ���ݵ��ݵ�������ѯ�õ���Ӧ�ĵ������ɵ�ƾ֤����Ϣ
	 * ����hash�ṹ�Ե��ݵ�����Ϊkey,ƾ֤��ϢvoΪ����
	 * */
	//FIXME 0426��ʱע��
//	public HashMap<String,GlVoucherInfoVO> getGlVoucherInfoBySourcePks(String[] pks) throws BusinessException{
//		HashMap<String,GlVoucherInfoVO> hashResult = null;
//		if(pks==null || pks.length==0){
//			return hashResult;
//		}
//		/*����dmo������ѯ����ƾ֤��Ϣ*/
//		try{
//		    BillQueryVoucherVO[] voConds = new BillQueryVoucherVO[pks.length];
//		    for(int i=0;i<pks.length;i++){
//		        voConds[i] = new BillQueryVoucherVO();
//		        voConds[i].setPk_bill(pks[i]);
//		        voConds[i].setDestSystem(IAccountPlat.DESTSYS_GL);
//		    }
//		    IDapQueryMessage dapbo = NCLocator.getInstance().lookup(IDapQueryMessage.class);
//		    BillQueryVoucherVO[] voResults = dapbo.queryAllVouchers(voConds);
//		    hashResult = new HashMap<String,GlVoucherInfoVO>();
//		    if(voResults==null){
//		        return hashResult;
//		    }
//		    Object otemp = null;
//		    GlVoucherInfoVO tempvo = null;
//		    boolean isGlBookused = isEnableGLBook();
//		    for(int i=0;i<voResults.length;i++){
//		        if(voResults[i]==null || voResults[i].getVoucherNo()==null){
//		            continue;
//		        }
//		        otemp=hashResult.get(voResults[i].getPk_bill());
//		        if(otemp==null){
//		            tempvo = new GlVoucherInfoVO();
//		            tempvo.setIsAduited(new Boolean(false));
//		        }else{
//		            tempvo = (GlVoucherInfoVO)otemp;
//		        }
//		        tempvo.setGlVoucherid(voResults[i].getPk_voucher());
//		        if(voResults[i].getVoucherTallyDate()!=null){
//		            tempvo.setIsAduited(new Boolean(true));
//		        }
//		        if(tempvo.getVoucherno()!=null){
//		            tempvo.setVoucherno(tempvo.getVoucherno()+",");
//		        }else{
//		            tempvo.setVoucherno("");
//		        }
//		        if(voResults[i].getVouchertype_name()==null){
//		            voResults[i].setVouchertype_name("");
//		        }
//		        if(isGlBookused){
//		            tempvo.setVoucherno(tempvo.getVoucherno()+voResults[i].getGl_org_name()+"+"+voResults[i].getGl_book_name()+"+");
//		        }
//		        tempvo.setVoucherno(tempvo.getVoucherno()+voResults[i].getVouchertype_name()+" "+voResults[i].getVoucherNo());
//		        tempvo.setJzrq(voResults[i].getVoucherTallyDate());
//		        hashResult.put(voResults[i].getPk_bill(),tempvo);
//		    }
//		}catch(Exception e){
//			Log.getInstance(this.getClass()).error(e.getMessage());
//			throw new BusinessShowException(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("20060504","UPP20060504-000071")/*@res "��ѯƾ֤��Ϣ����"*/,e);
//		}
//		return hashResult;
//	}
	private boolean isEnableGLBook(){
		boolean b =false;
		try {
			//FIXME
//			b= GLOrgBookAcc.isEnableGLBook();
			if(b){
				b=false;
				//FIXME
//				String pk_corp=InvocationInfoProxy.getInstance().get().getCorpCode();
//				GlorgbookVO[] vos = GLOrgBookAcc.getGLOrgBookVOsByPk_Corp2(pk_corp);
//				String pk_corp=null;
//				if(vos!=null && vos.length>1){
//					b=true;
//				}
			}

		} catch (Exception e) {
			Log.getInstance(this.getClass()).error(e.getMessage());
		}
		Log.getInstance(this.getClass()).debug(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0093")/*@res "++++++++�Ƿ����ö��˲�Ϊ:"*/+b);
		return b;
	}



}