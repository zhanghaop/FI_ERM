package nc.vo.erm.control;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import nc.bs.logging.Log;
import nc.vo.tb.obj.NtbParamVO;


/**
 * �˴���������������
 * �������ڣ�(2004-3-23 15:00:43)
 * @author������
 */
public class TestChangeVO {
    public static String Spliter = "###";
/**
 * TestChangeVO ������ע�⡣
 */
public TestChangeVO() {
	super();
}
//����Ԫ����ȫһ�²ſ��Ժϲ�
///*��˾���룬�����ֶΣ�����Ѿ��ṩm_pk_corp_db�����Բ��ṩ����Ϣ*/
//private String m_pk_corp = null;	
////*��ʼ����,����ֻ�ṩ����ʱ���,��������ƥ���,��ʱ�������ڴ��ֶ�,����Ҫ��֤m_enddateΪnull*/
//private String m_begdate = null;
////*��ֹ����*/
//private String m_enddate = null;
////*ҵ��ϵͳ��ʶ,�����ɸ���ҵ��ϵͳ�Լ��ṩע���SQL������Ӧ�Ľӿ�VO,��:ARAP*/
//private String m_sys_id = null;
////*����,��Ҫ���ṩ������ϵͳʹ��,ȡֵ:��,��,����͸���ҵ��ϵͳע���VO���ṩ��ҵ�����ͽ���ƥ��*/
//private String m_direction = null;
////*��Ӧ��VO���ԣ���Ӧ�����������͵�VO����ֵ"*/
//private String[] m_busiAttrs = null;

//��������djrq��shrq��V5.5��������

public QueryVO[] changeToQueryVO(NtbParamVO[] ntbvos) {
    Vector<QueryVO> vect = new Vector<QueryVO>();
    for (int i = 0; i < ntbvos.length; i++) {
        boolean isAdded = false;
        for (int j = 0; j < vect.size(); j++) {
            QueryVO qvo = (QueryVO) vect.get(j);
            NtbParamVO ntbvo =
                ((NtbParamVO[]) qvo
                    .getSourceArr()
                    .toArray(new NtbParamVO[0]))[0];
            if (isEnableMerge(ntbvos[i], ntbvo)) {
                qvo.addSource(ntbvos[i]);
                isAdded = true;
                break;
            }
        }
        if (!isAdded) {
            QueryVO qvoNew = new QueryVO();
            qvoNew.addSource(ntbvos[i]);
            vect.add(qvoNew);
        }
    }

    QueryVO[] qvos = new QueryVO[vect.size()];
    if (vect.size() <= 0) {
        Log.getInstance(this.getClass()).debug("####:��������QueryVO[]ʱ���ִ���"); /*-=notranslate=-*/
        return null;
    }
    vect.copyInto(qvos);
    return qvos;
}
//�ж�������������ݲ���Ԫ��˳���������Ƿ���ȫһ��,
public boolean isAllSame(String[] busiAttrs, String[] busiAttrs2) {

    HashSet<String> set = new HashSet<String>();
    List<String> list = Arrays.asList(busiAttrs);
    set.addAll(list);

    HashSet<String> set2 = new HashSet<String>();
    list = Arrays.asList(busiAttrs2);
    set2.addAll(list);

    return set.equals(set2);
}
//����Ԫ����ȫһ�²ſ��Ժϲ�
///*��˾���룬�����ֶΣ�����Ѿ��ṩm_pk_corp_db�����Բ��ṩ����Ϣ*/
//private String m_pk_corp = null;	
////*��ʼ����,����ֻ�ṩ����ʱ���,��������ƥ���,��ʱ�������ڴ��ֶ�,����Ҫ��֤m_enddateΪnull*/
//private String m_begdate = null;
////*��ֹ����*/
//private String m_enddate = null;
////*ҵ��ϵͳ��ʶ,�����ɸ���ҵ��ϵͳ�Լ��ṩע���SQL������Ӧ�Ľӿ�VO,��:ARAP*/
//private String m_sys_id = null;
////*����,��Ҫ���ṩ������ϵͳʹ��,ȡֵ:��,��,����͸���ҵ��ϵͳע���VO���ṩ��ҵ�����ͽ���ƥ��*/
//private String m_direction = null;
////*��Ӧ��VO���ԣ���Ӧ�����������͵�VO����ֵ"*/
//private String[] m_busiAttrs = null;

public boolean isEnableMerge(
    NtbParamVO ntbvo,
    NtbParamVO ntbvo2) {/*��2Ϊ��׼*/
//    String pkCorp = ntbvo.getPkcorp();
//    String pkCorp2 = ntbvo2.getPkcorp();
//    if (!pkCorp.equals(pkCorp2)) {
//        return false;
//    }
//	if (!ntbvo.getPk_currency().equals(ntbvo2.getPk_currency())) {
//        return false;
//    }
	//V5.5������������
	if (!ntbvo.getDateType().equals(ntbvo2.getDateType())) {
        return false;
    }
	
    if (!ntbvo.getBegDate().equals(ntbvo2.getBegDate())) {
        return false;
    }
    if (!ntbvo.getEndDate().equals(ntbvo2.getEndDate())) {
        return false;
    }
    if (!ntbvo.getSys_id().equals(ntbvo2.getSys_id())) {
        return false;
    }
    if (!ntbvo.getDirection().equals(ntbvo2.getDirection())) {
        return false;
    }
    if(!(ntbvo.isUnInure()==ntbvo2.isUnInure())){
    	return false;
    }
    String[] busiAttrs = ntbvo.getBusiAttrs();
    String[] busiAttrs2 = ntbvo2.getBusiAttrs();
    if(busiAttrs.length!=busiAttrs2.length || !isAllSame(busiAttrs, busiAttrs2)){
        return false;
    }
    //Add by Songtao 2005-04-11�������Ͳ���ͬ���ܺϲ�
    TokenTools token = new TokenTools(
            ntbvo.getBill_type(), ",", false);
    String[] billTypes = token.getStringArray();
    TokenTools token2 = new TokenTools(
            ntbvo2.getBill_type(), ",", false);
    String[] billTypes2 = token2.getStringArray();
    if(billTypes.length!=billTypes2.length || !isAllSame(billTypes, billTypes2)){
        return false;
    }
    //end add
    
    resortAttrs(ntbvo,ntbvo2);
    boolean[] bhxj=ntbvo.getIncludelower();
    boolean[] bhxj2=ntbvo2.getIncludelower();
   
    
    String[] s_bhxj=bhxj(bhxj);
    String[] s_bhxj2=bhxj(bhxj2);
     return isAllSame(s_bhxj, s_bhxj2);
}
/**��������ntbvo�е������Լ����Ե�ȡֵ���Ƿ�����¼���ϵ���˷���ֻ�����Ѿ��ж�������vo�е�������ͬ������²���ʹ��
 * �����ʹ����vo�е����԰�����ͬ��˳�����С�
 * */
private void resortAttrs(NtbParamVO ntbvo,NtbParamVO ntbvo2){/*��2Ϊ��׼*/
    String[] busiAttrs = ntbvo.getBusiAttrs();
    String[] busiAttrs2 = ntbvo2.getBusiAttrs();    
    int[] indexs = getIndexs(busiAttrs,busiAttrs2);
    if(indexs==null){
        return;
    }
    String[] pk_dim= ntbvo.getPkDim();
    boolean[] bhxj=ntbvo.getIncludelower();
    boolean[] newbhxj = new boolean[bhxj.length];
    String[] newpk_dim = new String[busiAttrs.length];
    for(int i=0;i<indexs.length;i++){
        newbhxj[i]=bhxj[indexs[i]];
        newpk_dim[i]=pk_dim[indexs[i]];
    }
    ntbvo.setIncludelower(newbhxj);
    ntbvo.setPkDim(newpk_dim);
    ntbvo.setBusiAttrs(busiAttrs2);
    
}
private int[] getIndexs(String[] busiAttrs,String[] busiAttrs2 ){
    if(busiAttrs==null || busiAttrs.length==0)
        return null;
    int[] indexs=new int[busiAttrs.length];
    String tempAttr = null;
    for(int i=0;i<busiAttrs.length;i++){
        tempAttr = busiAttrs[i];
        for(int j=0;j<busiAttrs2.length;j++){
            if(tempAttr.equalsIgnoreCase(busiAttrs2[j])){
                indexs[i]=j;
                break;
            }
        }
    }
    return indexs;
}
/**Update by Songtao ,���ܷ���NY�����飬��Ϊ�ڱȽ�isAllSameʱ3�����ϻ�����жϴ���
 * */
private String[] bhxj(boolean[] bhxj){
	if(bhxj==null){
		return null;
	}
    String[] s_bhxj=new String[1];
    s_bhxj[0]="";
    for(int i=0; i<bhxj.length; i++){
    	if(bhxj[i]){
    		s_bhxj[0]+="Y";
    	}else{
    		s_bhxj[0]+="N";
    	}
    }
    return s_bhxj;
}

/**�õ�ntbvo�з�busiattri�е�������Ϣ�����ҽ�������Ϊһ��
 * �ñ�Ƿֿ���String��
 * */
public static String getAppendKey(NtbParamVO vo){
    String sResult = "";
//    sResult += vo.getPkcorp()+Spliter;
    sResult += vo.getBegDate()+Spliter;
    sResult += vo.getEndDate()+Spliter;
//    sResult += vo.getSys_id()+Spliter;
    sResult += vo.getDirection()+Spliter;
    sResult +=vo.isUnInure()+Spliter;
    return sResult;
}
public static String getAllNtbKey(NtbParamVO vo){
	String sResult = "";//vo.getPk_Org()+Spliter;
    String[] sAttValues = vo.getPkDim();    
    for(int i=0  ;i<sAttValues.length ;i++){// 0 �䵽1 �� ��Ϊgroup�ֶ�����fkdwbm
        sResult+= sAttValues[i]+Spliter;
    }
    sResult+=getBhxjStr(vo);
    sResult+=Spliter;
    sResult += getAppendKey(vo);
    return sResult;
}
public static String getBhxjStr(NtbParamVO ntbvo){
	String ret="N";
	if(ntbvo==null){
		return "";
	}
	boolean[] bhxj=ntbvo.getIncludelower();
	if(bhxj==null||bhxj.length==0){
		return "";
	}
	for(int i=0; i<bhxj.length; i++){
		if(bhxj[i]){
			ret+="Y";
		}	else{
			ret+="N";
		}
	}
	return ret;
}
}
