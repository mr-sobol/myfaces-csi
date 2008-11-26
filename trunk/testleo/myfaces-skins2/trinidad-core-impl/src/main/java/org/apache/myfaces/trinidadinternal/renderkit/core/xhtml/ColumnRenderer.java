/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.myfaces.trinidadinternal.renderkit.core.xhtml;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.apache.myfaces.trinidad.bean.FacesBean;
import org.apache.myfaces.trinidad.component.UIXColumn;
import org.apache.myfaces.trinidad.component.core.data.CoreColumn;
import org.apache.myfaces.trinidad.logging.TrinidadLogger;
import org.apache.myfaces.trinidad.context.RenderingContext;
import org.apache.myfaces.trinidadinternal.renderkit.core.xhtml.table.BandingData;
import org.apache.myfaces.trinidadinternal.renderkit.core.xhtml.table.CellUtils;
import org.apache.myfaces.trinidadinternal.renderkit.core.xhtml.table.ColumnData;
import org.apache.myfaces.trinidadinternal.renderkit.core.xhtml.table.RenderStage;
import org.apache.myfaces.trinidadinternal.renderkit.core.xhtml.table.RowData;
import org.apache.myfaces.trinidadinternal.renderkit.core.xhtml.table.TableRenderingContext;

public class ColumnRenderer extends ColumnGroupRenderer
{
  public ColumnRenderer()
  {
    super();
  }

  /**
   * @todo Will need to support TREE_NODE_STAGE
   */
  @Override
  protected void encodeAll(
    FacesContext        context,
    RenderingContext arc,
    UIComponent         component,
    FacesBean           bean) throws IOException
  {
    TableRenderingContext tContext =
      TableRenderingContext.getCurrentInstance();

    if (tContext == null)
    {
      _LOG.warning("COMPONENT_COLUMN_OUTSIDE_TABLE", component);
      return;
    }


    if (_isColumnGroup(component))
    {
      _columnGroupRenderer.encodeAll(context, arc, component, bean);
      return;
    }

    // check to see if we are rendering a table column header, or table data
    RenderStage rs = tContext.getRenderStage();
    int stage = rs.getStage();
    switch (stage)
    {
    case RenderStage.INITIAL_STAGE:
      _computeMode(arc, tContext, component, bean);
      break;

    case RenderStage.COLUMN_HEADER_STAGE:
      _renderHeaderMode(context, arc, tContext, component);
      break;

    case RenderStage.COLUMN_FOOTER_STAGE:
      _renderFooterMode(context, arc, tContext, component);
      break;

    case RenderStage.TREE_NODE_STAGE:
      //      _renderHGridNodeStamp(context, arc, tContext, component);
      break;

    case RenderStage.DATA_STAGE:
      _renderDataMode(context, arc, tContext, component);
      break;

    case RenderStage.START_ROW_STAGE:
      _startRowMode(tContext, component);
      break;

    case RenderStage.END_STAGE:
      // we do not do any special clean up
      break;
    default:
      assert false : "Bad renderStage:"+stage;
    }

    // after we render, we must increment both the physical and the logical
    // column indices
    ColumnData colData = tContext.getColumnData();
    colData.incrementColumnIndex();
  }

  @SuppressWarnings("unchecked")
  private boolean _isColumnGroup(UIComponent column)
  {
    Iterator<UIComponent> kids = column.getChildren().iterator();
    // Special columns - nothing is a column group
    if (!kids.hasNext())
      return false;

    return ((kids.next() instanceof UIXColumn));
  }


  @SuppressWarnings("unchecked")
  protected void renderKids(FacesContext          context,
                            RenderingContext      arc,
                            TableRenderingContext trc,
                            UIComponent           column) throws IOException
  {
    boolean renderedOne = false;
    for(UIComponent child : (List<UIComponent>)column.getChildren())
    {
      if (child.isRendered())
      {
        // Put each component on a separate line, separated by a div
        if (renderedOne)
        {
          ResponseWriter rw = context.getResponseWriter();
          rw.startElement("div", null);
          rw.endElement("div");
        }
        else
        {
          renderedOne = true;
        }

        encodeChild(context, child);
      }
    }
  }

