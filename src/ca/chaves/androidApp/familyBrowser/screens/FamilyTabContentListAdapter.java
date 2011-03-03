package ca.chaves.androidApp.familyBrowser.screens;

import ca.chaves.androidApp.familyBrowser.R;
import ca.chaves.androidApp.familyBrowser.helpers.NodeAttributes;
import ca.chaves.androidApp.familyBrowser.helpers.NodeValueList;
import ca.chaves.androidApp.familyBrowser.helpers.ViewFactory;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * The class FamilyTabContentListAdapter is a ListAdapter used to populate the
 * TabContent tab
 * 
 * @author david@chaves.ca
 */
class FamilyTabContentListAdapter extends BaseAdapter {
    
    /** View Factory */
    private final ViewFactory m_viewFactory;
    /** Screen Controller */
    private final FamilyController m_controller;
    
    /**
     * Constructor.
     * 
     * @param viewFactory
     * @param controller
     */
    public FamilyTabContentListAdapter(final ViewFactory viewFactory, final FamilyController controller) {
        this.m_viewFactory = viewFactory;
        this.m_controller = controller;
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
        final NodeValueList list = this.m_controller.getTabContentValues();
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
        final NodeValueList list = this.m_controller.getTabContentValues();
        final NodeAttributes nodeAttributes = this.m_controller.getNodeAttributes();
        final String text = nodeAttributes.getAttributeLabel(list.getExternalId(position));
        final String text2 = list.getStringValue(position);
        return this.createView(text, text2, R.layout.family_content_child, parentView);
    }
}
