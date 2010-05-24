/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.engine.view.server;

import org.fudgemsg.FudgeFieldContainer;
import org.fudgemsg.MutableFudgeFieldContainer;
import org.fudgemsg.mapping.FudgeBuilder;
import org.fudgemsg.mapping.FudgeDeserializationContext;
import org.fudgemsg.mapping.FudgeSerializationContext;

import com.opengamma.engine.position.Portfolio;
import com.opengamma.engine.position.PortfolioImpl;
import com.opengamma.engine.position.PortfolioNode;
import com.opengamma.engine.position.PortfolioNodeImpl;
import com.opengamma.id.UniqueIdentifier;

/**
 * Fudge message builder for {@code Portfolio}.
 */
public class PortfolioBuilder implements FudgeBuilder<Portfolio> {

  private static final String FIELD_IDENTIFIER = "identifier";
  private static final String FIELD_NAME = "name";
  private static final String FIELD_ROOT = "root";

  @Override
  public MutableFudgeFieldContainer buildMessage(FudgeSerializationContext context, Portfolio portfolio) {
    final MutableFudgeFieldContainer message = context.newMessage();
    context.objectToFudgeMsg(message, FIELD_IDENTIFIER, null, portfolio.getUniqueIdentifier());
    message.add(FIELD_NAME, portfolio.getName());
    context.objectToFudgeMsg(message, FIELD_ROOT, null, portfolio.getRootNode());
    return message;
  }

  @Override
  public Portfolio buildObject(FudgeDeserializationContext context, FudgeFieldContainer message) {
    final UniqueIdentifier id = context.fieldValueToObject(UniqueIdentifier.class, message.getByName(FIELD_IDENTIFIER));
    final String name = message.getFieldValue(String.class, message.getByName(FIELD_NAME));
    final PortfolioNode node = context.fieldValueToObject(PortfolioNode.class, message.getByName(FIELD_ROOT));
    return new PortfolioImpl(id, name, (PortfolioNodeImpl) node);
  }

}
