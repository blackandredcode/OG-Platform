/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.livedata.server;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.junit.Test;

import com.opengamma.id.IdentifierBundle;
import com.opengamma.livedata.LiveDataValueUpdate;
import com.opengamma.livedata.normalization.StandardRules;

/**
 * 
 *
 * @author pietari
 */
public class MarketDataDistributorTest {
  
  @Test
  public void sequenceNumber() {
    MarketDataDistributor mdd = new MarketDataDistributor(
        new DistributionSpecification(
            new IdentifierBundle(),
            StandardRules.getNoNormalization(),
            "LiveData.Bloomberg.Equity.AAPL"),
        new Subscription("", false),
        Collections.<MarketDataSender>emptySet()
        );
    
    assertEquals(LiveDataValueUpdate.SEQUENCE_START, mdd.getNumMessagesSent());
  }

}
