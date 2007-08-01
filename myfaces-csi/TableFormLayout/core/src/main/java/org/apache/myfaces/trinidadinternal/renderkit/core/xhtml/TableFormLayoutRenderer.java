package org.apache.myfaces.trinidadinternal.renderkit.core.xhtml;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.trinidad.bean.FacesBean;
import org.apache.myfaces.trinidad.bean.PropertyKey;
import org.apache.myfaces.trinidad.component.UIXEditableValue;
import org.apache.myfaces.trinidad.component.UIXGroup;
import org.apache.myfaces.trinidad.component.UIXPanel;
import org.apache.myfaces.trinidad.component.core.input.CoreInputHidden;
import org.apache.myfaces.trinidad.component.core.layout.CoreTableFormLayout;
import org.apache.myfaces.trinidad.context.RenderingContext;

public class TableFormLayoutRenderer extends XhtmlRenderer
{

    private static final Log LOG = LogFactory
            .getLog(TableFormLayoutRenderer.class);

    public static final String USED = "used";

    public static final Marker FREE = new Marker("free");

    /**
     * Constructor
     * 
     * It calls its parent constructor to define what type of keys allow (to get
     * parameters)
     * 
     */
    public TableFormLayoutRenderer()
    {
        super(CoreTableFormLayout.TYPE);
    }

    /**
     * This method is called to get the keys for the properties required in
     * order to render the component.
     * 
     */
    @Override
    protected void findTypeConstants(FacesBean.Type type)
    {
        super.findTypeConstants(type);

        _rowsKey = type.findKey("rows");
        _columnsKey = type.findKey("columns");
        _heightKey = type.findKey("height");
        _widthKey = type.findKey("width");
        _inlineStyleKeyWidth = type.findKey("inlineStyle");
        _cellspacingKey = type.findKey("cellspacing");
    }

    @Override
    public boolean getRendersChildren()
    {
        return true;
    }

    /**
     * This is how we can render both the user defined styleClass and our
     * component style class.
     */
    @Override
    protected void renderStyleAttributes(FacesContext context,
            RenderingContext arc, FacesBean bean) throws IOException
    {
        renderStyleAttributes(context, arc, bean,
                AF_TABLE_FORM_STYLE_CLASS);
    }

    private String _getRows(FacesBean bean)
    {
        return (String) bean.getProperty(_rowsKey);
    }

    private String _getColumns(FacesBean bean)
    {
        return (String) bean.getProperty(_columnsKey);
    }

    private Integer _getHeight(FacesBean bean)
    {
        Integer i = null;
        try
        {
            i = (Integer) Integer.parseInt((String) bean
                    .getProperty(_heightKey));
        }
        catch (Exception ex)
        {
        }
        return i;
    }

    private Integer _getWidth(FacesBean bean)
    {
        return (Integer) Integer.parseInt((String) bean.getProperty(_widthKey));
    }

    private String _getInlineStyleWidth(FacesBean bean)
    {
        return (String) bean.getProperty(_inlineStyleKeyWidth);
    }

    private void _setInlineStyleWidth(FacesBean bean, String style)
    {
        bean.setProperty(_inlineStyleKeyWidth, style);
    }

    private Integer _getCellspacing(FacesBean bean)
    {
        Integer i = 0;
        try
        {
            i = (Integer) Integer.parseInt((String) bean
                    .getProperty(_cellspacingKey));
        }
        catch (Exception ex)
        {
        }
        return i;
    }

    /**
     * Get how many columns has been defined
     * 
     */
    public int _getColumnCount(FacesBean bean)
    {
        String columns = this._getColumns(bean);
        int columnCount;
        if (columns != null)
        {
            columnCount = 1 + StringUtils.countMatches(columns, ";");
        }
        else
        {
            columnCount = 1;
        }
        return columnCount;
    }

    /**
     * Calculate the widths of each column based on the description of width and
     * columns attributes
     * 
     * @return List of widths for each column
     */
    public String[] _getColumnWidths(FacesBean bean)
    {

        if (this._getWidth(bean) == null)
        {
            return null;
        }
        if (this._getWidth(bean).equals(""))
        {
            return null;
        }
        String columns = this._getColumns(bean);

        String[] sw = StringUtils.split(columns, ';');
        if (sw.length == 0)
        {
            return null;
        }
        String[] widths = new String[sw.length];

        double absolutePixels = 0; // Defines how many absolute space has
        double maxRelative = 0;

        for (int i = 0; i < sw.length; i++)
        {
            String col = sw[i];
            if (StringUtils.contains(col, '*'))
            {
                String col1 = StringUtils.stripEnd(col, "*");
                sw[i] = col1; // remove *
                try
                {
                    Double relative = Double.parseDouble(col1);
                    widths[i] = null;
                    maxRelative += relative;
                }
                catch (NumberFormatException e)
                {
                    widths[i] = "-2";
                }
            }
            else
            {
                // Measures in pixels, let it as is
                widths[i] = col;
                absolutePixels += Double.parseDouble(col);
            }
        }

        Double cellspacing = null;
        try
        {
            cellspacing = Double.parseDouble("" + this._getCellspacing(bean));
        }
        catch (NumberFormatException e)
        {
            cellspacing = 0d;
        }

        Double width = null;
        try
        {
            width = Double.parseDouble("" + this._getWidth(bean));
            // Now calculate the widths based on
            double actualwidth = cellspacing;
            double remainingspace = width - absolutePixels
                    - (cellspacing * (sw.length + 1));
            for (int i = 0; i < sw.length; i++)
            {
                String col = sw[i];
                String col1 = widths[i];

                if (col1 == null)
                {
                    if (remainingspace > 0)
                    {
                        widths[i] = ""
                                + (new Double(remainingspace
                                        * Double.parseDouble(col) / maxRelative)
                                        .intValue());
                    }
                    else
                    {
                        // Nothing happens
                        widths[i] = "0";
                    }
                }
                else
                {
                    // Nothing happens
                    actualwidth = actualwidth + Double.parseDouble(col1)
                            + cellspacing;
                }
            }
        }
        catch (NumberFormatException e)
        {
            for (int i = 0; i < sw.length; i++)
            {
                //String col = sw[i];
                String col1 = widths[i];
                if (col1 == null)
                {

                }
                else
                {
                    // Nothing happens
                }
            }
        }
        catch (NullPointerException e)
        {

        }
        return widths;
    }

    /**
     * Get how many columns has been defined
     * 
     */
    public int _getRowCount(FacesBean bean)
    {

        String rows = this._getRows(bean);
        int rowCount;
        if (rows != null)
        {
            rowCount = 1 + StringUtils.countMatches(rows, ";");
        }
        else
        {
            rowCount = 1;
        }
        return rowCount;
    }

