/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.web.analytics;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anySetOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.testng.annotations.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.opengamma.engine.ComputationTargetSpecification;
import com.opengamma.engine.ComputationTargetType;
import com.opengamma.engine.value.ValueProperties;
import com.opengamma.engine.value.ValuePropertyNames;
import com.opengamma.engine.value.ValueSpecification;
import com.opengamma.engine.view.ExecutionLogMode;
import com.opengamma.engine.view.client.ViewClient;
import com.opengamma.id.UniqueId;
import com.opengamma.web.analytics.formatting.TypeFormatter;

/**
 *
 */
public class LoggingViewportListenerTest {

  private final GridCell _cell1 = new GridCell(0, 1);
  private final GridCell _cell2 = new GridCell(0, 2);
  private final GridCell _cell3 = new GridCell(0, 3);
  private final List<GridCell> _cells12 = Lists.newArrayList(_cell1, _cell2);
  private final List<GridCell> _cells23 = Lists.newArrayList(_cell2, _cell3);
  private final GridStructure _gridStructure = gridStructure(_cell1, _cell2, _cell3);

  /**
   * creates a viewport with logging enabled and then deletes it
   */
  @Test
  public void createDeleteWithLogging() {
    ViewClient viewClient = mock(ViewClient.class);
    LoggingViewportListener listener = new LoggingViewportListener(viewClient);
    ViewportDefinition viewportDef = viewportDef(true, _cells12);
    listener.viewportCreated(viewportDef, _gridStructure);
    verify(viewClient).setMinimumLogMode(ExecutionLogMode.FULL, valueSpecs(_cells12));
    listener.viewportDeleted(viewportDef, _gridStructure);
    verify(viewClient).setMinimumLogMode(ExecutionLogMode.INDICATORS, valueSpecs(_cells12));
  }

  /**
   * creates a deletes a viewport with no logging enabled
   */
  @Test
  public void createDeleteNoLogging() {
    ViewClient viewClient = mock(ViewClient.class);
    LoggingViewportListener listener = new LoggingViewportListener(viewClient);
    ViewportDefinition viewportDef = viewportDef(false, _cells12);
    listener.viewportCreated(viewportDef, _gridStructure);
    listener.viewportDeleted(viewportDef, _gridStructure);
    verify(viewClient, never()).setMinimumLogMode(any(ExecutionLogMode.class), anySetOf(ValueSpecification.class));
  }

  @Test
  public void createUpdateDeleteWithLogging() {
    ViewClient viewClient = mock(ViewClient.class);
    LoggingViewportListener listener = new LoggingViewportListener(viewClient);
    ViewportDefinition viewportDef1 = viewportDef(true, _cells12);
    listener.viewportCreated(viewportDef1, _gridStructure);
    verify(viewClient).setMinimumLogMode(ExecutionLogMode.FULL, valueSpecs(_cells12));

    ViewportDefinition viewportDef2 = viewportDef(true, _cells23);
    listener.viewportUpdated(viewportDef1, viewportDef2, _gridStructure);
    verify(viewClient).setMinimumLogMode(ExecutionLogMode.INDICATORS, valueSpecs(_cell1));
    verify(viewClient).setMinimumLogMode(ExecutionLogMode.FULL, valueSpecs(_cell3));

    listener.viewportDeleted(viewportDef2, _gridStructure);
    verify(viewClient).setMinimumLogMode(ExecutionLogMode.INDICATORS, valueSpecs(_cells23));
  }

  @Test
  public void createUpdateDeleteNoLogging() {
    ViewClient viewClient = mock(ViewClient.class);
    LoggingViewportListener listener = new LoggingViewportListener(viewClient);
    ViewportDefinition viewportDef1 = viewportDef(false, _cells12);
    listener.viewportCreated(viewportDef1, _gridStructure);
    ViewportDefinition viewportDef2 = viewportDef(false, _cells23);
    listener.viewportUpdated(viewportDef1, viewportDef2, _gridStructure);
    listener.viewportDeleted(viewportDef2, _gridStructure);
    verify(viewClient, never()).setMinimumLogMode(any(ExecutionLogMode.class), anySetOf(ValueSpecification.class));
  }

  @Test
  public void twoViewportsWithLoggingAndOverlappingCells() {
    ViewClient viewClient = mock(ViewClient.class);
    LoggingViewportListener listener = new LoggingViewportListener(viewClient);
    ViewportDefinition viewportDef1 = viewportDef(true, _cells12);
    ViewportDefinition viewportDef2 = viewportDef(true, _cells23);
    listener.viewportCreated(viewportDef1, _gridStructure);
    verify(viewClient).setMinimumLogMode(ExecutionLogMode.FULL, valueSpecs(_cell1, _cell2));
    listener.viewportCreated(viewportDef2, _gridStructure);
    verify(viewClient).setMinimumLogMode(ExecutionLogMode.FULL, valueSpecs(_cell3));
    listener.viewportDeleted(viewportDef1, _gridStructure);
    verify(viewClient).setMinimumLogMode(ExecutionLogMode.INDICATORS, valueSpecs(_cell1));
    listener.viewportDeleted(viewportDef2, _gridStructure);
    verify(viewClient).setMinimumLogMode(ExecutionLogMode.INDICATORS, valueSpecs(_cell2, _cell3));
  }
// -----------------------------------------------------------------------------------

  private static ViewportDefinition viewportDef(boolean enableLogging, GridCell... cells) {
    return viewportDef(enableLogging, Arrays.asList(cells));
  }

  private static ViewportDefinition viewportDef(boolean enableLogging, List<GridCell> cells) {
    return new ArbitraryViewportDefinition(0, cells, TypeFormatter.Format.CELL, enableLogging);
  }

  private static GridStructure gridStructure(GridCell... cells) {
    return gridStructure(Arrays.asList(cells));
  }

  private static GridStructure gridStructure(List<GridCell> cells) {
    GridStructure mock = mock(GridStructure.class);
    for (GridCell cell : cells) {
      when(mock.getValueSpecificationForCell(cell.getRow(), cell.getColumn())).thenReturn(valueSpec(cell));
    }
    return mock;
  }

  private static ValueSpecification valueSpec(GridCell cell) {
    int row = cell.getRow();
    int col = cell.getColumn();
    ComputationTargetSpecification target = new ComputationTargetSpecification(ComputationTargetType.POSITION,
                                                                               UniqueId.of("Cell", row + "," + col));
    ValueProperties properties = ValueProperties.with(ValuePropertyNames.FUNCTION, "fnName").get();
    return new ValueSpecification("valueName(" + row + "," + col + ")", target, properties);
  }

  private Set<ValueSpecification> valueSpecs(GridCell... cells) {
    return valueSpecs(Arrays.asList(cells));
  }

  private Set<ValueSpecification> valueSpecs(List<GridCell> cells) {
    Set<ValueSpecification> specs = Sets.newHashSet();
    for (GridCell cell : cells) {
      specs.add(valueSpec(cell));
    }
    return specs;
  }
}