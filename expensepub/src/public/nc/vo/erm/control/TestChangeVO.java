package nc.vo.erm.control;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import nc.bs.logging.Log;
import nc.vo.tb.obj.NtbParamVO;


/**
 * 此处插入类型描述。
 * 创建日期：(2004-3-23 15:00:43)
 * @author：钟悦
 */
public class TestChangeVO {
    public static String Spliter = "###";
/**
 * TestChangeVO 构造子注解。
 */
public TestChangeVO() {
	super();
}
//以下元素完全一致才可以合并
///*公司编码，辅助字段，如果已经提供m_pk_corp_db，可以不提供该信息*/
//private String m_pk_corp = null;	
////*起始日期,对于只提供单个时间点,进行周期匹配的,将时间点放置在此字段,并且要保证m_enddate为null*/
//private String m_begdate = null;
////*终止日期*/
//private String m_enddate = null;
////*业务系统标识,必须由各个业务系统自己提供注册的SQL语句和相应的接口VO,如:ARAP*/
//private String m_sys_id = null;
////*方向,主要是提供给总帐系统使用,取值:借,贷,必须和各个业务系统注册的VO中提供的业务类型进行匹配*/
//private String m_direction = null;
////*对应的VO属性，对应基础档案类型的VO属性值"*/
//private String[] m_busiAttrs = null;

//日期类型djrq、shrq。V5.5新增需求

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
        Log.getInstance(this.getClass()).debug("####:错误！生成QueryVO[]时出现错误。"); /*-=notranslate=-*/
        return null;
    }
    vect.copyInto(qvos);
    return qvos;
}
//判断两个数组的内容不记元素顺序的情况下是否完全一致,
public boolean isAllSame(String[] busiAttrs, String[] busiAttrs2) {

    HashSet<String> set = new HashSet<String>();
    List<String> list = Arrays.asList(busiAttrs);
    set.addAll(list);

    HashSet<String> set2 = new HashSet<String>();
    list = Arrays.asList(busiAttrs2);
    set2.addAll(list);

    return set.equals(set2);
}
//以下元素完全一致才可以合并
///*公司编码，辅助字段，如果已经提供m_pk_corp_db，可以不提供该信息*/
//private String m_pk_corp = null;	
////*起始日期,对于只提供单个时间点,进行周期匹配的,将时间点放置在此字段,并且要保证m_enddate为null*/
//private String m_begdate = null;
////*终止日期*/
//private String m_enddate = null;
////*业务系统标识,必须由各个业务系统自己提供注册的SQL语句和相应的接口VO,如:ARAP*/
//private String m_sys_id = null;
////*方向,主要是提供给总帐系统使用,取值:借,贷,必须和各个业务系统注册的VO中提供的业务类型进行匹配*/
//private String m_direction = null;
////*对应的VO属性，对应基础档案类型的VO属性值"*/
//private String[] m_busiAttrs = null;

public boolean isEnableMerge(
    NtbParamVO ntbvo,
    NtbParamVO ntbvo2) {/*以2为基准*/
//    String pkCorp = ntbvo.getPkcorp();
//    String pkCorp2 = ntbvo2.getPkcorp();
//    if (!pkCorp.equals(pkCorp2)) {
//        return false;
//    }
//	if (!ntbvo.getPk_currency().equals(ntbvo2.getPk_currency())) {
//        return false;
//    }
	//V5.5新增日期类型
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
    //Add by Songtao 2005-04-11单据类型不相同不能合并
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
/**重新排列ntbvo中的属性以及属性的取值和是否包含下级关系，此方法只能在已经判断了两个vo中的属性相同的情况下才能使用
 * 结果会使两个vo中的属性按照相同的顺序排列。
 * */
private void resortAttrs(NtbParamVO ntbvo,NtbParamVO ntbvo2){/*以2为基准*/
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
/**Update by Songtao ,不能返回NY的数组，因为在比较isAllSame时3个以上会出现判断错误，
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

/**得到ntbvo中非busiattri中的属性信息，并且将其整理为一个
 * 用标记分开的String。
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
    for(int i=0  ;i<sAttValues.length ;i++){// 0 变到1 ， 因为group字段少了fkdwbm
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
