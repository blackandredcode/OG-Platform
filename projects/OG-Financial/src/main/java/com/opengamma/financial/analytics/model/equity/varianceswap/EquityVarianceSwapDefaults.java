/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.model.equity.varianceswap;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opengamma.core.security.Security;
import com.opengamma.engine.ComputationTarget;
import com.opengamma.engine.ComputationTargetType;
import com.opengamma.engine.function.FunctionCompilationContext;
import com.opengamma.engine.value.ValuePropertyNames;
import com.opengamma.engine.value.ValueRequirement;
import com.opengamma.engine.value.ValueRequirementNames;
import com.opengamma.financial.analytics.OpenGammaFunctionExclusions;
import com.opengamma.financial.property.DefaultPropertyFunction;
import com.opengamma.financial.security.FinancialSecurity;
import com.opengamma.financial.security.equity.EquityVarianceSwapSecurity;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.tuple.Pair;

/**
 *
 */
public class EquityVarianceSwapDefaults extends DefaultPropertyFunction {
  private static final Logger s_logger = LoggerFactory.getLogger(EquityForwardCalculationDefaults.class);
  private static final String[] VALUE_REQUIREMENTS = new String[] {
    ValueRequirementNames.PRESENT_VALUE,
    ValueRequirementNames.YIELD_CURVE_NODE_SENSITIVITIES,
    ValueRequirementNames.VEGA_QUOTE_MATRIX
  };
  private final PriorityClass _priority;
  private final Map<String, Pair<String, String>> _curvesPerEquity;
  private final Map<String, String> _surfacesPerEquity;

  public EquityVarianceSwapDefaults(final String priority, final String... perEquityConfig) {
    super(ComputationTargetType.SECURITY, true);
    ArgumentChecker.notNull(priority, "priority");
    ArgumentChecker.notNull(perEquityConfig, "per equity config");
    final int n = perEquityConfig.length;
    ArgumentChecker.isTrue(n % 4 == 0, "Must have one curve config, discounting curve name and surface name per equity");
    _priority = PriorityClass.valueOf(priority);
    _curvesPerEquity = new LinkedHashMap<String, Pair<String, String>>();
    _surfacesPerEquity = new LinkedHashMap<String, String>();
    for (int i = 0; i < perEquityConfig.length; i += 4) {
      final String currency = perEquityConfig[i];
      final Pair<String, String> pair = Pair.of(perEquityConfig[i + 1], perEquityConfig[i + 2]);
      final String surfaceName = perEquityConfig[i + 3];
      _curvesPerEquity.put(currency, pair);
      _surfacesPerEquity.put(currency, surfaceName);
    }
  }

  @Override
  public boolean canApplyTo(final FunctionCompilationContext context, final ComputationTarget target) {
    if (target.getType() != ComputationTargetType.SECURITY) {
      return false;
    }
    final Security security = target.getSecurity();
    if (!(security instanceof FinancialSecurity)) {
      return false;
    }
    if (!(security instanceof EquityVarianceSwapSecurity)) {
      return false;
    }
    final EquityVarianceSwapSecurity varianceSwap = (EquityVarianceSwapSecurity) security;
    final String underlyingEquity = varianceSwap.getSpotUnderlyingId().getValue();
    return _surfacesPerEquity.containsKey(underlyingEquity);
  }

  @Override
  protected void getDefaults(final PropertyDefaults defaults) {
    for (final String valueRequirement : VALUE_REQUIREMENTS) {
      defaults.addValuePropertyName(valueRequirement, ValuePropertyNames.CURVE);
      defaults.addValuePropertyName(valueRequirement, ValuePropertyNames.CURVE_CALCULATION_CONFIG);
      defaults.addValuePropertyName(valueRequirement, ValuePropertyNames.SURFACE);
    }
  }

  @Override
  protected Set<String> getDefaultValue(final FunctionCompilationContext context, final ComputationTarget target, final ValueRequirement desiredValue,
      final String propertyName) {
    final EquityVarianceSwapSecurity varianceSwap = (EquityVarianceSwapSecurity) target.getSecurity();
    final String underlyingEquity = varianceSwap.getSpotUnderlyingId().getValue();
    if (!_surfacesPerEquity.containsKey(underlyingEquity)) {
      s_logger.error("Could not get config for underlying equity " + underlyingEquity + "; should never happen");
      return null;
    }
    if (ValuePropertyNames.SURFACE.equals(propertyName)) {
      final String surfaceName = _surfacesPerEquity.get(underlyingEquity);
      return Collections.singleton(surfaceName);
    }
    final Pair<String, String> pair = _curvesPerEquity.get(underlyingEquity);
    if (ValuePropertyNames.CURVE.equals(propertyName)) {
      return Collections.singleton(pair.getFirst());
    }
    if (ValuePropertyNames.CURVE_CALCULATION_CONFIG.equals(propertyName)) {
      return Collections.singleton(pair.getSecond());
    }
    return null;
  }

  @Override
  public PriorityClass getPriority() {
    return _priority;
  }

  @Override
  public String getMutualExclusionGroup() {
    return OpenGammaFunctionExclusions.EQUITY_VARIANCE_SWAP_DEFAULTS;
  }

}