    public String[] _getRowHeights(FacesBean bean)
    {
        if (this._getHeight(bean) == null)
        {
            return null;
        }
        if (this._getHeight(bean).equals(""))
        {
            return null;
        }
        String rows = this._getRows(bean);

        String[] sw = StringUtils.split(rows, ';');
        if (sw.length == 0)
        {
            return null;
        }
        String[] heights = new String[sw.length];

        double absolutePixels = 0; // Defines how many absolute space has
        double maxRelative = 0;

        for (int i = 0; i < sw.length; i++)
        {
            String col = sw[i];
            if (StringUtils.contains(col, '*'))
            {
                String col1 = StringUtils.stripEnd(col, "*");
                sw[i] = col1; // remove *
                try
                {
                    Double relative = Double.parseDouble(col1);
                    heights[i] = null;
                    maxRelative += relative;
                }
                catch (NumberFormatException e)
                {
                    heights[i] = "-2";
                }
            }
            else
            {
                // Measures in pixels, let it as is
                heights[i] = col;
                absolutePixels += Double.parseDouble(col);
            }
        }

        Double cellspacing = null;
        try
        {
            cellspacing = Double.parseDouble("" + this._getCellspacing(bean));
            cellspacing = 0d;
        }
        catch (NumberFormatException e)
        {
            cellspacing = 0d;
        }

        Double height = null;
        try
        {
            height = Double.parseDouble("" + this._getHeight(bean));
            // Now calculate the heights based on
            double actualheight = cellspacing;
            double remainingspace = height - absolutePixels
                    - (cellspacing * (sw.length + 1));
            for (int i = 0; i < sw.length; i++)
            {
                String col = sw[i];
                String col1 = heights[i];

                if (col1 == null)
                {
                    if (remainingspace > 0)
                    {
                        heights[i] = ""
                                + (new Double(remainingspace
                                        * Double.parseDouble(col) / maxRelative)
                                        .intValue());
                    }
                    else
                    {
                        // Nothing happens
                        heights[i] = "0";
                    }
                }
                else
                {
                    // Nothing happens
                    actualheight = actualheight + Double.parseDouble(col1)
                            + cellspacing;
                }
            }
        }
        catch (NumberFormatException e)
        {
            for (int i = 0; i < sw.length; i++)
            {
                //String col = sw[i];
                String col1 = heights[i];
                if (col1 == null)
                {

                }
                else
                {
                    // Nothing happens
                }
            }
        }
        catch (NullPointerException e)
        {

        }
        return heights;
    }

    /**
     * Add all children component to this list.
     * 
     * @param children
     * @param panel
     * @return
     */
    public static List<UIComponent> addChildren(List<UIComponent> children,
            UIComponent panel)
    {
        for (Object o : panel.getChildren())
        {
            UIComponent child = (UIComponent) o;
            if (isTransparentForLayout(child))
            {
                // addChildren(children, child);
            }
            else
            {
                children.add(child);
            }
        }
        return children;
    }

    /**
     * 
     * Checks if a component is transparent or not
     * 
     * @param component
     * @return
     */
    public static boolean isTransparentForLayout(UIComponent component)
    {

        // SubViewTag's component is UINamingContainer with 'null' rendererType
        // is transparent for layouting

        if (component instanceof UINamingContainer
                && component.getRendererType() == null)
        {
            return true;
        }
        if ("facelets".equals(component.getFamily()))
        {
            return true;
        }

        //inputHidden is transparent too
        if (component instanceof CoreInputHidden)
        {
            return true;
        }

        // also Forms are transparent for layouting
        return component instanceof UIForm;
    }

    private boolean _isFullRow(UIComponent component)
    {
        String rendererType = component.getRendererType();

        if (component instanceof UIXEditableValue)
        {
            return !_UNSUPPORTED_RENDERER_TYPES.contains(rendererType);
        }

        if (UIXPanel.COMPONENT_FAMILY.equals(component.getFamily()))
        {
            if ("org.apache.myfaces.trinidad.LabelAndMessage"
                    .equals(rendererType)
                    || "org.apache.myfaces.trinidad.rich.LabelAndMessage"
                            .equals(rendererType))
                return true;
            return false;
        }
        return false;
    }

    @Override
    protected String getDefaultStyleClass(FacesBean bean)
    {
        return SkinSelectors.AF_LABEL_TEXT_STYLE_CLASS;
    }

    /**
     * Do all encoding of the component
     * 
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void encodeAll(FacesContext context, RenderingContext arc,
            UIComponent component, FacesBean bean) throws IOException
    {
        // LOG.info("TableFormLayoutRenderer encodeAll");

        ResponseWriter rw = context.getResponseWriter();
        rw.startElement("div", component); // the root element

        // Check if a inlineStyle has a width property, if not
        // it append to the property the width and height in pixels
        // to the div tag that contains the table
        if (!StringUtils.contains(this._getInlineStyleWidth(bean), "width"))
        {
            if (this._getWidth(bean) != null)
            {
                this._setInlineStyleWidth(bean,
                        (this.getInlineStyle(bean) == null ? "" : this
                                .getInlineStyle(bean))
                                + ";width: " + this._getWidth(bean) + "px");
            }
        }
        /*
         if (!StringUtils.contains(this._getInlineStyleWidth(bean), "height")){
         if (this._getHeight(bean) != null){
         this._setInlineStyleWidth(bean, 
         (this.getInlineStyle(bean)==null?"":this.getInlineStyle(bean))+";height: "+
         this._getHeight(bean)+"px");
         }			
         }	
         */
        renderId(context, component);
        // When render all attributes, it render the InlineStyle
        renderAllAttributes(context, arc, bean);

        // Set the column count based on the info related on columns field
        int maxColumns = 0;
        maxColumns = this._getColumnCount(bean);

        if (isPDA(arc))
        {
            maxColumns = 1;
        }

        // Set the row count based on the info related on rows field
        int rows = 0;
        rows = this._getRowCount(bean);

        // Fetch a list of footer components:

        List<UIComponent> footerComponents = null;
        UIComponent footerFacetComponent = component.getFacet("footer");
        if (footerFacetComponent != null)
        {
            if (footerFacetComponent instanceof UIXGroup)
            {
                // a grouping of components
                if (footerFacetComponent.isRendered())
                {
                    footerComponents = footerFacetComponent.getChildren();
                }
            }
            else
            {
                // a single component
                footerComponents = new ArrayList<UIComponent>();
                footerComponents.add(footerFacetComponent);
            }
        }

        _encodeChildren(context, arc, component, bean, footerComponents,
                maxColumns, rows);

