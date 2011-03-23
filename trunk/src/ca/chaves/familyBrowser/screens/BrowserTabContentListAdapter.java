package ca.chaves.familyBrowser.screens;

import ca.chaves.familyBrowser.R;
import ca.chaves.familyBrowser.helpers.NodeAttributes;
import ca.chaves.familyBrowser.helpers.NodeValueList;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * This class is a ListAdapter used to populate the TabContent tab.
 *
 * @author "David Chaves <david@chaves.ca>"
 */
public class BrowserTabContentListAdapter extends BaseAdapter {

    /** This is the view factory */
    private final ViewFactory m_viewFactory;
    /** This is the screen controller */
    private final BrowserScreenController m_screenController;

    /**
     * Constructor.
     *
     * @param viewFactory
     * @param screenController
     */
    public BrowserTabContentListAdapter(final ViewFactory viewFactory, final BrowserScreenController screenController) {
        this.m_viewFactory = viewFactory;
        this.m_screenController = screenController;
    }

    /**
     * Create view from the layout.
     *
     * @param text
     * @param text2
     * @param layoutResource
     * @param parentView
     * @return View
     */
    private View createView(final String text, final String text2, final int layoutResource, final ViewGroup parentView) {
        return this.m_viewFactory.createTextViews(text, R.id.label, text2, R.id.label2, layoutResource, parentView);
    }

    /**
     * Implement the BaseAdaptor interface.
     */
    @Override
    public int getCount() {
        final NodeValueList list = this.m_screenController.getTabContentValues();
        return list.getLength();
    }

    /**
     * Implement the BaseAdaptor interface.
     */
    @Override
    public Object getItem(final int position) {
        return null;
    }

    /**
     * Implement the BaseAdaptor interface.
     */
    @Override
    public long getItemId(final int position) {
        return position;
    }

    /**
     * Implement the BaseAdaptor interface.
     */
    @Override
    public View getView(final int position, final View convertView, final ViewGroup parentView) {
        final NodeValueList list = this.m_screenController.getTabContentValues();
        final NodeAttributes nodeAttributes = this.m_screenController.getNodeAttributes();
        final String text = nodeAttributes.getAttributeLabel(list.getId(position));
        final String text2 = list.getString(position);
        return this.createView(text, text2, R.layout.browser_tab_content_child, parentView);
    }
}
