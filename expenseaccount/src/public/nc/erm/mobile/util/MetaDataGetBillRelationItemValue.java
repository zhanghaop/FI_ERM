package nc.erm.mobile.util;

import java.util.ArrayList;
import java.util.Map;
import nc.bs.logging.Logger;
import nc.md.data.access.DASFacade;
import nc.md.model.IBusinessEntity;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.pub.beans.constenum.IConstEnum;


public class MetaDataGetBillRelationItemValue
{
  private IBusinessEntity be = null;
  
  public MetaDataGetBillRelationItemValue(IBusinessEntity be)
  {
    this.be = be;
  }
  


  public IConstEnum[] getRelationItemValue(ArrayList<IConstEnum> ies, String[] ids)
  {
    IConstEnum[] os = null;
    
    if ((ies != null) && (ies.size() > 0)) {
      String[] para = new String[ies.size()];
      
      for (int i = 0; i < ies.size(); i++) {
        para[i] = ((IConstEnum)ies.get(i)).getValue().toString();
        
        Logger.debug("ItemKey:" + ((IConstEnum)ies.get(i)).getName() + " ¼ÓÔØÊôÐÔ£º" + para[i]);
      }
      
      Map<String, Object[]> values = DASFacade.getAttributeValues(this.be, ids, para);
      

      os = new DefaultConstEnum[ies.size()];
      
      for (int i = 0; i < os.length; i++) {
        Object value = null;
        
        if (values != null) {
          value = values.get(para[i]);
        }
        DefaultConstEnum ic = new DefaultConstEnum(value, ((IConstEnum)ies.get(i)).getName());
        
        os[i] = ic;
      }
    }
    
    return os;
  }
}