        rw.endElement("div"); // the root element
    }

    @SuppressWarnings("unchecked")
    private void _encodeChildren(FacesContext context, RenderingContext arc,
            UIComponent component, FacesBean bean,
            List<UIComponent> footerComponents, int maxColumns, int rows)
            throws IOException
    {
        // We cannot render a nested tableForm with any more than a single
        // column
        // so we must monitor whether we are nested or not:
        Map<String, Object> requestMap = context.getExternalContext()
                .getRequestMap();

        Integer nestLevelObject = (Integer) requestMap
                .get(TABLE_FORM_NEST_LEVEL_KEY);
        int nestLevel = 0;
        if (nestLevelObject != null)
        {
            nestLevel = nestLevelObject.intValue() + 1;
        }
        requestMap.put(TABLE_FORM_NEST_LEVEL_KEY, nestLevel);

        // Iterate through the childPeers extracting and counting the number of
        // visible children, also count the visible children inside of visible	
        List<UIComponent> childComponents = component.getChildren();
        //Here is adding row calculation
        FormItemInfo visibleFormItemInfo = _extractVisibleItems(context, bean,
                component, childComponents);

        List<FormItem> visibleFormItems = visibleFormItemInfo.getFormItems();
        //int totalFormItemCount = visibleFormItemInfo.getTotalFormItemCount();

        // Iterate through the footerPeers extracting the visible children:
        int totalFooterItemCount = 0;
        List<FormItem> visibleFooterItems = null;
        if (footerComponents != null)
        {
            FormItemInfo visibleFooterItemInfo = _extractVisibleItems(footerComponents);
            visibleFooterItems = visibleFooterItemInfo.getFormItems();
            totalFooterItemCount = visibleFooterItemInfo
                    .getTotalFormItemCount();
        }

        //boolean startAlignedLabels = (nestLevel == 0);
        boolean startAlignedLabels = false;

        /*
         // Now that we have the list and counts of visible form items (and group
         // arrangements), we must figure out how many actual columns and actual
         // rows
         // we really need:
         int actualColumns = maxColumns;
         int actualRows = rows;
         
         if (!startAlignedLabels || (totalFormItemCount == 0)) {
         // Must use a single column and unlimited rows:
         actualColumns = 1;
         actualRows = Integer.MAX_VALUE;
         } else if (actualColumns == 1) {
         // Developer wanted to use a single column and unlimited rows:
         actualRows = Integer.MAX_VALUE;
         } else {
         // We must compute how many rows will fit in the given max number of
         // columns
         // and also see if there are actually fewer columns needed:
         Dimension actualResults = TableFormLayoutRenderer
         ._computeActualRowsAndColumns(actualRows, actualColumns,
         totalFormItemCount, visibleFormItems);
         actualRows = (int) actualResults.getHeight();
         actualColumns = (int) actualResults.getWidth();
         }
         if (actualColumns < 1) {
         return;
         }
         */

        // These widths can either be pixels, percentages, or undefined.
        // We must ensure that if using percentages or undefined that we correct
        // them
        // to total up properly.
        // String labelWidth = (String) _getLabelWidth(bean);
        // String fieldWidth = (String) _getFieldWidth(bean);
        // Create the DOM for the form:
        ResponseWriter rw = context.getResponseWriter();
        rw.startElement("table", null); // the outer table
        //OutputUtils.renderLayoutTableAttributes(context, arc, "0", null,_getWidth(bean));
        OutputUtils.renderLayoutTableAttributes(context, arc, this
                ._getCellspacing(bean) == null ? "0" : this
                ._getCellspacing(bean), "100%");

        String footerLabelWidth = null;
        String footerFieldWidth = null;

        rw.writeAttribute("style", "width: 100%", null);

        rw.startElement("tbody", null); // the outer tbody

        // Create the form columns:
        /*
         _encodeFormColumns(context, arc,
         component, bean,
         rw, startAlignedLabels,
         mainLabelWidth, mainFieldWidth, actualRows, actualColumns, 1, // colSpan
         visibleFormItems);
         */
        _encodeFormColumns(context, arc, component, bean, rw,
                visibleFormItemInfo, visibleFormItems);

        // Create the column-spanning footer row(s):
        if (totalFooterItemCount > 0)
        {
            LOG.info("Draw Footer components: " + totalFooterItemCount + " "
                    + startAlignedLabels);
            _encodeFormColumns(context, arc, component, bean, rw,
                    startAlignedLabels, footerLabelWidth, footerFieldWidth,
                    totalFooterItemCount, // row
                    // count
                    1, // column count
                    this._getColumnCount(bean), // this is actually colSpan
                    visibleFooterItems);

        }

        // Indicate that we are leaving this level of nesting:
        if (nestLevel == 0)
        {
            // delete the value altogether:
            requestMap.remove(TABLE_FORM_NEST_LEVEL_KEY);
        }
        else
        {
            // decrement the value:
            requestMap.put(TABLE_FORM_NEST_LEVEL_KEY, nestLevel - 1);
        }

        rw.endElement("tbody"); // the outer tbody
        rw.endElement("table"); // the outer table
    }

    public int getSpanXLabel(UIComponent component)
    {
        Object o = component.getAttributes().get("spanXLabel");
        if (o == null)
        {
            return 1;
        }
        else
        {
            try
            {
                return Integer.parseInt("" + o);
            }
            catch (Exception e)
            {
                return 1;
            }
        }
    }

    public int getSpanXItem(UIComponent component)
    {
        Object o = component.getAttributes().get("spanXItem");
        if (o == null)
        {
            return 1;
        }
        else
        {
            try
            {
                return Integer.parseInt("" + o);
            }
            catch (Exception e)
            {
                return 1;
            }
        }
    }

    public int getSpanX(UIComponent component)
    {
        Object o = component.getAttributes().get("spanX");
        if (o == null)
        {
            return 1;
        }
        else
        {
            try
            {
                return Integer.parseInt("" + o);
            }
            catch (Exception e)
            {
                return 1;
            }
        }
    }

    public int getSpanY(UIComponent component)
    {
        Object o = component.getAttributes().get("spanY");
        if (o == null)
        {
            return 1;
        }
        else
        {
            try
            {
                return Integer.parseInt("" + o);
            }
            catch (Exception e)
            {
                return 1;
            }
        }
    }

    private List<Row> createRows(FacesContext context, FacesBean bean,
            UIComponent parent)
    {
        List<Row> rows = new ArrayList<Row>();
        int columnCount = this._getColumnCount(bean);

        // List<UIComponent> children = addChildren(new
        // ArrayList<UIComponent>(),
        // getParent());

        List<UIComponent> children = addChildren(new ArrayList<UIComponent>(),
                parent);

        int r = 0;
        for (UIComponent component : children)
        {

            Renderer renderer = context.getRenderKit().getRenderer(
                    component.getFamily(), component.getRendererType());

            LOG.info("Component:" + component.toString());
            LOG.info("Family:" + component.getFamily());
            LOG.info("Renderer:" + component.getRendererType());

            if (LabelAndMessageRenderer.class.isAssignableFrom(renderer
                    .getClass()))
            {

                /*
                 if ("org.apache.myfaces.trinidad.LabelAndMessage"
                 .equals(component.getRendererType())
                 || "org.apache.myfaces.trinidad.rich.LabelAndMessage"
                 .equals(component.getRendererType())){
                 */
                //if (component instanceof UIXValue){
                int spanXLabel = this.getSpanXLabel(component);
                int spanXItem = this.getSpanXItem(component);
                int spanY = this.getSpanY(component);

                //try to allocate in actual row
                int r1 = nextFreeRow(rows, r, spanXLabel + spanXItem, spanY,
                        columnCount);

                if (r1 == -2)
                {
                    //not found, add a Row and try again
                    rows.add(new Row(columnCount));
                    r = nextFreeRow(rows, r == 0 ? r : r + 1, spanXLabel
                            + spanXItem, spanY, columnCount);
                }
                else
                {
                    r = r1;
                }
                //if (r == rows.size()) {					
                //}

                int c = rows.get(r).nextFreeColumn(spanXLabel + spanXItem);

                for (int i = r; i < r + spanY; i++)
                {
                    rows.get(i)
                            .addControl(component, spanXLabel + spanXItem, c);
                }

                if (c + spanXLabel + spanXItem > columnCount)
                {
                    rows.get(r).fill(c + 1, c + spanXLabel + spanXItem,
                            component.isRendered());
                    for (int i = r + 1; i < r + spanY; i++)
                    {

                        if (i == rows.size())
                        {
                            rows.add(new Row(columnCount));
                        }
                        rows.get(i).fill(c, c + spanXLabel + spanXItem,
                                component.isRendered());
                    }
                }
                else
                {
                    rows.get(r).fill(c + 1, c + spanXLabel + spanXItem,
                            component.isRendered());
                    for (int i = r + 1; i < r + spanY; i++)
                    {

                        if (i == rows.size())
                        {
                            rows.add(new Row(columnCount));
                        }
                        rows.get(i).fill(c, c + spanXLabel + spanXItem,
                                component.isRendered());
                    }
                }
            }
            else
            {
                int spanX = getSpanX(component);
                int spanY = getSpanY(component);

                int r1 = nextFreeRow(rows, r, spanX, spanY, columnCount);

                if (r1 == -2)
                {
                    rows.add(new Row(columnCount));
                    r = nextFreeRow(rows, r == 0 ? r : r + 1, spanX, spanY,
                            columnCount);
                }
                else
                {
                    r = r1;
                }

                //if (r == rows.size()) {
                //	rows.add(new Row(columnCount));
                //}
                int c = rows.get(r).nextFreeColumn(spanX);

                for (int i = r; i < r + spanY; i++)
                {
                    rows.get(i).addControl(component, spanX, c);
                }
                rows.get(r).fill(c + 1, c + spanX, component.isRendered());

                for (int i = r + 1; i < r + spanY; i++)
                {

                    if (i == rows.size())
                    {
                        rows.add(new Row(columnCount));
                    }
                    rows.get(i).fill(c, c + spanX, component.isRendered());
                }
            }
        }
        return rows;
    }

    /*
     * Start looking from row i
     */
    private int nextFreeRow(List rows, int i, int spanX, int spanY,
            int maxColumn)
    {
        //int i = 0;
        for (; i < rows.size(); i++)
        {
            if (((Row) rows.get(i)).nextFreeColumn(spanX) != -1)
            {
                //create additional rows if needed
                int curRow = i;
                if (rows.size() > i + 1)
                {
                    boolean success = true;
                    for (int j = i + 1; j < rows.size(); j++)
                    {
                        if (((Row) rows.get(i)).nextFreeColumn(spanX) == -1)
                        {
                            success = false;
                            break;
                        }
                    }
                    if (!success)
                    {
                        continue;
                    }
                }
                while (rows.size() < i + spanY)
                {
                    rows.add(new Row(maxColumn));
                }
                return curRow;
            }
        }
        LOG.info("rows Avaliable: " + rows.size());
        return -2;
        //return i;
    }

    /**
     * Iterates through the childPeers extracting and counting the number of
     * visible children, also counts the visible children inside of visible
     * UIXGroups.
     * 
     * @param
     */
    @SuppressWarnings("unchecked")
    private FormItemInfo _extractVisibleItems(List<UIComponent> children)
    {
        FormItemInfo formItemInfo = new FormItemInfo();
        int totalFormItemCount = 0;
        for (UIComponent child : children)
        {
            /*
             * Object spanXItem =
             * (Object)child.getAttributes().get("spanXItem"); if (spanXItem !=
             * null){ LOG.debug("spanXItem:"+spanXItem); }
             */
            if (child.isRendered())
            {
                if (child instanceof UIXGroup)
                {
                    // only count children of the group
                    List<UIComponent> groupChildren = child.getChildren();
                    int visibleChildrenCount = 0;
                    for (UIComponent groupChild : groupChildren)
                    {
                        if (groupChild.isRendered())
                        {
                            // count the group child
                            visibleChildrenCount++;
                        }
                    }
                    if (visibleChildrenCount > 0)
                    {
                        totalFormItemCount += visibleChildrenCount;
                        formItemInfo.add(child, visibleChildrenCount, true);
                    }
                }
                else
                {
                    // only count the child
                    totalFormItemCount++;
                    formItemInfo.add(child, 1, false);
                }
            }
        }
        formItemInfo.setTotalFormItemCount(totalFormItemCount);

        return formItemInfo;
    }

    /**
     * Iterates through the childPeers extracting and counting the number of
     * visible children, also counts the visible children inside of visible
     * UIXGroups.
     * 
     * @param
     */
    @SuppressWarnings("unchecked")
    private FormItemInfo _extractVisibleItems(FacesContext context,
            FacesBean bean, UIComponent parent, List<UIComponent> children)
    {
        FormItemInfo formItemInfo = new FormItemInfo();
        int totalFormItemCount = 0;
        for (UIComponent child : children)
        {
            /*
             * Object spanXItem =
             * (Object)child.getAttributes().get("spanXItem"); if (spanXItem !=
             * null){ LOG.debug("spanXItem:"+spanXItem); }
             */
            if (child.isRendered())
            {
                if (child instanceof UIXGroup)
                {
                    // only count children of the group
                    List<UIComponent> groupChildren = child.getChildren();
                    int visibleChildrenCount = 0;
                    for (UIComponent groupChild : groupChildren)
                    {
                        if (groupChild.isRendered())
                        {
                            // count the group child
                            visibleChildrenCount++;
                        }
                    }
                    if (visibleChildrenCount > 0)
                    {
                        totalFormItemCount += visibleChildrenCount;
                        formItemInfo.add(child, visibleChildrenCount, true);
                    }
                }
                else
                {
                    // only count the child
                    totalFormItemCount++;
                    formItemInfo.add(child, 1, false);
                }
            }
        }
        formItemInfo.setTotalFormItemCount(totalFormItemCount);

        formItemInfo.setLayoutRows(this.createRows(context, bean, parent));

        return formItemInfo;
    }

    private boolean isLabelAndMessageRenderer(UIComponent cell)
    {
        return false;
    }

    @SuppressWarnings("unchecked")
    private void _encodeFormColumns(FacesContext context, RenderingContext arc,
            UIComponent component, FacesBean bean, ResponseWriter rw,
            FormItemInfo visibleFormItemInfo, List<FormItem> visibleItems)
            throws IOException
    {

        if (visibleItems.isEmpty())
            return;

        String[] columnWidths = this._getColumnWidths(bean);

        // List columnWidths = (List) component.getAttributes().get(
        // START COLUMN DEFINE

        if (columnWidths != null)
        {
            rw.startElement("colgroup", null);
            for (int i = 0; i < columnWidths.length; i++)
            {
                if (columnWidths[i] != null)
                {
                    int cellWidth = ((Integer) Integer
                            .parseInt(columnWidths[i])).intValue();
                    if (cellWidth != -2)
                    {
                        // cellWidth += getCellPadding(context, component,
                        // i);
                        rw.startElement("col", null);
                        rw.writeAttribute("width", Integer.toString(cellWidth),
                                null);
                        rw.endElement("col");
                    }
                }

            }
            rw.endElement("colgroup");
        }

        // END COLUMN DEFINE

        //START ROW DEFINE
        List<Row> rows = visibleFormItemInfo.getLayoutRows();

        String[] rowHeights = this._getRowHeights(bean);
        //String[] columnWidths = this._getColumnWidths(bean);

        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++)
        {
            Row row = rows.get(rowIndex);

            if (!row.isHidden())
            {
                rw.startElement("tr", null);
                renderStyleClass(context, arc, AF_TABLE_FORM_COLUMN_STYLE_CLASS);

                if (rowHeights != null)
                {
                    rw.writeAttribute("height", rowHeights[rowIndex], null);
                }

                List cells = row.getElements();

                int numColumn = 0; // This is for count the actual column for
                // set the width of the cell

                for (int columnIndex = 0; columnIndex < cells.size(); columnIndex++)
                {
                    Object object = cells.get(columnIndex);
                    if (object.toString().equals(TableFormLayoutRenderer.USED))
                    {
                        continue; // ignore the markers UIGridLayout.Used
                    }
                    if (object.equals(TableFormLayoutRenderer.FREE))
                    {
                        continue;
                    }
                    UIComponent cell = (UIComponent) object;

                    if (!cell.isRendered())
                    {
                        continue;
                    }

                    int spanX = 1;
                    int spanY = 1;

                    LOG.info("CELL:" + cell.toString());
                    LOG.info("Renderer:" + cell.getRendererType());
                    //String rendererType = cell.getRendererType();					
                    if (LabelAndMessageRenderer.class.isAssignableFrom(context
                            .getRenderKit().getRenderer(cell.getFamily(),
                                    cell.getRendererType()).getClass()))
                    {
                        spanX = this.getSpanXLabel(cell)
                                + this.getSpanXItem(cell);
                        spanY = this.getSpanY(cell);
                    }
                    else
                    {
                        spanX = this.getSpanX(cell);
                        spanY = this.getSpanY(cell);
                    }

                    String cw = this.calculateSize(bean, columnWidths,
                            numColumn, spanX);

                    if (columnWidths != null)
                    {
                        numColumn += spanX;
                    }

                    String ch = this.calculateSize(bean, rowHeights, rowIndex,
                            spanY);

                    rw.startElement("td", cell);

                    renderStyleClass(context, arc,
                            AF_TABLE_FORM_CONTENT_CELL_STYLE_CLASS);
                    rw.writeAttribute("colspan", spanX, null);
                    rw.writeAttribute("rowspan", spanY, null);
                    rw.writeAttribute("width", cw, null);

                    rw.startElement("div", cell);

                    String style = (String) cell.getAttributes().get("style");
                    if (style != null)
                    {
                        String cad = "";
                        if (columnWidths != null)
                        {
                            cad = ";width:" + cw + "px";
                        }
                        if (rowHeights != null)
                        {
                            cad += ";height:" + ch + "px";
                        }
                        //System.out.println("SET:" + style + cad);
                        cell.getAttributes().put("style", style + cad);

                    }
                    else
                    {
                        String cad = "";
                        if (columnWidths != null)
                        {
                            cad = ";width:" + cw + "px";
                        }
                        if (rowHeights != null)
                        {
                            cad += ";height:" + ch + "px";
                        }
                        // System.out.println("SET:" + style + ";width:" + cw
                        // + "px" + ";height:" + ch + "px");
                        cell.getAttributes().put("style", cad);
                    }

                    rw.startElement("table", null); // inner table
                    OutputUtils.renderLayoutTableAttributes(context, arc, "0",
                            "100%");
                    rw.startElement("tbody", null); // inner tbody

                    if (LabelAndMessageRenderer.class.isAssignableFrom(context
                            .getRenderKit().getRenderer(cell.getFamily(),
                                    cell.getRendererType()).getClass()))
                    {
                        rw.startElement("colgroup", null);
                        rw.startElement("col", null);
                        rw.writeAttribute("width", this.calculateSize(bean,
                                columnWidths, numColumn - spanX, this
                                        .getSpanXLabel(cell)), null);
                        rw.endElement("col");
                        rw.startElement("col", null);
                        rw.writeAttribute("width", this.calculateSize(bean,
                                columnWidths, numColumn
                                        - this.getSpanXItem(cell), this
                                        .getSpanXItem(cell)), null);
                        rw.endElement("col");
                        rw.endElement("colgroup");
                    }

                    String rowHeight = this.calculateSize(bean, rowHeights,
                            rowIndex, spanY);
                    rw.startElement("tr", null); // label row

                    if (!rowHeight.equals("0"))
                    {
                        rw.writeAttribute("height", rowHeight, null);
                    }

                    //Now i have to modify inlineContentStyle to add height value
                    if (LabelAndMessageRenderer.class.isAssignableFrom(context
                            .getRenderKit().getRenderer(cell.getFamily(),
                                    cell.getRendererType()).getClass()))
                    {
                        FacesBean cbean = getFacesBean(cell);

                        FacesBean.Type ctype = cbean.getType();

                        PropertyKey _cstyle = ctype.findKey("contentStyle");

                        String cstyle = (String) cbean.getProperty(_cstyle);
                        //LOG.info("contentStyle:"+cstyle);

                        if (!StringUtils.contains(cstyle, "height"))
                        {
                            if (!rowHeight.equals("0"))
                            {
                                cbean.setProperty(_cstyle, (cstyle == null ? ""
                                        : cstyle)
                                        + ";height: " + rowHeight + "px");
                            }
                        }

                        cstyle = (String) cbean.getProperty(_cstyle);

                        if (!StringUtils.contains(cstyle, "width"))
                        {
                            cbean.setProperty(_cstyle, (cstyle == null ? ""
                                    : cstyle)
                                    + ";width: 100%");
                        }

                        _encodeFormItem2(context, arc, rw, false, cell);
                    }
                    else
                    {
                        rw.startElement("td", null);
                        _encodeFormItem2(context, arc, rw, false, cell);
                        rw.endElement("td");
                    }

                    rw.endElement("tr"); // field row
                    rw.endElement("tbody"); // inner tbody
                    rw.endElement("table"); // inner table

                    rw.endElement("div");
                    rw.endElement("td");

                }
                rw.endElement("tr");
            }
        }
    }

    public String calculateSize(FacesBean bean, String[] sizes, int num,
            int span)
    {
        String cw = "0";
        if (sizes != null)
        {
            LOG.debug("calculateSize:" + sizes.toString() + " " + num + " "
                    + span);
            if (span == 1)
            {
                cw = sizes[num];
                //numColumn++;
            }
            else
            {
                int cw1 = 0;
                for (int i = num; i < num + span; i++)
                {
                    cw1 += Integer.parseInt(sizes[i]);
                }
                cw1 = cw1 + this._getCellspacing(bean) * (span - 1);
                cw = "" + cw1;
            }
        }
        return cw;
    }

    @SuppressWarnings("unchecked")
    private void _encodeFormColumns(FacesContext context, RenderingContext arc,
            UIComponent component, FacesBean bean, ResponseWriter rw,
            boolean startAlignedLabels, String effectiveLabelWidth,
            String effectiveFieldWidth, int actualRows, int actualColumns,
            int colSpan, List<FormItem> visibleItems) throws IOException
    {

        if (visibleItems.isEmpty())
            return;

        rw.startElement("tr", null); // the outer row
        int currentItemIndex = 0;
        int visibleItemsLength = visibleItems.size();
        String outerColumnWidth = Math.floor(((double) 100) / actualColumns)
                + "%";
        for (int col = 0; col < actualColumns; col++)
        {
            rw.startElement("td", null); // the outer column
            renderStyleClass(context, arc, AF_TABLE_FORM_COLUMN_STYLE_CLASS);
            rw.writeAttribute("colspan", colSpan, null);
            if (col < actualColumns - 1) // let the last column take the leftover space
            {
                rw.writeAttribute("width", outerColumnWidth, null);
            }

            rw.startElement("table", null); // the inner table
            OutputUtils.renderLayoutTableAttributes(context, arc, "0", "100%");
            rw.startElement("tbody", null); // the inner tbody
            if (startAlignedLabels)
            {
                rw.startElement("tr", null); // the sizing row
                rw.startElement("td", null); // the sizing label cell
                if (effectiveLabelWidth != null)
                {
                    rw.writeAttribute("style", "width: " + effectiveLabelWidth,
                            null);
                }
                rw.endElement("td"); // the sizing label cell
                rw.startElement("td", null); // the sizing field cell
                if (effectiveFieldWidth != null)
                {
                    rw.writeAttribute("style", "width: " + effectiveFieldWidth,
                            null);
                }
                rw.endElement("td"); // the sizing field cell
                rw.endElement("tr"); // the sizing row
            }
            int currentRow = 0;
            boolean groupSeparatorNeeded = false;
            while (currentRow < actualRows)
            {
                FormItem item = visibleItems.get(currentItemIndex);
                UIComponent itemChild = item.getChild();
                int itemSize = item.getSize();
                boolean isGroup = item.isGroup();
                int sizeAfterThis = currentRow + itemSize;
                if ((currentRow == 0) || (sizeAfterThis <= actualRows))
                {
                    if (isGroup)
                    {
                        if (currentRow > 0)
                        {
                            // insert group separator
                            TableFormLayoutRenderer._encodeGroupDivider(
                                    context, arc, rw, startAlignedLabels);
                        }
                        groupSeparatorNeeded = true;
                        List<UIComponent> groupChildren = itemChild
                                .getChildren();
                        for (UIComponent groupChild : groupChildren)
                        {
                            if (groupChild.isRendered())
                            {
                                // add the group child
                                _encodeFormItem(context, arc, rw,
                                        startAlignedLabels, groupChild);
                            }
                        }
                    }
                    else
                    {
                        if (groupSeparatorNeeded)
                        {
                            groupSeparatorNeeded = false;
                            // insert group separator
                            TableFormLayoutRenderer._encodeGroupDivider(
                                    context, arc, rw, startAlignedLabels);
                        }
                        _encodeFormItem(context, arc, rw, startAlignedLabels,
                                itemChild);
                    }

                    // This particular item or group of items fits:
                    currentItemIndex++;
                    currentRow += itemSize;

                    if (currentItemIndex >= visibleItemsLength)
                    {
                        // there are no more items
                        break;
                    }
                }
                else
                {
                    break;
                }
            }
            rw.endElement("tbody"); // the inner tbody
            rw.endElement("table"); // the inner table
            rw.endElement("td"); // the outer column
        }
        rw.endElement("tr"); // the outer row
    }

    private static void _encodeGroupDivider(FacesContext context,
            RenderingContext arc, ResponseWriter rw, boolean startAlignedLabels)
            throws IOException
    {
        rw.startElement("tr", null);
        rw.startElement("td", null);
        if (startAlignedLabels)
        {
            rw.writeAttribute("colspan", "2", null);
        }
        // =-= mcc I considered using an HR but IE6 always adds its own border
        // around
        // any background graphics you attempt to put inside. Firefox 1.5
        // behaves
        // as expected. Using a DIV until we know a way to fix IE6.
        rw.startElement("div", null);
        renderStyleClass(context, arc, AF_TABLE_FORM_SEPARATOR_STYLE_CLASS);
        rw.endElement("div");
        rw.endElement("td");
        rw.endElement("tr");
    }

    private void _encodeFormItem(FacesContext context, RenderingContext arc,
            ResponseWriter rw, boolean startAlignedLabels, UIComponent item)
            throws IOException
    {
        boolean isFullRow = _isFullRow(item);
        if (isFullRow) // "plays well" with panelForm
        {
            // If a peer wants to play well with panelForm, it must use the proper
            // PanelForm wrapper APIs to ensure proper DOM structure.
            _encodeBeforeLabelTd(context, arc, rw, startAlignedLabels);
            Map<String, String> originalResourceKeyMap = arc
                    .getSkinResourceKeyMap();
            try
            {
                if (startAlignedLabels)
                {
                    arc
                            .setSkinResourceKeyMap(TableFormLayoutRenderer._RESOURCE_KEY_SIDE_BY_SIDE_MAP);
                }
                else
                {
                    arc
                            .setSkinResourceKeyMap(TableFormLayoutRenderer._RESOURCE_KEY_STACKED_MAP);
                }
                encodeChild(context, item);
            }
            finally
            {
                arc.setSkinResourceKeyMap(originalResourceKeyMap);
            }
            _encodeAfterFieldTd(rw, startAlignedLabels);
        }
        else
        // does not "play well" with panelForm
        {
            if (startAlignedLabels) // (labels side-by-side with fields)
            {
                rw.startElement("tr", null);

                rw.startElement("td", null); // label cell (empty)
                rw.endElement("td"); // label cell (empty)

                rw.startElement("td", null); // field cell (non-empty)
                renderStyleClass(context, arc,
                        AF_TABLE_FORM_CONTENT_CELL_STYLE_CLASS);
                encodeChild(context, item);
                rw.endElement("td"); // field cell (non-empty)

                rw.endElement("tr");
            }
            else
            // top-aligned (labels stacked above fields)
            {
                rw.startElement("tr", null);

                rw.startElement("td", null); // field cell (non-empty)
                renderStyleClass(context, arc,
                        AF_TABLE_FORM_CONTENT_CELL_STYLE_CLASS);
                encodeChild(context, item);
                rw.endElement("td"); // field cell (non-empty)

                rw.endElement("tr");
            }
        }
    }

    private void _encodeFormItem2(FacesContext context, RenderingContext arc,
            ResponseWriter rw, boolean startAlignedLabels, UIComponent item)
            throws IOException
    {

        encodeChild(context, item);

    }

    private static void _encodeBeforeLabelTd(FacesContext context,
            RenderingContext arc, ResponseWriter rw, boolean startAlignedLabels)
            throws IOException
    {
        rw.startElement("tr", null); // form item row
        // startAlignedLabels means (labels side-by-side with fields)
        if (!startAlignedLabels) // top-aligned (labels stacked above fields)
        {
            rw.startElement("td", null); // stack cell
            rw.startElement("table", null); // inner table
            OutputUtils.renderLayoutTableAttributes(context, arc, "0", "100%");
            rw.startElement("tbody", null); // inner tbody

            rw.startElement("tr", null); // label row
        }
    }

    /**
     * The form children that support rendering inside of panelForms must call
     * this method between encoding their labels and fields.
     * 
     * @param context
     *            FacesContext
     * @param arc
     *            AdfRenderingContext
     * @param rw
     *            ResponseWriter
     * 
     * @return <code>true</code> if a new element was opened,
     *         <code>false</code> otherwise.
     * 
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    protected static boolean encodeBetweenLabelAndFieldCells(
            FacesContext context, RenderingContext arc, ResponseWriter rw)
            throws IOException
    {
        Map<String, Object> requestMap = context.getExternalContext()
                .getRequestMap();

        Integer nestLevelObject = (Integer) requestMap
                .get(TABLE_FORM_NEST_LEVEL_KEY);
        if ((nestLevelObject != null) && (nestLevelObject.intValue() > 0)) // top-aligned
        // (labels
        // stacked
        // above
        // fields)
        {
            rw.endElement("tr"); // label row

            rw.startElement("tr", null); // field row
            return true;
        }

        return false;
    }

    private static void _encodeAfterFieldTd(ResponseWriter rw,
            boolean startAlignedLabels) throws IOException
    {
        if (!startAlignedLabels) // top-aligned (labels stacked above fields)
        {
            rw.endElement("tr"); // field row

            rw.endElement("tbody"); // inner tbody
            rw.endElement("table"); // inner table
            rw.endElement("td"); // stack cell
        }
        rw.endElement("tr"); // form item row
    }

    /**
     * The idea of Row class is help to organize the order of the elements will
     * be layout
     * 
     * @author Leonardo
     * 
     */
    public static class Row implements Serializable
    {
        private static final long serialVersionUID = 3511093677488052045L;

        private int columns;

        private List cells;

        private boolean hidden;

        public Row(int columns)
        {
            setColumns(columns);
        }

        private void addControl(UIComponent component, int spanX, int col)
        {

            //int i = nextFreeColumn(spanX);

            //cells.set(i, component);
            //fill(i + 1, i + spanX, component.isRendered());

            cells.set(col, component);
            fill(col + 1, col + spanX, component.isRendered());
        }

        private void fill(int start, int end, boolean rendered)
        {

            if (end > columns)
            {
                LOG.error("Error in Jsp (end > columns). "
                        + "Try to insert more spanX as possible.");
                LOG.error("start:   " + start);
                LOG.error("end:     " + end);
                LOG.error("columns: " + columns);
                LOG.error("Actual cells:");
                for (Object component : cells)
                {
                    if (component instanceof UIComponent)
                    {
                        LOG.error("Cell-ID: "
                                + ((UIComponent) component).getId() + " "
                                + ((UIComponent) component).getRendererType());
                    }
                    else
                    {
                        LOG.error("Cell:    " + component); // e.g. marker
                    }
                }

                end = columns; // fix the "end" parameter to continue the
                // processing.
            }

            for (int i = start; i < end; i++)
            {
                cells.set(i, new Marker(USED, rendered));
            }
        }

        private int nextFreeColumn(int spanX)
        {
            if (spanX > columns)
            {
                return -2;
            }
            for (int i = 0; i < columns; i++)
            {
                if (FREE.equals(cells.get(i)))
                {
                    if (spanX + i > columns)
                    {
                        //The component does not fit
                        return -1;
                    }
                    boolean success = true;
                    for (int j = i + 1; j < i + spanX; j++)
                    {
                        if (FREE.equals(cells.get(i)))
                        {
                        }
                        else
                        {
                            success = false;
                        }
                    }
                    if (success)
                    {
                        return i;
                    }
                }
            }
            return -1;
        }

        public List getElements()
        {
            return cells;
        }

        public int getColumns()
        {
            return columns;
        }

        private void setColumns(int columns)
        {
            this.columns = columns;
            cells = new ArrayList(columns);
            for (int i = 0; i < columns; i++)
            {
                cells.add(FREE);
            }
        }

        public boolean isHidden()
        {
            return hidden;
        }

        public void setHidden(boolean hidden)
        {
            this.hidden = hidden;
        }
    }

    /**
     * This class keep data about if a component has been rendered or not
     * 
     * @author Leonardo Uribe
     */
    public static class Marker implements Serializable
    {
        private static final long serialVersionUID = 2545389428902504453L;

        private final String name;

        private boolean rendered;

        private Marker(String name)
        {
            this.name = name;
        }

        public Marker(String name, boolean rendered)
        {
            this.name = name;
            this.rendered = rendered;
        }

        @Override
        public String toString()
        {
            return name;
        }

        public boolean isRendered()
        {
            return rendered;
        }
    }

    static private class FormItem
    {
        FormItem(UIComponent child, int size, boolean group)
        {
            _child = child;
            _size = size;
            _group = group;
        }

        UIComponent getChild()
        {
            return _child;
        }

        int getSize()
        {
            return _size;
        }

        boolean isGroup()
        {
            return _group;
        }

        private UIComponent _child;

        private int _size;

        private boolean _group;
    }

    static private class FormItemInfo
    {
        FormItemInfo()
        {
            _formItems = new ArrayList<FormItem>();
        }

        void add(UIComponent child, int size, boolean group)
        {
            _formItems.add(new FormItem(child, size, group));
        }

        List<FormItem> getFormItems()
        {
            return _formItems;
        }

        int getTotalFormItemCount()
        {
            return _totalFormItemCount;
        }

        void setTotalFormItemCount(int totalFormItemCount)
        {
            _totalFormItemCount = totalFormItemCount;
        }

        List<Row> getLayoutRows()
        {
            return _layoutRows;
        }

        void setLayoutRows(List<Row> layoutRows)
        {
            this._layoutRows = layoutRows;
        }

        private List<Row> _layoutRows;

        private List<FormItem> _formItems;

        private int _totalFormItemCount;
    }

    private PropertyKey _rowsKey;
    private PropertyKey _columnsKey;
    private PropertyKey _heightKey;
    private PropertyKey _widthKey;
    private PropertyKey _inlineStyleKeyWidth;
    private PropertyKey _cellspacingKey;

    // Overallocate because we basically want everything to miss
    private static final Set<String> _UNSUPPORTED_RENDERER_TYPES;
    static
    {
        _UNSUPPORTED_RENDERER_TYPES = new HashSet<String>(64);
        _UNSUPPORTED_RENDERER_TYPES.add("org.apache.myfaces.trinidad.Hidden");
        _UNSUPPORTED_RENDERER_TYPES.add("org.apache.myfaces.trinidad.Shuttle");
        _UNSUPPORTED_RENDERER_TYPES
                .add("org.apache.myfaces.trinidad.rich.Hidden");
        _UNSUPPORTED_RENDERER_TYPES
                .add("org.apache.myfaces.trinidad.rich.Shuttle");
    }

    private static final String TABLE_FORM_NEST_LEVEL_KEY = "org.apache.myfaces.trinidadinternal.TableFormNestLevel";

    // private static final int _COLUMNS_DEFAULT = 3;

    // we need a resource key map since we are using LabelAndMessageRenderer.
    private static final Map<String, String> _RESOURCE_KEY_SIDE_BY_SIDE_MAP;

    //                                                                         //
    //                                                                         //
    // ========================= tr:tableFormLayout ========================== //
    //                                                                         //
    //                                                                         //

    // ============================ Style classes ============================ //
    public static final String AF_TABLE_FORM_COLUMN_STYLE_CLASS = "af|tableFormLayout::column";
    public static final String AF_TABLE_FORM_CONTENT_CELL_STYLE_CLASS = "af|tableFormLayout::content-cell";
    public static final String AF_TABLE_FORM_LABEL_CELL_STYLE_CLASS = "af|tableFormLayout::label-cell";
    public static final String AF_TABLE_FORM_LABEL_STACKED_CELL_STYLE_CLASS = "af|tableFormLayout::label-stacked-cell";
    public static final String AF_TABLE_FORM_MESSAGE_CELL_STYLE_CLASS = "af|tableFormLayout::message-cell";
    public static final String AF_TABLE_FORM_SEPARATOR_STYLE_CLASS = "af|tableFormLayout::separator";
    public static final String AF_TABLE_FORM_STYLE_CLASS = "af|tableFormLayout";

    private static final Map<String, String> _RESOURCE_KEY_STACKED_MAP;

    static
    {
        // style keys.
        // for panelForm, we want a specific af|panelFormLayout style for the
        // label cell,
        // instead of the generic prompt cell style.

        // Start-aligned labels for side-by-side orientation:
        _RESOURCE_KEY_SIDE_BY_SIDE_MAP = new HashMap<String, String>();

        _RESOURCE_KEY_SIDE_BY_SIDE_MAP.put(
                SkinSelectors.AF_LABEL_TEXT_STYLE_CLASS,
                AF_TABLE_FORM_LABEL_CELL_STYLE_CLASS);
        _RESOURCE_KEY_SIDE_BY_SIDE_MAP.put(
                SkinSelectors.AF_CONTENT_CELL_STYLE_CLASS,
                AF_TABLE_FORM_CONTENT_CELL_STYLE_CLASS);
        _RESOURCE_KEY_SIDE_BY_SIDE_MAP.put(
                SkinSelectors.AF_COMPONENT_MESSAGE_CELL_STYLE_CLASS,
                AF_TABLE_FORM_MESSAGE_CELL_STYLE_CLASS);

        // Stacked labels for one-over-the-other orientation:
        _RESOURCE_KEY_STACKED_MAP = new HashMap<String, String>();

        _RESOURCE_KEY_STACKED_MAP.put(SkinSelectors.AF_LABEL_TEXT_STYLE_CLASS,
                AF_TABLE_FORM_LABEL_STACKED_CELL_STYLE_CLASS);
        _RESOURCE_KEY_STACKED_MAP.put(
                SkinSelectors.AF_CONTENT_CELL_STYLE_CLASS,
                AF_TABLE_FORM_CONTENT_CELL_STYLE_CLASS);
        _RESOURCE_KEY_STACKED_MAP.put(
                SkinSelectors.AF_COMPONENT_MESSAGE_CELL_STYLE_CLASS,
                AF_TABLE_FORM_MESSAGE_CELL_STYLE_CLASS);
    }

}
