/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.volatility.surface;

import javax.time.Instant;

import com.opengamma.core.config.ConfigSource;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.money.Currency;

/**
 * A source of yield curve definitions based on configuration.
 * <p>
 * This supplies curve definitions from a {@link ConfigSource}.
 */
public class ConfigDBVolatilitySurfaceDefinitionSource implements VolatilitySurfaceDefinitionSource {

  /**
   * The config source for the data.
   */
  private final ConfigSource _configSource;

  /**
   * Creates an instance backed by a config source.
   * @param configSource  the source, not null
   */
  public ConfigDBVolatilitySurfaceDefinitionSource(final ConfigSource configSource) {
    ArgumentChecker.notNull(configSource, "configSource");
    _configSource = configSource;
  }

  /**
   * Gets the config source.
   * @return the config source, not null
   */
  protected ConfigSource getConfigSource() {
    return _configSource;
  }

  //-------------------------------------------------------------------------
  @Override
  public VolatilitySurfaceDefinition<?, ?> getDefinition(final Currency ccy, final String name, final String instrumentType) {
    return _configSource.getLatestByName(VolatilitySurfaceDefinition.class, name + "_" + instrumentType + "_" + ccy.getCode());
  }

  @Override
  public VolatilitySurfaceDefinition<?, ?> getDefinition(final Currency ccy, final String name, final Instant version, final String instrumentType) {
    return _configSource.getByName(VolatilitySurfaceDefinition.class, name + "_" + instrumentType + "_" + ccy.getCode(), version);
  }

}
