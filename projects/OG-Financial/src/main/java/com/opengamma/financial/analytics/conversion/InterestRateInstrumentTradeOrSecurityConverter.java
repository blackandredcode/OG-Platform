/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.conversion;

import org.apache.commons.lang.Validate;

import com.opengamma.analytics.financial.instrument.InstrumentDefinition;
import com.opengamma.core.holiday.HolidaySource;
import com.opengamma.core.position.Trade;
import com.opengamma.core.region.RegionSource;
import com.opengamma.core.security.Security;
import com.opengamma.core.security.SecuritySource;
import com.opengamma.financial.convention.ConventionBundleSource;
import com.opengamma.financial.security.FinancialSecurity;
import com.opengamma.financial.security.FinancialSecurityVisitor;
import com.opengamma.financial.security.FinancialSecurityVisitorAdapter;
import com.opengamma.financial.security.bond.BondSecurity;
import com.opengamma.financial.security.future.BondFutureSecurity;
import com.opengamma.financial.security.future.InterestRateFutureSecurity;
import com.opengamma.financial.security.option.IRFutureOptionSecurity;

/**
 *
 */
public class InterestRateInstrumentTradeOrSecurityConverter {
  private final FinancialSecurityVisitor<InstrumentDefinition<?>> _securityVisitor;
  private final BondFutureTradeConverter _bondFutureConverter;
  private final BondTradeConverter _bondTradeConverter;
  private final InterestRateFutureTradeConverter _interestRateFutureTradeConverter;
  private final InterestRateFutureOptionTradeConverter _interestRateFutureOptionTradeConverter;

  public InterestRateInstrumentTradeOrSecurityConverter(final HolidaySource holidaySource, final ConventionBundleSource conventionSource, final RegionSource regionSource,
      final SecuritySource securitySource, final boolean forCurves) {
    Validate.notNull(holidaySource, "holiday source");
    Validate.notNull(conventionSource, "convention source");
    Validate.notNull(regionSource, "region source");
    Validate.notNull(securitySource, "security source");
    final CashSecurityConverter cashConverter = new CashSecurityConverter(holidaySource, regionSource);
    final FRASecurityConverter fraConverter = new FRASecurityConverter(holidaySource, regionSource, conventionSource);
    final SwapSecurityConverter swapConverter = new SwapSecurityConverter(holidaySource, conventionSource, regionSource, forCurves);
    final BondSecurityConverter bondConverter = new BondSecurityConverter(holidaySource, conventionSource, regionSource);
    final InterestRateFutureOptionSecurityConverter irFutureOptionConverter = new InterestRateFutureOptionSecurityConverter(holidaySource, conventionSource, regionSource, securitySource);
    final InterestRateFutureSecurityConverter irFutureConverter = new InterestRateFutureSecurityConverter(holidaySource, conventionSource, regionSource);
    final BondFutureSecurityConverter bondFutureConverter = new BondFutureSecurityConverter(securitySource, bondConverter);

    final ZeroDepositConverter zeroDepositConverter = new ZeroDepositConverter(conventionSource);
    _securityVisitor = FinancialSecurityVisitorAdapter.<InstrumentDefinition<?>>builder()
        .cashSecurityVisitor(cashConverter)
        .fraSecurityVisitor(fraConverter)
        .swapSecurityVisitor(swapConverter)
        .interestRateFutureSecurityVisitor(irFutureConverter)
        .bondSecurityVisitor(bondConverter)
        .periodicZeroDepositSecurityVisitor(zeroDepositConverter).create();
    _bondTradeConverter = new BondTradeConverter(bondConverter);
    _bondFutureConverter = new BondFutureTradeConverter(bondFutureConverter);
    _interestRateFutureTradeConverter = new InterestRateFutureTradeConverter(irFutureConverter);
    _interestRateFutureOptionTradeConverter = new InterestRateFutureOptionTradeConverter(irFutureOptionConverter);
  }

  public InstrumentDefinition<?> visit(final Trade trade) {
    Validate.notNull(trade, "trade");
    final FinancialSecurity security = (FinancialSecurity) trade.getSecurity();
    if (security instanceof BondSecurity) {
      return _bondTradeConverter.convert(trade);
    } else if (security instanceof BondFutureSecurity) {
      return _bondFutureConverter.convert(trade);
    } else if (security instanceof InterestRateFutureSecurity) {
      return _interestRateFutureTradeConverter.convert(trade);
    } else if (security instanceof IRFutureOptionSecurity) {
      return _interestRateFutureOptionTradeConverter.convert(trade);
    }
    return security.accept(_securityVisitor);
  }

  public InstrumentDefinition<?> visit(final Security security) {
    return ((FinancialSecurity) security).accept(_securityVisitor);
  }

}