  /**
   * Render either one child or all (which depends on useSeparateRows)
   * @todo This dual-purpose method is creepy.
   */
  private void _renderKids(FacesContext          context,
                           RenderingContext   arc,
                           TableRenderingContext tContext,
                           UIComponent           column,
                           int          kid) throws IOException
  {
    if (kid < 0)
    {
      renderKids(context, arc, tContext, column);
    }
    else
    {
      UIComponent child = (UIComponent) column.getChildren().get(kid);
      encodeChild(context, child);
    }
  }

  private void _startRowMode(
    TableRenderingContext tContext,
    UIComponent           column)
    throws IOException
  {
    ColumnData colData = tContext.getColumnData();
    int physicalIndex  = colData.getPhysicalColumnIndex();

    // check to see if each cell in this column comprises of multiple rows:
    if (colData.useSeparateRows(physicalIndex))
    {
      // if so, then the number of rows it spans is the number of visible
      // children it has:
      int kids = getRenderedChildCount(column);
      RowData rowData = tContext.getRowData();
      rowData.setCurrentRowSpan(kids);
    }
    // the default is to span just one row.
  }


  private void _renderDataMode(
    FacesContext        context,
    RenderingContext arc,
    TableRenderingContext tContext,
    UIComponent           column)
    throws IOException
  {
    ColumnData colData = tContext.getColumnData();
    RowData rowData = tContext.getRowData();
    final int physicalIndex  = colData.getPhysicalColumnIndex();
    final int subRow = rowData.getCurrentSubRow();
    // this is the row span for the entire row:
    final int currentRowSpan = rowData.getCurrentRowSpan();

    // check to see if each cell in this column comprises of multiple rows:
    if (colData.useSeparateRows(physicalIndex))
      handleSeparateRows:
    {
      // if so, then we need to render the next child that has not been
      // rendered yet:
      int totalKids = getRenderedChildCount(column);

      // if we have no visible kids, then we should return to the
      // non-multiple-row case. this was bug 2942881:
      if (totalKids == 0)
        break handleSeparateRows;

      if (totalKids > subRow)
      {
        // make sure that the last child we render (for this row) fills up any
        // remaining room in this row span:
        int span = (subRow == (totalKids - 1))
          ? (currentRowSpan - subRow)
          : 1;
        _renderTD(context, arc, tContext, column, subRow, span);
      }

      return;
    }

    // we only render if we haven't been rendered before:
    if (subRow == 0)
    {
      _renderTD(context, arc, tContext, column, -1, currentRowSpan);
    }
  }

  /**
   * renders the "headers" attribute for a TD table cell
   * @param tContext
   * @throws java.io.IOException
   */
  public static void renderHeadersAttr(
    FacesContext context,
    TableRenderingContext tContext) throws IOException
  {
    if (tContext.isExplicitHeaderIDMode())
    {
      RowData rowData = tContext.getRowData();
      String rowID = rowData.getCurrentRowHeaderID();

      ColumnData colData = tContext.getColumnData();
      int physicalIndex  = colData.getPhysicalColumnIndex();
      String headers = colData.getHeaderIDs(physicalIndex);
      if (rowID != null)
      {
        headers = rowID+" "+headers;
      }

      context.getResponseWriter().writeAttribute("headers", headers, null);
    }
  }

  /**
   * @todo Generate Unique IDs correctly!
   */
  private void _renderTD(FacesContext          context,
                         RenderingContext   arc,
                         TableRenderingContext tContext,
                         UIComponent           column,
                         int kidIndex,
                         int rowSpan)
    throws IOException
  {
    ResponseWriter writer = context.getResponseWriter();
    ColumnData colData = tContext.getColumnData();
    final int physicalIndex  = colData.getPhysicalColumnIndex();
    final boolean isRowHeader = colData.isRowHeader(physicalIndex);

    final String cellClass;
    final String borderStyleClass;

    if (isRowHeader)
    {
      writer.startElement(XhtmlConstants.TABLE_HEADER_ELEMENT, column);
      cellClass = TableRenderer.getRowHeaderFormatClass();
      if (tContext.isExplicitHeaderIDMode())
      {
        String headerID = column.getClientId(context);
        RowData rowData = tContext.getRowData();
        rowData.setCurrentRowHeaderID(headerID);
        writer.writeAttribute(XhtmlConstants.ID_ATTRIBUTE, headerID, null);
      }
      else
      {
        writer.writeAttribute("scope", "row", null);
      }

      borderStyleClass =
      CellUtils.getHeaderBorderStyle(tContext, arc, false, false);
    }
    else // !isRowHeader
    {
      writer.startElement(XhtmlConstants.TABLE_DATA_ELEMENT, column);
      cellClass = getTableDataStyleClass(tContext);
      renderHeadersAttr(context, tContext);
      borderStyleClass = CellUtils.getDataBorderStyle(arc, tContext);
    } // endif (isRowHeader)

    FacesBean bean = getFacesBean(column);
    String userStyleClass = getStyleClass(bean);
    String userInlineStyle = getInlineStyle(bean);

    renderStyleClasses(context, arc, new String[]{userStyleClass, cellClass, borderStyleClass});

    writer.writeAttribute("style", userInlineStyle, null);

    if (colData.getNoWrap(physicalIndex) && !getShouldWrap())
      writer.writeAttribute(XhtmlConstants.NOWRAP_ATTRIBUTE, Boolean.TRUE, null);

    CellUtils.renderSpan(context, true /*isRowSpan*/, rowSpan);

    _renderKids(context, arc, tContext, column, kidIndex);

    writer.endElement(isRowHeader
      ? XhtmlConstants.TABLE_HEADER_ELEMENT
      : XhtmlConstants.TABLE_DATA_ELEMENT);
  }

