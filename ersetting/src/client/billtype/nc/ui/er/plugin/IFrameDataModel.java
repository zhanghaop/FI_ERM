package nc.ui.er.plugin;

import nc.ui.pub.beans.ValueChangedListener;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;

/**数据模型，用于MainFrame中数据管理*/
public interface IFrameDataModel {
/**添加数据变化监听器*/
public abstract void addDataChangeListener(ValueChangedListener vl);
/**得到选中的数据*/
public abstract AggregatedValueObject[] getSelectedDatas() throws BusinessException;
/**得到当前数据*/
public abstract AggregatedValueObject getCurrData()throws BusinessException;
/**初始化数据*/
public abstract void initData()throws BusinessException;
/**将数据添加数据到缓存中,如果缓存中已经存在数据则更新数据，否则追加
 * 此方法会触发数据改变事件
 * */
public abstract void setData(AggregatedValueObject vo)throws BusinessException;
/**清除缓存，并且将数据更新到缓存中
 * 此方法会触发数据改变事件
 * */
public abstract void setDatas(AggregatedValueObject[] vo)throws BusinessException;

/**取缓存中所有数据
 * 此方法会触发数据改变事件
 * */
public abstract AggregatedValueObject[] getAllData()throws BusinessException;

/**删除数据变化监听器*/
public void removeDataChangeListener(ValueChangedListener vl)throws BusinessException;

public AggregatedValueObject getData(Object key)throws BusinessException;

/**将数据从数据缓存中删除
 * 此方法会触发数据改变事件
 * */
public abstract boolean removeData(AggregatedValueObject vo)throws BusinessException;
}
