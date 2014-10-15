package nc.ui.er.plugin;

public interface IInfoInitializer {

	public abstract void initTreeListeners(IListenerController lc);

	public abstract void initBillCardListeners(IListenerController lc);

	public abstract void initBillListListeners(IListenerController lc);

	public abstract void initBtns(IListenerController lc);

}