  // added this method so that the column width can be overridden
  /**
   *  @todo I don't think this method is used.
   */
  protected Object getColumnWidthFromTable(TableRenderingContext tContext,
                                  int index)
  {
    return tContext.getColumnWidth(index);
  }

  /**
   * protected so that the table data style can be overridden.
   * see FocusColumnRenderer
   */
  protected String getTableDataStyleClass(TableRenderingContext tContext)
  {
    return getDataStyleClass(tContext);
  }


  protected boolean isSpecialColumn()
  {
    return false;
  }

  /**
   */
  private void _computeMode(
    RenderingContext arc,
    TableRenderingContext tContext,
    UIComponent           component,
    FacesBean             bean)
  {
    boolean shouldWrap = getShouldWrap();

    UIComponent header = getFacet(component, CoreColumn.HEADER_FACET);
    Object headerText = getHeaderText(bean);

    // SpecialColumn check is to fix
    // 3820854 APPS: NEED WAY TO NOT RENDER SELECTION COLUMN HEADER
    if (((header!=null) || (headerText != null)) &&
        !isSpecialColumn())
      tContext.columnHeadersPresent();

    ColumnData colData = tContext.getColumnData();
    if (isSpecialColumn())
    {
      colData.setSpecialColumnData(tContext,
                                   arc,
                                   getNoWrap(bean) && !shouldWrap,
                                   shouldWrap,
                                   getFormatType(bean));
    }
    else
    {
      colData.setColumnData(getWidth(bean),
                            getFormatType(bean),
                            getNoWrap(bean) && !shouldWrap,
                            getHeaderNoWrap(bean) && !shouldWrap,
                            getSeparateRows(bean),
                            getRowHeader(bean));
    }

    NodeData parentNode = getParentNode(tContext);
    if (parentNode!=null)
    {
      // we are inside a column group

      NodeData currentNode = new NodeData(); //create a leaf node
      if (parentNode.rows < 2)
        parentNode.rows = 2;
      parentNode.cols++;
      parentNode.set(parentNode.currentChild, currentNode);
    }

    UIComponent footer = getFacet(component, CoreColumn.FOOTER_FACET);
    if (footer != null)
    {
      colData.setCurrentColumnHasFooter();
      // if there is a footer, then we need to use explicit headers.
      // see bug 3211593:
      tContext.setExplicitHeaderIDMode(true);
    }
  }

