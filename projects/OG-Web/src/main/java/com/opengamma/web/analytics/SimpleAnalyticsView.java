/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.web.analytics;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.opengamma.engine.ComputationTargetResolver;
import com.opengamma.engine.view.ViewResultModel;
import com.opengamma.engine.view.calc.ViewCycle;
import com.opengamma.engine.view.compilation.CompiledViewDefinition;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.tuple.Pair;

/**
 * Default implementation of {@link AnalyticsView}. This class isn't meant to be thread safe. A thread calling any
 * method that mutates the state must have an exclusive lock. The get methods can safely be called by multiple
 * concurrent threads.
 * @see LockingAnalyticsView
 * @see com.opengamma.web.analytics Package concurrency notes
 */
/* package */ class SimpleAnalyticsView implements AnalyticsView {

  private static final Logger s_logger = LoggerFactory.getLogger(SimpleAnalyticsView.class);

  private final ResultsCache _cache = new ResultsCache();
  private final ComputationTargetResolver _targetResolver;
  private final String _viewId;

  private MainAnalyticsGrid _portfolioGrid;
  private MainAnalyticsGrid _primitivesGrid;
  private CompiledViewDefinition _compiledViewDefinition;

  /**
   * @param viewId ID of the view
   * @param portoflioCallbackId ID that is passed to the listener when the structure of the portfolio grid changes.
   * This class makes no assumptions about its value
   * @param primitivesCallbackId ID that is passed to the listener when the structure of the primitives grid changes.
 * This class makes no assumptions about its value
   * @param targetResolver For looking up calculation targets by specification
   */
  /* package */ SimpleAnalyticsView(String viewId,
                                    String portoflioCallbackId,
                                    String primitivesCallbackId,
                                    ComputationTargetResolver targetResolver) {
    _viewId = viewId;
    ArgumentChecker.notEmpty(viewId, "viewId");
    ArgumentChecker.notEmpty(portoflioCallbackId, "portoflioGridId");
    ArgumentChecker.notEmpty(primitivesCallbackId, "primitivesGridId");
    ArgumentChecker.notNull(targetResolver, "targetResolver");
    _targetResolver = targetResolver;
    _portfolioGrid = MainAnalyticsGrid.emptyPortfolio(portoflioCallbackId);
    _primitivesGrid = MainAnalyticsGrid.emptyPrimitives(primitivesCallbackId);
  }

  @Override
  public List<String> updateStructure(CompiledViewDefinition compiledViewDefinition) {
    _compiledViewDefinition = compiledViewDefinition;
    // TODO this loses all dependency graphs. new grid needs to rebuild graphs from old grid. need stable row and col IDs to do that
    _portfolioGrid = MainAnalyticsGrid.portfolio(_compiledViewDefinition, _portfolioGrid.getCallbackId(), _targetResolver);
    _primitivesGrid = MainAnalyticsGrid.primitives(_compiledViewDefinition, _primitivesGrid.getCallbackId(), _targetResolver);
    List<String> gridIds = new ArrayList<String>();
    gridIds.add(_portfolioGrid.getCallbackId());
    gridIds.add(_primitivesGrid.getCallbackId());
    gridIds.addAll(_portfolioGrid.getDependencyGraphCallbackIds());
    gridIds.addAll(_primitivesGrid.getDependencyGraphCallbackIds());
    return gridIds;
  }

  @Override
  public List<String> updateResults(ViewResultModel results, ViewCycle viewCycle) {
    _cache.put(results);
    List<String> updatedIds = Lists.newArrayList();
    updatedIds.addAll(_portfolioGrid.updateResults(_cache, viewCycle));
    updatedIds.addAll(_primitivesGrid.updateResults(_cache, viewCycle));
    return updatedIds;
  }

  private MainAnalyticsGrid getGrid(GridType gridType) {
    switch (gridType) {
      case PORTFORLIO:
        return _portfolioGrid;
      case PRIMITIVES:
        return _primitivesGrid;
      default:
        throw new IllegalArgumentException("Unexpected grid type " + gridType);
    }
  }

  @Override
  public GridStructure getGridStructure(GridType gridType) {
    GridStructure gridStructure = getGrid(gridType).getGridStructure();
    s_logger.debug("View {} returning grid structure for the {} grid: {}", new Object[]{_viewId, gridType, gridStructure});
    return gridStructure;
  }

  @Override
  public Pair<Long, String> createViewport(GridType gridType,
                                           int viewportId,
                                           String callbackId,
                                           ViewportDefinition viewportDefinition) {
    long version = getGrid(gridType).createViewport(viewportId, callbackId, viewportDefinition);
    s_logger.debug("View {} created viewport ID {} for the {} grid from {}",
                   new Object[]{_viewId, viewportId, gridType, viewportDefinition});
    return Pair.of(version, callbackId);
  }

  @Override
  public Pair<Long, String> updateViewport(GridType gridType, int viewportId, ViewportDefinition viewportDefinition) {
    s_logger.debug("View {} updating viewport {} for {} grid to {}",
                   new Object[]{_viewId, viewportId, gridType, viewportDefinition});
    long version = getGrid(gridType).updateViewport(viewportId, viewportDefinition);
    String callbackId = getGrid(gridType).getViewport(viewportId).getCallbackId();
    return Pair.of(version, callbackId);
  }

  @Override
  public void deleteViewport(GridType gridType, int viewportId) {
    s_logger.debug("View {} deleting viewport {} from the {} grid", new Object[]{_viewId, viewportId, gridType});
    getGrid(gridType).deleteViewport(viewportId);
  }

  @Override
  public ViewportResults getData(GridType gridType, int viewportId) {
    s_logger.debug("View {} getting data for viewport {} of the {} grid", new Object[]{_viewId, viewportId, gridType});
    return getGrid(gridType).getData(viewportId);
  }

  @Override
  public String openDependencyGraph(GridType gridType, int graphId, String callbackId, int row, int col) {
    s_logger.debug("View {} opening dependency graph for cell ({}, {}) of the {} grid",
                   new Object[]{_viewId, row, col, gridType});
    getGrid(gridType).openDependencyGraph(graphId, callbackId, row, col, _compiledViewDefinition);
    return callbackId;
  }

  @Override
  public void closeDependencyGraph(GridType gridType, int graphId) {
    s_logger.debug("View {} closing dependency graph {} of the {} grid", new Object[]{_viewId, graphId, gridType});
    getGrid(gridType).closeDependencyGraph(graphId);
  }

  @Override
  public GridStructure getGridStructure(GridType gridType, int graphId) {
    DependencyGraphGridStructure gridStructure = getGrid(gridType).getGridStructure(graphId);
    s_logger.debug("View {} returning grid structure for dependency graph {} of the {} grid: {}",
                   new Object[]{_viewId, graphId, gridType, gridStructure});
    return gridStructure;
  }

  @Override
  public Pair<Long, String> createViewport(GridType gridType,
                                           int graphId,
                                           int viewportId,
                                           String callbackId,
                                           ViewportDefinition viewportDefinition) {
    long version = getGrid(gridType).createViewport(graphId, viewportId, callbackId, viewportDefinition);
    s_logger.debug("View {} created viewport ID {} for dependency graph {} of the {} grid using {}",
                   new Object[]{_viewId, viewportId, graphId, gridType, viewportDefinition});
    return Pair.of(version, callbackId);
  }

  @Override
  public Pair<Long, String> updateViewport(GridType gridType,
                                           int graphId,
                                           int viewportId,
                                           ViewportDefinition viewportDefinition) {
    s_logger.debug("View {} updating viewport for dependency graph {} of the {} grid using {}",
                   new Object[]{_viewId, graphId, gridType, viewportDefinition});
    long version = getGrid(gridType).updateViewport(graphId, viewportId, viewportDefinition);
    String callbackId = getGrid(gridType).getCallbackId(graphId, viewportId);
    return Pair.of(version, callbackId);
  }

  @Override
  public void deleteViewport(GridType gridType, int graphId, int viewportId) {
    s_logger.debug("View {} deleting viewport {} from dependency graph {} of the {} grid",
                   new Object[]{_viewId, viewportId, graphId, gridType});
    getGrid(gridType).deleteViewport(graphId, viewportId);
  }

  @Override
  public ViewportResults getData(GridType gridType, int graphId, int viewportId) {
    s_logger.debug("View {} getting data for the viewport {} of the dependency graph {} of the {} grid",
                   new Object[]{_viewId, viewportId, graphId, gridType});
    return getGrid(gridType).getData(graphId, viewportId);
  }

}