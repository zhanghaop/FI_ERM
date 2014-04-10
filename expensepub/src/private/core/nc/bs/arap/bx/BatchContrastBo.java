package nc.bs.arap.bx;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import nc.bs.logging.Log;
import nc.itf.fi.pub.Currency;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.arap.pub.VoComparer;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.BatchContratParam;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.er.util.ArapCommonTool;
import nc.vo.er.util.UFDoubleTool;
import nc.vo.erm.util.VOUtils;
import nc.vo.jcom.util.SortUtils;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;

public class BatchContrastBo {

	public static String getAllkeys(JKBXHeaderVO vo, String[] keys) {
		if (vo == null) {
			return null;
		}

		String allKeys = "";

		for (int j = 0; j < keys.length; j++) {
			Object obj = vo.getAttributeValue(keys[j]);
			if (obj != null) {
				allKeys += obj;
			}
		}
		return allKeys;
	}

	public List<BxcontrastVO> batchContrast(JKBXHeaderVO[] bxds, JKBXHeaderVO[] jkds, String[] keys, BatchContratParam param) throws BusinessException {

		List<BxcontrastVO> results=new ArrayList<BxcontrastVO>();

		if(jkds==null || bxds==null || jkds.length==0 || bxds.length==0)
			return results;

		for (JKBXHeaderVO vo:jkds) {
			vo.setJsybye(vo.getYjye());
		}

	    for (JKBXHeaderVO vo:bxds) {
	    	vo.setJsybye(vo.getZfybje());
		}

		sortVector(jkds, 0 , new String []{"djrq"});
		sortVector(bxds, 0, new String []{"djrq"});

		Hashtable<String,Vector<Vector<JKBXHeaderVO>>> voHash=new Hashtable<String, Vector<Vector<JKBXHeaderVO>>>();

	    dealJkdToHash(jkds, keys, voHash,0);

	    dealJkdToHash(bxds, keys, voHash,1);



	    Iterator it=voHash.keySet().iterator();
	    while(it.hasNext())
	    {
    		Vector vec=voHash.get(it.next());
    	    Vector bxdVector=(Vector)vec.get(1);
    	    Vector jkdVector=(Vector)vec.get(0);
    		int k=0;
			for(int i =0; i< bxdVector.size();i++){
				JKBXHeaderVO vo =(JKBXHeaderVO)bxdVector.elementAt(i);
				if(ArapCommonTool.isZero(vo.getJsybye()))
					continue;
				for( int j = k;j < jkdVector.size();j++){
					JKBXHeaderVO vo2 = (JKBXHeaderVO)jkdVector.elementAt(j);
					if(ArapCommonTool.isZero(vo2.getJsybye()))
						continue;

					BxcontrastVO bxcontrastVO = doContrast(vo, vo2,param);
					if(bxcontrastVO!=null){
						results.add(bxcontrastVO);
					}

					if(ArapCommonTool.isZero(vo.getJsybye())){
						k=j;
						break;
					}
				}
			}
    	}

	    return results;
	}