  private void _renderFooterMode(
    FacesContext        context,
    RenderingContext arc,
    TableRenderingContext tContext,
    UIComponent           column)
    throws IOException
  {
    ColumnData colData = tContext.getColumnData();

    // only columns that are after the first column that has a footer should
    // render anything:
    if (colData.getPhysicalColumnIndex() <
        colData.getPhysicalIndexOfFirstFooter())
      return;

    ResponseWriter writer = context.getResponseWriter();
    writer.startElement(XhtmlConstants.TABLE_DATA_ELEMENT, null);
    renderHeadersAttr(context, tContext);

    // determine the style for the total cell based on the
    // type of the cell in that table column.
    // totalRow children are either left-justified or right-justified. We
    // right-justify for numbers, and left-justify for everything else:
    String styleClass = ColumnData.selectFormat(tContext,
                                  SkinSelectors.AF_COLUMN_TOTAL_TEXT_STYLE,
                                  SkinSelectors.AF_COLUMN_TOTAL_NUMBER_STYLE,
                                  SkinSelectors.AF_COLUMN_TOTAL_TEXT_STYLE);

    if (tContext.hasGrid(colData.getPhysicalColumnIndex(),
                         true /* vertical*/))
    {
      String borderStyleClass =
               CellUtils.getBorderClass(false, true, false, false);

      renderStyleClasses(context, arc,
                         new String[]{styleClass, borderStyleClass});

    }
    else
      renderStyleClass(context, arc, styleClass);

    UIComponent footer = getFacet(column, CoreColumn.FOOTER_FACET);
    if (footer != null)
      encodeChild(context, footer);


    writer.endElement(XhtmlConstants.TABLE_DATA_ELEMENT);
  }

  private void _renderHeaderMode(
    FacesContext        context,
    RenderingContext arc,
    TableRenderingContext tContext,
    UIComponent           column)
    throws IOException
  {
    final ColumnData colData = tContext.getColumnData();
    int rowSpan = colData.getHeaderRowSpan();
    NodeData parentNode = getParentNode(tContext);
    if (parentNode!=null)
    {
      // we are inside a columnGroupBean

      NodeData currentNode = parentNode.get(parentNode.currentChild);
      if (currentNode.waitUntilRow != 0)
        return;  // we have already been rendered
      else
      {
        rowSpan -= colData.getRowIndex();
        currentNode.waitUntilRow = -1; // mark that we have been rendered
      }
    }
    else if (colData.getRowIndex() > 0)
    {
      // we are not inside a columnGroup. However, we are spanning rows, and
      // the table is trying to render us several times. We must make sure we
      // render only the first time
      return;
    }

    String colID = renderHeaderAndSpan(context,
                                       arc,
                                       tContext,
                                       column,
                                       rowSpan,
                                       1//colSpan
                                       );

    if (colID != null)
    {
      // set the headerID for this column
      colData.setHeaderID(colData.getPhysicalColumnIndex(),
                          colID);

      if (parentNode != null)
        colID = parentNode.headerIDs + " " + colID;

      // set the collected space-delimited set of headerIDs
      // for this column and its parents
      colData.setHeaderIDs(colData.getPhysicalColumnIndex(),
                           colID);
    }
  }


  //
  // static code follows ***********************************************
  //

  public static String getDataStyleClass(TableRenderingContext tContext)
  {
    final String cellClass;
    final ColumnData colData = tContext.getColumnData();
    final RowData rowData = tContext.getRowData();
    int logicalColumn = colData.getLogicalColumnIndex();
    //ystem.out.println("logCol:"+logicalColumn);
    BandingData banding = tContext.getBanding();
    boolean band = banding.getBand(tContext,
                                   rowData.getRangeIndex(),
                                   colData.getPhysicalColumnIndex(),
                                   logicalColumn);

    if (band)
    {
      cellClass = ColumnData.selectFormat(
           tContext,
           SkinSelectors.AF_COLUMN_CELL_TEXT_BAND_STYLE,
           SkinSelectors.AF_COLUMN_CELL_NUMBER_BAND_STYLE,
           SkinSelectors.AF_COLUMN_CELL_ICON_BAND_STYLE);
    }
    else
    {

      cellClass = ColumnData.selectFormat(
           tContext,
           SkinSelectors.AF_COLUMN_CELL_TEXT_STYLE,
           SkinSelectors.AF_COLUMN_CELL_NUMBER_STYLE,
           SkinSelectors.AF_COLUMN_CELL_ICON_FORMAT_STYLE);
    }

    return cellClass;
  }

  public static String renderDataStyleClass(
    FacesContext          context,
    RenderingContext   arc,
    TableRenderingContext tContext)
    throws IOException
  {
    String cellClass = getDataStyleClass(tContext);
    renderStyleClass(context, arc, cellClass);
    return cellClass;

  }

  protected boolean getShouldWrap()
  {
    return false;
  }

  private ColumnGroupRenderer _columnGroupRenderer =
    new ColumnGroupRenderer();
  private static final TrinidadLogger _LOG = TrinidadLogger.createTrinidadLogger(ColumnRenderer.class);
}
