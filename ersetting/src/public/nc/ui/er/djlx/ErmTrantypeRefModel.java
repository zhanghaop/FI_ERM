package nc.ui.er.djlx;

import nc.bs.logging.Logger;
import nc.itf.org.IOrgConst;
import nc.ui.bd.ref.AbstractRefModel;
import nc.util.fi.pub.SqlUtils;
import nc.vo.bd.ref.RefVO_mlang;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;

import org.apache.commons.lang.StringUtils;

/**
 * 费用交易类型参照
 * 
 * @author yuanjunc
 * 
 */
public class ErmTrantypeRefModel extends AbstractRefModel {
    // 要可以参照到封存的交易类型
    private String wherePart = " systemcode = 'erm' and pk_billtypecode like '26%' and istransaction = 'Y' and (pk_group='"
            + getPk_group() + "' or pk_org = '" + IOrgConst.GLOBEORG + "')";

    public ErmTrantypeRefModel() {
        super();
        setAddEnableStateWherePart(true);
    }

    /**
     * 是否显示停用的数据
     * 
     * @param isEnable
     * @return
     */
    @Override
    protected String getDisableDataWherePart(boolean isDisableDataShow) {
        if (isDisableDataShow) {
            return " islock = 'Y' or isnull(islock,'N') = 'N' ";
        } else {
            return " isnull(islock,'N') = 'N' ";
        }
    }

    /**
     * getDefaultFieldCount 方法注解。
     */
    @Override
    public int getDefaultFieldCount() {
        return getFieldCode().length;
    }

    /**
     * 参照数据库字段名数组
     * 
     * @return java.lang.String
     */
    @Override
    public java.lang.String[] getFieldCode() {
        return new String[] { "pk_billtypecode", "billtypename",
                "pk_billtypeid" };
    }

    /*
     * (non-Javadoc)
     * 
     * @see nc.ui.bd.ref.IRefModel#getRefCodeField()
     */
    @Override
    public String getRefCodeField() {
        return "pk_billtypecode";
    }

    /*
     * (non-Javadoc)
     * 
     * @see nc.ui.bd.ref.IRefModel#getRefNameField()
     */
    @Override
    public String getRefNameField() {
        return "billtypename";
    }

    /**
     * 和数据库字段名数组对应的中文名称数组
     * 
     * @return java.lang.String
     */
    @Override
    public java.lang.String[] getFieldName() {
        return new String[] {
                nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common",
                        "UCMD1-000172")/*
                                        * @res "交易类型编码"
                                        */,
                nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
                        "ersetting_0", "02011001-0023")/*
                                                        * @res "交易类型名称"
                                                        */
                ,
                nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common",
                        "UCMD1-000026") /*
                                         * @res "交易类型主键"
                                         */};
    }

    @Override
    public java.lang.String[] getHiddenFieldCode() {
        return new String[] { "pk_billtypeid" };
    }

    /**
     * 要返回的主键字段名
     * 
     * @return java.lang.String
     */
    @Override
    public String getPkFieldCode() {
        return "pk_billtypeid";
    }

    /**
     * 参照标题
     * 
     * @return java.lang.String
     */
    @Override
    public String getRefTitle() {
        return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
                "ersetting_0", "02011001-0024")/* @res "交易类型参照" */;
    }

    /**
     * 参照数据库表或者视图名
     * 
     * @return java.lang.String
     */
    @Override
    public String getTableName() {
        return " bd_billtype ";
    }

    /**
     * 此处插入方法说明。
     * 
     * @return java.lang.String
     */
    @Override
    public java.lang.String getWherePart() {
        if (StringUtil.isEmpty(super.getWherePart())) {
            return wherePart;
        }
        return wherePart + " and " + super.getWherePart();
    }
    
    public String getErmWherePart() {
        return wherePart;
    }
    
    public void setErmWherePart(String wherePart) {
        this.wherePart = wherePart;
    }
    
    public void setDjlx(String djlx) {
        if (StringUtils.isNotEmpty(djlx)) {
            String[] djlxArr = djlx.split(",");
            try {
                String billtypeWhere = SqlUtils.getInStr("parentbilltype", djlxArr);
                if (djlx.indexOf("264X") >= 0) {
                    billtypeWhere += " and pk_billtypecode <> '2647' and " + 
                    "pk_billtypecode in (select DJLXBM from er_djlx where (bxtype is null or bxtype != 2) and pk_group = '" + getPk_group() + "')";
                }
                if ("261X".equals(djlx)) {
                    billtypeWhere = billtypeWhere + " and pk_billtypecode in (select DJLXBM from er_djlx where djdl = 'ma' and matype = 1 and pk_group = '" + getPk_group() + "')";
                    setWherePart(billtypeWhere);
                } else if (djlx.indexOf("266X") >= 0) {
                    wherePart = " systemcode = 'erm' and pk_billtypecode like '26%' and (pk_group='"
                        + getPk_group() + "' or pk_org = '" + IOrgConst.GLOBEORG + "')";
                    String sWhere = " ((istransaction = 'Y' and " +  billtypeWhere + ") or (istransaction = 'N' and pk_billtypecode = '266X')) ";
                    setWherePart(sWhere);
                } else {
                    setWherePart(billtypeWhere);
                }
            } catch (BusinessException e) {
                Logger.error(e.getMessage(), e);
            }
        }
    }

    @Override
    protected RefVO_mlang[] getRefVO_mlang() {
        RefVO_mlang refVO_mlang = new RefVO_mlang();
        refVO_mlang.setDirName("billtype"); // 资源目录名.
        refVO_mlang.setFieldName("billtypename"); // 要翻译的列明
        refVO_mlang.setResIDFieldNames(new String[] { "pk_billtypecode" }); // 资源ID列名,多列就简单拼接.
        refVO_mlang.setPreStr("D"); // 资源ID前缀.

        return new RefVO_mlang[] { refVO_mlang };
    }
}