	/**
	 * @param bxd
	 * @param jkd
	 * @param param
	 * @return
	 * @throws BusinessException
	 */
	public BxcontrastVO doContrast(JKBXHeaderVO bxd, JKBXHeaderVO jkd, BatchContratParam param) throws BusinessException {

		if(bxd == null || jkd == null){
			return null;
		}
		UFDouble bxjsybye = bxd.getJsybye();
		UFDouble jkjsybye = jkd.getJsybye();

		//����ԭ�ҽ��
		UFDouble clybje =null;

		if(UFDoubleTool.isAbsDayu(bxjsybye, jkjsybye)){
			clybje = jkjsybye;
		}else{
			clybje = bxjsybye;
		}

		bxd.setJsybye(bxd.getJsybye().sub(clybje));
		jkd.setJsybye(jkd.getJsybye().sub(clybje));

		//�����ҽ��
		UFDouble bbje = bb_jisuan(bxd,jkd,clybje);

		BxcontrastVO bxcontrastVO = new BxcontrastVO();
		bxcontrastVO.setYbje(clybje);
		bxcontrastVO.setBbje(bbje);
		bxcontrastVO.setCjkybje(clybje);
		bxcontrastVO.setCjkbbje(bbje);
		bxcontrastVO.setCxrq(param.getCxrq());
		bxcontrastVO.setDeptid(jkd.getDeptid());
		bxcontrastVO.setDjlxbm(jkd.getDjlxbm());
		bxcontrastVO.setJkbxr(jkd.getJkbxr());
		bxcontrastVO.setJobid(jkd.getJobid());
		bxcontrastVO.setPk_bxd(bxd.getPk_jkbx());
		bxcontrastVO.setPk_org(jkd.getPk_org());
		bxcontrastVO.setPk_jkd(jkd.getPk_jkbx());
		bxcontrastVO.setSxbz(BXStatusConst.SXBZ_NO);
		bxcontrastVO.setSxrq(null);
		bxcontrastVO.setSzxmid(jkd.getSzxmid());
		bxcontrastVO.setJkdjbh(jkd.getDjbh());
		bxcontrastVO.setBxdjbh(bxd.getDjbh());
		bxcontrastVO.setFybbje(bbje);
		bxcontrastVO.setFyybje(clybje);
		bxcontrastVO.setTs(jkd.getTs());
		

		return bxcontrastVO;
	}

	/**
	 *
	 * ���б������ļ���
	 *
	 * //�������,����,֧���ĺͱ���, ȡ������, ����ȡ������
	 * @param clybje
	 * @throws BusinessException
	 */
	private UFDouble bb_jisuan(JKBXHeaderVO bxd,JKBXHeaderVO jkd, UFDouble clybje) throws BusinessException {
		if(bxd == null || jkd == null){
			return null;
		}
		String pk_currtype = jkd.getBzbm();
		UFDate date = jkd.getDjrq();
		try
		{
			UFDouble bbhl=null;

			String jkdpk_corp = jkd.getPk_group();
			String bxdpk_corp = bxd.getPk_group();
			//ֻ�д�������			
			UFDouble rateBoth = Currency.getRate(jkdpk_corp,pk_currtype,date);
			bbhl= rateBoth;

			String bbpk = Currency.getOrgLocalCurrPK(jkdpk_corp);

			String bbpk2 = Currency.getOrgLocalCurrPK(bxdpk_corp);

			if(!VOUtils.simpleEquals(bbpk2, bbpk)){
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000382")/*@res "���ŵ������ڹ�˾�ı�λ�Ҳ�ͬ�����ܽ������������������ѡ�����ϸ��¼�빫˾����"*/);
			}

//			�跽���㱾�ҽ��
			UFDouble jsbbje = BXConstans.DOUBLE_ZERO;

			try {
			    jsbbje = Currency.getAmountByOpp(jkdpk_corp,pk_currtype, bbpk, clybje, bbhl, date);

			} catch (Exception e) {
				Log.getInstance(this.getClass()).debug(e.getMessage());
			}

			return jsbbje;
		}
		catch(Exception e)
		{
			throw new BusinessException(e.getMessage(),e);
		}

	}

	private void dealJkdToHash(JKBXHeaderVO[] jkds, String[] keys, Hashtable<String, Vector<Vector<JKBXHeaderVO>>> voHash,int index) {
		for(int i = 0; i < jkds.length; i++){
			String allkeys = getAllkeys(jkds[i], keys);

			if(voHash.containsKey(allkeys))
			{
				voHash.get(allkeys).get(index).add(jkds[i]);
			}else{
				Vector<Vector<JKBXHeaderVO>> vec=new Vector<Vector<JKBXHeaderVO>>();
				vec.add(new Vector<JKBXHeaderVO>());
				vec.add(new Vector<JKBXHeaderVO>());
				voHash.put(allkeys, vec);
				voHash.get(allkeys).get(index).add(jkds[i]);
			}
		}
	}

	public static void sortVector(JKBXHeaderVO [] vos, int ascflag,String[] keys) {
		if (vos == null || vos.length < 2) {
			return;
		}
		VoComparer comp = new VoComparer();
		comp.setAscend(ascflag == 0);
		comp.setCompareKey(keys);
		comp.setDaterange(0);
		SortUtils.sort(vos, null, comp);
	}
}