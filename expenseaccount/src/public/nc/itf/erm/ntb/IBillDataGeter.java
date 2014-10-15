package nc.itf.erm.ntb;

/**
 * <p>
 * TODO 增加报销取单据大类，表体方向接口，报销单据实现
 * </p>
 *
 * 修改记录：<br>
 * <li>修改人：修改日期：修改内容：</li>
 * <br><br>
 *
 * @see 
 * @author liansg
 * @version V6.0
 * @since V6.0 创建时间：2011-3-15 下午04:08:06
 */
public interface IBillDataGeter
{

    public abstract String getDjdl();

    public abstract Integer getItemFx();

    public abstract String getItem_bill_pk();

    public abstract String getDdlx();
}