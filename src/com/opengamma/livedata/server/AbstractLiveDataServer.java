/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.livedata.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opengamma.livedata.LiveDataSpecification;

/**
 * The base class from which most OpenGamma Live Data feed servers should
 * extend. Handles most common cases for distributed contract management.
 *
 * @author kirk
 */
public abstract class AbstractLiveDataServer {
  private static final Logger s_logger = LoggerFactory.getLogger(AbstractLiveDataServer.class);


  public void terminatePublication(LiveDataSpecification dataSpec) {
    s_logger.info("Terminating publication of {}", dataSpec);
  }
}
