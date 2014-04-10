package nc.ui.arap.bx;

/**
 * 被观察的主题
 * @author chendya
 */
public interface IPageSubject {

	public  void addObserver(IPageObserver o);
	
	public  void deleteObserver(IPageObserver o); 
	
	public  void notifyObservers(Object object);
}
